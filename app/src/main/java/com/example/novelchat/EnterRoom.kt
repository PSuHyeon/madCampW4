package com.example.novelchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
lateinit var mSocket: Socket
class EnterRoom : AppCompatActivity() {
    private var entered = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_room)



        val enter_button = findViewById<Button>(R.id.enterroom_button)
        val create_button = findViewById<Button>(R.id.makeroom_button)
        val call_button = findViewById<Button>(R.id.callTest_button)

        val id_type = findViewById<EditText>(R.id.roomId_type)

        create_button.setOnClickListener{
            mSocket.emit("create_room")
        }

        enter_button.setOnClickListener{
            val text = id_type.text.toString()
            if (text == ""){
                Toast.makeText(this, "enter room ID", Toast.LENGTH_SHORT).show()
            }
            else{
                mSocket.emit("enter_room", text)
            }
        }

        call_button.setOnClickListener {
            Log.d("button","call button clicked")
            val intent = Intent(this, CallTest::class.java)
            startActivity(intent)
        }

        mSocket.on("create_room", Emitter.Listener {args ->

            if (!entered){
                val intent = Intent(this, ChatRoom::class.java)
                intent.putExtra("request_type", "create")
                intent.putExtra("room_num", args[0] as String)
                startActivity(intent)
                entered = true
            }
        })

        mSocket.on("enter_room", Emitter.Listener {args ->

            if (!entered){
                val position = args[0] as String

                if (position == "Wrong ID"){
                    Toast.makeText(this, "This room is full!", Toast.LENGTH_LONG).show()
                }
                else{
                    val room_num = args[1] as String
                    val intent = Intent(this, ChatRoom::class.java)
                    intent.putExtra("request_type", "enter")
                    intent.putExtra("position", position)
                    intent.putExtra("room_num", room_num)
                    startActivity(intent)
                    entered = true
                }
            }

        })

    }
}