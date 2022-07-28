package com.example.novelchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class Addfriend : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addfriend)

        val enter_id = findViewById<EditText>(R.id.enter_id)
        val find_button = findViewById<Button>(R.id.find_button)
        val find_image = findViewById<ImageView>(R.id.find_image)
        val find_name = findViewById<TextView>(R.id.find_name)
        val find_context = findViewById<TextView>(R.id.find_context)
        val add_button = findViewById<Button>(R.id.find_add)
        val find_id = findViewById<TextView>(R.id.find_id)
        val found_friend = findViewById<com.google.android.material.card.MaterialCardView>(R.id.found_friend)

        add_button.setOnClickListener {

            if (find_id.text.toString() == "null"){
                Toast.makeText(this,"No user found", Toast.LENGTH_SHORT).show()
            }
            else{
                val queue = Volley.newRequestQueue(this)
                val url = "http://192.249.18.125:80/addfriend/" + id + "," + find_id.text.toString()

                val Request: StringRequest = object : StringRequest(
                    Method.GET, url,
                    Response.Listener {
                        Toast.makeText(this, "successfully added to friend list", Toast.LENGTH_SHORT).show()
                        intent.putExtra("done", "")
                        finish()
                    },
                    Response.ErrorListener {  }
                ) {

                }

                Request.retryPolicy = DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
                if (enter_id.text.toString() != id)
                queue.add(Request)
                else{
                    Toast.makeText(this, "나 자신은 영원한 인생의 친구입니다" , Toast.LENGTH_SHORT).show()
                }
            }
        }


        find_button.setOnClickListener {
            if (enter_id.text.isEmpty()){
                Toast.makeText(this, "아이디를 입력하세요", Toast.LENGTH_SHORT).show()
            }
            else{
                enter_id.onEditorAction(EditorInfo.IME_ACTION_DONE)
                val params = HashMap<String, String>()
                // 서버 접근
                val queue = Volley.newRequestQueue(this)
                val url = "http://192.249.18.125:80/findfriend"
                params["id"] = enter_id.text.toString()
                // image, position
                // uri로 이미지 띄우기

                // image, position
                val jsonObject: JSONObject = JSONObject(params as Map<*, *>?)
                // Request a string response from the provided URL.
                // Request a string response from the provided URL.
                val Request: JsonObjectRequest = object : JsonObjectRequest(
                    Method.POST, url, null,
                    Response.Listener { // Display the first 500 characters of the response string.
                        Log.d("upload", "success")

                        if (it.getString("success") == "fail"){
                            find_id.text = "null"
                        }
                        else{
                            find_id.text = it.getString("id")
                            find_image.setImageBitmap(StringToBitmap(it.getString("image")))
                            find_name.text = it.getString("name")
                            find_context.text = it.getString("context")
                            found_friend.visibility = View.VISIBLE
                            add_button.visibility = View.VISIBLE
                        }
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
            }
        }

    }




}