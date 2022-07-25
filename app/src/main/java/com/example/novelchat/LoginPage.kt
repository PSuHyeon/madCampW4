package com.example.novelchat

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class LoginPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        val login_button = findViewById<Button>(R.id.login_button)
        val signup_button = findViewById<Button>(R.id.signup_button)

        val id_text = findViewById<EditText>(R.id.id)
        val pass_text = findViewById<EditText>(R.id.password)

        login_button.setOnClickListener {
            if(id_text.text.isEmpty() || pass_text.text.isEmpty()){
                Toast.makeText(this, "빈칸을 모두 채워주세요", Toast.LENGTH_SHORT).show()
            }
            else{
                val url = "http://192.249.18.125:80/login/" + id_text.text.toString() + "," + pass_text.text.toString()

                val request = object : JsonObjectRequest(
                    Request.Method.GET,
                    url, null, Response.Listener {

                            val intent = Intent(this, friendsList::class.java)
                            intent.putExtra("id", it.getString("id"))
                            myImage = StringToBitmap(it.getString("image"))!!
                            intent.putExtra("context", it.getString("context"))
                            intent.putExtra("name", it.getString("name"))
                            startActivity(intent)

                    }, Response.ErrorListener {

                    }
                ) {

                }
                request.retryPolicy = DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                    // 0 means no retry
                    0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
                    1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
                VolleySingleton.getInstance(this).addToRequestQueue(request)
            }
        }

        signup_button.setOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)

        }
    }
}
class VolleySingleton constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: VolleySingleton? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: VolleySingleton(context).also {
                    INSTANCE = it
                }
            }
    }
    private val requestQueue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        Volley.newRequestQueue(context.applicationContext)
    }
    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }
}