package com.example.notes_android.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes_android.data.Note
import com.example.notes_android.ui.repository.AddNoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNoteViewModel @Inject constructor(
    private val addNoteRepository: AddNoteRepository
) : ViewModel() {

    private val _isAdded = MutableStateFlow<Boolean>(false)
    var isAdded = _isAdded.asStateFlow()

    private val _isDeleted = MutableLiveData<Boolean>(false)
    var isDeleted = _isDeleted

    fun addNote(note: Note) {
        viewModelScope.launch {
            addNoteRepository.addNote(note).collectLatest {
                Log.d("added", it.toString())
                _isAdded.emit(it)
            }
        }
    }

    fun updateNote(note: Note, id: String) {
        viewModelScope.launch {
            addNoteRepository.updateNote(note, id).collectLatest {
                _isAdded.emit(it)
            }
        }
    }

    fun deleteNote(id: String) {
        viewModelScope.launch {
            addNoteRepository.deleteNote(id).collectLatest {
                _isDeleted.value = it
            }
        }
    }
}