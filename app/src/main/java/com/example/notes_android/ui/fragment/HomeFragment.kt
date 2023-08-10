package com.example.notes_android.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notes_android.R
import com.example.notes_android.data.Note
import com.example.notes_android.databinding.FragmentHomeBinding
import com.example.notes_android.ui.adapter.NotesAdapter
import com.example.notes_android.ui.viewmodel.HomeViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    lateinit var adapter: NotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        requireActivity().title = "Notes"
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.options.collectLatest { options ->
                options?.let {
                    setupRecyclerView(it)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView(options: FirestoreRecyclerOptions<Note>) {
        adapter = NotesAdapter(options)
        binding.rvNotes.adapter = adapter
        binding.rvNotes.layoutManager = StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
        adapter.startListening()
        adapter.updateData()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}