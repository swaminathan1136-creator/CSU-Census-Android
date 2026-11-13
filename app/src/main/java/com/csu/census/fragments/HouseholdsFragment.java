package com.csu.census.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import org.json.JSONObject;

import java.util.ArrayList;

public class HouseholdsFragment extends Fragment {

    private HouseholdAdapter adapter;
    private TextView emptyState;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_households, container, false);

        recyclerView = view.findViewById(R.id.rvHouseholds);
        emptyState = view.findViewById(R.id.tvEmpty);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new HouseholdAdapter(new ArrayList<>(), this::showHouseholdDetail);
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.btnAddHousehold)
                .setOnClickListener(v -> showAddDialog(requireContext(), this::refreshList));

        refreshList();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        ArrayList<JSONObject> list = AppStore.households(requireContext());
        adapter.update(list);
        boolean empty = list.isEmpty();
        emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void showHouseholdDetail(JSONObject household) {
        String id = household.optString("id");
        String name = household.optString("name");
        String status = household.optString("status");
        String address = household.optString("address");
        int members = household.optInt("members");
        String date = household.optString("date");

        Context context = requireContext();
        int pad = (int) (22 * context.getResources().getDisplayMetrics().density);

        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(pad, pad / 2, pad, pad / 2);

        content.addView(makeDetailRow(context, "ID", id));
        content.addView(makeDetailRow(context, "Address", address));
        content.addView(makeDetailRow(context, "Members", String.valueOf(members)));
        content.addView(makeDetailRow(context, "Status", status));
        content.addView(makeDetailRow(context, "Date", date));

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(name)
                .setView(content)
                .setPositiveButton("Close", null);

        if ("Draft".equals(status) || "Returned".equals(status)) {
            builder.setNeutralButton("Submit", (d, w) -> {
                AppStore.submitHousehold(context, id);
                refreshList();
                Toast.makeText(context, "Household submitted", Toast.LENGTH_SHORT).show();
            });
        }

        builder.show();
    }

    private TextView makeDetailRow(Context context, String label, String value) {
        TextView row = new TextView(context);
        row.setText(label + "  ·  " + value);
        row.setTextColor(Color.parseColor("#0E2542"));
        row.setTextSize(14f);
        row.setPadding(0, 10, 0, 10);
        return row;
    }

    public void showAddDialog(Context context, Runnable onDone) {
        LinearLayout form = new LinearLayout(context);
        form.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (20 * context.getResources().getDisplayMetrics().density);
        form.setPadding(pad, pad / 2, pad, pad / 2);

        form.addView(makeLabel(context, "Household name"));
        EditText nameInput = makeInput(context, "e.g. Sharma Family", InputType.TYPE_CLASS_TEXT);
        form.addView(nameInput);

        form.addView(makeLabel(context, "Address"));
        EditText addressInput = makeInput(context, "Street, area, city", InputType.TYPE_CLASS_TEXT);
        form.addView(addressInput);

        form.addView(makeLabel(context, "Number of members"));
        EditText membersInput = makeInput(context, "e.g. 4", InputType.TYPE_CLASS_NUMBER);
        form.addView(membersInput);

        new AlertDialog.Builder(context)
                .setTitle("Add household")
                .setView(form)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    String address = addressInput.getText().toString().trim();
                    String membersText = membersInput.getText().toString().trim();

                    if (name.isEmpty() || address.isEmpty() || membersText.isEmpty()) {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int members;
                    try {
                        members = Integer.parseInt(membersText);
                    } catch (NumberFormatException e) {
                        Toast.makeText(context, "Enter a valid member count", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    AppStore.addHousehold(context, name, address, members);
                    Toast.makeText(context, "Household added", Toast.LENGTH_SHORT).show();
                    if (onDone != null) {
                        onDone.run();
                    }
                })
                .show();
    }

    private TextView makeLabel(Context context, String text) {
        TextView label = new TextView(context);
        label.setText(text);
        label.setTextColor(Color.parseColor("#0E2542"));
        label.setTextSize(13f);
        label.setPadding(0, 14, 0, 6);
        return label;
    }

    private EditText makeInput(Context context, String hint, int inputType) {
        int pad = (int) (12 * context.getResources().getDisplayMetrics().density);
        EditText input = new EditText(context);
        input.setHint(hint);
        input.setInputType(inputType);
        input.setBackgroundResource(R.drawable.bg_input);
        input.setPadding(pad, pad, pad, pad);
        input.setSingleLine(true);
        input.setTextSize(15f);
        return input;
    }
}