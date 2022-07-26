package com.example.novelchat

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.socket.client.IO
import io.socket.emitter.Emitter
import java.text.SimpleDateFormat
import java.util.*


lateinit var yourImage: Bitmap
lateinit var myImage: Bitmap
class NewChatRoom : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_chat_room)

        val recyclerView = findViewById< RecyclerView>(R.id.chat_recyclerView)
        val mytext = findViewById<TextView>(R.id.my_text)
        val yourtext = findViewById<TextView>(R.id.your_text)
        val yourprofile = findViewById<ImageView>(R.id.your_image)
        val myprofile = findViewById<ImageView>(R.id.my_image)
        val send_edit = findViewById<EditText>(R.id.send_edit_text)
        val send_button = findViewById<Button>(R.id.send_button)
        val save_check = findViewById<CheckBox>(R.id.save_check)
        val your_id = intent.getStringExtra("id1")

        val mSocket= IO.socket("http://192.249.18.125:443")
        mSocket.connect()

        mSocket.emit("enter_room", id + "," + your_id)

        send_edit.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mSocket.emit("message_from", id + "," + your_id + "," + send_edit.text.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
                return
            }
        })

        mSocket.on("message_from", Emitter.Listener { args ->
            val temp = (args[0] as String).split(",")

            if (temp[1] == "me"){
                mytext.text = temp[0]
            }
            else{
                yourtext.text = temp[0]
            }
        })

        send_button.setOnClickListener {
            if(send_edit.text.isEmpty()){
                Toast.makeText(this, "메세지를 입력하세요", Toast.LENGTH_SHORT).show()
            }
            else{
                if (save_check.isChecked){
                    val now = System.currentTimeMillis()
                    val date = Date(now)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                    val getTime = dateFormat.format(date)
                    mSocket.emit("send_message", send_edit.text.toString() + "," + id + "," + your_id + "," + getTime + "," + "save" + "," + name)
                }
                else{
                    val now = System.currentTimeMillis()
                    val date = Date(now)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                    val getTime = dateFormat.format(date)
                    mSocket.emit("send_message", send_edit.text.toString() + "," + id + ","+ your_id + "," + getTime + "," + "no_save"+"," + name)
                }
            }
        }



        yourprofile.setImageBitmap(yourImage)
        myprofile.setImageBitmap(myImage)
        var chatLogs = ArrayList<chat>()
        recyclerView.adapter = chatAdapter(this, chatLogs)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mSocket.on("send_message", Emitter.Listener { args ->

            // Display the first 500 characters of the response string.
            val listType = object : TypeToken<ArrayList<chat?>?>() {}.type
            val myExList: ArrayList<chat> =
                Gson().fromJson<ArrayList<chat>>(args[0].toString(), listType)
            chatLogs = myExList
            Log.d("po", ""+chatLogs)
            runOnUiThread{
                recyclerView.adapter = chatAdapter(this, chatLogs)
            }



        })
    }
}
class chat(val name : String, val time : String, val text: String, val id: String)
class chatAdapter(val context: Context, val arrayList: ArrayList<chat>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        if (viewType == 2){
            view = LayoutInflater.from(context).inflate(R.layout.yourchat, parent, false)
            return YourHolder(view)
        }
        else{
            view = LayoutInflater.from(context).inflate(R.layout.mychat, parent, false)
            return MyHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyHolder){
            Log.d("hihi", ""+arrayList.get(position).text)
            holder.my_text.text = arrayList.get(position).text
            holder.my_time.text = arrayList.get(position).time
        }
        else if (holder is YourHolder){
            holder.your_image.setImageBitmap(yourImage)
            holder.your_text.text = arrayList.get(position).text
            holder.your_name.text = arrayList.get(position).name
            holder.your_time.text = arrayList.get(position).time
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun getItemViewType(position: Int): Int {
        if (arrayList.get(position).id == id){
            return 1
        }
        else{
            return 2
        }
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val my_text = itemView.findViewById<TextView>(R.id.chat_my_text)
        val my_time = itemView.findViewById<TextView>(R.id.chat_my_time)
    }

    inner class YourHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val your_image = itemView.findViewById<ImageView>(R.id.chat_your_profile)
        val your_text = itemView.findViewById<TextView>(R.id.chat_your_text)
        val your_time = itemView.findViewById<TextView>(R.id.chat_your_time)
        val your_name = itemView.findViewById<TextView>(R.id.chat_your_name)
    }
}