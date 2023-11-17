package com.example.antigenderbaseviolence.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.antigenderbaseviolence.R
import com.example.antigenderbaseviolence.databinding.ActivityHotspotsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

private lateinit var mMap: GoogleMap

//view binding
private lateinit var binding:ActivityHotspotsBinding

private lateinit var placesClient: PlacesClient

//firebase auth
private lateinit var auth: FirebaseAuth

private const val locationPermissionCode = 1

class HotspotsActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val TAG = "HotspotsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHotspotsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        auth = FirebaseAuth.getInstance()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initialize the Places API
        Places.initialize(applicationContext, "AIzaSyA9543Ev1wo8ZS7hUehBD7rmz458xQbywg")
        placesClient = Places.createClient(this)

// Handle click on the menu button
        binding.menuBtn.setOnClickListener {
            // Implement your menu functionality here, for example, show a popup menu
            val popupMenu = PopupMenu(this@HotspotsActivity, binding.menuBtn)
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
                                            startActivity(Intent(this@HotspotsActivity, DashboardUser::class.java))
                                            finish()
                                        }
                                        "admin" -> {
                                            startActivity(Intent(this@HotspotsActivity, DashboardAdmin::class.java))
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
                                            startActivity(Intent(this@HotspotsActivity, ForumActivity::class.java))
                                            finish()
                                        }
                                        "admin" -> {
                                            startActivity(Intent(this@HotspotsActivity, AdminForumActivity::class.java))
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
                                            startActivity(Intent(this@HotspotsActivity, GetHelpActivity::class.java))
                                            finish()
                                        }
                                        "admin" -> {
                                            startActivity(Intent(this@HotspotsActivity, GetHelpAdminActivity::class.java))
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
                                            startActivity(Intent(this@HotspotsActivity, LearnActivity::class.java))
                                            finish()
                                        }
                                        "admin" -> {
                                            startActivity(Intent(this@HotspotsActivity, LearnAdminActivity::class.java))
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
                                            startActivity(Intent(this@HotspotsActivity, DashboardUser::class.java))
                                            finish()
                                        }
                                        "admin" -> {
                                            startActivity(Intent(this@HotspotsActivity, HotspotsActivity::class.java))
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

        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        // Initialize the map fragment in your layout
        val mapFragment1 = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        // Load the map asynchronously
        mapFragment1.getMapAsync { googleMap ->
            val location = LatLng(latitude, longitude)

            // Add a marker at the specified location
            googleMap.addMarker(MarkerOptions().position(location).title("Target Location"))

            // Move the camera to the specified location
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 150f))
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Enable user location tracking and move camera to current location
        checkLocationPermission()

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
        for (location in alertLocations) {
//            mMap.clear()
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