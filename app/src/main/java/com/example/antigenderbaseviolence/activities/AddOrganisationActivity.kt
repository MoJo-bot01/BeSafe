package com.example.antigenderbaseviolence.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Patterns
import android.view.Menu
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.antigenderbaseviolence.databinding.ActivityAddOrganisationBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Locale

//firebase auth
private lateinit var auth: FirebaseAuth

//progress dialog
private lateinit var progressDialog: ProgressDialog

//Image uri
private var imageUri: Uri? = null

//view binding
private lateinit var binding: ActivityAddOrganisationBinding

class AddOrganisationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddOrganisationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        auth = FirebaseAuth.getInstance()

        checkUser()

        //init progress dialog, will show while creating account
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.backBtn.setOnClickListener{
            super.onBackPressed()
            finish()
        }

        //Handle click, pick image from camera/gallery
        binding.profileIv.setOnClickListener {
            showImageAttachMenu()
        }

        binding.submitBtn.setOnClickListener {
            validateData()
        }

    }

    private var name = " "
    private var description = " "
    private var telephone = " "
    private var email = " "

    private fun validateData() {
        name = binding.nameEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        description = binding.descriptionEt.text.toString().trim()
        telephone = binding.telephoneNumberEt.text.toString().trim()

        val phonePattern = "^(\\+27|0)[6-8][0-9]{8}$"

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(baseContext, "Enter The Organisation Name...", Toast.LENGTH_SHORT).show()
        }else if (TextUtils.isEmpty(description)) {
            Toast.makeText(baseContext, "Enter The Organisation Description...", Toast.LENGTH_SHORT).show()
        }else if (TextUtils.isEmpty(telephone)) {
            Toast.makeText(baseContext, "Enter The Organisation Phone Number...", Toast.LENGTH_SHORT).show()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(baseContext, "Invalid Email Address...", Toast.LENGTH_SHORT).show()
        }else{
            if (imageUri == null){
                Toast.makeText(baseContext, "Capture or Upload Organisation Image...", Toast.LENGTH_SHORT).show()
            }
            else{
                uploadImage()
            }
        }
    }

    private fun uploadImage() {
        progressDialog.setMessage("Adding Organisation...")
        progressDialog.show()

        val filePathAndName = "OrganisationImages/"+auth.uid

        val ref = FirebaseStorage.getInstance().getReference(filePathAndName)
        ref.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot->

                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while(!uriTask.isSuccessful);
                val uploadedImageUrl = "${uriTask.result}"

                uploadOrganisationInfo(uploadedImageUrl)
            }
    }

    private fun uploadOrganisationInfo(uploadedImageUrl: String) {
        val timestamp = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        //init firebase auth
        auth = FirebaseAuth.getInstance()
        val uid = auth.uid

        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["id"] = "$timestamp"
        hashMap["uid"] = uid
        hashMap["organisationName"] = name
        hashMap["organisationDescription"] = description
        hashMap["organisationTelephoneNumber"] = telephone
        hashMap["organisationEmail"] = email
        hashMap["timestamp"] = timestamp
        if (imageUri != null){
            hashMap["organisationProfileImage"] = uploadedImageUrl
        }

        val ref = FirebaseDatabase.getInstance().getReference("Organisations")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(baseContext, "Organisation Added Successfully...", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@AddOrganisationActivity, GetHelpAdminActivity::class.java))
                finish()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(baseContext, "Failed To Add Organisation Due To ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showImageAttachMenu(){

        //Show popup menu with options Camera, Gallery to pick image

        //Setup popup menu
        val popupMenu = PopupMenu(this,binding.profileIv)
        popupMenu.menu.add(Menu.NONE,0,0,"Camera")
        popupMenu.menu.add(Menu.NONE,1,1,"Gallery")
        popupMenu.show()

        //Handle popup menu item click
        popupMenu.setOnMenuItemClickListener { item->

            val id = item.itemId
            if (id == 0){
                pickImageCamera()
            }
            else if(id ==1){
                pickImageGallery()
            }

            true
        }
    }

    private fun pickImageGallery() {

        //Intent to pick image from gallery
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    private fun pickImageCamera() {
        //Intent to pick image from camera
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE,"Temp Title")
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description")

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)
        cameraActivityResultLauncher.launch(intent)
    }

    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult>{ result ->
            if(result.resultCode == Activity.RESULT_OK){
                val data = result.data

                binding.profileIv.setImageURI(imageUri)
            }
            else{
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    )

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult>{ result ->
            if(result.resultCode == RESULT_OK){
                val data = result.data
                imageUri = data!!.data

                binding.profileIv.setImageURI(imageUri)
            }
            else{
                Toast.makeText(this, "Cancelled",Toast.LENGTH_SHORT).show()
            }
        }
    )

    private fun checkUser() {

        val firebaseUser = auth.currentUser
        if (firebaseUser == null){
            startActivity(Intent(this, LaunchActivity::class.java))
            finish()
        }
        else{

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}