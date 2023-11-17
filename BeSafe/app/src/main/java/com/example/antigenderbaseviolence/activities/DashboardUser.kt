package com.example.antigenderbaseviolence.activities

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.antigenderbaseviolence.R
import com.example.antigenderbaseviolence.databinding.ActivityDashboardUserBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//view binding
private lateinit var binding: ActivityDashboardUserBinding

//firebase auth
private lateinit var auth: FirebaseAuth

private lateinit var user: FirebaseUser

private var userId = ""

//progress dialog
private lateinit var progressDialog: ProgressDialog

private lateinit var fusedLocationClient: FusedLocationProviderClient

class DashboardUser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        auth = FirebaseAuth.getInstance()

//        userId = intent.getStringExtra("id")!!

        user = auth.currentUser!!

        checkUser()

        //init progress dialog, will show while creating account
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //Handle click, logout
        binding.logoutBtn.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
        }

        //handle send alert button
        binding.alertBtn.setOnClickListener {
            showAbuseTypesDialog()
        }

        //Handle click, open profile
        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this,ProfileActivity::class.java))
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

    var latitude = ""
    var longitude = ""
    private var selectedType: String? = null

    private fun showAbuseTypesDialog() {
        val abuseTypes = listOf("Physical abuse", "Verbal abuse", "Sexual abuse", "Emotional abuse")

        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Select Abuse Type")
        dialog.setItems(abuseTypes.toTypedArray()) { _, which ->
            selectedType = abuseTypes[which]
            getLocation()
        }
        dialog.show()
    }

    private fun getLocation() {
        Log.d("Location", "getLocation() called") // Add this line
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
            return
        }

        val task = fusedLocationClient.lastLocation

        task.addOnSuccessListener { location ->
            if (location != null) {
                latitude = location.latitude.toString()
                longitude = location.longitude.toString()
                fetchUserInfo(latitude,longitude) // Move the function call here
            }
        }
    }

    private fun fetchUserInfo(latitude: String, longitude: String) {
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
                    val phoneNumber = "${snapshot.child("phoneNumber").value}"
                    val uid = "${snapshot.child("uid").value}"
                    val userType = "${snapshot.child("userType").value}"

                    saveAlertToFirebase(latitude,longitude,name,phoneNumber)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }


    private fun saveAlertToFirebase(latitude: String, longitude: String, name: String, phoneNumber: String) {

        progressDialog.setMessage("Sending Alert...")
        progressDialog.show()
        val uid = auth.uid
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timestamp = System.currentTimeMillis()
        var email = auth.currentUser?.email

        val latitude = latitude.toDouble()
        val longitude = longitude.toDouble()

        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["latitude"] = latitude
        hashMap["longitude"] = longitude
        hashMap["selectedAbuseType"] = selectedType
        hashMap["alertDate"] = dateFormat.format(Date(timestamp))
        hashMap["email"] = email
        hashMap["name"] = name
        hashMap["phoneNumber"] = phoneNumber
        hashMap["userType"] = "user"
        hashMap["timestamp"] = timestamp

        val ref = FirebaseDatabase.getInstance().getReference("Alerts")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(baseContext, "Alert Sent, Do Expect A Call From Us To Confirm The Alert...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(baseContext, "Failed To Send Alert Due To ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 101
    }

    private fun checkUser() {

        val firebaseUser = auth.currentUser
        if (firebaseUser == null){
            startActivity(Intent(this, LaunchActivity::class.java))
            finish()
        }
        else{
            val email = firebaseUser.email
            binding.subTitleTv.text = email
        }
    }
}