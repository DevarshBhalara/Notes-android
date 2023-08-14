package com.example.notes_android.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.notes_android.R
import com.example.notes_android.databinding.ActivityAuthBinding
import com.example.notes_android.ui.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.Exception

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val viewModel: LoginViewModel by viewModels()

    private var firebaseAuth = FirebaseAuth.getInstance()
    private val REQ_ONE_TAP = 2

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

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

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
        binding.loginWithGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, REQ_ONE_TAP)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_ONE_TAP) {
            val result = data?.let { GoogleSignIn.getSignedInAccountFromIntent(data) }
            result?.let { handleSignInResult(it) }
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account)
        } catch (e: Exception) {
            e.localizedMessage?.let { Log.d("e", it) }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FirebaseAuth", "signInWithCredential:success")
                val intent = Intent(applicationContext, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            } else {
                Log.w("FirebaseAuth", "signInWithCredential:failure", task.exception)
            }
        }
    }
}