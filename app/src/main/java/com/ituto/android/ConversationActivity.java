package com.ituto.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class ConversationActivity extends AppCompatActivity  {

    private SharedPreferences sharedPreferences;

    private RecyclerView recyclerConversation;
    private EditText txtEnterMessage;
    private Button btnSend;
    private ImageButton btnAttachment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

    }

    private void init() {

    }



}