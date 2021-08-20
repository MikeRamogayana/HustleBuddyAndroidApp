package com.hustlebuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class ResetPasswordActivity extends AppCompatActivity {

    int vendorId;
    int code;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Intent intent = getIntent();
        vendorId = intent.getIntExtra("vendorId", 0);
        code = intent.getIntExtra("code", 0);
        email = intent.getStringExtra("email");
    }
}