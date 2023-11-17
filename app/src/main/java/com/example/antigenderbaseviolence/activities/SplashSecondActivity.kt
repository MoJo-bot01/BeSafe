package com.example.antigenderbaseviolence.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.antigenderbaseviolence.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private lateinit var auth: FirebaseAuth


class SplashSecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_second)

        auth = FirebaseAuth.getInstance()

        Handler().postDelayed(Runnable{
            checkUser()
        }, 3000)//means 3 seconds
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
                                startActivity(Intent(this@SplashSecondActivity, DashboardUser::class.java))
                                finish()
                            }
                            "admin" -> {
                                startActivity(Intent(this@SplashSecondActivity, DashboardAdmin::class.java))
                                finish()
                            }
                            "anonymous" -> {
                                startActivity(Intent(this@SplashSecondActivity, DashboardAnonymous::class.java))
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