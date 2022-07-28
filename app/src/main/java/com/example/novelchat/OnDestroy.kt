package com.example.novelchat

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast

class OnDestroy : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        mSocket.emit("iamout", id)
        Thread.sleep(5000)
        Toast.makeText(this, "hey", Toast.LENGTH_SHORT).show()
        stopSelf()
    }
}