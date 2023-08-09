package com.example.notes_android.ui.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class LoginRepository(private val firebaseAuth: FirebaseAuth) {

    suspend fun loginUser(email: String, password: String): Flow<Map<Boolean, String>> = flow {
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user

            emit(mapOf( (user != null) to "Welcome"))
        } catch (e: Exception) {
            emit(mapOf(false to (e.localizedMessage?.toString() ?: "Some Error Occurred")))
        }
    }
}