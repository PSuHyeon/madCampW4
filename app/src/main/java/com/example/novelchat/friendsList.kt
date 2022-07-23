package com.example.novelchat

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest


lateinit var id: String
class friendsList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_list)

        val friendListView = findViewById<RecyclerView>(R.id.friend_list)
        val friendList = ArrayList<friend>()
        val add = findViewById<ImageView>(R.id.add_friend)

        add.setOnClickListener {
            val intent = Intent(this, Addfriend::class.java)
            startActivityForResult(intent, 300)
        }

        id = intent.getStringExtra("id")!!

        val url = "http://192.249.18.125:80/get_friend/" + id

        val request = object : JsonArrayRequest(
            Request.Method.GET,
            url,null, Response.Listener {
                Log.d("whyno", "" + it)
                for (i in 0 until it.length()){
                    val jsonObject = it.getJSONObject(i)
                    val name = jsonObject.getString("name")
                    val image = StringToBitmap(jsonObject.getString("image"))
                    val context = jsonObject.getString("context")
                    val id = jsonObject.getString("id")
                    friendList.add(friend(name, image!!, context, id))

                }
                friendListView.adapter = friendAdapter(this, friendList)
                friendListView.layoutManager = LinearLayoutManager(this)

            }, Response.ErrorListener {

            }
        ) {

        }
        request.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            // 0 means no retry
            0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
            1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        VolleySingleton.getInstance(this).addToRequestQueue(request)
        friendListView.adapter = friendAdapter(this, friendList)
        friendListView.layoutManager = LinearLayoutManager(this)

    }
}

class friend(val name: String, val image: Bitmap, val context: String, val id: String)

class friendAdapter(val context: Context, val array: ArrayList<friend>): RecyclerView.Adapter<friendAdapter.friendHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): friendAdapter.friendHolder {
        val view: View
        view = LayoutInflater.from(context).inflate(R.layout.friend_item,parent, false)
        return friendHolder(view)
    }

    override fun onBindViewHolder(holder: friendAdapter.friendHolder, position: Int) {
        holder.profile_image.setImageBitmap(array.get(position).image)
        holder.profile_name.text = array.get(position).name
        holder.profile_context.text = array.get(position).context
    }

    override fun getItemCount(): Int {
        return array.size
    }
    inner class friendHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val profile_image = itemView.findViewById<ImageView>(R.id.profile_pic)
        val profile_name = itemView.findViewById<TextView>(R.id.profile_name)
        val profile_context = itemView.findViewById<TextView>(R.id.profile_text)
    }
}