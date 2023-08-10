package com.example.notes_android.data

import com.google.firebase.Timestamp
import java.sql.Time

data class Note (
    val title: String = "",
    val content: String = "",
    val timeStamp: Timestamp = Timestamp.now(),

)