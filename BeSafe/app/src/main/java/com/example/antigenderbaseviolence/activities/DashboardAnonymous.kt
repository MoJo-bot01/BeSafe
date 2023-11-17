package com.example.antigenderbaseviolence.activities

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.antigenderbaseviolence.databinding.ActivityDashboardAnonymousBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

//view binding
private lateinit var binding: ActivityDashboardAnonymousBinding

//firebase auth
private lateinit var auth: FirebaseAuth

//progress dialog
private lateinit var progressDialog: ProgressDialog

private lateinit var fusedLocationClient: FusedLocationProviderClient

class DashboardAnonymous : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardAnonymousBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        auth = FirebaseAuth.getInstance()

        updateAnonymousInfo()

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
    }

    var latitude = ""
    var longitude = ""
    private var selectedType: String? = null
    var phoneNumber = " "

    private fun showAbuseTypesDialog() {
        val abuseTypes = listOf("Physical abuse", "Verbal abuse", "Sexual abuse", "Emotional abuse")

        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Select Abuse Type")
        dialog.setItems(abuseTypes.toTypedArray()) { _, which ->
            selectedType = abuseTypes[which]
            showPhoneNumberDialog()
        }
        dialog.show()
    }

    private fun showPhoneNumberDialog() {
        val phoneDialog = AlertDialog.Builder(this)
        phoneDialog.setTitle("Enter Phone Number")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_PHONE
        phoneDialog.setView(input)

        phoneDialog.setPositiveButton("OK") { _, _ ->
            phoneNumber = input.text.toString().trim()
            if (isValidSouthAfricanPhoneNumber(phoneNumber)) {
                // Proceed with alert submission
                getLocation()
            } else {
                Toast.makeText(baseContext, "Enter a valid South African cellphone number...", Toast.LENGTH_SHORT).show()
                showPhoneNumberDialog()
            }
        }

        phoneDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        phoneDialog.show()
    }

    private fun isValidSouthAfricanPhoneNumber(phoneNumber: String): Boolean {
        val phonePattern = "^\\+?(27|0)[6-8][0-9]{8}$"
        return Pattern.matches(phonePattern, phoneNumber)
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
                saveAlertToFirebase(latitude,longitude) // Move the function call here
            }
        }
    }

var timestamp1 = 0
    private fun saveAlertToFirebase(latitude: String, longitude: String) {

        progressDialog.setMessage("Sending Alert...")
        progressDialog.show()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timestamp = System.currentTimeMillis()

        val latitude = latitude.toDouble()
        val longitude = longitude.toDouble()

        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = timestamp1.toString()
        hashMap["latitude"] = latitude
        hashMap["longitude"] = longitude
        hashMap["selectedAbuseType"] = selectedType
        hashMap["alertDate"] = dateFormat.format(Date(timestamp))
        hashMap["phoneNumber"] = phoneNumber
        hashMap["userType"] = "anonymous"
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

    private fun updateAnonymousInfo() {

        timestamp1 = System.currentTimeMillis().toInt()

        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = timestamp1
        hashMap["userType"] = "anonymous"
        hashMap["timestamp"] = timestamp1

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child("$timestamp1")
            .setValue(hashMap)
            .addOnSuccessListener {
//                progressDialog.dismiss()
                Toast.makeText(baseContext, "Anonymous Session Started...", Toast.LENGTH_SHORT).show()
                checkUser()
            }
            .addOnFailureListener { e->
//                progressDialog.dismiss()
                Toast.makeText(baseContext, "Failed Anonymous Session Start Due To ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {

        val firebaseUser = auth.currentUser

        if (firebaseUser != null){
            startActivity(Intent(this, DashboardAnonymous::class.java))
            finish()
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}