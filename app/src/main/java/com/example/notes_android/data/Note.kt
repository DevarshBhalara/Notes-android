package com.example.notes_android.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note (
    val title: String = "",
    val content: String = "",
    val timeStamp: Timestamp = Timestamp.now(),
): Parcelable