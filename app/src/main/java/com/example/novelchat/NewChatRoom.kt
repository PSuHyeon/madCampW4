package com.example.novelchat

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import io.socket.client.IO
import io.socket.emitter.Emitter


lateinit var yourImage: Bitmap
lateinit var myImage: Bitmap
class NewChatRoom : AppCompatActivity(), RecognitionListener {

    private var speech: SpeechRecognizer? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1);

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_chat_room)

        val recyclerView = findViewById< RecyclerView>(R.id.chat_recyclerView)
        val mytext = findViewById<TextView>(R.id.my_text)
        val yourtext = findViewById<TextView>(R.id.your_text)
        val yourprofile = findViewById<ImageView>(R.id.your_image)
        val myprofile = findViewById<ImageView>(R.id.my_image)
        val send_edit = findViewById<EditText>(R.id.send_edit_text)
        val stt_button = findViewById<Button>(R.id.stt_button)
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
        //end of chat
        speech?.stopListening();
    }

    override fun onReadyForSpeech(params: Bundle?) {
    }

    override fun onRmsChanged(rmsdB: Float) {
    }

    override fun onBufferReceived(buffer: ByteArray?) {
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

        val view = findViewById<TextView>(R.id.send_edit_text)
        view.text = matches?.get(0)
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

        val view = findViewById<TextView>(R.id.send_edit_text)
        view.text = matches?.get(0)
        // 여기서 말풍선 보내기
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
    }

    override fun onBeginningOfSpeech() {

    }

    override fun onEndOfSpeech() {
        val view = findViewById<TextView>(R.id.send_edit_text)

        speech?.stopListening();
        Thread.sleep(500);
        startRecognition(view);
    }

    override fun onError(error: Int) {
        val message = getErrorText(error)
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