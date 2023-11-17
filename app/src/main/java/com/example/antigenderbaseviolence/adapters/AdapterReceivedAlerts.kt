package com.example.antigenderbaseviolence.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.antigenderbaseviolence.activities.AlertEditActivity
import com.example.antigenderbaseviolence.activities.HotspotsActivity
import com.example.antigenderbaseviolence.databinding.RowReceivedAlertsBinding
import com.example.antigenderbaseviolence.models.ModelSendAlert

class AdapterReceivedAlerts : RecyclerView.Adapter<AdapterReceivedAlerts.HolderAlerts> {

    private val context: Context

    public var alertsArrayList: ArrayList<ModelSendAlert>

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
        val moreBtn = binding.moreBtn
        val categoryTV = binding.categoryTV
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
            holder.phoneNumber.text = phoneNumber
            holder.username.text = name
            holder.dateTV.text = alertDate
            holder.descriptionTv.text = selectedAbuseType
        holder.categoryTV.text = alertCategory
            holder.titleTv.text = "Coordinate: $latitude, $longitude"

        //Handle click, show dialog with option 1)Edit Item, 2)Delete Item
        holder.moreBtn.setOnClickListener {
            moreOptionDialog(model, holder)
        }

            holder.itemView.setOnClickListener {
                val intent = Intent(context, HotspotsActivity::class.java)
                intent.putExtra("latitude", latitude)
                intent.putExtra("longitude", longitude)
                context.startActivity(intent)
            }
        }

    private fun moreOptionDialog(
        model: ModelSendAlert,
        holder: HolderAlerts
    ) {
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

        //Options to show in dialog
        val options = arrayOf("Edit Alert Status")

        //Alert dialog
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose Option")
            .setItems(options){ _, position->
                //Handle option click
                if (position == 0){
                    //Edit is clicked
                    val intent = Intent(context, AlertEditActivity::class.java)
                    intent.putExtra("id", id)//Passed itemId, will be used to edit the item
                    context.startActivity(intent)
                }
            }
            .show()
    }

}
