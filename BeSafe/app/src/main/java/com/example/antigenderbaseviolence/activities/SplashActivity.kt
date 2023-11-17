package com.example.antigenderbaseviolence.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.antigenderbaseviolence.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private lateinit var auth: FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()

        Handler().postDelayed(Runnable{
            checkUser()
        }, 2000)//means 2 seconds
    }

    private fun checkUser() {

        val firebaseUser = auth.currentUser
        if (firebaseUser == null){
            startActivity(Intent(this, LaunchActivity::class.java))
            finish()
        }
        else{

            val ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.child(firebaseUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        when (snapshot.child("userType").value) {
                            "user" -> {
                                startActivity(Intent(this@SplashActivity, DashboardUser::class.java))
                                finish()
                            }
                            "admin" -> {
                                startActivity(Intent(this@SplashActivity, DashboardAdmin::class.java))
                                finish()
                            }
                            "anonymous" -> {
                                startActivity(Intent(this@SplashActivity, DashboardAnonymous::class.java))
                                finish()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }
    }
}