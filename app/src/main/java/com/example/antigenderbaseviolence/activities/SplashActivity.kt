package com.example.antigenderbaseviolence.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.antigenderbaseviolence.R


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed(Runnable{
            showSecondSplash()
        }, 2000)//means 2 seconds
    }

    private fun showSecondSplash() {
        startActivity(Intent(this@SplashActivity, SplashSecondActivity::class.java))
        finish()
    }

}