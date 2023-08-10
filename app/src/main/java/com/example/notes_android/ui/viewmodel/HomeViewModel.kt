package com.example.notes_android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.notes_android.data.Note
import com.example.notes_android.ui.repository.HomeRepository
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository
): ViewModel() {

    private val _options = MutableStateFlow<FirestoreRecyclerOptions<Note>?>(null)
    val options = _options.asStateFlow()

    init {
        getNotes()
    }

     fun getNotes() {
        viewModelScope.launch {
            homeRepository.getNotes().collectLatest {
                _options.emit(it)
            }
        }
    }

}