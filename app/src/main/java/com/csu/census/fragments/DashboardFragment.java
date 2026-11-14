
package com.csu.census.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.csu.census.AddHouseholdActivity;
import com.csu.census.MainActivity;
import com.csu.census.R;
import com.csu.census.data.AppStore;

public class DashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(
                R.layout.fragment_dashboard,
                container,
                false
        );

        bindHeader(view);
        bindProgress(view);
        bindStats(view);
        bindActions(view);

        return view;
    }

    @Override
    public void onResume() {

        super.onResume();

        View view = getView();

        if (view != null) {

            bindProgress(view);
            bindStats(view);
        }
    }

    private void bindHeader(View view) {

        String name = AppStore.profile(
                requireContext(),
                "name"
        );

        if (name == null || name.trim().isEmpty()) {
            name = "Collector";
        }

        ((TextView) view.findViewById(R.id.tvGreeting))
                .setText("Welcome back,");

        ((TextView) view.findViewById(R.id.tvCollectorName))
                .setText(name);

        ((TextView) view.findViewById(R.id.tvAvatar))
                .setText(
                        name.trim()
                                .substring(0, 1)
                                .toUpperCase()
                );
    }

    private void bindProgress(View view) {

        int total = AppStore.households(
                requireContext()
        ).size();

        int submitted = AppStore.countStatus(
                requireContext(),
                "Submitted"
        );

        int percentage = total == 0
                ? 0
                : Math.round((submitted * 100f) / total);

        ((TextView) view.findViewById(R.id.tvProgressCount))
                .setText(
                        submitted + " of " + total +
                                " households submitted"
                );

        ((TextView) view.findViewById(R.id.tvCampaignPercent))
                .setText(percentage + "%");

        ProgressBar progressBar =
                view.findViewById(R.id.progressBar);

        progressBar.setMax(100);
        progressBar.setProgress(percentage);
    }

    private void bindStats(View view) {

        ((TextView) view.findViewById(R.id.tvHouseholds))
                .setText(
                        String.valueOf(
                                AppStore.households(
                                        requireContext()
                                ).size()
                        )
                );

        ((TextView) view.findViewById(R.id.tvSubmitted))
                .setText(
                        String.valueOf(
                                AppStore.countStatus(
                                        requireContext(),
                                        "Submitted"
                                )
                        )
                );

        ((TextView) view.findViewById(R.id.tvReview))
                .setText(
                        String.valueOf(
                                AppStore.countStatus(
                                        requireContext(),
                                        "In Review"
                                )
                        )
                );

        ((TextView) view.findViewById(R.id.tvReturned))
                .setText(
                        String.valueOf(
                                AppStore.countStatus(
                                        requireContext(),
                                        "Returned"
                                )
                        )
                );
    }

    private void bindActions(View view) {

        view.findViewById(R.id.btnAddHousehold)
                .setOnClickListener(button -> {

                    startActivity(
                            new Intent(
                                    requireContext(),
                                    AddHouseholdActivity.class
                            )
                    );
                });

        // View Households
        view.findViewById(R.id.btnViewHouseholds)
                .setOnClickListener(button -> {

                    ((MainActivity) requireActivity())
                            .openTab(R.id.nav_households);
                });

        // Submissions are now accessed through Households
        view.findViewById(R.id.btnSubmissions)
                .setOnClickListener(button -> {

                    ((MainActivity) requireActivity())
                            .openTab(R.id.nav_households);
                });

        // Open the new Leaderboard tab
        view.findViewById(R.id.btnLeaderboard)
                .setOnClickListener(button -> {

                    ((MainActivity) requireActivity())
                            .openTab(R.id.nav_leaderboard);
                });
    }
}
