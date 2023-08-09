package com.example.notes_android.ui.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class CreateUserRepository(
    private val firebaseAuth: FirebaseAuth
) {

    suspend fun createUserWithEmailPassword(email: String, password: String): Flow<Map<Boolean, String>> = flow {
        try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user

            emit(mapOf((user != null) to "Account Created Successfully!"))
        } catch (e: Exception) {
            e.localizedMessage?.let {
                emit(mapOf(false to it))
            }

        }
    }.flowOn(Dispatchers.IO)

}