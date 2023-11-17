package com.example.antigenderbaseviolence.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.antigenderbaseviolence.R
import com.example.antigenderbaseviolence.adapters.AdapterSendAlert
import com.example.antigenderbaseviolence.databinding.ActivityProfileBinding
import com.example.antigenderbaseviolence.models.ModelSendAlert
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

//View binding
private lateinit var binding: ActivityProfileBinding

//Firebase auth
private lateinit var auth: FirebaseAuth

//firebase user
private lateinit var user: FirebaseUser

private lateinit var alertsArrayList: ArrayList<ModelSendAlert>
private lateinit var adapterSendAlerts: AdapterSendAlert

private lateinit var progressDialog: ProgressDialog

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        loadUserInfo()
        loadSentAlerts()

        binding.backBtn.setOnClickListener{
            super.onBackPressed()
            finish()
        }

        binding.profileEditBtn.setOnClickListener {
            startActivity(Intent(this,ProfileEditActivity::class.java))
        }

        binding.accountStatusTv.setOnClickListener {
            if(user.isEmailVerified){
                Toast.makeText(this,"Already verified...", Toast.LENGTH_SHORT).show()
            }
            else{
                emailVerificationDialog()
            }
        }
    }

    private fun loadSentAlerts() {
        alertsArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Alerts")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                alertsArrayList.clear()
                for (ds in snapshot.children) {
                    val alertCategory = ds.child("alertCategory").getValue(String::class.java)
                    val alertUid = ds.child("uid").getValue(String::class.java)

                    if (alertCategory == "Pending" || alertCategory == "Armed Responders Dispatched"
                        || alertCategory == "Cancelled" || alertCategory == "Solved") {
                        if (alertUid == auth.uid) {
                            val model = ds.getValue(ModelSendAlert::class.java)
                            alertsArrayList.add(model!!)
                        }
                    }
                }
                binding.favoriteItemsCountTv.text = "${alertsArrayList.size}"

                adapterSendAlerts = AdapterSendAlert(this@ProfileActivity, alertsArrayList)
                binding.favoriteRv.adapter = adapterSendAlerts
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Data retrieval cancelled. Error: ${error.message}")
            }
        })
    }

    private fun emailVerificationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Verify Email")
            .setMessage("Are you sure you want to send email verification instructions to your email ${user.email}")
            .setPositiveButton("SEND"){d,e ->
                sendEmailVerification()
            }
            .setNegativeButton("CANCEL"){d,e ->
                d.dismiss()
            }
            .show()
    }

    private fun sendEmailVerification() {
        progressDialog.setMessage("Sending email verification instructions to email ${user.email}")
        progressDialog.show()

        user.sendEmailVerification()
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,"Instructions sent! Check your email ${user.email}",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e ->
                progressDialog.dismiss()
                Toast.makeText(this,"Failed to send instructions to your email ${user.email} due to ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

//    private fun loadSentAlerts() {
//        alertsArrayList = ArrayList()
//
//        val userId = auth.uid
//
//        val ref = FirebaseDatabase.getInstance().getReference("Alerts")
//        ref.orderByChild("uid").equalTo(userId)
//            .addValueEventListener(object: ValueEventListener{
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    alertsArrayList.clear()
//                    for (ds in snapshot.children){
//                        val alertId = "${ds.child("uid").value}"
//
//                        val modelAlerts = ModelSendAlert()
//                        modelAlerts.uid = alertId
//                        alertsArrayList.add(modelAlerts)
//                    }
//                    binding.favoriteItemsCountTv.text = "${alertsArrayList.size}"
//
//                    adapterSendAlerts = AdapterSendAlert(this@ProfileActivity,alertsArrayList)
//
//                    binding.favoriteRv.adapter = adapterSendAlerts
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
//                }
//            })
//    }

    private fun loadUserInfo() {

        if (user.isEmailVerified){
            binding.accountStatusTv.text = "Verified"
        }
        else{
            binding.accountStatusTv.text = "Not Verified"
        }
        //Load user info
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(auth.uid!!)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //Get user info
                    val email = "${snapshot.child("email").value}"
                    val name = "${snapshot.child("name").value}"
                    val profileImage = "${snapshot.child("profileImage").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    val uid = "${snapshot.child("uid").value}"
                    val userType = "${snapshot.child("userType").value}"

                    val formattedDate = timestamp.toLong()

                    binding.nameTv.text = name
                    binding.emailTv.text = email
                    binding.memberDateTv.text = formattedDate.toString()
                    binding.accountTypeTv.text = userType

                    try {
                        Glide.with(this@ProfileActivity)
                            .load(profileImage)
                            .placeholder(R.drawable.person_gray)
                            .into(binding.profileIv)
                    }
                    catch (e: Exception){

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}