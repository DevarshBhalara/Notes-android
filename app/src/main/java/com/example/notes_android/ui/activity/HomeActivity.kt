package com.example.notes_android.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.notes_android.R
import com.example.notes_android.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        (supportFragmentManager.findFragmentById(binding.hostFragmentContainer.id) as? NavHostFragment)?.let {
            navController = it.navController
        }
        binding.toolbar.setupWithNavController(navController)
    }
}