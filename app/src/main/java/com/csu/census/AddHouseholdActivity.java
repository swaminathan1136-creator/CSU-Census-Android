package com.csu.census;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.csu.census.data.AppStore;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddHouseholdActivity extends AppCompatActivity {

    private EditText headName;
    private EditText address;
    private EditText ward;

    private LinearLayout membersContainer;

    private final ArrayList<MemberFields> memberFields =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(
                getWindow(),
                false
        );

        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_add_household);

        View root = findViewById(android.R.id.content);

        ViewCompat.setOnApplyWindowInsetsListener(
                root,
                (v, insets) -> {

                    Insets bars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            bars.left,
                            bars.top,
                            bars.right,
                            bars.bottom
                    );

                    return insets;
                }
        );

        findViewById(R.id.btnBack)
                .setOnClickListener(v -> finish());

        headName = findViewById(R.id.etHeadName);
        address = findViewById(R.id.etAddress);
        ward = findViewById(R.id.etWard);

        membersContainer =
                findViewById(R.id.membersContainer);

        // Start with one family member form.
        addMemberForm();

        findViewById(R.id.btnAddMember)
                .setOnClickListener(v -> addMemberForm());

        findViewById(R.id.btnSaveHousehold)
                .setOnClickListener(v -> saveHousehold());
    }

    private void addMemberForm() {

        int number = memberFields.size() + 1;

        MaterialCardView card =
                new MaterialCardView(this);

        card.setRadius(dp(16));
        card.setCardElevation(dp(1));
        card.setStrokeWidth(dp(1));

        card.setStrokeColor(
                Color.rgb(229, 236, 243)
        );

        card.setCardBackgroundColor(Color.WHITE);

        LinearLayout body = new LinearLayout(this);

        body.setOrientation(LinearLayout.VERTICAL);

        body.setPadding(
                dp(16),
                dp(14),
                dp(16),
                dp(16)
        );

        card.addView(body);

        TextView title = new TextView(this);

        title.setText("Family Member " + number);
        title.setTextSize(16);
        title.setTextColor(Color.rgb(18, 48, 78));

        title.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        body.addView(title);

        MemberFields fields = new MemberFields();

        fields.name = addInput(
                body,
                "Full name",
                "Enter member name"
        );

        fields.age = addInput(
                body,
                "Age",
                "Enter age"
        );

        fields.gender = addInput(
                body,
                "Gender",
                "Male / Female / Other"
        );

        fields.relationship = addInput(
                body,
                "Relationship",
                "e.g. Spouse, Son, Daughter"
        );

        fields.occupation = addInput(
                body,
                "Occupation",
                "Enter occupation"
        );

        fields.education = addInput(
                body,
                "Education",
                "Enter education level"
        );

        memberFields.add(fields);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.topMargin = dp(12);

        membersContainer.addView(card, params);
    }

    private EditText addInput(
            LinearLayout parent,
            String label,
            String hint
    ) {

        TextView caption = new TextView(this);

        caption.setText(label);
        caption.setTextColor(Color.rgb(18, 48, 78));
        caption.setTextSize(13);

        caption.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        LinearLayout.LayoutParams captionParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        captionParams.topMargin = dp(12);

        parent.addView(caption, captionParams);

        EditText input = new EditText(this);

        input.setSingleLine(true);
        input.setTextSize(14);
        input.setHint(hint);

        input.setPadding(
                dp(12),
                dp(8),
                dp(12),
                dp(8)
        );

        input.setBackgroundResource(
                R.drawable.bg_input
        );

        parent.addView(
                input,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(48)
                )
        );

        return input;
    }

    private void saveHousehold() {

        String name = headName.getText()
                .toString()
                .trim();

        String fullAddress = address.getText()
                .toString()
                .trim();

        String wardNumber = ward.getText()
                .toString()
                .trim();

        if (name.isEmpty()
                || fullAddress.isEmpty()
                || wardNumber.isEmpty()) {

            Toast.makeText(
                    this,
                    "Enter the household head, address and ward number",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        JSONArray family = new JSONArray();

        for (MemberFields fields : memberFields) {

            String memberName = fields.name.getText()
                    .toString()
                    .trim();

            if (memberName.isEmpty()) {

                Toast.makeText(
                        this,
                        "Enter the name of every family member",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            try {

                JSONObject member = new JSONObject();

                member.put("name", memberName);

                member.put(
                        "age",
                        fields.age.getText().toString().trim()
                );

                member.put(
                        "gender",
                        fields.gender.getText().toString().trim()
                );

                member.put(
                        "relationship",
                        fields.relationship.getText().toString().trim()
                );

                member.put(
                        "occupation",
                        fields.occupation.getText().toString().trim()
                );

                member.put(
                        "education",
                        fields.education.getText().toString().trim()
                );

                family.put(member);

            } catch (Exception ignored) {
            }
        }

        // Save household and family information.
        AppStore.addHousehold(
                this,
                name,
                fullAddress,
                wardNumber,
                family
        );

        Toast.makeText(
                this,
                "Household saved successfully",
                Toast.LENGTH_SHORT
        ).show();

        setResult(RESULT_OK);

        // Return to the household list.
        finish();
    }

    private int dp(int value) {

        return Math.round(
                value * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }

    private static class MemberFields {

        EditText name;
        EditText age;
        EditText gender;
        EditText relationship;
        EditText occupation;
        EditText education;
    }
}