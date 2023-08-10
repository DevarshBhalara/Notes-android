package com.example.notes_android.di

import android.util.Log
import com.example.notes_android.data.Utility
import com.example.notes_android.ui.repository.CreateUserRepository
import com.example.notes_android.ui.repository.HomeRepository
import com.example.notes_android.ui.repository.LoginRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
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

    @Provides
    @Singleton
    fun provideCollectionReference(): CollectionReference =
        Utility.getCollectionReference()

    @Provides
    @Singleton
    fun provideHomeRepository(
        collectionReference: CollectionReference
    ): HomeRepository = HomeRepository(collectionReference)

}