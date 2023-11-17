package com.example.antigenderbaseviolence.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import com.example.antigenderbaseviolence.R
import com.example.antigenderbaseviolence.databinding.ActivityAddOrganisationBinding
import com.example.antigenderbaseviolence.databinding.ActivityAddYtVideoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Locale

//firebase auth
private lateinit var auth: FirebaseAuth

//progress dialog
private lateinit var progressDialog: ProgressDialog

//view binding
private lateinit var binding: ActivityAddYtVideoBinding

class AddYtVideoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddYtVideoBinding.inflate(layoutInflater)
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

    private var link = " "

    private fun validateData() {
        link = binding.linkEt.text.toString().trim()

        if (TextUtils.isEmpty(link)) {
            Toast.makeText(baseContext, "Enter The YouTube Video Link...", Toast.LENGTH_SHORT).show()
        }else{
            uploadYtVideoLink()
        }
    }

    private fun uploadYtVideoLink() {
        val timestamp = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        //init firebase auth
        auth = FirebaseAuth.getInstance()
        val uid = auth.uid

        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["Link"] = link

        val ref = FirebaseDatabase.getInstance().getReference("YoutubeVideos")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(baseContext, "YouTube Video Added Successfully...", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@AddYtVideoActivity, LearnAdminActivity::class.java))
                finish()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(baseContext, "Failed To Add YouTube Video Due To ${e.message}", Toast.LENGTH_SHORT).show()
            }
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