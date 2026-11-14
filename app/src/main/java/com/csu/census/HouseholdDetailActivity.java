package com.csu.census;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.csu.census.data.AppStore;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

public class HouseholdDetailActivity extends AppCompatActivity {
    private String householdId;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_household_detail);
        View root = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets b = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(b.left, b.top, b.right, b.bottom);
            return insets;
        });
        householdId = getIntent().getStringExtra("household_id");
        if (householdId == null) {
            finish();
            return;
        }
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        bind();
    }

    private void bind() {
        JSONObject h = AppStore.getHousehold(this, householdId);
        if (h == null) {
            Toast.makeText(this, "Household not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        ((TextView) findViewById(R.id.tvDetailName)).setText(h.optString("name"));
        ((TextView) findViewById(R.id.tvDetailId)).setText(h.optString("id"));
        ((TextView) findViewById(R.id.tvDetailAddress)).setText(h.optString("address"));
        ((TextView) findViewById(R.id.tvDetailMembers)).setText(h.optInt("members") + " members");
        ((TextView) findViewById(R.id.tvDetailPhone)).setText(
                h.optString("phone").isEmpty() ? "Not provided" : h.optString("phone"));
        ((TextView) findViewById(R.id.tvDetailDate)).setText(h.optString("date"));
        ((TextView) findViewById(R.id.tvDetailNotes)).setText(
                h.optString("notes").isEmpty() ? "No notes." : h.optString("notes"));

        String status = h.optString("status");
        TextView sv = findViewById(R.id.tvDetailStatus);
        sv.setText(status);
        switch (status) {
            case "Submitted":
                sv.setBackgroundResource(R.drawable.bg_status_submitted);
                sv.setTextColor(Color.parseColor("#1F8A5B"));
                break;
            case "In Review":
                sv.setBackgroundResource(R.drawable.bg_status_review);
                sv.setTextColor(Color.parseColor("#C4781A"));
                break;
            case "Returned":
                sv.setBackgroundResource(R.drawable.bg_status_returned);
                sv.setTextColor(Color.parseColor("#C44A5A"));
                break;
            default:
                sv.setBackgroundResource(R.drawable.bg_status_draft);
                sv.setTextColor(Color.parseColor("#5A6A7E"));
        }

        MaterialButton btn = findViewById(R.id.btnSubmitHousehold);
        if ("Draft".equals(status) || "Returned".equals(status)) {
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(v -> {
                AppStore.submitHousehold(this, householdId);
                Toast.makeText(this, "Submitted", Toast.LENGTH_SHORT).show();
                bind();
            });
        } else {
            btn.setVisibility(View.GONE);
        }
    }
}