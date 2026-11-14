package com.csu.census;

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

public class NewPasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_new_password);
        View root = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets b = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(b.left, b.top, b.right, b.bottom);
            return insets;
        });
        String email = getIntent().getStringExtra("email");
        EditText pass = findViewById(R.id.etNewPassword);
        EditText confirm = findViewById(R.id.etConfirmPassword);
        PasswordToggle.bind(pass, findViewById(R.id.btnTogglePassword));
        PasswordToggle.bind(confirm, findViewById(R.id.btnToggleConfirm));
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSave).setOnClickListener(v -> {
            String p = pass.getText().toString();
            String c = confirm.getText().toString();
            if (p.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!p.equals(c)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            if (AppStore.resetPassword(this, email, p)) {
                Toast.makeText(this, "Password updated. Please sign in.", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Could not update password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}