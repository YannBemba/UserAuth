package com.example.userauth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.userauth.databinding.ActivityLogInBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class LogInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogInBinding

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        binding.btnInscription.setOnClickListener {
            registerUser()
        }

        binding.tvSeConnecter.setOnClickListener {
            val intent = Intent(this@LogInActivity, SignInActivity::class.java)
            startActivity(intent)
        }

    }

    private fun registerUser() {

        val email = binding.tilEmail.editText?.text.toString()
        val mdp = binding.tilMdp.editText?.text.toString()

        if(email.isNotEmpty() && mdp.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try{
                    mAuth.createUserWithEmailAndPassword(email, mdp).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.IO){
                        Toast.makeText(this@LogInActivity, e.message, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }

    }

    private fun checkLoggedInState() {
        val user = mAuth.currentUser
        if(user == null) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Erreur de connexion")
                .setMessage("Vous n'êtes pas inscrit ")
                .show()
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