package com.example.antigenderbaseviolence.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.example.antigenderbaseviolence.R
import com.example.antigenderbaseviolence.adapters.AdapterAdminForum
import com.example.antigenderbaseviolence.databinding.ActivityAdminForumBinding
import com.example.antigenderbaseviolence.models.ModelForums
import com.example.antigenderbaseviolence.models.ModelUsers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminForumActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivityAdminForumBinding

    private lateinit var forumsArrayList: ArrayList<ModelForums>
    private lateinit var usersArrayList: ArrayList<ModelUsers>

    private lateinit var adapterForums: AdapterAdminForum

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminForumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        checkUser()
        loadForums()

        binding.addForumBtn.setOnClickListener {
            val intent= Intent(this, AddForumAdminActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Handle click on the menu button
        binding.menuBtn.setOnClickListener {
            showMenu()
        }
    }

    private fun showMenu() {
        // Implement your menu functionality here, for example, show a popup menu
        val popupMenu = PopupMenu(this@AdminForumActivity, binding.menuBtn)
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
                                        startActivity(Intent(this@AdminForumActivity, DashboardUser::class.java))
                                        finish()
                                    }
                                    "admin" -> {
                                        startActivity(Intent(this@AdminForumActivity, DashboardAdmin::class.java))
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
                                        startActivity(Intent(this@AdminForumActivity, ForumActivity::class.java))
                                        finish()
                                    }
                                    "admin" -> {
                                        startActivity(Intent(this@AdminForumActivity, AdminForumActivity::class.java))
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
                                        startActivity(Intent(this@AdminForumActivity, GetHelpActivity::class.java))
                                        finish()
                                    }
                                    "admin" -> {
                                        startActivity(Intent(this@AdminForumActivity, GetHelpAdminActivity::class.java))
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
                                        startActivity(Intent(this@AdminForumActivity, LearnActivity::class.java))
                                        finish()
                                    }
                                    "admin" -> {
                                        startActivity(Intent(this@AdminForumActivity, LearnAdminActivity::class.java))
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
                                        startActivity(Intent(this@AdminForumActivity, DashboardUser::class.java))
                                        finish()
                                    }
                                    "admin" -> {
                                        startActivity(Intent(this@AdminForumActivity, HotspotsActivity::class.java))
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

                adapterForums = AdapterAdminForum(this@AdminForumActivity,forumsArrayList,usersArrayList)

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