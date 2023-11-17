package com.example.antigenderbaseviolence.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.antigenderbaseviolence.R
import com.example.antigenderbaseviolence.adapters.AdapterForums
import com.example.antigenderbaseviolence.databinding.ActivityForumBinding
import com.example.antigenderbaseviolence.models.ModelForums
import com.example.antigenderbaseviolence.models.ModelUsers
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ForumActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivityForumBinding

    private lateinit var forumsArrayList: ArrayList<ModelForums>
    private lateinit var usersArrayList: ArrayList<ModelUsers>

    private lateinit var adapterForums: AdapterForums

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        checkUser()
        loadForums()

        //Handle click, logout
        binding.logoutBtn.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
        }

        binding.addForumBtn.setOnClickListener {
            val intent= Intent(this, AddForumActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Setup bottom navigation
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavView)
        bottomNavView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeBtn -> {
                    startActivity(Intent(this, DashboardUser::class.java))
                    true
                }
                R.id.forumBtn -> {
                    startActivity(Intent(this, ForumActivity::class.java))
                    true
                }
                R.id.helpBtn -> {
                    startActivity(Intent(this, GetHelpActivity::class.java))
                    true
                }
                R.id.learnBtn -> {
                    startActivity(Intent(this, LearnActivity::class.java))
                    true
                }
                R.id.hotspotsBtn -> {
                    startActivity(Intent(this, HotspotsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun loadForums() {
        forumsArrayList = ArrayList()
        usersArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Forums")
        ref.addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    forumsArrayList.clear()
                    for (ds in snapshot.children){
                        val forumId = "${ds.child("id").value}"

                        val modelForums = ModelForums()
                        modelForums.id = forumId

                        forumsArrayList.add(modelForums)
                    }

                    adapterForums = AdapterForums(this@ForumActivity,forumsArrayList,usersArrayList)

                    binding.favoriteRv.adapter = adapterForums
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun checkUser() {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
        } else {
            //get user type from firebase database.....Firebase DB > Categories
            val ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {
                        val user = "${snapshot.child("userType").value}"
                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }
}