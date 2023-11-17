package com.example.antigenderbaseviolence.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.antigenderbaseviolence.R
import com.example.antigenderbaseviolence.databinding.RowSupportiveOrganisationsBinding
import com.example.antigenderbaseviolence.models.ModelGetHelp
import com.example.antigenderbaseviolence.models.ModelGetHelpp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterGetHelp : RecyclerView.Adapter<AdapterGetHelp.HolderGetHelpp> {

    private val context: Context

    private var getHelpArrayList: ArrayList<ModelGetHelpp>

    //firebase auth
    private lateinit var auth: FirebaseAuth

    private lateinit var binding: RowSupportiveOrganisationsBinding

    constructor(
        context: Context,
        getHelpArrayList: ArrayList<ModelGetHelpp>
    ) {
        this.context = context
        this.getHelpArrayList = getHelpArrayList
    }

    inner class HolderGetHelpp(getHelpView: View):RecyclerView.ViewHolder(getHelpView){
        var name = binding.nameTextView1
        var description = binding.descriptionTextView1
        var contacts = binding.contactDetailsTextView1
        var email = binding.emailTextView1
        var profile = binding.profileIv
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterGetHelp.HolderGetHelpp {
        binding = RowSupportiveOrganisationsBinding.inflate(LayoutInflater.from(context),parent,false)

        return HolderGetHelpp(binding.root)
    }

    override fun onBindViewHolder(holder: AdapterGetHelp.HolderGetHelpp, position: Int) {
        val model = getHelpArrayList[position]

        val id = model.id
        val uid = model.uid
        val organisationName = model.organisationName
        val organisationDescription = model.organisationDescription
        val organisationTelephoneNumber = model.organisationTelephoneNumber
        val organisationEmail = model.organisationEmail
        val organisationProfileImage = model.organisationProfileImage
        val timestamp = model.timestamp

        auth = FirebaseAuth.getInstance()

        loadOrganisation(model,holder)
    }

    private fun loadOrganisation(model: ModelGetHelpp, holder: HolderGetHelpp) {
        val organisationId = model.id

        val ref = FirebaseDatabase.getInstance().getReference("Organisations")
        ref.child(organisationId)
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

    override fun getItemCount(): Int {
        return getHelpArrayList.size
    }

}