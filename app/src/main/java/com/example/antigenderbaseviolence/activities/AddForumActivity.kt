package com.example.antigenderbaseviolence.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.antigenderbaseviolence.databinding.ActivityAddForumBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//view binding
private lateinit var binding: ActivityAddForumBinding

//firebase auth
private lateinit var auth: FirebaseAuth

//progress dialog
private lateinit var progressDialog: ProgressDialog

class AddForumActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddForumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        auth = FirebaseAuth.getInstance()

        checkUser()

        //init progress dialog, will show while creating account
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.backBtn.setOnClickListener{
            super.onBackPressed()
            finish()
        }

        binding.submitBtn.setOnClickListener {
            validateData()
        }
    }

    private var message = " "

    private fun validateData() {
        message = binding.messageEt.text.toString().trim()

        val phonePattern = "^(\\+27|0)[6-8][0-9]{8}$"

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(baseContext, "Enter Your Forum Message...", Toast.LENGTH_SHORT).show()
        } else {
            updateForumInfo()
        }
    }

    private fun updateForumInfo() {
        progressDialog.setMessage("Posting Forum...")
        progressDialog.show()
        val timestamp = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        //init firebase auth
        auth = FirebaseAuth.getInstance()
        val uid = auth.uid

        val ref1 = FirebaseDatabase.getInstance().getReference("Users")
        ref1.child(uid!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //Get data
                val email = "${snapshot.child("email").value}"
                val name = "${snapshot.child("name").value}"
                val phoneNumber = "${snapshot.child("phoneNumber").value}"
                val profileImage = "${snapshot.child("profileImage").value}"
                val userType = "${snapshot.child("userType").value}"

                val hashMap: HashMap<String, Any?> = HashMap()
                hashMap["id"] = "$timestamp"
                hashMap["uid"] = uid
                hashMap["message"] = message
                hashMap["username"] = name
                hashMap["profileImage"] = profileImage
                hashMap["forumDate"] = dateFormat.format(Date(timestamp))
                hashMap["timestamp"] = timestamp
                hashMap["userType"] = "user"

                val ref = FirebaseDatabase.getInstance().getReference("Forums")
                ref.child("$timestamp")
                    .setValue(hashMap)
                    .addOnSuccessListener {
                        progressDialog.dismiss()
                        Toast.makeText(baseContext, "Forum Posted...", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@AddForumActivity, ForumActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener { e->
                        progressDialog.dismiss()
                        Toast.makeText(baseContext, "Failed To Post Forum Due To ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event if needed
            }
        })
    }


    private fun checkUser() {

        val firebaseUser = auth.currentUser
        if (firebaseUser == null){
            startActivity(Intent(this, LaunchActivity::class.java))
            finish()
        }
        else{

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}