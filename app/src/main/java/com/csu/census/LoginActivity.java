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

        EditText idInput = findViewById(R.id.etCollectorId);
        EditText passInput = findViewById(R.id.etPassword);
        PasswordToggle.bind(passInput, findViewById(R.id.btnTogglePassword));

        findViewById(R.id.btnLogin).setOnClickListener(v -> {
            String id = idInput.getText().toString().trim();
            String pass = passInput.getText().toString();
            if (id.isEmpty() || pass.isEmpty()) {
                toast("Enter collector ID and password");
                return;
            }
            if (AppStore.signIn(this, id, pass)) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                toast("Invalid collector ID or password");
            }
        });

        findViewById(R.id.tvCreateAccount).setOnClickListener(v ->
                startActivity(new Intent(this, SignupActivity.class)));
        findViewById(R.id.tvForgot).setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));
    }

    private void toast(String m) {
        Toast.makeText(this, m, Toast.LENGTH_SHORT).show();
    }
}