package com.example.novelchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Locale;
import java.util.Objects;

public class PushTest extends AppCompatActivity {

    private EditText title, message, token;
    private Button sent_btn, single_btn;

    private EditText tvToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_test);

        FirebaseMessaging.getInstance().subscribeToTopic("all");

        tvToken = findViewById(R.id.tvToken);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            String token = Objects.requireNonNull(task.getResult()).getToken();
                            tvToken.setText(token);
                        }

                    }
                });

        title = findViewById(R.id.title_id);
        message = findViewById(R.id.message_id);
        token = findViewById(R.id.token_id);

        sent_btn = findViewById(R.id.sent_btn);
        single_btn = findViewById(R.id.single_btn);

        sent_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!title.getText().toString().isEmpty() && !message.getText().toString().isEmpty()) {
                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/all",
                            title.getText().toString(),
                            message.getText().toString(),
                            getApplicationContext(),
                            PushTest.this);
                    notificationsSender.SendNotifications();
                } else {
                    Toast.makeText(PushTest.this, "Input Missing", Toast.LENGTH_SHORT).show();
                }

            }
        });

        single_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!title.getText().toString().isEmpty() && !message.getText().toString().isEmpty()
                    && !token.getText().toString().isEmpty()) {

                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
                            token.getText().toString(),
                            title.getText().toString(),
                            message.getText().toString(),
                            getApplicationContext(),
                            PushTest.this);
                    notificationsSender.SendNotifications();
                } else {
                    Toast.makeText(PushTest.this, "Token Missing", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}