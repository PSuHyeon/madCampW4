package com.example.novelchat

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.example.novelchat.friendsList.Companion.my_name
import com.google.android.material.card.MaterialCardView


lateinit var id: String
lateinit var name: String
class friendsList : AppCompatActivity() {

    companion object {
        lateinit var friendListActivity:friendsList
        lateinit var my_name:String
    }

    lateinit var friendListView: RecyclerView
    lateinit var friendList: ArrayList<friend>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_list)

        friendListActivity = this
        my_name = intent.getStringExtra("name").toString()

        friendListView = findViewById<RecyclerView>(R.id.friend_list)
        friendList = ArrayList<friend>()
        val add = findViewById<MaterialCardView>(R.id.add_friend)

        val my_pic = findViewById<ImageView>(R.id.my_pic)
        val my_name = findViewById<TextView>(R.id.my_name)
        val my_context = findViewById<TextView>(R.id.my_text)

        my_pic.setImageBitmap(myImage)
        my_name.text = intent.getStringExtra("name")
        my_context.text = intent.getStringExtra("context")


        add.setOnClickListener {
            val intent = Intent(this, Addfriend::class.java)
            startActivityForResult(intent, 300)
        }

        id = intent.getStringExtra("id")!!
        name = intent.getStringExtra("name")!!

        val url = "http://192.249.18.125:80/get_friend/" + id

        val request = object : JsonArrayRequest(
            Request.Method.GET,
            url,null, Response.Listener {
                Log.d("res", ""+it)
                for (i in 0 until it.length()){
                    val jsonObject = it.getJSONObject(i)
                    val name = jsonObject.getString("name")
                    val image = StringToBitmap(jsonObject.getString("image"))
                    val context = jsonObject.getString("context")
                    val id = jsonObject.getString("id")
                    val token = jsonObject.getString("token")
                    friendList.add(friend(name, image!!, context, id, token))
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


    fun getFriends(){
        val url = "http://192.249.18.125:80/get_friend/" + id

        val request = object : JsonArrayRequest(
            Request.Method.GET,
            url,null, Response.Listener {
                Log.d("res", ""+it)
                friendList = ArrayList<friend>()
                for (i in 0 until it.length()){
                    val jsonObject = it.getJSONObject(i)
                    val name = jsonObject.getString("name")
                    val image = StringToBitmap(jsonObject.getString("image"))
                    val context = jsonObject.getString("context")
                    val id = jsonObject.getString("id")
                    val token = jsonObject.getString("token")
                    friendList.add(friend(name, image!!, context, id, token))
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        getFriends()
    }
}

class friend(val name: String, val image: Bitmap, val context: String, val id: String, val token: String)

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
        holder.profile_id.text = array.get(position).id
        holder.profile_token.text = array.get(position).token
        holder.call_friend_btn.setOnClickListener{
            Log.d("tooken--", "call-friend-btn")
            Log.d("tooken-call-friend", array.get(position).token)
            val notificationsSender = FcmNotificationsSender(
                array.get(position).token,
                "TIKITALK",
                my_name + " 친구가 대화하고 싶어해요!",
                context.applicationContext,
                friendsList.friendListActivity
            )
            notificationsSender.SendNotifications()
        }
    }

    override fun getItemCount(): Int {
        return array.size
    }

    inner class friendHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val profile_image = itemView.findViewById<ImageView>(R.id.profile_pic)
        val profile_name = itemView.findViewById<TextView>(R.id.profile_name)
        val profile_context = itemView.findViewById<TextView>(R.id.profile_text)
        val profile_id = itemView.findViewById<TextView>(R.id.profile_id)
        val profile_token = itemView.findViewById<TextView>(R.id.profile_token)
        val call_friend_btn = itemView.findViewById<Button>(R.id.btnCallFriend)
        init{
            itemView.setOnClickListener {
                com.example.novelchat.yourImage = (profile_image.getDrawable() as BitmapDrawable).getBitmap()
                val intent = Intent(context, NewChatRoom::class.java)
                intent.putExtra("id1", profile_id.text.toString())
                context.startActivity(intent)
            }

        }

    }
}