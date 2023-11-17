package com.example.antigenderbaseviolence.activities

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.antigenderbaseviolence.R
import com.example.antigenderbaseviolence.databinding.ActivityDashboardUserBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
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

private lateinit var mMap: GoogleMap

//firebase auth
private lateinit var auth: FirebaseAuth

private lateinit var user: FirebaseUser

private lateinit var placesClient: PlacesClient

private val locationPermissionCode = 1

//progress dialog
private lateinit var progressDialog: ProgressDialog

private lateinit var fusedLocationClient: FusedLocationProviderClient

class DashboardUser : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val TAG = "DashboardUserActivity"

        private const val LOCATION_PERMISSION_REQUEST_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        auth = FirebaseAuth.getInstance()

        user = auth.currentUser!!

        checkUser()

        //init progress dialog, will show while creating account
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initialize the Places API
        Places.initialize(applicationContext, "AIzaSyA9543Ev1wo8ZS7hUehBD7rmz458xQbywg")
        placesClient = Places.createClient(this)

        //handle send alert button
        binding.alertBtn.setOnClickListener {
            showAbuseTypesDialog()
        }

        //Handle click, open profile
        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this,ProfileActivity::class.java))
        }

        // Handle click on the menu button
        binding.menuBtn.setOnClickListener {
            // Implement your menu functionality here, for example, show a popup menu
            val popupMenu = PopupMenu(this@DashboardUser, binding.menuBtn)
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
                                            startActivity(Intent(this@DashboardUser, DashboardUser::class.java))
                                            finish()
                                        }
                                        "admin" -> {
                                            startActivity(Intent(this@DashboardUser, DashboardAdmin::class.java))
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
                                            startActivity(Intent(this@DashboardUser, ForumActivity::class.java))
                                            finish()
                                        }
                                        "admin" -> {
                                            startActivity(Intent(this@DashboardUser, AdminForumActivity::class.java))
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
                                            startActivity(Intent(this@DashboardUser, GetHelpActivity::class.java))
                                            finish()
                                        }
                                        "admin" -> {
                                            startActivity(Intent(this@DashboardUser, GetHelpAdminActivity::class.java))
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
                                            startActivity(Intent(this@DashboardUser, LearnActivity::class.java))
                                            finish()
                                        }
                                        "admin" -> {
                                            startActivity(Intent(this@DashboardUser, LearnAdminActivity::class.java))
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
                                            startActivity(Intent(this@DashboardUser, DashboardUser::class.java))
                                            finish()
                                        }
                                        "admin" -> {
                                            startActivity(Intent(this@DashboardUser, HotspotsActivity::class.java))
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
        hashMap["id"] = "$timestamp"
        hashMap["uid"] = uid
        hashMap["latitude"] = latitude
        hashMap["longitude"] = longitude
        hashMap["selectedAbuseType"] = selectedType
        hashMap["alertDate"] = dateFormat.format(Date(timestamp))
        hashMap["email"] = email
        hashMap["name"] = name
        hashMap["alertCategory"] = "Pending"
        hashMap["phoneNumber"] = phoneNumber
        hashMap["userType"] = "user"
        hashMap["timestamp"] = timestamp

        val ref = FirebaseDatabase.getInstance().getReference("Alerts")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(baseContext, "Alert Sent, Do Expect A Call From Us To Confirm The Alert...", Toast.LENGTH_SHORT).show()

                showNotification()
                retrieveAlertsAndAddMarkers() // Refresh markers on the map
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(baseContext, "Failed To Send Alert Due To ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }


    private fun showNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "alert_channel"
        val channelName = "Alert Channel"
        val description = "Notification channel for sent alerts"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
                this.description = description
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = 1
        val notificationBuilder = NotificationCompat.Builder(this@DashboardUser, channelId)
            .setSmallIcon(R.drawable.crisis_alert_black)
            .setContentTitle("Alert Sent!!!")
        notificationBuilder.setContentText("Do Expect A Call From Us To Confirm The Alert...")

        notificationManager.notify(notificationId, notificationBuilder.build())
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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Enable user location tracking and move camera to current location
        checkLocationPermission()

        // Retrieve alerts and add markers
        retrieveAlertsAndAddMarkers()

        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            enableMyLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        }
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mMap.isMyLocationEnabled = true
        retrieveAlertsAndAddMarkers()
        moveCameraToCurrentUserLocation()
    }

    private fun retrieveAlertsAndAddMarkers() {
        val ref = FirebaseDatabase.getInstance().getReference("Alerts")
        val alertLocations = mutableListOf<AlertLocation>()

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val latitude = childSnapshot.child("latitude").getValue(Double::class.java)
                    val longitude = childSnapshot.child("longitude").getValue(Double::class.java)

                    if (latitude != null && longitude != null) {
                        // Perform reverse geocoding to get city or suburb based on latitude and longitude
                        val cityOrSuburb = reverseGeocode(latitude, longitude)

                        // Find existing alert location with the same city or suburb
                        val existingLocation = alertLocations.find { it.cityOrSuburb == cityOrSuburb }
                        if (existingLocation != null) {
                            existingLocation.alertCount++
                            existingLocation.latitudeSum += latitude
                            existingLocation.longitudeSum += longitude
                        } else {
                            alertLocations.add(
                                AlertLocation(
                                    latitude,
                                    longitude,
                                    cityOrSuburb,
                                    1,
                                    latitude,
                                    longitude
                                )
                            )
                        }
                    }
                }

                addMarkersForAlertLocations(alertLocations)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled if needed
            }
        })
    }

    private fun reverseGeocode(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        return if (addresses!!.isNotEmpty()) {
            // Get city or suburb from the first address
            addresses[0].locality ?: addresses[0].subLocality ?: ""
        } else {
            // Return empty string if no address is found
            ""
        }
    }

    private fun addMarkersForAlertLocations(alertLocations: List<AlertLocation>) {
        mMap.clear() // Clear existing markers
        for (location in alertLocations) {
            val latLng = LatLng(location.latitudeSum / location.alertCount, location.longitudeSum / location.alertCount)
            val cityOrSuburb = location.cityOrSuburb
            val alertCount = location.alertCount
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("$cityOrSuburb: $alertCount Alerts")
            )
        }
    }

    data class AlertLocation(
        val latitude: Double,
        val longitude: Double,
        var cityOrSuburb: String = "",
        var alertCount: Int = 0,
        var latitudeSum: Double = 0.0,
        var longitudeSum: Double = 0.0
    )

    private fun moveCameraToCurrentUserLocation() {
        if (mMap.isMyLocationEnabled && mMap.myLocation != null) {
            val userLocation = LatLng(
                mMap.myLocation.latitude,
                mMap.myLocation.longitude
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
            } else {
                // Handle the case when permission is denied
            }
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