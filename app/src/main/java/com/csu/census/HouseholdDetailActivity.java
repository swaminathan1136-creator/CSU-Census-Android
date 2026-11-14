
package com.csu.census;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.csu.census.data.AppStore;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

public class HouseholdDetailActivity extends AppCompatActivity {

    private String householdId;
    private LinearLayout familyContainer;

    private final int navy = Color.rgb(18, 48, 78);
    private final int muted = Color.rgb(105, 120, 140);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_household_detail);

        householdId = getIntent().getStringExtra("household_id");

        if (householdId == null || householdId.trim().isEmpty()) {
            finish();
            return;
        }

        familyContainer = findViewById(
                R.id.familyMembersContainer
        );

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        displayHousehold();
    }

    private void displayHousehold() {

        JSONObject household = AppStore.getHousehold(
                this,
                householdId
        );

        if (household == null) {
            Toast.makeText(
                    this,
                    "Household not found",
                    Toast.LENGTH_SHORT
            ).show();
            finish();
            return;
        }

        setText(R.id.tvDetailName,
                household.optString("name", "Household"));

        setText(R.id.tvDetailId,
                "Reference: " + household.optString("id", "-"));

        setText(R.id.tvDetailAddress,
                "Address: " + household.optString("address", "-"));

        setText(R.id.tvDetailWard,
                "Ward: " + household.optString("ward", "-"));

        setText(R.id.tvDetailMembers,
                "Total family members: "
                        + household.optInt("members", 0));

        setText(R.id.tvDetailPhone,
                "Phone: " + household.optString("phone", "Not provided"));

        setText(R.id.tvDetailDate,
                "Registered: " + household.optString("date", "-"));

        setText(R.id.tvDetailNotes,
                household.optString("notes", ""));

        String status = household.optString("status", "Draft");

        TextView statusView = findViewById(R.id.tvDetailStatus);
        statusView.setText(status);

        JSONArray members = household.optJSONArray("familyMembers");

        displayFamilyMembers(members);

        MaterialButton submitButton =
                findViewById(R.id.btnSubmitHousehold);

        boolean canSubmit =
                status.equalsIgnoreCase("Draft")
                        || status.equalsIgnoreCase("Returned");

        submitButton.setVisibility(
                canSubmit ? View.VISIBLE : View.GONE
        );

        submitButton.setOnClickListener(v -> {
            AppStore.submitHousehold(this, householdId);
            Toast.makeText(
                    this,
                    "Household submitted successfully",
                    Toast.LENGTH_SHORT
            ).show();
            displayHousehold();
        });
    }

    private void displayFamilyMembers(JSONArray members) {

        familyContainer.removeAllViews();

        if (members == null || members.length() == 0) {

            TextView empty = new TextView(this);
            empty.setText(
                    "No individual family-member details have been saved for this household. "
                            + "Please add the member information through the household registration form."
            );
            empty.setTextColor(muted);
            empty.setTextSize(14);
            empty.setPadding(12, 18, 12, 18);

            familyContainer.addView(empty);
            return;
        }

        for (int i = 0; i < members.length(); i++) {

            JSONObject member = members.optJSONObject(i);

            if (member == null) {
                continue;
            }

            MaterialCardView card = new MaterialCardView(this);

            card.setRadius(dp(16));
            card.setCardElevation(dp(1));
            card.setStrokeWidth(dp(1));
            card.setStrokeColor(Color.rgb(225, 234, 243));
            card.setCardBackgroundColor(Color.WHITE);

            LinearLayout content = new LinearLayout(this);
            content.setOrientation(LinearLayout.VERTICAL);
            content.setPadding(dp(16), dp(16), dp(16), dp(16));

            card.addView(content);

            TextView heading = new TextView(this);

            heading.setText(
                    (i + 1) + ". "
                            + member.optString("name", "Family Member")
            );

            heading.setTextSize(17);
            heading.setTextColor(navy);
            heading.setTypeface(null, Typeface.BOLD);

            content.addView(heading);

            addField(content, "Full Name",
                    member.optString("name", "-"));

            addField(content, "Age",
                    member.optString("age", "-"));

            addField(content, "Gender",
                    member.optString("gender", "-"));

            addField(content, "Relationship",
                    member.optString("relationship", "-"));

            addField(content, "Occupation",
                    member.optString("occupation", "-"));

            addField(content, "Education",
                    member.optString("education", "-"));

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            params.bottomMargin = dp(12);

            familyContainer.addView(card, params);
        }
    }

    private void addField(
            LinearLayout parent,
            String label,
            String value
    ) {

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);

        TextView fieldLabel = new TextView(this);
        fieldLabel.setText(label);
        fieldLabel.setTextColor(muted);
        fieldLabel.setTextSize(12);

        TextView fieldValue = new TextView(this);
        fieldValue.setText(
                value == null || value.trim().isEmpty()
                        ? "Not provided"
                        : value
        );

        fieldValue.setTextColor(navy);
        fieldValue.setTextSize(14);
        fieldValue.setTypeface(null, Typeface.BOLD);

        row.addView(fieldLabel);
        row.addView(fieldValue);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.topMargin = dp(12);

        parent.addView(row, params);
    }

    private void setText(int id, String value) {
        TextView text = findViewById(id);
        text.setText(value);
    }

    private int dp(int value) {
        return Math.round(
                value * getResources()
                        .getDisplayMetrics().density
        );
    }
}
