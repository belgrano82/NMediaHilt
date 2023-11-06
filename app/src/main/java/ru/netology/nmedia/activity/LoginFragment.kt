package ru.netology.nmedia.activity

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentLoginBinding
import ru.netology.nmedia.viewmodel.LoginViewModel
import ru.netology.nmedia.viewmodel.LoginResult
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val usernameEditText = binding.username
        val passwordEditText = binding.password
        val loginButton = binding.login

        fun checkUsernameAndPassword() {
            val userText = usernameEditText.text.toString().isNotEmpty()
            val passwordText = passwordEditText.text.toString().isNotEmpty()

            loginButton.isEnabled = userText && passwordText
        }

        val textChangedListener = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                checkUsernameAndPassword()

            }
        }

        usernameEditText.addTextChangedListener(textChangedListener)
        passwordEditText.addTextChangedListener(textChangedListener)


        loginButton.setOnClickListener {


            loginViewModel.auth(
                usernameEditText.text.toString(),
                passwordEditText.text.toString()
            ).observe(viewLifecycleOwner) { result ->
                when (result) {

                    is LoginResult.Success -> {
                        val data = result.data

                        Log.d("LoginFragment", data.token.toString())

                        if (data.token != null) {

                            findNavController().navigateUp()

                            Toast.makeText(
                                context, "Welcome, ${usernameEditText.text}!",
                                Toast.LENGTH_LONG
                            ).show()

                        }

                    }

                    is LoginResult.Error -> {
                        val errorMessage = result.message
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }


    override fun onDestroyView() {

        super.onDestroyView()
        _binding = null
    }

}