package com.example.notes_android.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notes_android.data.Note
import com.example.notes_android.databinding.ItemRvNotesBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class NotesAdapter(options: FirestoreRecyclerOptions<Note>,val onItemClick: (Note, String) -> Unit): FirestoreRecyclerAdapter<Note, NotesAdapter.NoteViewHolder>(options) {

    inner class NoteViewHolder(private val binding: ItemRvNotesBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note, position: Int) {
            binding.note = note
            binding.root.setOnClickListener {
                val id = this@NotesAdapter.snapshots.getSnapshot(position).id
                onItemClick(note, id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder =
        NoteViewHolder(ItemRvNotesBinding.inflate(LayoutInflater.from(parent.context), parent, false))


    override fun onBindViewHolder(holder: NoteViewHolder, position: Int, note: Note) {
        holder.bind(note, position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData() {
        notifyDataSetChanged()
    }
}