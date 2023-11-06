package ru.netology.nmedia.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostViewHolder
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostPagingSource
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : AppCompatActivity(R.layout.activity_app) {
    @Inject
    lateinit var auth: AppAuth

    @Inject
    lateinit var googleApiAvailability: GoogleApiAvailability

    @Inject
    lateinit var fcmToken: Task<String>






    private val authViewModel: AuthViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text?.isNotBlank() != true) {
                return@let
            }

            intent.removeExtra(Intent.EXTRA_TEXT)
            findNavController(R.id.nav_host_fragment)
                .navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = text
                    }
                )
        }

        authViewModel.data.observe(this) {
            invalidateOptionsMenu()
        }

        fcmToken.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("some stuff happened: ${task.exception}")
                return@addOnCompleteListener
            }

            val token = task.result
            println(token)
        }

        checkGoogleApiAvailability()

        requestNotificationsPermission()



        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)

                menu.let {
                    it.setGroupVisible(R.id.unauthenticated, !authViewModel.authenticated)
                    it.setGroupVisible(R.id.authenticated, authViewModel.authenticated)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.signin -> {
                        auth.setAuth(5, "x-token")

//                        findNavController(R.id.nav_host_fragment)
//                            .navigate(R.id.action_feedFragment_to_loginFragment)
//                        authViewModel.data.value?.let { authViewModel.data.value!!.token?.let { token ->
//                            auth.setAuth(it.id,
//                                token
//                            )
//                        } }


                        true
                    }

                    R.id.signup -> {
                        // TODO: just hardcode it, implementation must be in homework
                        auth.setAuth(5, "x-token")
                        true
                    }

                    R.id.signout -> {
                        // TODO: just hardcode it, implementation must be in homework
                        auth.removeAuth()

                        true
                    }

                    else -> false
                }

        })
    }

    private fun requestNotificationsPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }

        val permission = Manifest.permission.POST_NOTIFICATIONS

        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            return
        }

        requestPermissions(arrayOf(permission), 1)
    }

    private fun checkGoogleApiAvailability() {
        val code = googleApiAvailability.isGooglePlayServicesAvailable(this@AppActivity)
        if (code == ConnectionResult.SUCCESS) {
            return
        }
        if (googleApiAvailability.isUserResolvableError(code)) {
            googleApiAvailability.getErrorDialog(this@AppActivity, code, 9000)?.show()
            return
        }
        Toast.makeText(this@AppActivity, R.string.google_play_unavailable, Toast.LENGTH_LONG)
            .show()
    }
}