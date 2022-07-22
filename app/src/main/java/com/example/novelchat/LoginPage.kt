package com.example.novelchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

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

            }
        }

        signup_button.setOnClickListener {


        }
    }
}