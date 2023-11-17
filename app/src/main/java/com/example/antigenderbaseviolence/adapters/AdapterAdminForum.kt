package com.example.antigenderbaseviolence.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.antigenderbaseviolence.R
import com.example.antigenderbaseviolence.databinding.RowAdminForumsBinding
import com.example.antigenderbaseviolence.models.ModelForums
import com.example.antigenderbaseviolence.models.ModelUsers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterAdminForum
    : RecyclerView.Adapter<AdapterAdminForum.HolderForums>{

    private val context: Context

    private var forumsArrayList: ArrayList<ModelForums>
    private var usersArrayList: ArrayList<ModelUsers>

    //firebase auth
    private lateinit var auth: FirebaseAuth

    private lateinit var binding: RowAdminForumsBinding

    constructor(
        context: Context,
        forumsArrayList: ArrayList<ModelForums>,
        usersArrayList: ArrayList<ModelUsers>
    ) {
        this.context = context
        this.forumsArrayList = forumsArrayList
        this.usersArrayList = usersArrayList
    }

    inner class HolderForums(forumView: View): RecyclerView.ViewHolder(forumView){
        var username = binding.usernameTextView1
        var date = binding.dateTV1
        var message = binding.messageTextView1
        var moreOptions = binding.moreBtn
        var userProfile = binding.profileIv
        var reports = binding.reportsTV1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderForums {
        binding = RowAdminForumsBinding.inflate(LayoutInflater.from(context),parent,false)

        return HolderForums(binding.root)
    }

    override fun getItemCount(): Int {
        return forumsArrayList.size
        return usersArrayList.size
    }

    override fun onBindViewHolder(holder: HolderForums, position: Int) {
        val model = forumsArrayList[position]

        val id = model.id
        val uid = model.uid
        val message = model.message
        val username = model.username
        val profileImage = model.profileImage
        val forumDate = model.forumDate
        val reports = model.reports
        val timestamp = model.timestamp
        val userType = model.userType

        auth = FirebaseAuth.getInstance()

        loadUsers(holder)
        loadForums(model,holder)

        holder.moreOptions.setOnClickListener {
            moreOptionDialog(model, holder)
        }
    }

    private fun moreOptionDialog(model: ModelForums, holder: HolderForums) {
        val id = model.id

        // Define the options array
        val options = arrayOf("Delete Forum")

        // Create and show the AlertDialog
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose Option")
            .setItems(options) { _, _ ->
                showDeleteConfirmationDialog(id)
            }
            .show()
    }

    private fun showDeleteConfirmationDialog(id: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Forum")
            .setMessage("Are you sure you want to delete this forum?")
            .setPositiveButton("Delete") { _, _ ->
                deleteForum(id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteForum(id: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Forums")
        ref.child(id).removeValue()
            .addOnSuccessListener {
                // Forum deleted successfully, you might want to update your UI here
                notifyDataSetChanged() // Refresh the RecyclerView
                Toast.makeText(context, "Forum deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Handle failure to delete forum
                Toast.makeText(context, "Failed to delete forum: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadForums(model: ModelForums, holder: HolderForums) {
        val forumId = model.id

        val ref = FirebaseDatabase.getInstance().getReference("Forums")
        ref.child(forumId)
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //Get data
                    val id = "${snapshot.child("id").value}"
                    val uid = "${snapshot.child("uid").value}"
                    val message = "${snapshot.child("message").value}"
                    val name = "${snapshot.child("username").value}"
                    val profileImage = "${snapshot.child("profileImage").value}"
                    val forumDate = "${snapshot.child("forumDate").value}"
                    val reports = snapshot.child("reports").getValue(Int::class.java) // Safe conversion
                    val userType = "${snapshot.child("userType").value}"

                    model.id = id
                    model.uid = uid
                    model.message = message
                    model.username = name
                    model.profileImage = profileImage
                    model.forumDate = forumDate
                    model.reports = reports
                    model.userType = userType

                    holder.username.text = name
                    holder.date.text = forumDate
                    holder.message.text = message

                    try {
                        Glide.with(context)
                            .load(profileImage)
                            .placeholder(R.drawable.person_gray)
                            .into(holder.userProfile) //
                    } catch (e: Exception) {
                        // Handle Glide exception if needed
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun loadUsers(holder: HolderForums) {
        //init firebase auth
        auth = FirebaseAuth.getInstance()
        val uid = auth.uid

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //Get data
                val name = "${snapshot.child("name").value}"
//                holder.username.text = name
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}