package com.csu.census.fragments;

import android.app.AlertDialog;
import android.content.Context;
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
        View view = inflater.inflate(
                R.layout.fragment_households,
                container,
                false
        );

        recyclerView = view.findViewById(R.id.rvHouseholds);
        emptyState = view.findViewById(R.id.tvEmpty);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        adapter = new HouseholdAdapter(
                new ArrayList<>(),
                household -> {
                    String id = household.optString("id");
                    String status = household.optString("status");

                    if ("Draft".equals(status) || "Returned".equals(status)) {
                        new AlertDialog.Builder(requireContext())
                                .setTitle(household.optString("name"))
                                .setMessage(
                                        "Address: " + household.optString("address")
                                                + "\nMembers: " + household.optInt("members")
                                                + "\nStatus: " + status
                                                + "\n\nSubmit this household?"
                                )
                                .setNegativeButton("Cancel", null)
                                .setPositiveButton("Submit", (d, w) -> {
                                    AppStore.submitHousehold(requireContext(), id);
                                    refreshList();
                                    Toast.makeText(
                                            requireContext(),
                                            "Household submitted",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                })
                                .show();
                    } else {
                        new AlertDialog.Builder(requireContext())
                                .setTitle(household.optString("name"))
                                .setMessage(
                                        "Address: " + household.optString("address")
                                                + "\nMembers: " + household.optInt("members")
                                                + "\nStatus: " + status
                                                + "\nDate: " + household.optString("date")
                                )
                                .setPositiveButton("Close", null)
                                .show();
                    }
                }
        );

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
        ArrayList<JSONObject> list =
                AppStore.households(requireContext());
        adapter.update(list);

        boolean empty = list.isEmpty();
        emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    public void showAddDialog(Context context, Runnable onDone) {
        LinearLayout form = new LinearLayout(context);
        form.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (20 * context.getResources().getDisplayMetrics().density);
        form.setPadding(pad, pad / 2, pad, pad / 2);

        TextView nameLabel = new TextView(context);
        nameLabel.setText("Household name");
        nameLabel.setTextColor(0xFF0E2542);
        nameLabel.setPadding(0, 8, 0, 4);
        form.addView(nameLabel);

        EditText nameInput = new EditText(context);
        nameInput.setHint("e.g. Sharma Family");
        nameInput.setBackgroundResource(R.drawable.bg_input);
        nameInput.setPadding(pad / 2, pad / 2, pad / 2, pad / 2);
        nameInput.setSingleLine(true);
        form.addView(nameInput);

        TextView addressLabel = new TextView(context);
        addressLabel.setText("Address");
        addressLabel.setTextColor(0xFF0E2542);
        addressLabel.setPadding(0, 16, 0, 4);
        form.addView(addressLabel);

        EditText addressInput = new EditText(context);
        addressInput.setHint("Street, area, city");
        addressInput.setBackgroundResource(R.drawable.bg_input);
        addressInput.setPadding(pad / 2, pad / 2, pad / 2, pad / 2);
        addressInput.setSingleLine(true);
        form.addView(addressInput);

        TextView membersLabel = new TextView(context);
        membersLabel.setText("Number of members");
        membersLabel.setTextColor(0xFF0E2542);
        membersLabel.setPadding(0, 16, 0, 4);
        form.addView(membersLabel);

        EditText membersInput = new EditText(context);
        membersInput.setHint("e.g. 4");
        membersInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        membersInput.setBackgroundResource(R.drawable.bg_input);
        membersInput.setPadding(pad / 2, pad / 2, pad / 2, pad / 2);
        membersInput.setSingleLine(true);
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
}