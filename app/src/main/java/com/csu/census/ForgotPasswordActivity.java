package com.csu.census;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.csu.census.data.AppStore;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_password);

        EditText emailInput = findViewById(R.id.etResetEmail);
        EditText passwordInput = findViewById(R.id.etResetPassword);

        // Back button — must exist in activity_forgot_password.xml as @+id/btnBack
        findViewById(R.id.btnBack).setOnClickListener(view -> finish());

        findViewById(R.id.btnReset).setOnClickListener(view -> {

            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString();

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast("Enter a valid registered email address");
                return;
            }

            if (password.length() < 6) {
                showToast("Password must be at least 6 characters");
                return;
            }

            boolean updated = AppStore.resetPassword(this, email, password);

            if (updated) {
                showToast("Password updated on this device. Please sign in again.");
                finish();
            } else {
                showToast("No local account found for this email address");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}