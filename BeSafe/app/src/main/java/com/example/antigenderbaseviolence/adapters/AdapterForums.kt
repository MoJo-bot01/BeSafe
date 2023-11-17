package com.example.antigenderbaseviolence.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.antigenderbaseviolence.activities.DashboardUser
import com.example.antigenderbaseviolence.databinding.RowForumsBinding
import com.example.antigenderbaseviolence.models.ModelForums
import com.example.antigenderbaseviolence.models.ModelUsers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterForums : RecyclerView.Adapter<AdapterForums.HolderForums>{

    private val context: Context

    private var forumsArrayList: ArrayList<ModelForums>
    private var usersArrayList: ArrayList<ModelUsers>

    //firebase auth
    private lateinit var auth: FirebaseAuth

    private lateinit var binding: RowForumsBinding

    constructor(
        context: Context,
        forumsArrayList: ArrayList<ModelForums>,
        usersArrayList: ArrayList<ModelUsers>
    ) {
        this.context = context
        this.forumsArrayList = forumsArrayList
        this.usersArrayList = usersArrayList
    }

    inner class HolderForums(forumView: View):RecyclerView.ViewHolder(forumView){
        var username = binding.usernameTextView1
        var date = binding.dateTV1
        var message = binding.messageTextView1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderForums {
        binding = RowForumsBinding.inflate(LayoutInflater.from(context),parent,false)

        return HolderForums(binding.root)
    }

    override fun getItemCount(): Int {
        return forumsArrayList.size
        return usersArrayList.size
    }

    override fun onBindViewHolder(holder: HolderForums, position: Int) {
        val model = forumsArrayList[position]

        loadUsers(holder)
        loadForums(model,holder)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DashboardUser::class.java)
            intent.putExtra("id",model.id)
            context.startActivity(intent)
        }
    }

    private fun loadUsers(holder: AdapterForums.HolderForums) {

        //init firebase auth
        auth = FirebaseAuth.getInstance()
        val uid = auth.uid

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //Get data
                    val email = "${snapshot.child("email").value}"
                    val name = "${snapshot.child("name").value}"
                    val phoneNumber = "${snapshot.child("phoneNumber").value}"
                    val profileImage = "${snapshot.child("profileImage").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    val uid = "${snapshot.child("uid").value}"
                    val userType = "${snapshot.child("userType").value}"

                    holder.username.text = name
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
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
                    val forumDate = "${snapshot.child("forumDate").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    val userType = "${snapshot.child("userType").value}"

                    model.id = id
                    model.uid = uid
                    model.message = message
                    model.username = name
                    model.forumDate = forumDate
//                    model.timestamp = timestamp.toLong()
                    model.userType = userType

                    holder.username.text = name
                    holder.date.text = forumDate
                    holder.message.text = message

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}