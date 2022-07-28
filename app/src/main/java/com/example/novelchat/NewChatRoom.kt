package com.example.novelchat

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.Dimension
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget
import com.google.android.material.card.MaterialCardView
import com.google.android.material.slider.Slider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.models.ChannelMediaOptions
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

lateinit var yourImage: Bitmap
//lateinit var myImage: Bitmap
lateinit var your_id: String
class NewChatRoom : AppCompatActivity(), RecognitionListener {

    private var speech: SpeechRecognizer? = null
    private var sttONOFF: Int = 0
    private val TIMEOUT: Long = 2000
    private var presstime: Long = 0
    lateinit var viewTv: TextView
    lateinit var subScriberTv :TextView
    lateinit var mytext :TextView
    lateinit var yourtext :TextView
    lateinit var yourState :ImageView
    lateinit var myState :ImageView
    lateinit var myStateText :TextView
    lateinit var yourName :TextView
    lateinit var your_name :String
    lateinit var stt_button :CardView
    lateinit var exitBtn : CardView
    var isCallMode :Boolean = false
    var speakerOn :Boolean = false

    // VOICE CHAT
    private val PERMISSION_REQ_ID_RECORD_AUDIO = 22
//    private val PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1

    // Fill the App ID of your project generated on Agora Console.
    private val APP_ID = "f87f901b9b6c4a6aa4b2bdf5edea1331"
    // Fill the temp token generated on Agora Console.
    lateinit var USERACCOUNT: String;
    lateinit var TOKEN: String;
    lateinit var CHANNEL: String;
    private var mRtcEngine: RtcEngine?= null
    private var mChannelMediaOptions: ChannelMediaOptions = ChannelMediaOptions();
    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
    }
    lateinit var mSocket: Socket;


    override fun onCreate(savedInstanceState: Bundle?) {

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1);

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_chat_room)
        viewTv = findViewById(R.id.viewTv)
        exitBtn = findViewById(R.id.exitBtn)
        subScriberTv = findViewById(R.id.subScriberTv)
        val recyclerView = findViewById< RecyclerView>(R.id.chat_recyclerView)
        mytext = findViewById<TextView>(R.id.my_text)
        val mytextwrapper = findViewById<MaterialCardView>(R.id.my_text_wrapper)
        yourtext = findViewById<TextView>(R.id.your_text)
        yourName = findViewById(R.id.your_name)
        val yourprofile = findViewById<ImageView>(R.id.your_image)
//        val myprofile = findViewById<ImageView>(R.id.my_image)
        val send_edit = findViewById<EditText>(R.id.send_edit_text)
        stt_button = findViewById(R.id.stt_button)
//        val send_button = findViewById<Button>(R.id.send_button)
        val save_check = findViewById<CheckBox>(R.id.save_check)
        USERACCOUNT = id;
        your_id = intent.getStringExtra("id1").toString()
        your_name = intent.getStringExtra("yourName").toString()
        yourState = findViewById(R.id.your_state)
        myState = findViewById(R.id.my_state)
        myStateText = findViewById(R.id.my_state_text)

        val textSizeSlider: Slider = findViewById(R.id.slider)
        textSizeSlider.addOnChangeListener{ slider, value, fromUser ->
            mytext.setTextSize(Dimension.SP, value.toFloat())
            // size도 말풍선 보낼 때 보내야함
        }


        mSocket= IO.socket("http://192.249.18.125:443")
        mSocket.connect()

        mSocket.emit("enter_room", id + "," + your_id)

        send_edit.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mSocket.emit("message_from", id + "," + your_id + "," + send_edit.text.toString())
                Log.d("send_edit text", send_edit.text.toString());
                if (send_edit.text.contains("\n")){
                    if(send_edit.text.isEmpty()){

                    }
                    else{
                        if (save_check.isChecked){
                            val now = System.currentTimeMillis()
                            val date = Date(now)
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                            val getTime = dateFormat.format(date)
                            mSocket.emit("send_message", send_edit.text.toString().split("\n").get(0) + "," + id + "," + your_id + "," + getTime + "," + "save" + "," + name + "," + mytext.getTextSize() / resources.displayMetrics.scaledDensity)

                            send_edit.setText("")
                        }
                        else{

                            val now = System.currentTimeMillis()
                            val date = Date(now)
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                            val getTime = dateFormat.format(date)
                            mSocket.emit("send_message", send_edit.text.toString().split("\n").get(0) + "," + id + ","+ your_id + "," + getTime + "," + "no_save"+"," + name+ "," + mytext.getTextSize() / resources.displayMetrics.scaledDensity)
                            send_edit.setText("")
                        }
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                return
            }
        })

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
        yourName.text = your_name
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
            Log.d("click", "false")
            setXMLToggle(false)
            mSocket.emit("tofalse", id + "," + your_id)
        }
        subScriberTv.setOnClickListener {
            Log.d("click", "true")
            setXMLToggle(true)
            mSocket.emit("totrue", id + "," + your_id)
        }

        exitBtn.setOnClickListener {
            finish()
        }

        send_edit.isFocusable = true;
//        send_edit.setFocusableInTouchMode(true);
        mytextwrapper.setOnClickListener {
            send_edit.requestFocus()
//            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            val imm: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(send_edit, InputMethodManager.SHOW_IMPLICIT)
        }

        //VOICE CHAT
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

        mSocket.on("toggle", Emitter.Listener {
            args ->
            runOnUiThread {
                if (args.get(0) == "true"){
                    setXMLToggle(true)
                }
                else{
                    setXMLToggle(false)
                }
            }

        })

        mSocket.on("image_change", Emitter.Listener{
            args ->
            Log.d("image_change", ""+args)
                runOnUiThread {
                    if (isCallMode){
                        if (args.get(0) == "off"){
//                            yourState.setImageResource(R.drawable.ic_baseline_volume_off_24)
                            yourState.visibility = View.GONE
                        }
                        else{
//                            yourState.setImageResource(R.drawable.ic_baseline_volume_up_24)
                            yourState.visibility = View.VISIBLE
                            Glide.with(this).load(R.raw.blikingred).into(yourState)
                        }
                }
                    else{
                        if (args.get(0) == "off"){
//                            yourState.setImageResource(R.drawable.ic_baseline_mic_off_24)
                            yourState.visibility = View.GONE
                        }
                        else{
//                            yourState.setImageResource(R.drawable.ic_baseline_mic_24)
                            yourState.visibility = View.VISIBLE
                            Glide.with(this).load(R.raw.blikingred).into(yourState)
                        }
                    }
        }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mRtcEngine?.leaveChannel()
        RtcEngine.destroy()
    }

//    lateinit var mytext :TextView
//    lateinit var yourtext :TextView
//    lateinit var yourState :ImageView
//    lateinit var myState :ImageView
//    lateinit var  :TextView

    private fun setXMLToggle(isViewClicked: Boolean) {
        if (isViewClicked) {
            viewTv.setTextColor(Color.GRAY)
            viewTv.setBackgroundResource(0)
            subScriberTv.setTextColor(resources.getColor(R.color.gray))
            subScriberTv.setBackgroundResource(R.drawable.item_bg_on)
            //TODO 현재 모드에 따라 반영 마이크 끄기 등
            stopRecognition()
            isCallMode = true
//            mytext.text = "..."
//            yourtext.text = "..."
//            yourState.setImageResource(R.drawable.ic_baseline_volume_off_24)
//            Glide.with(this).load(R.raw.blikingred).override(560, 560).into(yourState)
//            Glide.with(this).load(R.raw.blikingred).into(yourState)
            stt_button.setCardBackgroundColor(resources.getColor(R.color.lightgray))
            myState.setImageResource(R.drawable.ic_baseline_mic_off_24)
            myStateText.text = "OFF"
            mSocket.emit("voice_chat_init", USERACCOUNT + "," + your_id)
        } else {
            viewTv.setTextColor(resources.getColor(R.color.gray))
            viewTv.setBackgroundResource(R.drawable.item_bg_on)
            subScriberTv.setTextColor(Color.GRAY)
            subScriberTv.setBackgroundResource(0)
            //TODO 현재 모드에 따라 반영 마이크 끄기 등
            isCallMode = false
//            mytext.text = ""
//            yourtext.text = ""
//            yourState.setImageResource(R.drawable.ic_baseline_mic_off_24)
//            Glide.with(this).load(R.raw.blikingred).into(yourState)
            stt_button.setCardBackgroundColor(resources.getColor(R.color.lightgray))
            myState.setImageResource(R.drawable.ic_stt)
            myStateText.text = "OFF"
            mRtcEngine?.leaveChannel()
            RtcEngine.destroy()
        }
    }

    fun onClickSTT(view: android.view.View){
        if (!isCallMode) { // 문자 모드
            sttONOFF += 1
            sttONOFF %= 2
            if(sttONOFF == 0){
                stopRecognition()
                stt_button.setCardBackgroundColor(resources.getColor(R.color.lightgray))
                myState.setImageResource(R.drawable.ic_stt)
                myStateText.text = "OFF"
                mSocket.emit("micOff", id + "," + your_id)
            }
            else{
                startRecognition()
                stt_button.setCardBackgroundColor(resources.getColor(R.color.greenMain))
                myState.setImageResource(R.drawable.ic_stt)
                myStateText.text = "ON"
                mSocket.emit("micOn", id + "," + your_id)
            }
        } else { // 통화 모드
            speakerOn = !speakerOn
            if (!speakerOn) {
                mRtcEngine!!.adjustRecordingSignalVolume(0);
                stt_button.setCardBackgroundColor(resources.getColor(R.color.lightgray))
                myState.setImageResource(R.drawable.ic_baseline_mic_off_24)
                myStateText.text = "OFF"
                mSocket.emit("micOff", id + "," + your_id)
            } else {
                mRtcEngine!!.adjustRecordingSignalVolume(100);
                stt_button.setCardBackgroundColor(resources.getColor(R.color.greenMain))
                mytext.text = "..."
                myState.setImageResource(R.drawable.ic_baseline_mic_24)
                myStateText.text = "ON"
                mSocket.emit("micOn", id + "," + your_id)

            }
        }
    }

    fun startRecognition() {

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
        view.text = matches?.get(0)+"\n"
        // 여기서 말풍선 보내기
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
    }

    override fun onBeginningOfSpeech() {

    }

    override fun onEndOfSpeech() {
//        val view = findViewById<TextView>(R.id.send_edit_text)

        speech?.stopListening();
        Thread.sleep(500);
        startRecognition();
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
            stopRecognition()
            finish()

        } else {
            presstime = tempTime
            Toast.makeText(applicationContext, "한번더 누르시면 대화가 종료됩니다", Toast.LENGTH_SHORT).show()
        }
    }

    //Voice
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
        mRtcEngine!!.adjustRecordingSignalVolume(0);
        Log.d("errCode", errCode.toString())
        Log.d("TOKEN", TOKEN)
        Log.d("CHANNEL", CHANNEL)
    }
}
class chat(val name : String, val time : String, val text: String, val id: String, val size: Float, val stoid: String)
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
    fun View.setOnVeryLongClickListener(listener: () -> Unit) {
        setOnTouchListener(object : View.OnTouchListener {

            private val longClickDuration = 2000L
            private val handler = Handler()

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    handler.postDelayed({ listener.invoke() }, longClickDuration)
                } else if (event?.action == MotionEvent.ACTION_UP) {
                    handler.removeCallbacksAndMessages(null)
                }
                return true
            }
        })
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyHolder){
            holder.my_text.text = arrayList.get(position).text
            holder.my_time.text = arrayList.get(position).time
            holder.my_text.setTextSize(Dimension.SP, arrayList.get(position).size)
            if(arrayList.get(position).stoid == "saved"){
                holder.my_saved.text = "저장됨"
            }
            else{
                holder.my_saved.text = ""
            }

        }
        else if (holder is YourHolder){
//            holder.your_image.setImageBitmap(yourImage)
            holder.your_text.text = arrayList.get(position).text
//            holder.your_name.text = arrayList.get(position).name
            holder.your_time.text = arrayList.get(position).time
            holder.your_text.setTextSize(Dimension.SP, arrayList.get(position).size)
            if(arrayList.get(position).stoid == "saved"){
                holder.your_saved.text = "저장됨"
            }
            else{
                holder.your_saved.text = ""
            }
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
        val my_saved = itemView.findViewById<TextView>(R.id.saved_mine)
        init {
            itemView.setOnVeryLongClickListener {
                mSocket.emit("save_m", id +"," + your_id + "," + my_text.text.toString() + "," + my_time.text.toString())
                my_saved.text = "저장됨"
                Toast.makeText(context, "저장 완료!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class YourHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val your_image = itemView.findViewById<ImageView>(R.id.chat_your_profile)
        val your_text = itemView.findViewById<TextView>(R.id.chat_your_text)
        val your_time = itemView.findViewById<TextView>(R.id.chat_your_time)
        val your_saved = itemView.findViewById<TextView>(R.id.saved_yours)
//        val your_name = itemView.findViewById<TextView>(R.id.chat_your_name)
        init {
            itemView.setOnVeryLongClickListener {
                mSocket.emit("save_m", id +"," + your_id + "," + your_text.text.toString() + "," + your_time.text.toString())
                your_saved.text = "저장됨"
                Toast.makeText(context, "저장 완료!", Toast.LENGTH_SHORT).show()
            }
        }

    }
}