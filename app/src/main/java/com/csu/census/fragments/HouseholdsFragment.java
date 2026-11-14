package com.csu.census.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csu.census.AddHouseholdActivity;
import com.csu.census.HouseholdDetailActivity;
import com.csu.census.R;
import com.csu.census.adapters.HouseholdAdapter;
import com.csu.census.data.AppStore;

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
    private TextView[] filters;

    private String selectedStatus = "All";

    private final String[] statuses = {
            "All",
            "Submitted",
            "In Review",
            "Returned"
    };

    private final int navy = Color.rgb(18, 48, 78);

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

        // Open the separate Add Household screen.
        view.findViewById(R.id.btnAddHousehold)
                .setOnClickListener(v -> {

                    Intent intent = new Intent(
                            requireContext(),
                            AddHouseholdActivity.class
                    );

                    startActivity(intent);
                });

        // Status filters.
        for (int i = 0; i < filters.length; i++) {

            final String status = statuses[i];

            filters[i].setOnClickListener(v -> {

                selectedStatus = status;

                updateFilters();
                refresh();
            });
        }

        // Search.
        searchInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
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

        if (filters == null) {
            return;
        }

        for (int i = 0; i < filters.length; i++) {

            boolean selected =
                    statuses[i].equals(selectedStatus);

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

        if (!isAdded()
                || recyclerView == null
                || searchInput == null) {
            return;
        }

        List<JSONObject> all =
                AppStore.households(requireContext());

        totalView.setText(all.size() + " Households");

        submittedView.setText(
                String.valueOf(
                        AppStore.countStatus(
                                requireContext(),
                                "Submitted"
                        )
                )
        );

        reviewView.setText(
                String.valueOf(
                        AppStore.countStatus(
                                requireContext(),
                                "In Review"
                        )
                )
        );

        returnedView.setText(
                String.valueOf(
                        AppStore.countStatus(
                                requireContext(),
                                "Returned"
                        )
                )
        );

        String query = searchInput.getText()
                .toString()
                .trim()
                .toLowerCase(Locale.ROOT);

        ArrayList<JSONObject> filtered = new ArrayList<>();

        for (JSONObject household : all) {

            String status = household.optString(
                    "status",
                    "Draft"
            );

            if (!"All".equals(selectedStatus)
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

        // Every household card opens its own detail screen.
        adapter = new HouseholdAdapter(
                filtered,
                household -> {

                    Intent intent = new Intent(
                            requireContext(),
                            HouseholdDetailActivity.class
                    );

                    intent.putExtra(
                            "household_id",
                            household.optString("id")
                    );

                    startActivity(intent);
                }
        );

        recyclerView.setAdapter(adapter);

        boolean empty = filtered.isEmpty();

        emptyView.setVisibility(
                empty ? View.VISIBLE : View.GONE
        );

        recyclerView.setVisibility(
                empty ? View.GONE : View.VISIBLE
        );

        emptyView.setText(
                all.isEmpty()
                        ? "No households added yet. Tap New Household to begin."
                        : "No households match your search or filter."
        );

        updateFilters();
    }
}