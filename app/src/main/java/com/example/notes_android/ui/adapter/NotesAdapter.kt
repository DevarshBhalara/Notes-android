package com.example.notes_android.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notes_android.data.Note
import com.example.notes_android.databinding.ItemRvNotesBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class NotesAdapter(options: FirestoreRecyclerOptions<Note>): FirestoreRecyclerAdapter<Note, NotesAdapter.NoteViewHolder>(options) {

    class NoteViewHolder(private val binding: ItemRvNotesBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) {
            binding.note = note
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder =
        NoteViewHolder(ItemRvNotesBinding.inflate(LayoutInflater.from(parent.context), parent, false))


    override fun onBindViewHolder(holder: NoteViewHolder, position: Int, note: Note) {
        holder.bind(note)
    }

    fun updateData() {
        notifyDataSetChanged()
    }
}