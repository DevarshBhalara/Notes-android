package com.example.notes_android.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.notes_android.R
import com.example.notes_android.data.Note
import com.example.notes_android.databinding.FragmentAddNoteBinding
import com.example.notes_android.ui.activity.HomeActivity
import com.example.notes_android.ui.viewmodel.AddNoteViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddNoteFragment : Fragment() {

    private val navArgs: AddNoteFragmentArgs by navArgs()
    lateinit var binding: FragmentAddNoteBinding
    private val viewModel: AddNoteViewModel by viewModels()
    private var isNewNote = false
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddNoteBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObservers()
        setupUI()
        Log.d("title", navArgs.note?.title ?: "NA")
    }

    private fun bindObservers() {
        lifecycleScope.launch {
            launch {
                viewModel.isAdded.collectLatest {
                    if (it) {
                        Toast.makeText(requireContext(), "Added", Toast.LENGTH_SHORT).show()
                        Log.e("added", "Success")

                    }
                }
            }
            launch {
                viewModel.isDeleted.observe(viewLifecycleOwner) {
                    if(it) {
                        navController.popBackStack()
                    }
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setupUI() {
        navController = Navigation.findNavController(binding.root)
        (requireActivity() as HomeActivity).binding.root.background = requireContext().getDrawable(R.drawable.add_note_bg)
        navArgs.id.let {
            if (it?.isEmpty() != false) {
                isNewNote = true
            } else {
                isNewNote = false
                binding.note = navArgs.note
            }
        }
        (requireActivity() as HomeActivity).binding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_ios_24)
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_toolbar_add_note, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.add -> {
                        if (isNewNote) {
                            viewModel.addNote(Note(
                                title = binding.title.text.toString(),
                                content = binding.subTitle.text.toString(),
                                timeStamp = Timestamp.now()
                            ))
                            return true
                        } else {
                            navArgs.id?.let {
                                viewModel.updateNote( Note(
                                    title = binding.title.text.toString(),
                                    content = binding.subTitle.text.toString(),
                                    timeStamp = Timestamp.now()), it
                                )
                            }
                        }
                        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(null, 0)
                    }
                    R.id.delete -> {
                        if (!isNewNote) {
                            navArgs.id?.let {
                                viewModel.deleteNote(it)
                            }
                        }
                    }
                }
                return false
            }
        })
    }
}