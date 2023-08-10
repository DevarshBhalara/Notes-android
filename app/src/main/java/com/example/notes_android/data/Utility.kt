package com.example.notes_android.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class Utility {
    companion object {
        fun getCollectionReference() : CollectionReference {
            val currentUser = FirebaseAuth.getInstance().currentUser
            Log.d("title", currentUser?.uid ?: "Na")
            return FirebaseFirestore.getInstance().collection("notes").document(currentUser?.uid ?: "").collection("my_notes")
        }
    }
}