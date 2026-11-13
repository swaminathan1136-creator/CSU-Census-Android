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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_household_detail);

        View root = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        householdId = getIntent().getStringExtra("household_id");
        if (householdId == null) {
            finish();
            return;
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        bindData();
    }

    private void bindData() {
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
                h.optString("phone").isEmpty() ? "Not provided" : h.optString("phone")
        );
        ((TextView) findViewById(R.id.tvDetailDate)).setText(h.optString("date"));
        ((TextView) findViewById(R.id.tvDetailNotes)).setText(
                h.optString("notes").isEmpty() ? "No notes added." : h.optString("notes")
        );

        String status = h.optString("status");
        TextView statusView = findViewById(R.id.tvDetailStatus);
        statusView.setText(status);

        switch (status) {
            case "Submitted":
                statusView.setBackgroundResource(R.drawable.bg_status_submitted);
                statusView.setTextColor(Color.parseColor("#1F8A5B"));
                break;
            case "In Review":
                statusView.setBackgroundResource(R.drawable.bg_status_review);
                statusView.setTextColor(Color.parseColor("#C4781A"));
                break;
            case "Returned":
                statusView.setBackgroundResource(R.drawable.bg_status_returned);
                statusView.setTextColor(Color.parseColor("#C44A5A"));
                break;
            default:
                statusView.setBackgroundResource(R.drawable.bg_status_draft);
                statusView.setTextColor(Color.parseColor("#5A6A7E"));
                break;
        }

        MaterialButton submitBtn = findViewById(R.id.btnSubmitHousehold);
        if ("Draft".equals(status) || "Returned".equals(status)) {
            submitBtn.setVisibility(View.VISIBLE);
            submitBtn.setOnClickListener(v -> {
                AppStore.submitHousehold(this, householdId);
                Toast.makeText(this, "Household submitted successfully", Toast.LENGTH_SHORT).show();
                bindData();
            });
        } else {
            submitBtn.setVisibility(View.GONE);
        }
    }
}