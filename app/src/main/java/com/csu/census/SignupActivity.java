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
        EditText collectorIdInput = findViewById(R.id.etNewCollectorId);
        EditText passwordInput = findViewById(R.id.etNewPassword);
        EditText confirmPasswordInput = findViewById(R.id.etConfirmPassword);

        findViewById(R.id.btnCreate).setOnClickListener(view -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String collectorId = collectorIdInput.getText().toString().trim();
            String password = passwordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();

            if (name.isEmpty() || email.isEmpty() || collectorId.isEmpty()
                    || password.isEmpty() || confirmPassword.isEmpty()) {
                showToast("Please complete all fields");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast("Enter a valid email address");
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

            boolean registered = AppStore.register(this, name, email, collectorId, password);
            if (registered) {
                showToast("Account created. Please sign in.");
                finish();
            } else {
                showToast("Collector ID already exists");
            }
        });

        findViewById(R.id.btnBack).setOnClickListener(view -> finish());
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}