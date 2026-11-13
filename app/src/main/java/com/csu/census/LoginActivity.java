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

import com.csu.census.data.AppStore;
import com.csu.census.ui.PasswordToggle;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_login);

        View root = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        EditText collectorIdInput = findViewById(R.id.etCollectorId);
        EditText passwordInput = findViewById(R.id.etPassword);

        PasswordToggle.bind(passwordInput, findViewById(R.id.btnTogglePassword));

        findViewById(R.id.btnLogin).setOnClickListener(view -> {
            String collectorId = collectorIdInput.getText().toString().trim();
            String password = passwordInput.getText().toString();

            if (collectorId.isEmpty() || password.isEmpty()) {
                showToast("Enter your collector ID and password");
                return;
            }

            if (AppStore.signIn(this, collectorId, password)) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                showToast("Invalid collector ID or password");
            }
        });

        findViewById(R.id.tvCreateAccount).setOnClickListener(view ->
                startActivity(new Intent(this, SignupActivity.class))
        );

        findViewById(R.id.tvForgot).setOnClickListener(view ->
                startActivity(new Intent(this, ForgotPasswordActivity.class))
        );
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}