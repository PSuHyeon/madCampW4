package com.example.novelchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.agora.rtc.RtcEngine
import io.agora.rtc.IRtcEngineEventHandler
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.util.Log
import java.lang.Exception

class CallTest : AppCompatActivity() {
    private val PERMISSION_REQ_ID_RECORD_AUDIO = 22
//    private val PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1

    // Fill the App ID of your project generated on Agora Console.
    private val APP_ID = "f87f901b9b6c4a6aa4b2bdf5edea1331"
    // Fill the channel name.
    private val CHANNEL = "0723test"
    // Fill the temp token generated on Agora Console.
    private val TOKEN = "006f87f901b9b6c4a6aa4b2bdf5edea1331IABIDxQWYqCRjQgAZKiLYt8dsPkd6sGEF8+0k4oJhbSLTSjSI9sAAAAAEAAtDEjTvxrcYgEAAQC+Gtxi"
    private var mRtcEngine: RtcEngine ?= null
    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
    }

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
        setContentView(R.layout.activity_call_test)

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
            initializeAndJoinChannel();
        }
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
        var errCode = mRtcEngine!!.joinChannel(TOKEN, CHANNEL, "", 0)

    }

}