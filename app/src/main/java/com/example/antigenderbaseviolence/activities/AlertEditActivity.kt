package com.example.antigenderbaseviolence.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.antigenderbaseviolence.databinding.ActivityAlertEditBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

//View binding
private lateinit var binding: ActivityAlertEditBinding

class AlertEditActivity : AppCompatActivity() {

    private companion object{
        private const val TAG = "ALERT_EDIT_TAG"
    }

    //Item id get from intent started from AdapterItemClass
    private var alertId = ""
    //Progress dialog
    private lateinit var progressDialog: ProgressDialog

    //Arraylist to hold category titles
    private lateinit var alertTitleArrayList:ArrayList<String>

    //Arraylist to hold category ids
    private lateinit var alertIdArrayList:ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlertEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Get item id to edit the item info
        alertId = intent.getStringExtra("id")!!

        //Setup progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        loadCategories()
        loadAlertInfo()

        binding.backBtn.setOnClickListener{
            super.onBackPressed()
            finish()
        }

        binding.itemCategoryTv.setOnClickListener{
            itemCategoryPickDialog()
        }

        binding.submitBtn.setOnClickListener {
            validateData()
        }
    }

    private var itemCat=""

    private fun validateData() {
        itemCat = binding.itemCategoryTv.text.toString().trim()

        if (TextUtils.isEmpty(selectedCategoryId)) {
            Toast.makeText(baseContext, "Select item category...",
                Toast.LENGTH_SHORT).show()
        }
        else{
            updateAlert()
        }
    }

    private fun updateAlert() {
        Log.d(TAG,"updateItem: Updating item info...")

        //Show progress dialog
        progressDialog.setMessage("Updating Alert Info...")
        progressDialog.show()

        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["alertCategory"] = itemCat

        val ref = FirebaseDatabase.getInstance().getReference("Alerts")
        ref.child(alertId)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Log.d(TAG, "updateItem: Updated successfully...")
                Toast.makeText(this,"Updated successfully...",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Log.d(TAG, "updateItem: Failed to update due to ${e.message}...")
                Toast.makeText(this,"Failed to update due to ${e.message}...",Toast.LENGTH_SHORT).show()
            }

        val intent= Intent(this, DashboardAdmin::class.java)
        startActivity(intent)
        finish()
    }

    private var selectedCategoryId = ""
    private var selectedCategoryTitle = ""

    private fun itemCategoryPickDialog() {
        //Show dialog to pick the category of item. We already got the categories

        //Make string array from arraylist of string
        val categoriesArray = arrayOfNulls<String>(alertTitleArrayList.size)
        for(i in alertTitleArrayList.indices){
            categoriesArray[i] = alertTitleArrayList[i]
        }

        //Alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose Category")
            .setItems(categoriesArray){dialog,position ->
                //Handle click, save clicked category id and title
                selectedCategoryId = alertIdArrayList[position]
                selectedCategoryTitle = alertTitleArrayList[position]

                //Set to textview
                binding.itemCategoryTv.text = selectedCategoryTitle
            }
            .show() //Show dialog
    }

    private fun loadAlertInfo() {
        Log.d(TAG,"loadItemInfo: Loading item info")

        val ref = FirebaseDatabase.getInstance().getReference("Alerts")
        ref.child(alertId)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    selectedCategoryTitle = snapshot.child("alertCategory").value.toString()
                    val id = snapshot.child("id").value.toString()
                    val latitude = snapshot.child("latitude").value.toString()
                    val longitude = snapshot.child("longitude").value.toString()
                    val alertDate = snapshot.child("alertDate").value.toString()
                    val uid = snapshot.child("uid").value.toString()
                    val name = snapshot.child("name").value.toString()
                    val phoneNumber = snapshot.child("phoneNumber").value.toString()
                    val selectedAbuseType = snapshot.child("selectedAbuseType").value.toString()
                    val userType = snapshot.child("userType").value.toString()
                    val email = snapshot.child("email").value.toString()
                    val alertCategory = snapshot.child("alertCategory").value.toString()


                    //set to views
                    binding.phoneNumberTV.text = phoneNumber
                    binding.userName.text = name
                    binding.dateTV.text = alertDate
                    binding.descriptionTv.text = selectedAbuseType
                    binding.titleTv.text = "Coordinate: $latitude, $longitude"
                    binding.itemCategoryTv.text = alertCategory
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }


    private fun loadCategories() {
        Log.d(TAG,"loadCategories: loading categories...")

        alertIdArrayList = ArrayList()
        alertTitleArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                alertIdArrayList.clear()
                alertTitleArrayList.clear()

                for(ds in snapshot.children){
                    val id = "${ds.child("id").value}"
                    val category = "${ds.child("categoryName").value}"

                    alertIdArrayList.add(id)
                    alertTitleArrayList.add(category)

                    Log.d(TAG,"onDataChange: Category ID $id")
                    Log.d(TAG,"onDataChange: Category $category")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}