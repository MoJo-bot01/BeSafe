package com.example.antigenderbaseviolence.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.antigenderbaseviolence.R
import com.example.antigenderbaseviolence.databinding.ActivityProfileEditBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

private lateinit var binding: ActivityProfileEditBinding

//Firebase auth
private lateinit var auth: FirebaseAuth

//Image uri
private var imageUri: Uri? = null

//progress dialog
private lateinit var progressDialog: ProgressDialog

class ProfileEditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        loadUserInfo()

        //Setup progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        //Handle click, back image button
        binding.backBtn.setOnClickListener{
            super.onBackPressed()
            finish()
        }

        //Handle click, pick image from camera/gallery
        binding.profileIv.setOnClickListener {
            showImageAttachMenu()
        }

        //Handle click, begin update profile
        binding.updateBtn.setOnClickListener {
            validateData()
            onBackPressed()
        }
    }

    private var name =""
    private fun validateData() {
        //Get data
        name = binding.nameEt.text.toString().trim()

        //Validate data
        if (name.isEmpty()){
            Toast.makeText(this,"Enter name",Toast.LENGTH_SHORT).show()
        }
        else{
            if (imageUri == null){
                updateProfile("")
            }
            else{
                uploadImage()
            }
        }
    }

    private fun updateProfile(uploadedImageUrl: String) {
        progressDialog.setMessage("Updating profile")
//        progressDialog.show()

        //Setup info to update to db
        val hashmap: HashMap<String, Any> = HashMap()
        hashmap["name"] = "$name"
        if (imageUri != null){
            hashmap["profileImage"] = uploadedImageUrl
        }

        //update db
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(auth.uid!!)
            .updateChildren(hashmap)
            .addOnSuccessListener {
                //Profile updated
//                progressDialog.dismiss()
                Toast.makeText(this,"Profile updated",Toast.LENGTH_SHORT).show()

                // After updating the profile, update the forum posts
                updateForumPosts(auth.uid!!, uploadedImageUrl)
            }
            .addOnFailureListener {e ->
                //Failed to upload image
                progressDialog.dismiss()
                Toast.makeText(this,"Failed to update image due to ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateForumPosts(uid: String, uploadedImageUrl: String) {
        // Get a reference to the "Forums" node
        val forumRef = FirebaseDatabase.getInstance().getReference("Forums")

        // Query for forum posts with the same uid
        val query = forumRef.orderByChild("uid").equalTo(uid)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Loop through the forum posts by the user
                for (postSnapshot in snapshot.children) {
                    val postId = postSnapshot.key

                    // Update the profileImage field in each forum post
                    val updateData: MutableMap<String, Any> = mutableMapOf()
                    updateData["profileImage"] = uploadedImageUrl

                    forumRef.child(postId!!).updateChildren(updateData)
                        .addOnSuccessListener {
                            // Profile image updated in the forum post
                        }
                        .addOnFailureListener { e ->
                            // Handle failure to update forum post if needed
                            //Toast.makeText(this@ProfileEditActivity, "Failed to update forum post: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }

                // Dismiss progress dialog
                progressDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled if needed
                progressDialog.dismiss()
                //Toast.makeText(this@ProfileEditActivity, "Failed to update forum posts: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun uploadImage() {
        progressDialog.setMessage("Uploading profile image")
//        progressDialog.show()

        val filePathAndName = "ProfileImages/"+auth.uid

        val ref = FirebaseStorage.getInstance().getReference(filePathAndName)
        ref.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot->

                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while(!uriTask.isSuccessful);
                val uploadedImageUrl = "${uriTask.result}"

                updateProfile(uploadedImageUrl)
            }

    }

    private fun loadUserInfo() {
        //Load user info
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(auth.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //Get user info
                    val name = "${snapshot.child("name").value}"
                    val profileImage = "${snapshot.child("profileImage").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"

                    binding.nameEt.setText(name)

                    try {
                        Glide.with(this@ProfileEditActivity)
                            .load(profileImage)
                            .placeholder(R.drawable.person_gray)
                            .into(binding.profileIv)
                    } catch (e: Exception) {

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
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
        ActivityResultCallback<ActivityResult>{result ->
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}