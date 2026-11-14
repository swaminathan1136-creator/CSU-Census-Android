package com.csu.census;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class OtpVerifyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_otp_verify);
        View root = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets b = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(b.left, b.top, b.right, b.bottom);
            return insets;
        });
        String email = getIntent().getStringExtra("email");
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        EditText otp = findViewById(R.id.etOtp);
        findViewById(R.id.btnVerify).setOnClickListener(v -> {
            if (otp.getText().toString().trim().length() < 4) {
                Toast.makeText(this, "Enter the 4-digit OTP", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(this, NewPasswordActivity.class);
            i.putExtra("email", email);
            startActivity(i);
            finish();
        });
    }
}