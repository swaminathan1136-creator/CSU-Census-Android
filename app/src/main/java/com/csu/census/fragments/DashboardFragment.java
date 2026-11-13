package com.csu.census.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

        bindCollectorHeader(view);
        bindCampaignProgress(view);
        bindActivityStatistics(view);
        bindQuickActions(view);

        return view;
    }

    @Override
    public void onResume() {

        super.onResume();

        View currentView = getView();

        if (currentView != null) {

            bindCampaignProgress(currentView);
            bindActivityStatistics(currentView);
        }
    }

    private void bindCollectorHeader(View view) {

        String collectorName = AppStore.profile(
                requireContext(),
                "name"
        );

        if (collectorName == null
                || collectorName.trim().isEmpty()) {

            collectorName = "Collector";
        }

        String firstLetter = collectorName.trim()
                .substring(0, 1)
                .toUpperCase();

        TextView nameView = view.findViewById(R.id.tvCollectorName);
        TextView avatarView = view.findViewById(R.id.tvAvatar);
        TextView greetingView = view.findViewById(R.id.tvGreeting);

        greetingView.setText("Welcome back,");
        nameView.setText(collectorName);
        avatarView.setText(firstLetter);
    }

    private void bindCampaignProgress(View view) {

        int totalHouseholds = AppStore.households(requireContext()).size();

        int submittedHouseholds = AppStore.countStatus(
                requireContext(),
                "Submitted"
        );

        int progressPercent = totalHouseholds == 0
                ? 0
                : Math.round((submittedHouseholds * 100f) / totalHouseholds);

        TextView countView = view.findViewById(R.id.tvProgressCount);
        TextView percentView = view.findViewById(R.id.tvCampaignPercent);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);

        countView.setText(
                submittedHouseholds + " of " + totalHouseholds + " households submitted"
        );
        percentView.setText(progressPercent + "%");
        progressBar.setMax(100);
        progressBar.setProgress(progressPercent);
    }

    private void bindActivityStatistics(View view) {

        int total = AppStore.households(requireContext()).size();
        int submitted = AppStore.countStatus(requireContext(), "Submitted");
        int inReview = AppStore.countStatus(requireContext(), "In Review");
        int returned = AppStore.countStatus(requireContext(), "Returned");

        ((TextView) view.findViewById(R.id.tvHouseholds)).setText(String.valueOf(total));
        ((TextView) view.findViewById(R.id.tvSubmitted)).setText(String.valueOf(submitted));
        ((TextView) view.findViewById(R.id.tvReview)).setText(String.valueOf(inReview));
        ((TextView) view.findViewById(R.id.tvReturned)).setText(String.valueOf(returned));
    }

    private void bindQuickActions(View view) {

        view.findViewById(R.id.btnAddHousehold)
                .setOnClickListener(button -> {
                    HouseholdsFragment formFragment = new HouseholdsFragment();
                    formFragment.showAddDialog(
                            requireContext(),
                            () -> ((MainActivity) requireActivity()).openTab(R.id.nav_households)
                    );
                });

        view.findViewById(R.id.btnViewHouseholds)
                .setOnClickListener(button ->
                        ((MainActivity) requireActivity()).openTab(R.id.nav_households)
                );

        view.findViewById(R.id.btnSubmissions)
                .setOnClickListener(button ->
                        ((MainActivity) requireActivity()).openTab(R.id.nav_records)
                );

        view.findViewById(R.id.btnLeaderboard)
                .setOnClickListener(button ->
                        Toast.makeText(
                                requireContext(),
                                "Leaderboard will be available when the CSU service is connected.",
                                Toast.LENGTH_LONG
                        ).show()
                );
    }
}