package com.example.notes_android.di

import com.example.notes_android.ui.repository.CreateUserRepository
import com.example.notes_android.ui.repository.LoginRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
    @Provides
    @Singleton
    fun provideCreateUserRepository(
        firebaseAuth: FirebaseAuth
    ): CreateUserRepository = CreateUserRepository(firebaseAuth)

    @Provides
    @Singleton
    fun provideLoginRepository(
        firebaseAuth: FirebaseAuth
    ): LoginRepository = LoginRepository(firebaseAuth)

}