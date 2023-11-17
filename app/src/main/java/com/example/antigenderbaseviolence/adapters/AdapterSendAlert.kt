package com.example.antigenderbaseviolence.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.antigenderbaseviolence.databinding.RowSentAlertsBinding
import com.example.antigenderbaseviolence.models.ModelSendAlert

class AdapterSendAlert : RecyclerView.Adapter<AdapterSendAlert.HolderSendAlert>{

    private val context: Context

    private var alertsArrayList: ArrayList<ModelSendAlert>

    private lateinit var binding: RowSentAlertsBinding

    constructor(context: Context, alertsArrayList: ArrayList<ModelSendAlert>) {
        this.context = context
        this.alertsArrayList = alertsArrayList
    }

    inner class HolderSendAlert(alertView: View):RecyclerView.ViewHolder(alertView){
        var alertType = binding.descriptionTv
        var alertDate = binding.dateTV
        val status = binding.statusTV
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderSendAlert {
        binding = RowSentAlertsBinding.inflate(LayoutInflater.from(context),parent,false)

        return HolderSendAlert(binding.root)
    }

    override fun getItemCount(): Int {
        return alertsArrayList.size
    }

    override fun onBindViewHolder(holder: HolderSendAlert, position: Int) {
        val model = alertsArrayList[position]

        val id = model.id
        val latitude = model.latitude
        val longitude = model.longitude
        val alertDate = model.alertDate
        val uid = model.uid
        val name = model.name
        val phoneNumber = model.phoneNumber
        val selectedAbuseType = model.selectedAbuseType
        val userType = model.userType
        val email = model.email
        val alertCategory = model.alertCategory

        //set data
        holder.alertType.text = selectedAbuseType
        holder.alertDate.text = alertDate
        holder.status.text = alertCategory
    }
}