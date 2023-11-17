package com.example.antigenderbaseviolence.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.antigenderbaseviolence.activities.ProfileAdminActivity
import com.example.antigenderbaseviolence.databinding.RowReceivedAlertsBinding
import com.example.antigenderbaseviolence.models.ModelSendAlert
import com.example.antigenderbaseviolence.models.ModelUsers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterReceivedAlerts : RecyclerView.Adapter<AdapterReceivedAlerts.HolderAlerts> {

    private val context: Context

    private var alertsArrayList: ArrayList<ModelSendAlert>

    private lateinit var binding: RowReceivedAlertsBinding

    constructor(
        context: Context,
        alertsArrayList: ArrayList<ModelSendAlert>
    ) {
        this.context = context
        this.alertsArrayList = alertsArrayList
    }


    inner class HolderAlerts(userView: View) : RecyclerView.ViewHolder(userView) {

        var username = binding.usernameTv
        var phoneNumber = binding.phoneNumberTV
        var titleTv = binding.titleTv
        var descriptionTv = binding.descriptionTv
        var dateTV = binding.dateTV
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderAlerts {
        binding = RowReceivedAlertsBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderAlerts(binding.root)
    }

    override fun getItemCount(): Int {
        return alertsArrayList.size
    }

    override fun onBindViewHolder(holder: HolderAlerts, position: Int) {
        val model = alertsArrayList[position]
        loadAlerts(model, holder)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProfileAdminActivity::class.java)
            intent.putExtra("uid", model.uid)
            context.startActivity(intent)
        }
    }

    private fun loadAlerts(model: ModelSendAlert, holder: HolderAlerts) {
        val alertId = model.uid

        val ref = FirebaseDatabase.getInstance().getReference("Alerts")
        ref.orderByChild("uid").equalTo(alertId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (ds in snapshot.children) {
                            val latitude = ds.child("latitude").value.toString()
                            val longitude = ds.child("longitude").value.toString()
                            val alertDate = ds.child("alertDate").value.toString()
                            val uid = ds.child("uid").value.toString()
                            val name = ds.child("name").value.toString()
                            val phoneNumber = ds.child("phoneNumber").value.toString()
                            val selectedAbuseType = ds.child("selectedAbuseType").value.toString()
                            val userType = ds.child("userType").value.toString()
                            val email = ds.child("email").value.toString()

                            model.latitude = latitude
                            model.longitude = longitude
                            model.alertDate = alertDate
                            model.uid = uid
                            model.name = name
                            model.email = email
                            model.phoneNumber = phoneNumber
                            model.selectedAbuseType = selectedAbuseType
                            model.userType = userType

                            holder.titleTv.text = "Coordinates: $latitude, $longitude"
                            holder.descriptionTv.text = "$selectedAbuseType"
                            holder.dateTV.text = alertDate
                            holder.phoneNumber.text = phoneNumber
                            holder.username.text = "$name, $userType"

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle onCancelled event
                }
            })
    }


}