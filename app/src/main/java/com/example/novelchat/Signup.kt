package com.example.novelchat

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class Signup : AppCompatActivity() {
    private lateinit var pic: ImageView
    private lateinit var imageUri : Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val id_enter = findViewById<EditText>(R.id.signup_id)
        val pass_enter = findViewById<EditText>(R.id.signup_pass)
        val pass_reenter = findViewById<EditText>(R.id.signup_repass)
        val upload = findViewById<ImageView>(R.id.upload)
        val confirm_but = findViewById<Button>(R.id.signup_confirm)
        val name_enter = findViewById<EditText>(R.id.signup_name)
        val context_enter = findViewById<EditText>(R.id.signup_context)


        pic = findViewById<ImageView>(R.id.temp_pic)
        upload.setOnClickListener {

            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, 200)
        }




        confirm_but.setOnClickListener {
            if (pass_enter.text.toString() != pass_reenter.text.toString()){
                Toast.makeText(this, "비밀번호를 다시 확인해주세요", Toast.LENGTH_SHORT).show()
            }
            else{

                val params = HashMap<String, String>()
                // 서버 접근
                val queue = Volley.newRequestQueue(this)
                val url = "http://192.249.18.125:80/signup"
                params.put("id", id_enter.text.toString())
                params.put("pass", pass_enter.text.toString())
                params.put("image", "image")
                params.put("name", name_enter.text.toString())
                params.put("context", context_enter.text.toString())
                // image, position
                // uri로 이미지 띄우기
                val bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri)
                    params["image"] = BitmapToString(bitmap)!!

                // image, position
                val jsonObject: JSONObject = JSONObject(params as Map<*, *>?)
                // Request a string response from the provided URL.
                // Request a string response from the provided URL.
                val Request: JsonObjectRequest = object : JsonObjectRequest(
                    Method.POST, url, null,
                    Response.Listener { // Display the first 500 characters of the response string.
                        Log.d("upload", "success")
                    },
                    Response.ErrorListener { Log.d("check", "got error") }
                ) {
                    override fun getBody(): ByteArray {
                        return jsonObject.toString().toByteArray()
                    }
                }
                Request.retryPolicy = DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )

                queue.add(Request)



                //val url = "http://192.249.18.125:80/signup/" + id_enter.text.toString() + "," + pass_enter.text.toString()

//                val request = object : StringRequest(
//                    Request.Method.GET,
//                    url, Response.Listener {
//
//                    }, Response.ErrorListener {
//
//                    }
//                ) {
//
//                }
//                request.retryPolicy = DefaultRetryPolicy(
//                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
//                    // 0 means no retry
//                    0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
//                    1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//                )
//                VolleySingleton.getInstance(this).addToRequestQueue(request)
                finish()
            }
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200){
            imageUri = data?.data!!
            pic.setImageURI(imageUri)
        }
    }
}

fun BitmapToString(bitmap: Bitmap): String? {
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos)
    val bytes = baos.toByteArray()
    return Base64.encodeToString(bytes, Base64.DEFAULT)
}

fun StringToBitmap(encodedString: String?): Bitmap? {
    return try {
        val encodeByte =
            Base64.decode(encodedString, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
    } catch (e: Exception) {
        e.message
        null
    }
}