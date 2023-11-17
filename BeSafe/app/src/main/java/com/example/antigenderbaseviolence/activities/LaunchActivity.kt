package com.example.antigenderbaseviolence.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.antigenderbaseviolence.R
import com.example.antigenderbaseviolence.databinding.ActivityLaunchBinding

//view binding
private lateinit var binding: ActivityLaunchBinding

class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Handle click login
        binding.loginBtn.setOnClickListener{
            //move to login page
            val intent= Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Handle click sign up
        binding.signupBtn.setOnClickListener{
            //move to login page
            val intent= Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Handle click continue without logging in
        binding.continueBtn.setOnClickListener{
            //move to Dashboard anonymous page
            val intent= Intent(this, DashboardAnonymous::class.java)
            startActivity(intent)
            finish()
        }
    }
}