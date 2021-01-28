package com.example.userauth

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.userauth.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        binding.btnUpdateProfile.setOnClickListener {
            updateProfile()
        }

    }

    private fun updateProfile() {
        mAuth.currentUser?.let { user ->
            val username = binding.tilUsername.editText?.text.toString()
            val photoUri = Uri.parse("android.resource://$packageName/${R.drawable.marginalia_motorcycle}")
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .setPhotoUri(photoUri)
                .build()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    user.updateProfile(profileUpdates).await()
                    withContext(Dispatchers.Main) {
                        checkLoggedInState()
                        Toast.makeText(this@HomeActivity, "Modification du profil réussie", Toast.LENGTH_LONG)
                    }
                } catch(e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@HomeActivity, e.message, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }


    private fun checkLoggedInState() {
        val user = mAuth.currentUser

        if(user!!.equals(null)) {
            binding.tilUsername.editText?.setText(user.displayName)
            binding.ivProfilePicture.setImageURI(user.photoUrl)
        }
    }

}