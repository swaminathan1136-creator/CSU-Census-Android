package com.csu.census;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.csu.census.data.AppStore;
import com.csu.census.ui.PasswordToggle;

import java.util.UUID;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_signup);

        View root = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        EditText nameInput = findViewById(R.id.etName);
        EditText emailInput = findViewById(R.id.etEmail);
        EditText phoneInput = findViewById(R.id.etPhone);
        EditText passwordInput = findViewById(R.id.etNewPassword);
        EditText confirmPasswordInput = findViewById(R.id.etConfirmPassword);

        PasswordToggle.bind(passwordInput, findViewById(R.id.btnTogglePassword));
        PasswordToggle.bind(confirmPasswordInput, findViewById(R.id.btnToggleConfirm));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.tvBackLogin).setOnClickListener(v -> finish());

        findViewById(R.id.btnCreate).setOnClickListener(view -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();
            String password = passwordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty()
                    || password.isEmpty() || confirmPassword.isEmpty()) {
                showToast("Please complete all fields");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast("Enter a valid email address");
                return;
            }
            if (phone.length() < 10) {
                showToast("Enter a valid phone number");
                return;
            }
            if (password.length() < 6) {
                showToast("Password must be at least 6 characters");
                return;
            }
            if (!password.equals(confirmPassword)) {
                showToast("Passwords do not match");
                return;
            }

            // Auto-generate Collector ID
            String collectorId = "CSU" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

            boolean registered = AppStore.register(this, name, email, phone, collectorId, password);
            if (registered) {
                showToast("Account created. Your Collector ID is " + collectorId);
                finish();
            } else {
                showToast("Something went wrong. Please try again.");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}