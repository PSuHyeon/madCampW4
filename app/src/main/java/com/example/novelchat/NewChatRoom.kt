package com.example.novelchat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.JsonObject
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.models.ChannelMediaOptions
import io.socket.client.IO
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.lang.Exception


lateinit var yourImage: Bitmap
lateinit var myImage: Bitmap
class NewChatRoom : AppCompatActivity() {
    // VOICE CHAT
    private val PERMISSION_REQ_ID_RECORD_AUDIO = 22
//    private val PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1

    // Fill the App ID of your project generated on Agora Console.
    private val APP_ID = "f87f901b9b6c4a6aa4b2bdf5edea1331"
    // Fill the temp token generated on Agora Console.
    lateinit var USERACCOUNT: String;
    lateinit var TOKEN: String;
    lateinit var CHANNEL: String;
    private var mRtcEngine: RtcEngine ?= null
    private var mChannelMediaOptions: ChannelMediaOptions = ChannelMediaOptions();
    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
    }
    private var speech: SpeechRecognizer? = null

    private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(this, permission) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(permission),
                requestCode)
            return false
        }
        return true
    }

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

        USERACCOUNT = id;
        val your_id = intent.getStringExtra("id1")

        val mSocket= IO.socket("http://192.249.18.125:443")
        mSocket.connect()

        mSocket.emit("enter_room", id + "," + your_id)

        //VOICE CHAT
        mSocket.emit("voice_chat_init", USERACCOUNT + "," + your_id)
        mSocket.on("voice_chat_info", Emitter.Listener { info ->
            TOKEN = (info[0] as JSONObject).getString("token")
            CHANNEL = (info[0] as JSONObject).getString("channel")
//            TOKEN = info.get("token").toString()
            Log.d("TOKEN1", TOKEN)
            Log.d("CHANNEL1", CHANNEL)
            //voice chat
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
                initializeAndJoinChannel();
            }
        })

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

    override fun onDestroy() {
        super.onDestroy()
        mRtcEngine?.leaveChannel()
        RtcEngine.destroy()
    }

    private fun initializeAndJoinChannel() {
        try {
            mRtcEngine = RtcEngine.create(baseContext, APP_ID, mRtcEventHandler)
        } catch (e: Exception) {
        }
//        mChannelMediaOptions!!.publishLocalAudio = true
//        mChannelMediaOptions!!.publishLocalVideo = true
//        mChannelMediaOptions!!.autoSubscribeAudio = true
//        mChannelMediaOptions!!.autoSubscribeVideo = true
//        var errCode = mRtcEngine!!.joinChannel(TOKEN, CHANNEL, "", 0, mChannelMediaOptions)
        var errCode = mRtcEngine!!.joinChannelWithUserAccount(TOKEN, CHANNEL, USERACCOUNT)
        Log.d("errCode", errCode.toString())
        Log.d("TOKEN", TOKEN)
        Log.d("CHANNEL", CHANNEL)
    }
}