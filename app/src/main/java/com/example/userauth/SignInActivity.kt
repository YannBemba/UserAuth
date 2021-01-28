package com.example.userauth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.userauth.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

const val REQUEST_CODE_SIGN_IN = 0

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

        binding.btnGoogleSignin.setOnClickListener {
            googleSignIn()
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
    
    private fun googleSignIn() {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.webclient_id))
                .requestEmail()
                .requestProfile()
                .build()

        val signInClient = GoogleSignIn.getClient(this, options)
        signInClient.signInIntent.also {
            startActivityForResult(it, REQUEST_CODE_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_SIGN_IN){
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
            account?.let {
                googleAuthForFirebase(it)
            }
        }
    }

    private fun googleAuthForFirebase(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        CoroutineScope(Dispatchers.IO).launch {
            try{
                mAuth.signInWithCredential(credentials).await()
                withContext(Dispatchers.Main){
                    Toast.makeText(this@SignInActivity, "Connexion réussie", Toast.LENGTH_LONG)
                            .show()
                }
            } catch (e: Exception){
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SignInActivity, e.message, Toast.LENGTH_LONG)
                            .show()
                }
            }
        }
    }

}