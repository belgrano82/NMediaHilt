package ru.netology.nmedia.repository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Response
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(private val apiService: ApiService, private val appAuth: AppAuth) {
    suspend fun auth(username: String, password: String): Response<AuthState> {
        val response = apiService.updateUser(username, password)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        val body = response.body() ?: throw ApiError(response.code(), response.message())
        body.token?.let { appAuth.setAuth(body.id, it) }
        return response
    }
}

@Module
@InstallIn(SingletonComponent::class)
object LoginModule {

    @Provides
    fun provideLoginRepository(apiService: ApiService, appAuth: AppAuth): LoginRepository {
        return LoginRepository(apiService, appAuth)
    }
}