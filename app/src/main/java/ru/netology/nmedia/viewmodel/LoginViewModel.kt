package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.repository.LoginRepository
import javax.inject.Inject
import kotlin.RuntimeException

//class LoginViewModel : ViewModel() {
//private val loginRepository =  LoginRepository()
//
//
//    fun auth(username: String, password: String) = viewModelScope.async{
//       loginRepository.auth(username, password)
//    }
//}

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository) : ViewModel() {


    fun auth(username: String, password: String): LiveData<LoginResult<AuthState>> {
        val resultLiveData = MutableLiveData<LoginResult<AuthState>>()

        viewModelScope.launch {
            try {
                val response = loginRepository.auth(username, password)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        resultLiveData.value = LoginResult.Success(body)
                    } else {
                        resultLiveData.value = LoginResult.Error("Response body is null")
                    }
                }
            } catch (e: Exception) {
                if (e.message == null) {
                    resultLiveData.value =
                        LoginResult.Error("Invalid login or password. Try again!")
                } else {
                    resultLiveData.value = LoginResult.Error("Network error: ${e.message}")
                }
            }
        }

        return resultLiveData
    }
}

sealed class LoginResult<out T> {
    data class Success<out T>(val data: T) : LoginResult<T>()
    data class Error(val message: String) : LoginResult<Nothing>()
}