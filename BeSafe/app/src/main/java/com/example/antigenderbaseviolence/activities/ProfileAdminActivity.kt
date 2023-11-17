package com.example.antigenderbaseviolence.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.antigenderbaseviolence.R
import com.example.antigenderbaseviolence.adapters.AdapterReceivedAlerts
import com.example.antigenderbaseviolence.databinding.ActivityProfileAdminBinding
import com.example.antigenderbaseviolence.models.ModelSendAlert
import com.example.antigenderbaseviolence.models.ModelUsers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

//View binding
private lateinit var binding: ActivityProfileAdminBinding

//Firebase auth
private lateinit var auth: FirebaseAuth

//firebase user
private lateinit var user: FirebaseUser

private lateinit var progressDialog: ProgressDialog

class ProfileAdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        loadAdminInfo()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.profileEditBtn.setOnClickListener {
            startActivity(Intent(this,AdminProfileEditActivity::class.java))
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

    private fun loadAdminInfo() {

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
                        Glide.with(this@ProfileAdminActivity)
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
}