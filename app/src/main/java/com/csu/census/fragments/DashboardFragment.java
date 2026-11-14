package com.csu.census.fragments;

import android.content.Intent;
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

import com.csu.census.AddHouseholdActivity;
import com.csu.census.MainActivity;
import com.csu.census.R;
import com.csu.census.data.AppStore;

public class DashboardFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle s) {
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
        bindHeader(v);
        bindProgress(v);
        bindStats(v);
        bindActions(v);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        View v = getView();
        if (v != null) {
            bindProgress(v);
            bindStats(v);
        }
    }

    private void bindHeader(View v) {
        String name = AppStore.profile(requireContext(), "name");
        if (name == null || name.trim().isEmpty()) name = "Collector";
        ((TextView) v.findViewById(R.id.tvGreeting)).setText("Welcome back,");
        ((TextView) v.findViewById(R.id.tvCollectorName)).setText(name);
        ((TextView) v.findViewById(R.id.tvAvatar)).setText(name.trim().substring(0, 1).toUpperCase());
    }

    private void bindProgress(View v) {
        int total = AppStore.households(requireContext()).size();
        int submitted = AppStore.countStatus(requireContext(), "Submitted");
        int pct = total == 0 ? 0 : Math.round((submitted * 100f) / total);
        ((TextView) v.findViewById(R.id.tvProgressCount)).setText(submitted + " of " + total + " households submitted");
        ((TextView) v.findViewById(R.id.tvCampaignPercent)).setText(pct + "%");
        ProgressBar bar = v.findViewById(R.id.progressBar);
        bar.setMax(100);
        bar.setProgress(pct);
    }

    private void bindStats(View v) {
        ((TextView) v.findViewById(R.id.tvHouseholds)).setText(String.valueOf(AppStore.households(requireContext()).size()));
        ((TextView) v.findViewById(R.id.tvSubmitted)).setText(String.valueOf(AppStore.countStatus(requireContext(), "Submitted")));
        ((TextView) v.findViewById(R.id.tvReview)).setText(String.valueOf(AppStore.countStatus(requireContext(), "In Review")));
        ((TextView) v.findViewById(R.id.tvReturned)).setText(String.valueOf(AppStore.countStatus(requireContext(), "Returned")));
    }

    private void bindActions(View v) {
        v.findViewById(R.id.btnAddHousehold).setOnClickListener(b ->
                startActivity(new Intent(requireContext(), AddHouseholdActivity.class)));
        v.findViewById(R.id.btnViewHouseholds).setOnClickListener(b ->
                ((MainActivity) requireActivity()).openTab(R.id.nav_households));
        v.findViewById(R.id.btnSubmissions).setOnClickListener(b ->
                ((MainActivity) requireActivity()).openTab(R.id.nav_records));
        v.findViewById(R.id.btnLeaderboard).setOnClickListener(b ->
                Toast.makeText(requireContext(), "Leaderboard coming soon", Toast.LENGTH_SHORT).show());
    }
}