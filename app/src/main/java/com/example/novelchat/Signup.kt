package com.example.novelchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

class Signup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val id_enter = findViewById<EditText>(R.id.signup_id)
        val pass_enter = findViewById<EditText>(R.id.signup_pass)
        val pass_reenter = findViewById<EditText>(R.id.signup_repass)

        val confirm_but = findViewById<Button>(R.id.signup_confirm)

        confirm_but.setOnClickListener {
            if (pass_enter.text.toString() != pass_reenter.text.toString()){
                Toast.makeText(this, "비밀번호를 다시 확인해주세요", Toast.LENGTH_SHORT).show()
            }
            else{
                val url = "http://192.249.18.125:80/signup/" + id_enter.text.toString() + "," + pass_enter.text.toString()

                val request = object : StringRequest(
                    Request.Method.GET,
                    url, Response.Listener {

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

    }
}