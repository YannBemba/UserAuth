package com.example.userauth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.userauth.databinding.ActivityLogInBinding

class LogInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


}