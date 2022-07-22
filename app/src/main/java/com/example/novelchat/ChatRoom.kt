package com.example.novelchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.core.widget.addTextChangedListener
import io.socket.emitter.Emitter

class ChatRoom : AppCompatActivity() {
    private lateinit var myid: String
    private lateinit var roomnum: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val room_num = findViewById<TextView>(R.id.room_num)
        val enter_text = findViewById<EditText>(R.id.type_text)
        val send = findViewById<Button>(R.id.send_button)

        val chat_1 = findViewById<TextView>(R.id.user1_text)
        val chat_2 = findViewById<TextView>(R.id.user2_text)
        val chat_3 = findViewById<TextView>(R.id.user3_text)
        val chat_4 = findViewById<TextView>(R.id.user4_text)
        val chat_5 = findViewById<TextView>(R.id.user5_text)
        val chat_6 = findViewById<TextView>(R.id.user6_text)

        val user1 = findViewById<ImageView>(R.id.user1)
        val user2 = findViewById<ImageView>(R.id.user2)
        val user3 = findViewById<ImageView>(R.id.user3)
        val user4 = findViewById<ImageView>(R.id.user4)
        val user5 = findViewById<ImageView>(R.id.user5)
        val user6 = findViewById<ImageView>(R.id.user6)



        room_num.text = intent.getStringExtra("room_num")
        roomnum = room_num.text.toString()

        if (intent.getStringExtra("request_type") == "enter"){
            myid = intent.getStringExtra("position") as String
        }
        else{
            myid = "1"
        }

        when(myid){
            "1" -> user1.setImageResource(R.drawable.bear)
            "2" -> user2.setImageResource(R.drawable.bear)
            "3" -> user3.setImageResource(R.drawable.bear)
            "4" -> user4.setImageResource(R.drawable.bear)
            "5" -> user5.setImageResource(R.drawable.bear)
            "6" -> user6.setImageResource(R.drawable.bear)
        }
        enter_text.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mSocket.emit("on_message", enter_text.text.toString() + "," + roomnum + "," + myid)
            }

            override fun afterTextChanged(p0: Editable?) {
                return
            }
        })
        send.setOnClickListener {
            if (enter_text.text.toString() == ""){
                Toast.makeText(this, "텍스트를 입력하세요", Toast.LENGTH_SHORT).show()
            }
            else{
                mSocket.emit("on_message", enter_text.text.toString() + "," + roomnum + "," + myid)
            }
        }

        mSocket.on("on_message", Emitter.Listener { args ->
            Log.d("listen", "args")
            val msgs = (args[0] as String).split(",")
            if (msgs[2] == "1"){
                chat_1.text = msgs[0]
            }
            else if (msgs[2] == "2"){
                chat_2.text = msgs[0]
            }
            else if (msgs[2] == "3"){
                chat_3.text = msgs[0]
            }
            else if (msgs[2] == "4"){
                chat_4.text = msgs[0]
            }
            else if (msgs[2] == "5"){
                chat_5.text = msgs[0]
            }
            else if (msgs[2] == "6"){
                chat_6.text = msgs[0]
            }
        })



    }

}