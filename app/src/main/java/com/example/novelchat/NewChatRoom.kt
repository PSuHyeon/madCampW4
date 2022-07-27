package com.example.novelchat

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.Dimension
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.slider.Slider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.socket.client.IO
import io.socket.emitter.Emitter
import java.text.SimpleDateFormat
import java.util.*


lateinit var yourImage: Bitmap
//lateinit var myImage: Bitmap
class NewChatRoom : AppCompatActivity(), RecognitionListener {

    private var speech: SpeechRecognizer? = null
    private val TIMEOUT: Long = 1000
    private var presstime: Long = 0
    lateinit var viewTv: TextView
    lateinit var subScriberTv :TextView
    override fun onCreate(savedInstanceState: Bundle?) {

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1);

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_chat_room)
        viewTv = findViewById<TextView>(R.id.viewTv)
        subScriberTv = findViewById<TextView>(R.id.subScriberTv)
        val recyclerView = findViewById< RecyclerView>(R.id.chat_recyclerView)
        val mytext = findViewById<TextView>(R.id.my_text)
        val mytextwrapper = findViewById<MaterialCardView>(R.id.my_text_wrapper)
        val yourtext = findViewById<TextView>(R.id.your_text)
        val yourprofile = findViewById<ImageView>(R.id.your_image)
//        val myprofile = findViewById<ImageView>(R.id.my_image)
        val send_edit = findViewById<EditText>(R.id.send_edit_text)
        val stt_button = findViewById<CardView>(R.id.stt_button)
//        val send_button = findViewById<Button>(R.id.send_button)
        val save_check = findViewById<CheckBox>(R.id.save_check)
        val your_id = intent.getStringExtra("id1")

        val textSizeSlider: Slider = findViewById(R.id.slider)
        textSizeSlider.addOnChangeListener{ slider, value, fromUser ->
            mytext.setTextSize(Dimension.SP, value.toFloat())
            // size도 말풍선 보낼 때 보내야함


        }


        val mSocket= IO.socket("http://192.249.18.125:443")
        mSocket.connect()

        mSocket.emit("enter_room", id + "," + your_id)

        send_edit.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mSocket.emit("message_from", id + "," + your_id + "," + send_edit.text.toString())
                if (send_edit.text.contains("\n")){
                    if(send_edit.text.isEmpty()){

                    }
                    else{
                        if (save_check.isChecked){
                            val now = System.currentTimeMillis()
                            val date = Date(now)
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                            val getTime = dateFormat.format(date)
                            mSocket.emit("send_message", send_edit.text.toString().split("\n").get(0) + "," + id + "," + your_id + "," + getTime + "," + "save" + "," + name)
                            send_edit.setText("")
                        }
                        else{
                            val now = System.currentTimeMillis()
                            val date = Date(now)
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                            val getTime = dateFormat.format(date)
                            mSocket.emit("send_message", send_edit.text.toString().split("\n").get(0) + "," + id + ","+ your_id + "," + getTime + "," + "no_save"+"," + name)
                            send_edit.setText("")
                        }
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                return
            }
        })
//
//        val handler: Handler = object : Handler() {
//            fun editMyText(msg: Message?) {
//                mytext.text = msg.toString()
//            }
//        }

        mSocket.on("message_from", Emitter.Listener { args ->
            val temp = (args[0] as String).split(",")

            if (temp[1] == "me"){
//                mytext.text = temp[0]
                Handler(Looper.getMainLooper()).post { mytext.setText(temp[0]) }
            }
            else{
//                yourtext.text = temp[0]
                Handler(Looper.getMainLooper()).post { yourtext.setText(temp[0]) }
            }
        })

//        send_button.setOnClickListener {
//            if(send_edit.text.isEmpty()){
//                Toast.makeText(this, "메세지를 입력하세요", Toast.LENGTH_SHORT).show()
//            }
//            else{
//                if (save_check.isChecked){
//                    val now = System.currentTimeMillis()
//                    val date = Date(now)
//                    val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
//                    val getTime = dateFormat.format(date)
//                    mSocket.emit("send_message", send_edit.text.toString() + "," + id + "," + your_id + "," + getTime + "," + "save" + "," + name)
//                }
//                else{
//                    val now = System.currentTimeMillis()
//                    val date = Date(now)
//                    val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
//                    val getTime = dateFormat.format(date)
//                    mSocket.emit("send_message", send_edit.text.toString() + "," + id + ","+ your_id + "," + getTime + "," + "no_save"+"," + name)
//                }
//            }
//        }



        yourprofile.setImageBitmap(yourImage)
//        myprofile.setImageBitmap(myImage)
        var chatLogs = ArrayList<chat>()
        recyclerView.adapter = chatAdapter(this, chatLogs)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mSocket.on("first_get", Emitter.Listener { args ->
            val listType = object : TypeToken<ArrayList<chat?>?>() {}.type
            val myExList: ArrayList<chat> =
                Gson().fromJson<ArrayList<chat>>(args[0].toString(), listType)
            chatLogs = myExList
            runOnUiThread{
                recyclerView.adapter = chatAdapter(this, chatLogs)
                recyclerView.scrollToPosition(chatLogs.size - 1);
            }
        })
        mSocket.on("send_message", Emitter.Listener { args ->

            // Display the first 500 characters of the response string.
            val listType = object : TypeToken<ArrayList<chat?>?>() {}.type
            val myExList: ArrayList<chat> =
                Gson().fromJson<ArrayList<chat>>(args[0].toString(), listType)
            chatLogs = myExList
            runOnUiThread{
                recyclerView.adapter = chatAdapter(this, chatLogs)
                recyclerView.scrollToPosition(chatLogs.size - 1);
            }



        })
        mSocket.on("chat_history", Emitter.Listener { args ->
            Log.d("heyhye", ""+args.get(0))
            val listType = object : TypeToken<ArrayList<chat?>?>() {}.type
            val myExList: ArrayList<chat> =
                Gson().fromJson<ArrayList<chat>>(args[0].toString(), listType)
            chatLogs = myExList
            runOnUiThread{
                recyclerView.adapter = chatAdapter(this, chatLogs)
                recyclerView.scrollToPosition(chatLogs.size - 1);
            }
        })

        viewTv.setOnClickListener {
            setXMLToggle(false)
        }
        subScriberTv.setOnClickListener {
            setXMLToggle(true)
        }

//        send_edit.setFocusable(true);
//        send_edit.setFocusableInTouchMode(true);
//        mytextwrapper.setOnClickListener {
//            send_edit.requestFocus()
//            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//        }



    }
    private fun setXMLToggle(isViewClicked: Boolean) {

        if (isViewClicked) {
            viewTv.setTextColor(Color.GRAY)
            viewTv.setBackgroundResource(0)
            subScriberTv.setTextColor(resources.getColor(R.color.gray))
            subScriberTv.setBackgroundResource(R.drawable.item_bg_on)
        } else {
            viewTv.setTextColor(resources.getColor(R.color.gray))
            viewTv.setBackgroundResource(R.drawable.item_bg_on)
            subScriberTv.setTextColor(Color.GRAY)
            subScriberTv.setBackgroundResource(0)
        }
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

    fun stopRecognition() {
        //end of chat - confirm??
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

    override fun onBackPressed() {
        val tempTime = System.currentTimeMillis()
        val intervalTime: Long = tempTime - presstime
        if (intervalTime in 0..TIMEOUT) {
            finish()
        } else {
            presstime = tempTime
            stopRecognition()
            Toast.makeText(applicationContext, "한번더 누르시면 대화가 종료됩니다", Toast.LENGTH_SHORT).show()
        }
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
//            holder.your_image.setImageBitmap(yourImage)
            holder.your_text.text = arrayList.get(position).text
//            holder.your_name.text = arrayList.get(position).name
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
//        val your_image = itemView.findViewById<ImageView>(R.id.chat_your_profile)
        val your_text = itemView.findViewById<TextView>(R.id.chat_your_text)
        val your_time = itemView.findViewById<TextView>(R.id.chat_your_time)
//        val your_name = itemView.findViewById<TextView>(R.id.chat_your_name)
    }
}