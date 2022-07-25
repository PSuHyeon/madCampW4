package com.example.novelchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.agora.rtc.RtcEngine
import io.agora.rtc.IRtcEngineEventHandler
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.TextView
import java.lang.Exception
import io.agora.rtc.models.ChannelMediaOptions

class CallTest : AppCompatActivity(), RecognitionListener {
    private val PERMISSION_REQ_ID_RECORD_AUDIO = 22
//    private val PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1

    // Fill the App ID of your project generated on Agora Console.
    private val APP_ID = "f87f901b9b6c4a6aa4b2bdf5edea1331"
    // Fill the channel name.
    private val CHANNEL = "0723test"
    // Fill the temp token generated on Agora Console.
    private val TOKEN = "006f87f901b9b6c4a6aa4b2bdf5edea1331IABIDxQWYqCRjQgAZKiLYt8dsPkd6sGEF8+0k4oJhbSLTSjSI9sAAAAAEAAtDEjTvxrcYgEAAQC+Gtxi"
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
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1);

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
        mChannelMediaOptions!!.publishLocalAudio = false
        mChannelMediaOptions!!.publishLocalVideo = false
        mChannelMediaOptions!!.autoSubscribeAudio = true
        mChannelMediaOptions!!.autoSubscribeVideo = true
        var errCode = mRtcEngine!!.joinChannel(TOKEN, CHANNEL, "", 0, mChannelMediaOptions)
//        var errCode = mRtcEngine!!.joinChannel(TOKEN, CHANNEL, "", 0)
        Log.d("errCode", errCode.toString())
    }

    fun clear(view: android.view.View) {
        findViewById<TextView>(R.id.result).text = "";
        findViewById<TextView>(R.id.log).text = "";
    }

    fun startRecognition(view: android.view.View) {
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech?.setRecognitionListener(this);

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        speech?.startListening(intent);
    }

    fun stopRecognition(view: android.view.View) {
        val view = findViewById<TextView>(R.id.log)
        view.append("Stop button pressed\n")
        speech?.stopListening();
        view.append(".stopListening() called\n")
    }

    override fun onReadyForSpeech(params: Bundle?) {
        val view = findViewById<TextView>(R.id.log)
        view.append("onReadyForSpeech event\n")
    }

    override fun onRmsChanged(rmsdB: Float) {
    }

    override fun onBufferReceived(buffer: ByteArray?) {
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

        val view = findViewById<TextView>(R.id.result)
        view.text = matches?.get(0)
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

        val view = findViewById<TextView>(R.id.result)
        view.text = matches?.get(0)
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
    }

    override fun onBeginningOfSpeech() {
        val view = findViewById<TextView>(R.id.log)
        view.append("onBeginningOfSpeech event\n")
    }

    override fun onEndOfSpeech() {
        val view = findViewById<TextView>(R.id.log)
        view.append("onEndOfSpeech event\n")
    }

    override fun onError(error: Int) {
        val message = getErrorText(error)
        Log.d("SPEECH", message)

        val view = findViewById<TextView>(R.id.log)
        view.append("onError event: $message\n")
    }

    private fun getErrorText(errorCode: Int): String {
        return when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> "ERROR_AUDIO"
            SpeechRecognizer.ERROR_CLIENT -> "ERROR_CLIENT"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "ERROR_INSUFFICIENT_PERMISSIONS"
            SpeechRecognizer.ERROR_NETWORK -> "ERROR_NETWORK"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "ERROR_NETWORK_TIMEOUT"
            SpeechRecognizer.ERROR_NO_MATCH -> "ERROR_NO_MATCH"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "ERROR_RECOGNIZER_BUSY"
            SpeechRecognizer.ERROR_SERVER -> "ERROR_SERVER"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "ERROR_SPEECH_TIMEOUT"
            else -> "Didn't understand, please try again."
        }
    }

}