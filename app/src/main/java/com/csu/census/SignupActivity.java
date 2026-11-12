
package com.csu.census;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.csu.census.data.AppStore;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        EditText nameInput =
            findViewById(R.id.etName);

        EditText emailInput =
            findViewById(R.id.etEmail);

        EditText collectorIdInput =
            findViewById(R.id.etNewCollectorId);

        EditText passwordInput =
            findViewById(R.id.etNewPassword);

        EditText confirmPasswordInput =
            findViewById(R.id.etConfirmPassword);

        findViewById(R.id.btnCreate).setOnClickListener(view -> {

            String name =
                nameInput.getText().toString().trim();

            String email =
                emailInput.getText().toString().trim();

            String collectorId =
                collectorIdInput.getText().toString().trim();

            String password =
                passwordInput.getText().toString();

            String confirmPassword =
                confirmPasswordInput.getText().toString();

            if (name.isEmpty()
                || email.isEmpty()
                || collectorId.isEmpty()
                || password.isEmpty()
                || confirmPassword.isEmpty()) {

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

            boolean registered = AppStore.register(
                this,
                name,
                email,
                collectorId,
                password
            );

            if (registered) {

                showToast("Account created. Please sign in.");
                finish();

            } else {

                showToast("Collector ID already exists");
            }
        });
        findViewById(R.id.btnBack)
                .setOnClickListener(view -> finish());
    }

    private void showToast(String message) {

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show();
    }
}
