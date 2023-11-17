package com.example.antigenderbaseviolence.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.example.antigenderbaseviolence.R
import com.example.antigenderbaseviolence.adapters.AdapterGetHelp
import com.example.antigenderbaseviolence.adapters.AdapterGetHelpEmergencyAgency
import com.example.antigenderbaseviolence.databinding.ActivityGetHelpBinding
import com.example.antigenderbaseviolence.models.ModelGetHelp
import com.example.antigenderbaseviolence.models.ModelGetHelpp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GetHelpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivityGetHelpBinding

    private lateinit var getHelpArrayList: ArrayList<ModelGetHelpp>
    private lateinit var getHelpEmergencyArrayList: ArrayList<ModelGetHelp>

    private lateinit var adapterGetHelp: AdapterGetHelp
    private lateinit var adapterGetHelpEmergencyAgency: AdapterGetHelpEmergencyAgency

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        checkUser()
        loadOrganisations()
        loadEmergencyAgencies()

        // Handle click on the menu button
        binding.menuBtn.setOnClickListener {
            // Implement your menu functionality here, for example, show a popup menu
            val popupMenu = PopupMenu(this@GetHelpActivity, binding.menuBtn)
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
                                            startActivity(Intent(this@GetHelpActivity, DashboardUser::class.java))
                                            finish()
                                        }
                                        "admin" -> {
                                            startActivity(Intent(this@GetHelpActivity, DashboardAdmin::class.java))
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
                                            startActivity(Intent(this@GetHelpActivity, ForumActivity::class.java))
                                            finish()
                                        }
                                        "admin" -> {
                                            startActivity(Intent(this@GetHelpActivity, AdminForumActivity::class.java))
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
                                            startActivity(Intent(this@GetHelpActivity, GetHelpActivity::class.java))
                                            finish()
                                        }
                                        "admin" -> {
                                            startActivity(Intent(this@GetHelpActivity, GetHelpAdminActivity::class.java))
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
                                            startActivity(Intent(this@GetHelpActivity, LearnActivity::class.java))
                                            finish()
                                        }
                                        "admin" -> {
                                            startActivity(Intent(this@GetHelpActivity, LearnAdminActivity::class.java))
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
                                            startActivity(Intent(this@GetHelpActivity, DashboardUser::class.java))
                                            finish()
                                        }
                                        "admin" -> {
                                            startActivity(Intent(this@GetHelpActivity, HotspotsActivity::class.java))
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
    }

    private fun loadEmergencyAgencies() {
        getHelpEmergencyArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("EmergencyAgencies")
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                getHelpEmergencyArrayList.clear()
                for (ds in snapshot.children){
                    val emergencyAgencyId = "${ds.child("id").value}"

                    val modelGetHelp = ModelGetHelp()
                    modelGetHelp.id = emergencyAgencyId

                    getHelpEmergencyArrayList.add(modelGetHelp)

                    // Log the organization ID to check if it's being fetched
                    Log.d("FirebaseData", "emergencyAgency ID: $emergencyAgencyId")
                }

                adapterGetHelpEmergencyAgency = AdapterGetHelpEmergencyAgency(this@GetHelpActivity,getHelpEmergencyArrayList)

                // Set the adapter to the RecyclerView
                binding.agenciesRv.adapter = adapterGetHelpEmergencyAgency
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun loadOrganisations() {
        getHelpArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Organisations")
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                getHelpArrayList.clear()
                for (ds in snapshot.children){
                    val organisationId = "${ds.child("id").value}"

                    val modelGetHelp = ModelGetHelpp()
                    modelGetHelp.id = organisationId

                    getHelpArrayList.add(modelGetHelp)

                    // Log the organization ID to check if it's being fetched
                    Log.d("FirebaseData", "Organization ID: $organisationId")
                }

                adapterGetHelp = AdapterGetHelp(this@GetHelpActivity,getHelpArrayList)

                // Set the adapter to the RecyclerView
                binding.organisationsRv.adapter = adapterGetHelp
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