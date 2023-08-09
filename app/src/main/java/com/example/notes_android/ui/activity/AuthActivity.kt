package com.example.notes_android.ui.activity

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.notes_android.R
import com.example.notes_android.databinding.ActivityAuthBinding
import com.example.notes_android.ui.viewmodel.LoginViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.internal.managers.ApplicationComponentManager
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Singleton

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth)
        isLoggedIn()
        bindObservers()
        setupUI()
    }

    private fun isLoggedIn() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            val intent = Intent(applicationContext, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun bindObservers() {
        lifecycleScope.launch {
            launch {
                viewModel.message.collectLatest {
                    if (it.isNotEmpty()) {
                        runOnUiThread {
                            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            launch {
                viewModel.isSuccess.collectLatest {
                    if (it) {
                        val intent = Intent(applicationContext, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun setupUI() {
        binding.newUser.setOnClickListener {
            val intent = Intent(this, CreateUserActivity::class.java)
            startActivity(intent)
        }

        binding.addUser.setOnClickListener {
            if (binding.edEmail.text?.trim()?.isEmpty() == true) {
                binding.edEmailLayout.isErrorEnabled = true
                binding.edEmailLayout.error = "Please Enter Email"
            }
            if (binding.edPassword.text?.trim()?.isEmpty() == true) {
                binding.edPasswordLayout.isErrorEnabled = true
                binding.edPasswordLayout.error = "Please Enter Password"
            }

            if (binding.edEmail.text?.trim()
                    ?.isNotEmpty() == true && binding.edPassword.text?.trim()
                    ?.isNotEmpty() == true
            ) {
                viewModel.createUser(
                    binding.edEmail.text.toString(),
                    binding.edPassword.text.toString()
                )
            }
        }


    }
}