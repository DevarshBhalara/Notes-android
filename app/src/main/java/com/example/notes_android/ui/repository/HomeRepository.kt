package com.example.notes_android.ui.repository

import android.util.Log
import com.example.notes_android.data.Note
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HomeRepository(private val collectionReference: CollectionReference) {

    suspend fun getNotes(): Flow< FirestoreRecyclerOptions<Note>> = flow {
        val query = collectionReference.orderBy("title", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<Note>()
            .setQuery(query, Note::class.java)
            .build()
        Log.d( "title", options.snapshots.toString())
        emit(options)
    }

}