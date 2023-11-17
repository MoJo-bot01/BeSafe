package com.example.antigenderbaseviolence.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.antigenderbaseviolence.R
import com.example.antigenderbaseviolence.databinding.RowEmergencyAgenciesBinding
import com.example.antigenderbaseviolence.models.ModelGetHelp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterGetHelpEmergencyAgency : RecyclerView.Adapter<AdapterGetHelpEmergencyAgency.HolderGetHelpEmergencyAgency>{

    private val context: Context

    private var getHelpEmergencyArrayList: ArrayList<ModelGetHelp>

    //firebase auth
    private lateinit var auth: FirebaseAuth

    private lateinit var binding: RowEmergencyAgenciesBinding

    constructor(
        context: Context,
        getHelpEmergencyArrayList: ArrayList<ModelGetHelp>
    ) {
        this.context = context
        this.getHelpEmergencyArrayList = getHelpEmergencyArrayList
    }

    inner class HolderGetHelpEmergencyAgency(getHelpView: View): RecyclerView.ViewHolder(getHelpView){
        var name = binding.nameTextView1
        var description = binding.descriptionTextView1
        var contacts = binding.contactDetailsTextView1
        var email = binding.emailTextView1
        var profile = binding.profileIv
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterGetHelpEmergencyAgency.HolderGetHelpEmergencyAgency {
        binding = RowEmergencyAgenciesBinding.inflate(LayoutInflater.from(context),parent,false)

        return HolderGetHelpEmergencyAgency(binding.root)
    }

    override fun getItemCount(): Int {
        return getHelpEmergencyArrayList.size
    }

    override fun onBindViewHolder(holder: HolderGetHelpEmergencyAgency, position: Int) {
        val model = getHelpEmergencyArrayList[position]

        val id1 = model.id
        val uid1 = model.uid
        val organisationName1 = model.organisationName
        val organisationDescription1 = model.organisationDescription
        val organisationTelephoneNumber1 = model.organisationTelephoneNumber
        val organisationEmail1 = model.organisationEmail
        val organisationProfileImage1 = model.organisationProfileImage
        val timestamp1 = model.timestamp

        auth = FirebaseAuth.getInstance()

        loadEmergencyAgencies(model,holder)

    }

    private fun loadEmergencyAgencies(
        model: ModelGetHelp,
        holder: HolderGetHelpEmergencyAgency
    ) {
        val emergencyAgencyId = model.id

        val ref = FirebaseDatabase.getInstance().getReference("EmergencyAgencies")
        ref.child(emergencyAgencyId)
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //Get data
                    val id = "${snapshot.child("id").value}"
                    val uid = "${snapshot.child("uid").value}"
                    val organisationName = "${snapshot.child("organisationName").value}"
                    val organisationDescription = "${snapshot.child("organisationDescription").value}"
                    val organisationTelephoneNumber = "${snapshot.child("organisationTelephoneNumber").value}"
                    val organisationEmail = "${snapshot.child("organisationEmail").value}"
                    val organisationProfileImage = "${snapshot.child("organisationProfileImage").value}"

                    model.id = id
                    model.uid = uid
                    model.organisationName = organisationName
                    model.organisationDescription =organisationDescription
                    model.organisationTelephoneNumber = organisationTelephoneNumber
                    model.organisationEmail = organisationEmail
                    model.organisationProfileImage = organisationProfileImage

                    holder.contacts.text = organisationTelephoneNumber
                    holder.name.text = organisationName
                    holder.email.text = organisationEmail
                    holder.description.text = organisationDescription

                    try {
                        Glide.with(context)
                            .load(organisationProfileImage)
                            .placeholder(R.drawable.person_gray)
                            .into(holder.profile) //
                    } catch (e: Exception) {
                        // Handle Glide exception if needed
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}