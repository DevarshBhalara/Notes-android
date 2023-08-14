package com.example.notes_android.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notes_android.R
import com.example.notes_android.data.Note
import com.example.notes_android.databinding.ActivityMainBinding
import com.example.notes_android.databinding.FragmentHomeBinding
import com.example.notes_android.extensions.startAnimation
import com.example.notes_android.ui.activity.AuthActivity
import com.example.notes_android.ui.activity.HomeActivity
import com.example.notes_android.ui.adapter.NotesAdapter
import com.example.notes_android.ui.viewmodel.HomeViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: NotesAdapter
    private lateinit var animation: Animation
    private lateinit var activityBinding: ActivityMainBinding

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

    @SuppressLint("ResourceType", "UseCompatLoadingForDrawables")
    private fun setupUI() {
        activityBinding = (requireActivity() as HomeActivity).binding
        activityBinding.root.background = requireContext().getDrawable(R.drawable.home_bg)
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_home_toolbar, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.logout -> {
                        val user = FirebaseAuth.getInstance()
                        user.signOut()
                        val intent = Intent(requireContext(), AuthActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        requireActivity().finish()
                    }
                }
                return false
            }

        })

        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.myColor, typedValue, true)
        val colorPrimary = typedValue.data

        activityBinding.appbarLayout.setBackgroundColor(colorPrimary)
        requireActivity().title = "Notes"
        animation = AnimationUtils.loadAnimation(requireActivity(), R.anim.circle_explotion_animation).apply {
            duration = 700
        }

        binding.btnAddNote.setOnClickListener {
            binding.btnAddNote.isVisible = false
            binding.view.isVisible = true
            binding.view.startAnimation(animation) {
                binding.view.isVisible = false
                val destination = HomeFragmentDirections.actionFragmentHomeToFragmentAddNote(null, null)
                findNavController().navigate(destination)
            }
        }

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
        adapter = NotesAdapter(options) { note, id ->
            val destination = HomeFragmentDirections.actionFragmentHomeToFragmentAddNote(note, id)
            findNavController().navigate(destination)
        }
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