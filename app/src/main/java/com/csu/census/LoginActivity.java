
package com.csu.census;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.csu.census.data.AppStore;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        EditText collectorIdInput =
            findViewById(R.id.etCollectorId);

        EditText passwordInput =
            findViewById(R.id.etPassword);

        findViewById(R.id.btnLogin).setOnClickListener(view -> {

            String collectorId =
                collectorIdInput.getText().toString().trim();

            String password =
                passwordInput.getText().toString();

            if (collectorId.isEmpty() || password.isEmpty()) {
                showToast("Enter your collector ID and password");
                return;
            }

            if (AppStore.signIn(this, collectorId, password)) {

                Intent intent = new Intent(
                    this,
                    MainActivity.class
                );

                startActivity(intent);
                finish();

            } else {

                showToast("Invalid collector ID or password");
            }
        });

        findViewById(R.id.tvCreateAccount)
            .setOnClickListener(view -> {

                Intent intent = new Intent(
                    this,
                    SignupActivity.class
                );

                startActivity(intent);
            });

        findViewById(R.id.tvForgot)
            .setOnClickListener(view -> {

                Intent intent = new Intent(
                    this,
                    ForgotPasswordActivity.class
                );

                startActivity(intent);
            });
    }

    private void showToast(String message) {

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show();
    }
}
