package ru.netology.nmedia.service

import android.app.Application
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FCMModule {

    @Provides
    @Singleton
    fun provideFCMtoken(): Task<String> {
        return FirebaseMessaging.getInstance().token

    }
}