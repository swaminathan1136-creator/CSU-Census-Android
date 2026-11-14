
package com.csu.census.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csu.census.R;
import com.csu.census.adapters.HouseholdAdapter;
import com.csu.census.data.AppStore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HouseholdsFragment extends Fragment {

    private HouseholdAdapter adapter;
    private RecyclerView recyclerView;
    private EditText searchInput;
    private TextView emptyView;
    private TextView totalView;
    private TextView submittedView;
    private TextView reviewView;
    private TextView returnedView;

    private String selectedStatus = "All";

    private TextView[] filters;
    private final String[] statuses = {
            "All", "Submitted", "In Review", "Returned"
    };

    private final int navy = Color.rgb(18, 48, 78);
    private final int blue = Color.rgb(0, 102, 196);
    private final int muted = Color.rgb(116, 137, 161);
    private final int background = Color.rgb(241, 246, 251);

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(
                R.layout.fragment_households,
                container,
                false
        );

        recyclerView = view.findViewById(R.id.rvHouseholds);
        searchInput = view.findViewById(R.id.etHouseholdSearch);
        emptyView = view.findViewById(R.id.tvEmpty);

        totalView = view.findViewById(R.id.tvHouseholdTotal);
        submittedView = view.findViewById(R.id.tvSubmittedCount);
        reviewView = view.findViewById(R.id.tvReviewCount);
        returnedView = view.findViewById(R.id.tvReturnedCount);

        filters = new TextView[]{
                view.findViewById(R.id.filterAll),
                view.findViewById(R.id.filterSubmitted),
                view.findViewById(R.id.filterReview),
                view.findViewById(R.id.filterReturned)
        };

        recyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        recyclerView.setNestedScrollingEnabled(false);

        view.findViewById(R.id.btnAddHousehold)
                .setOnClickListener(v -> showRegistrationForm());

        for (int i = 0; i < filters.length; i++) {
            final String status = statuses[i];

            filters[i].setOnClickListener(v -> {
                selectedStatus = status;
                updateFilters();
                refresh();
            });
        }

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after
            ) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count
            ) {
                refresh();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        refresh();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void updateFilters() {
        for (int i = 0; i < filters.length; i++) {
            boolean selected = statuses[i].equals(selectedStatus);

            filters[i].setBackgroundResource(
                    selected
                            ? R.drawable.bg_filter_selected
                            : R.drawable.bg_filter_unselected
            );

            filters[i].setTextColor(
                    selected ? Color.WHITE : navy
            );

            filters[i].setTypeface(
                    null,
                    selected ? Typeface.BOLD : Typeface.NORMAL
            );
        }
    }

    private void refresh() {
        if (!isAdded() || recyclerView == null) {
            return;
        }

        Context context = requireContext();

        List<JSONObject> all = AppStore.households(context);

        totalView.setText(
                all.size() + " Households"
        );

        submittedView.setText(
                String.valueOf(
                        AppStore.countStatus(context, "Submitted")
                )
        );

        reviewView.setText(
                String.valueOf(
                        AppStore.countStatus(context, "In Review")
                )
        );

        returnedView.setText(
                String.valueOf(
                        AppStore.countStatus(context, "Returned")
                )
        );

        String query = searchInput.getText()
                .toString()
                .trim()
                .toLowerCase(Locale.ROOT);

        ArrayList<JSONObject> filtered = new ArrayList<>();

        for (JSONObject household : all) {

            String status = household.optString(
                    "status", "Draft"
            );

            if (!selectedStatus.equals("All")
                    && !selectedStatus.equalsIgnoreCase(status)) {
                continue;
            }

            String searchable =
                    household.optString("id") + " "
                            + household.optString("name") + " "
                            + household.optString("address") + " "
                            + household.optString("ward");

            if (!searchable.toLowerCase(Locale.ROOT)
                    .contains(query)) {
                continue;
            }

            filtered.add(household);
        }

        adapter = new HouseholdAdapter(
                filtered,
                this::showDetails
        );

        recyclerView.setAdapter(adapter);

        emptyView.setVisibility(
                filtered.isEmpty() ? View.VISIBLE : View.GONE
        );

        recyclerView.setVisibility(
                filtered.isEmpty() ? View.GONE : View.VISIBLE
        );

        emptyView.setText(
                all.isEmpty()
                        ? "No households added yet. Tap New Household to begin."
                        : "No households match your search or filter."
        );

        updateFilters();
    }

    private TextView createText(
            String value,
            int size,
            int color,
            boolean bold
    ) {
        TextView text = new TextView(requireContext());
        text.setText(value);
        text.setTextSize(size);
        text.setTextColor(color);

        if (bold) {
            text.setTypeface(null, Typeface.BOLD);
        }

        return text;
    }

    private LinearLayout createCard(
            LinearLayout parent,
            int padding
    ) {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(padding, padding, padding, padding);
        card.setBackgroundResource(R.drawable.bg_page_card);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 0, 0, 14);

        parent.addView(card, params);

        return card;
    }

    private void addInfoRow(
            LinearLayout parent,
            String label,
            String value
    ) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setOrientation(LinearLayout.HORIZONTAL);

        TextView left = createText(label, 13, muted, false);
        TextView right = createText(value, 13, navy, true);
        right.setGravity(Gravity.END);

        row.addView(left, new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1
        ));

        row.addView(right);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 8, 0, 8);

        parent.addView(row, params);
    }

    private void showDetails(JSONObject household) {

        ScrollView scroll = new ScrollView(requireContext());

        LinearLayout content = new LinearLayout(requireContext());
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(20, 16, 20, 24);
        content.setBackgroundColor(background);

        scroll.addView(content);

        String name = household.optString("name", "Household");
        String id = household.optString("id", "");
        String status = household.optString("status", "Draft");

        LinearLayout header = createCard(content, 20);

        header.addView(createText("⌂", 32, blue, true));
        header.addView(createText(name, 23, navy, true));

        TextView reference = createText(id, 12, muted, false);
        LinearLayout.LayoutParams refParams =
                new LinearLayout.LayoutParams(
                        -1, -2
                );
        refParams.topMargin = 6;
        header.addView(reference, refParams);

        TextView statusView = createText(status, 12, blue, true);
        LinearLayout.LayoutParams statusParams =
                new LinearLayout.LayoutParams(-2, -2);
        statusParams.topMargin = 12;
        header.addView(statusView, statusParams);

        LinearLayout info = createCard(content, 18);
        info.addView(createText("Household Details", 17, navy, true));

        addInfoRow(
                info,
                "Address",
                household.optString("address", "Not available")
        );

        addInfoRow(
                info,
                "Ward",
                household.optString("ward", "Not assigned")
        );

        addInfoRow(
                info,
                "Family Members",
                String.valueOf(household.optInt("members", 0))
        );

        LinearLayout membersCard = createCard(content, 18);
        membersCard.addView(
                createText("Family Member Details", 17, navy, true)
        );

        JSONArray members = household.optJSONArray("familyMembers");

        if (members == null || members.length() == 0) {
            TextView unavailable = createText(
                    "Individual family-member details are not available "
                            + "for this older household record.",
                    13,
                    muted,
                    false
            );

            LinearLayout.LayoutParams p =
                    new LinearLayout.LayoutParams(-1, -2);
            p.topMargin = 12;
            membersCard.addView(unavailable, p);
        } else {
            for (int i = 0; i < members.length(); i++) {
                JSONObject member = members.optJSONObject(i);

                if (member == null) {
                    continue;
                }

                LinearLayout memberCard = new LinearLayout(
                        requireContext()
                );

                memberCard.setOrientation(LinearLayout.VERTICAL);
                memberCard.setPadding(14, 14, 14, 14);
                memberCard.setBackgroundColor(background);

                LinearLayout.LayoutParams p =
                        new LinearLayout.LayoutParams(-1, -2);
                p.setMargins(0, 12, 0, 0);

                membersCard.addView(memberCard, p);

                memberCard.addView(createText(
                        member.optString("name", "Family Member"),
                        14,
                        navy,
                        true
                ));

                addInfoRow(
                        memberCard,
                        "Age",
                        member.optString("age", "-") + " years"
                );

                addInfoRow(
                        memberCard,
                        "Gender",
                        member.optString("gender", "-")
                );

                addInfoRow(
                        memberCard,
                        "Relationship",
                        member.optString("relationship", "-")
                );

                addInfoRow(
                        memberCard,
                        "Occupation",
                        member.optString("occupation", "-")
                );

                addInfoRow(
                        memberCard,
                        "Education",
                        member.optString("education", "-")
                );
            }
        }

        AlertDialog.Builder builder =
                new AlertDialog.Builder(requireContext())
                        .setTitle("Household Details")
                        .setView(scroll)
                        .setNegativeButton("Close", null);

        if (!"Submitted".equalsIgnoreCase(status)) {
            builder.setPositiveButton(
                    "Submit",
                    (dialog, which) -> {
                        AppStore.submitHousehold(
                                requireContext(), id
                        );
                        refresh();

                        Toast.makeText(
                                requireContext(),
                                "Household submitted locally",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
            );
        }

        builder.show();
    }

    private void showRegistrationForm() {

        ScrollView scroll = new ScrollView(requireContext());

        LinearLayout form = new LinearLayout(requireContext());
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(20, 16, 20, 20);
        form.setBackgroundColor(background);

        scroll.addView(form);

        EditText headName = addField(
                form, "Head of Household", "Enter full name"
        );

        EditText address = addField(
                form, "Household Address", "Enter address"
        );

        EditText ward = addField(
                form, "Ward Number", "Enter ward number"
        );

        LinearLayout membersContainer = new LinearLayout(
                requireContext()
        );

        membersContainer.setOrientation(LinearLayout.VERTICAL);
        form.addView(membersContainer);

        ArrayList<MemberFields> memberFields = new ArrayList<>();

        addMemberForm(membersContainer, memberFields, true);

        TextView addMember = createText(
                "+ Add Family Member",
                14,
                blue,
                true
        );

        addMember.setGravity(Gravity.CENTER);
        addMember.setPadding(14, 16, 14, 16);
        addMember.setBackgroundResource(R.drawable.bg_filter_unselected);

        LinearLayout.LayoutParams addParams =
                new LinearLayout.LayoutParams(-1, -2);
        addParams.setMargins(0, 14, 0, 14);
        form.addView(addMember, addParams);

        addMember.setOnClickListener(v ->
                addMemberForm(membersContainer, memberFields, false)
        );

        new AlertDialog.Builder(requireContext())
                .setTitle("Census Registration")
                .setView(scroll)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save Registration", (dialog, which) -> {

                    String name = headName.getText().toString().trim();
                    String fullAddress = address.getText().toString().trim();
                    String wardNumber = ward.getText().toString().trim();

                    if (name.isEmpty()
                            || fullAddress.isEmpty()
                            || wardNumber.isEmpty()) {
                        Toast.makeText(
                                requireContext(),
                                "Please complete the household information",
                                Toast.LENGTH_LONG
                        ).show();
                        return;
                    }

                    JSONArray family = new JSONArray();

                    for (MemberFields fields : memberFields) {
                        String memberName =
                                fields.name.getText().toString().trim();

                        if (memberName.isEmpty()) {
                            Toast.makeText(
                                    requireContext(),
                                    "Enter every family member's name",
                                    Toast.LENGTH_LONG
                            ).show();
                            return;
                        }

                        JSONObject member = new JSONObject();

                        try {
                            member.put("name", memberName);
                            member.put("age", fields.age.getText().toString());
                            member.put("gender", fields.gender.getText().toString());
                            member.put("relationship", fields.relationship.getText().toString());
                            member.put("occupation", fields.occupation.getText().toString());
                            member.put("education", fields.education.getText().toString());

                            family.put(member);
                        } catch (Exception ignored) {
                        }
                    }

                    AppStore.addHousehold(
                            requireContext(),
                            name,
                            fullAddress,
                            wardNumber,
                            family
                    );

                    refresh();

                    Toast.makeText(
                            requireContext(),
                            "Household saved successfully",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .show();
    }

    private EditText addField(
            LinearLayout parent,
            String label,
            String hint
    ) {
        TextView title = createText(label, 13, navy, true);

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(-1, -2);
        titleParams.setMargins(0, 10, 0, 6);

        parent.addView(title, titleParams);

        EditText input = new EditText(requireContext());
        input.setSingleLine(true);
        input.setTextSize(14);
        input.setHint(hint);
        input.setPadding(14, 10, 14, 10);
        input.setBackgroundResource(R.drawable.bg_input);

        parent.addView(input, new LinearLayout.LayoutParams(-1, -2));

        return input;
    }

    private void addMemberForm(
            LinearLayout parent,
            ArrayList<MemberFields> allFields,
            boolean first
    ) {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(16, 14, 16, 14);
        card.setBackgroundResource(R.drawable.bg_page_card);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(-1, -2);
        cardParams.setMargins(0, 14, 0, 0);

        parent.addView(card, cardParams);

        int number = allFields.size() + 1;

        card.addView(createText(
                "Member " + number,
                16,
                navy,
                true
        ));

        MemberFields fields = new MemberFields();

        fields.name = addField(
                card, "Member Name", "Enter full name"
        );

        fields.age = addField(
                card, "Age", "Enter age"
        );

        fields.gender = addField(
                card, "Gender", "Male / Female / Other"
        );

        fields.relationship = addField(
                card, "Relationship to Household Head", "e.g. Spouse"
        );

        fields.occupation = addField(
                card, "Occupation", "Enter occupation"
        );

        fields.education = addField(
                card, "Education", "Enter education level"
        );

        allFields.add(fields);
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
