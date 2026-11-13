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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_add_household);

        View root = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        EditText nameInput = findViewById(R.id.etHouseholdName);
        EditText addressInput = findViewById(R.id.etAddress);
        EditText membersInput = findViewById(R.id.etMembers);
        EditText phoneInput = findViewById(R.id.etPhone);
        EditText notesInput = findViewById(R.id.etNotes);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnSaveHousehold).setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String address = addressInput.getText().toString().trim();
            String membersStr = membersInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();
            String notes = notesInput.getText().toString().trim();

            if (name.isEmpty() || address.isEmpty() || membersStr.isEmpty()) {
                Toast.makeText(this, "Name, address and members are required", Toast.LENGTH_SHORT).show();
                return;
            }

            int members;
            try {
                members = Integer.parseInt(membersStr);
                if (members < 1) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Enter a valid number of members", Toast.LENGTH_SHORT).show();
                return;
            }

            AppStore.addHousehold(this, name, address, members, phone, notes);
            Toast.makeText(this, "Household added as Draft", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}