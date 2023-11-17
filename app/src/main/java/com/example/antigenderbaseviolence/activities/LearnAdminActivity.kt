package com.example.antigenderbaseviolence.activities

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.antigenderbaseviolence.R
import com.example.antigenderbaseviolence.VideoLink
import com.example.antigenderbaseviolence.adapters.VideoLinkAdapter
import com.example.antigenderbaseviolence.databinding.ActivityGetHelpAdminBinding
import com.example.antigenderbaseviolence.databinding.ActivityLearnAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private lateinit var auth: FirebaseAuth

private lateinit var binding: ActivityLearnAdminBinding

private lateinit var recyclerView: RecyclerView
private lateinit var adapter: VideoLinkAdapter
private val videoLinks: MutableList<VideoLink> = mutableListOf()

class LearnAdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearnAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        checkUser()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = VideoLinkAdapter(videoLinks)
        recyclerView.adapter = adapter

        fetchVideoLinksFromDatabase()

        // Handle click on the menu button
        binding.menuBtn.setOnClickListener {
            menuPopupMenu()
        }

        binding.addYtVideoBtn.setOnClickListener {
            startActivity(Intent(this@LearnAdminActivity, AddYtVideoActivity::class.java))
            finish()
        }
    }

    private fun menuPopupMenu() {
        val popupMenu = PopupMenu(this@LearnAdminActivity, binding.menuBtn)
        popupMenu.menuInflater.inflate(R.menu.whole_menu, popupMenu.menu)

        // Set menu item click listener
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeBtn -> {
                    val firebaseUser = auth.currentUser
                    val ref = FirebaseDatabase.getInstance().getReference("Users")
                    ref.child(firebaseUser!!.uid)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {

                                when (snapshot.child("userType").value) {
                                    "user" -> {
                                        startActivity(Intent(this@LearnAdminActivity, DashboardUser::class.java))
                                        finish()
                                    }
                                    "admin" -> {
                                        startActivity(Intent(this@LearnAdminActivity, DashboardAdmin::class.java))
                                        finish()
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                            }

                        })
                    true
                }
                R.id.forumBtn -> {
                    val firebaseUser = auth.currentUser
                    val ref = FirebaseDatabase.getInstance().getReference("Users")
                    ref.child(firebaseUser!!.uid)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {

                                when (snapshot.child("userType").value) {
                                    "user" -> {
                                        startActivity(Intent(this@LearnAdminActivity, ForumActivity::class.java))
                                        finish()
                                    }
                                    "admin" -> {
                                        startActivity(Intent(this@LearnAdminActivity, AdminForumActivity::class.java))
                                        finish()
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                            }

                        })
                    true
                }
                R.id.helpBtn -> {
                    val firebaseUser = auth.currentUser
                    val ref = FirebaseDatabase.getInstance().getReference("Users")
                    ref.child(firebaseUser!!.uid)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {

                                when (snapshot.child("userType").value) {
                                    "user" -> {
                                        startActivity(Intent(this@LearnAdminActivity, GetHelpActivity::class.java))
                                        finish()
                                    }
                                    "admin" -> {
                                        startActivity(Intent(this@LearnAdminActivity, GetHelpAdminActivity::class.java))
                                        finish()
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                            }

                        })
                    true
                }
                R.id.learnBtn -> {
                    val firebaseUser = auth.currentUser
                    val ref = FirebaseDatabase.getInstance().getReference("Users")
                    ref.child(firebaseUser!!.uid)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {

                                when (snapshot.child("userType").value) {
                                    "user" -> {
                                        startActivity(Intent(this@LearnAdminActivity, LearnActivity::class.java))
                                        finish()
                                    }
                                    "admin" -> {
                                        startActivity(Intent(this@LearnAdminActivity, LearnAdminActivity::class.java))
                                        finish()
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                            }

                        })
                    true
                }
                R.id.hotspotsBtn -> {
                    val firebaseUser = auth.currentUser
                    val ref = FirebaseDatabase.getInstance().getReference("Users")
                    ref.child(firebaseUser!!.uid)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {

                                when (snapshot.child("userType").value) {
                                    "user" -> {
                                        startActivity(Intent(this@LearnAdminActivity, DashboardUser::class.java))
                                        finish()
                                    }
                                    "admin" -> {
                                        startActivity(Intent(this@LearnAdminActivity, HotspotsActivity::class.java))
                                        finish()
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                            }

                        })
                    true
                }
                R.id.logoutBtn -> {
                    showLogoutConfirmationDialog()
                    true
                }
                else -> false
            }
        }

        // Show the popup menu
        popupMenu.show()
    }

    private fun fetchVideoLinksFromDatabase() {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("YoutubeVideos")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                videoLinks.clear()
                for (snapshot in dataSnapshot.children) {
                    val url = snapshot.child("Link").getValue(String::class.java) ?: ""
                    val videoLink = VideoLink(url)
                    videoLinks.add(videoLink)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database errors if needed
            }
        })
    }

    private fun checkUser() {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
        } else {

        }
    }

    private fun showLogoutConfirmationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_confirm_logout, null)
        val confirmButton = dialogView.findViewById<Button>(R.id.confirmButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)

        val alertDialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = alertDialogBuilder.create()

        confirmButton.setOnClickListener {
            // Handle logout action here
            auth.signOut()
            alertDialog.dismiss()
            startActivity(Intent(this, SplashActivity::class.java))
            finish()

        }
        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}