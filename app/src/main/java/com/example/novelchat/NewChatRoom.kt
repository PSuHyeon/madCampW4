package com.example.novelchat

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.socket.client.IO
import io.socket.emitter.Emitter


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


        yourprofile.setImageBitmap(yourImage)
        myprofile.setImageBitmap(myImage)

    }
}