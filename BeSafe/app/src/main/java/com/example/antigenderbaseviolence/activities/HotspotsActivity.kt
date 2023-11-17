package com.example.antigenderbaseviolence.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.antigenderbaseviolence.R
import com.example.antigenderbaseviolence.databinding.ActivityHotspotsBinding
import android.Manifest
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.GoogleMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import android.content.pm.PackageManager
import android.location.Geocoder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.location.Geofence
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import java.util.Locale

private lateinit var mMap: GoogleMap

//view binding
private lateinit var binding:ActivityHotspotsBinding

private lateinit var placesClient: PlacesClient

//firebase auth
private lateinit var auth: FirebaseAuth

private val locationPermissionCode = 1

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

        //Handle click, logout
        binding.logoutBtn.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Enable user location tracking and move camera to current location
        checkLocationPermission()
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
            val latLng = LatLng(location.latitudeSum / location.alertCount, location.longitudeSum / location.alertCount)
            val cityOrSuburb = location.cityOrSuburb
            val alertCount = location.alertCount
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("$cityOrSuburb: $alertCount")
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
}