package com.example.notes_android.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.notes_android.R
import com.example.notes_android.databinding.ActivityCreateUserBinding
import com.example.notes_android.ui.viewmodel.CreateUserViewModel
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.Exception

@AndroidEntryPoint
class CreateUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateUserBinding
    private val viewModel: CreateUserViewModel by viewModels()
    private var isSuccess = false
    private var firebaseAuth = FirebaseAuth.getInstance()

    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
    private var showOneTapUI = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupObservers()
        setupUI()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            launch {
                viewModel.isSuccess.collectLatest {
                    if (it) {
                        goToLogin()
                    }
                }
            }

            launch {
                viewModel.isSuccessString.collectLatest {
                    if (it.isNotEmpty()) {
                        Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    private fun setupUI() {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnCreateUser.setOnClickListener {
//            viewModel.validation(binding.edEmail.text.toString(), binding.edPassword.text.toString(), binding.edConfPassword.text.toString())
            if (binding.edEmail.text?.trim()?.isEmpty() == true) {
                binding.edEmailLayout.isErrorEnabled = true
                binding.edEmailLayout.error = "Please Enter Email"
            }
            if (binding.edPassword.text?.trim()?.isEmpty() == true) {
                binding.edPasswordLayout.isErrorEnabled = true
                binding.edPasswordLayout.error = "Please Enter Password"
            }
            if (binding.edConfPassword.text?.trim()?.isEmpty() == true) {
                binding.edConfPasswordLayout.isErrorEnabled = true
                binding.edConfPasswordLayout.error = "Please Re-Enter Password"
            }

            if (binding.edEmail.text?.trim()
                    ?.isNotEmpty() == true && binding.edPassword.text?.trim()
                    ?.isNotEmpty() == true && binding.edConfPassword.text?.trim()
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
            e.localizedMessage?.let { Log.d("e", it.toString()) }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FirebaseAuth", "signInWithCredential:success")
                val user = firebaseAuth.currentUser
                // Handle successful sign-in
            } else {
                Log.w("FirebaseAuth", "signInWithCredential:failure", task.exception)
            }
        }
    }
}
