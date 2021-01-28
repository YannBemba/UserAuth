package com.example.userauth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.userauth.databinding.ActivitySignInBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        binding.btnConnexion.setOnClickListener {
            loginUser()
        }

    }

    private fun loginUser() {

        val email = binding.tilEmailCo.editText?.text.toString()
        val mdp = binding.tilMdpCo.editText?.text.toString()

        if(email.isNotEmpty() && mdp.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try{
                    mAuth.signInWithEmailAndPassword(email, mdp).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.IO){
                        Toast.makeText(this@SignInActivity, e.message, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        checkLoggedInState()
    }

    private fun checkLoggedInState() {
        if(mAuth.currentUser == null) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Erreur de connexion")
                .setMessage("Vous n'êtes pas inscrit ")
                .show()
            val intent = Intent(this@SignInActivity, HomeActivity::class.java)
            startActivity(intent)

        } else {
            MaterialAlertDialogBuilder(this)
                .setTitle("Connexion réussie")
                .setMessage("Vous êtes bien connecté")
                .setPositiveButton("OK"){ dialog, which ->

                }
                .show()
        }
    }

}