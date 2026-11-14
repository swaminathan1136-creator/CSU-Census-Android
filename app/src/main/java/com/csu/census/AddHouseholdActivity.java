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

public class AddHouseholdActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_add_household);
        View root = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets b = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(b.left, b.top, b.right, b.bottom);
            return insets;
        });
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        EditText name = findViewById(R.id.etName);
        EditText address = findViewById(R.id.etAddress);
        EditText members = findViewById(R.id.etMembers);
        EditText phone = findViewById(R.id.etPhone);
        EditText notes = findViewById(R.id.etNotes);
        findViewById(R.id.btnSave).setOnClickListener(v -> {
            String n = name.getText().toString().trim();
            String a = address.getText().toString().trim();
            String m = members.getText().toString().trim();
            if (n.isEmpty() || a.isEmpty() || m.isEmpty()) {
                Toast.makeText(this, "Name, address and members are required", Toast.LENGTH_SHORT).show();
                return;
            }
            int count;
            try {
                count = Integer.parseInt(m);
            } catch (Exception e) {
                Toast.makeText(this, "Enter a valid member count", Toast.LENGTH_SHORT).show();
                return;
            }
            AppStore.addHousehold(this, n, a, count,
                    phone.getText().toString().trim(),
                    notes.getText().toString().trim());
            Toast.makeText(this, "Household added", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}