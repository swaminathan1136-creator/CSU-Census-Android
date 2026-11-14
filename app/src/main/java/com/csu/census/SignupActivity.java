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

        EditText name = findViewById(R.id.etName);
        EditText email = findViewById(R.id.etEmail);
        EditText phone = findViewById(R.id.etPhone);
        EditText pass = findViewById(R.id.etNewPassword);
        EditText confirm = findViewById(R.id.etConfirmPassword);
        PasswordToggle.bind(pass, findViewById(R.id.btnTogglePassword));
        PasswordToggle.bind(confirm, findViewById(R.id.btnToggleConfirm));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.tvBackLogin).setOnClickListener(v -> finish());

        findViewById(R.id.btnCreate).setOnClickListener(v -> {
            String n = name.getText().toString().trim();
            String e = email.getText().toString().trim();
            String p = phone.getText().toString().trim();
            String pw = pass.getText().toString();
            String c = confirm.getText().toString();

            if (n.isEmpty() || e.isEmpty() || p.isEmpty() || pw.isEmpty() || c.isEmpty()) {
                toast("Please complete all fields");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
                toast("Enter a valid email");
                return;
            }
            if (p.length() < 10) {
                toast("Enter a valid phone number");
                return;
            }
            if (pw.length() < 6) {
                toast("Password must be at least 6 characters");
                return;
            }
            if (!pw.equals(c)) {
                toast("Passwords do not match");
                return;
            }

            String id = "CSU" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            if (AppStore.register(this, n, e, p, id, pw)) {
                toast("Account created. Collector ID: " + id);
                finish();
            } else {
                toast("Could not create account");
            }
        });
    }

    private void toast(String m) {
        Toast.makeText(this, m, Toast.LENGTH_LONG).show();
    }
}