package com.example.antigenderbaseviolence.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import com.example.antigenderbaseviolence.R
import com.example.antigenderbaseviolence.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

//view binding
private lateinit var binding: ActivityForgotPasswordBinding

//firebase auth
private lateinit var auth: FirebaseAuth

//progress dialog
private lateinit var progressDialog: ProgressDialog

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init Firebase auth
        auth = FirebaseAuth.getInstance()

        //handle back button
        binding.backBtn.setOnClickListener{
            super.onBackPressed()
            finish()
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.submitBtn.setOnClickListener {
            validateData()
        }
    }

    //local variable
    private var email = ""

    private fun validateData() {
        email = binding.emailEt.text.toString().trim()

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(
                baseContext, "Enter email...",
                Toast.LENGTH_SHORT
            ).show()
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(
                baseContext, "Invalid email pattern...",
                Toast.LENGTH_SHORT
            ).show()
        }
        else{
            recoverPassword()
        }
    }

    private fun recoverPassword() {
        progressDialog.setMessage("Sending password reset instructions to $email")
        progressDialog.show()

        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                //sent
                progressDialog.dismiss()
                Toast.makeText(
                    baseContext, "Instructions sent to \n$email",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                //failed
                progressDialog.dismiss()
                Toast.makeText(baseContext, "Login Failed Due To ${e.message}", Toast.LENGTH_SHORT).show()
            }

        startActivity(Intent(this, LoginActivity::class.java))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}