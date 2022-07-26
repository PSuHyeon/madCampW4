package com.example.novelchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.TextView

class SttChat : AppCompatActivity(), RecognitionListener {

    private var speech: SpeechRecognizer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stt_chat)
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

        speech?.stopListening();
        view.append(".stopListening() called\n")
        Thread.sleep(500);
        view.append(".sleep() called\n")
        startRecognition(view);
        view.append(".startRecognition() called\n")
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