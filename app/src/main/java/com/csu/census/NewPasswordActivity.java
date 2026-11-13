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

public class NewPasswordActivity extends AppCompatActivity {

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_new_password);

        View root = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        email = getIntent().getStringExtra("email");
        if (email == null) email = "";

        EditText newPass = findViewById(R.id.etNewPassword);
        EditText confirmPass = findViewById(R.id.etConfirmPassword);

        PasswordToggle.bind(newPass, findViewById(R.id.btnToggleNew));
        PasswordToggle.bind(confirmPass, findViewById(R.id.btnToggleConfirm));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnUpdatePassword).setOnClickListener(v -> {
            String p1 = newPass.getText().toString();
            String p2 = confirmPass.getText().toString();

            if (p1.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!p1.equals(p2)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean ok = AppStore.resetPassword(this, email, p1);
            if (ok) {
                Toast.makeText(this, "Password updated. Please sign in.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Unable to update password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}