package com.example.notes_android.ui.repository

import com.example.notes_android.data.Note
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class AddNoteRepository(private val collectionReference: CollectionReference) {

    suspend fun addNote(note: Note): Flow<Boolean> = flow {
        val task = collectionReference.add(note)
        emit(task.isSuccessful)
    }.flowOn(Dispatchers.IO)

    suspend fun updateNote(note: Note, id: String): Flow<Boolean> = flow {
        val documentReference = collectionReference.document(id)
        val task = documentReference.set(note)
        emit(task.isSuccessful)
    }.flowOn(Dispatchers.IO)

    suspend fun deleteNote(id: String): Flow<Boolean> = flow {
        try {
            collectionReference.document(id).delete().await()
            emit(true)
        } catch (e: Exception) {
            emit(false)
        }
    }.flowOn(Dispatchers.IO)
}