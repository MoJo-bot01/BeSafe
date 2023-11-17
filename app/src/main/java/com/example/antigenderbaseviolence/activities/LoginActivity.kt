package com.example.antigenderbaseviolence.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.antigenderbaseviolence.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

//view binding
private lateinit var binding: ActivityLoginBinding

//firebase auth
private lateinit var auth: FirebaseAuth

//progress dialog
private lateinit var progressDialog: ProgressDialog

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        auth = FirebaseAuth.getInstance()

        //init progress dialog, will show while creating account
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.backBtn.setOnClickListener{
            super.onBackPressed()
            finish()
        }

        binding.noAccountTv.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.forgotTv.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.loginBtn.setOnClickListener{
            /*Steps
            * 1) Input data
            * 2) Validate data
            * 3) Login - Firebase auth
            * 4) Check user type - Firebase auth
            *    If user - move to user dashboard*/

            validateData()
        }
    }

    //local variables
    private var email1 = " "
    private var password1 = " "

    private fun validateData() {

        email1 = binding.emailEt.text.toString().trim()
        password1 = binding.passwordEt.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email1).matches()){
            Toast.makeText(baseContext, "Invalid Email Address...", Toast.LENGTH_SHORT).show()
        }
        else if (TextUtils.isEmpty(password1)){
            Toast.makeText(baseContext, "Enter Password...", Toast.LENGTH_SHORT).show()
        }
        else{
            loginUser()
        }
    }

    private fun loginUser() {

        progressDialog.setMessage("Logging In...")
        progressDialog.show()

        auth.signInWithEmailAndPassword(email1,password1)
            .addOnSuccessListener {
                progressDialog.dismiss()
                checkUser()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(baseContext, "Login Failed Due To ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {

        val firebaseUser = auth.currentUser
            val ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.child(firebaseUser!!.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        when (snapshot.child("userType").value) {
                            "user" -> {
                                startActivity(Intent(this@LoginActivity, DashboardUser::class.java))
                                finish()
                            }
                            "admin" -> {
                                startActivity(Intent(this@LoginActivity, DashboardAdmin::class.java))
                                finish()
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }

                })
        }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}