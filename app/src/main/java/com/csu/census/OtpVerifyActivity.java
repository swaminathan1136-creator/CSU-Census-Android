package com.csu.census;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class OtpVerifyActivity extends AppCompatActivity {

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_otp_verify);

        View root = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        email = getIntent().getStringExtra("email");
        if (email == null) email = "";

        TextView hint = findViewById(R.id.tvOtpHint);
        hint.setText("We sent a 6-digit code to " + email);

        EditText otpInput = findViewById(R.id.etOtp);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnVerifyOtp).setOnClickListener(v -> {
            String otp = otpInput.getText().toString().trim();
            if (otp.length() != 6) {
                Toast.makeText(this, "Enter the 6-digit OTP", Toast.LENGTH_SHORT).show();
                return;
            }
            // Demo OTP is always 123456
            if (!"123456".equals(otp)) {
                Toast.makeText(this, "Invalid OTP. Try 123456 for demo.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, NewPasswordActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
            finish();
        });
    }
}