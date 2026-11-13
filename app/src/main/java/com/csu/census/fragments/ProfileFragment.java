package com.csu.census.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.csu.census.LoginActivity;
import com.csu.census.ProfileDetailActivity;
import com.csu.census.R;
import com.csu.census.data.AppStore;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        bindProfileDetails(view);
        bindProfileActions(view);
        return view;
    }

    private void bindProfileDetails(View view) {
        String name = AppStore.profile(requireContext(), "name");
        String email = AppStore.profile(requireContext(), "email");
        String collectorId = AppStore.activeId(requireContext());

        if (name == null || name.trim().isEmpty()) {
            name = "Collector";
        }

        String firstLetter = name.trim().substring(0, 1).toUpperCase();

        ((TextView) view.findViewById(R.id.tvProfileAvatar)).setText(firstLetter);
        ((TextView) view.findViewById(R.id.tvProfileName)).setText(name);
        ((TextView) view.findViewById(R.id.tvProfileId)).setText(
                "CSU DATA COLLECTOR  •  " + collectorId
        );
        ((TextView) view.findViewById(R.id.tvProfileEmail)).setText(email);
    }

    private void bindProfileActions(View view) {
        view.findViewById(R.id.btnLeaderboardProfile).setOnClickListener(v ->
                openDetail("Leaderboard",
                        "Collector rankings and performance metrics will appear here once the CSU central service is connected.\n\nYou will be able to see your rank among other field collectors and track weekly/monthly targets."));

        view.findViewById(R.id.btnCertificates).setOnClickListener(v ->
                openDetail("My Certificates",
                        "Training certificates and completion badges issued by CSU will be listed on this page.\n\nCurrently offline – certificates will sync when you are online."));

        view.findViewById(R.id.btnHelp).setOnClickListener(v ->
                openDetail("Help & Support",
                        "Need assistance?\n\n• Contact your local CSU supervisor\n• Email: support@csu.org\n• In-app help will be expanded in a future update.\n\nFor urgent field issues, reach out to your district coordinator."));

        view.findViewById(R.id.btnPrivacy).setOnClickListener(v ->
                openDetail("Privacy Policy",
                        "CSU Census respects your privacy.\n\n• Field data is stored locally on your device until submitted.\n• Submitted records are transmitted securely to CSU servers.\n• Personal collector information is used only for account management and campaign coordination.\n\nFull policy will be published by CSU headquarters."));

        view.findViewById(R.id.btnAppInfo).setOnClickListener(v ->
                openDetail("App Information",
                        "CSU Census & Data Collection\n\nVersion 1.0.0\nOffline-first starter build\n\nBuilt for field collectors to record household census data efficiently, even without continuous connectivity."));

        view.findViewById(R.id.btnSignOut).setOnClickListener(v -> confirmSignOut());
    }

    private void openDetail(String title, String body) {
        Intent intent = new Intent(requireContext(), ProfileDetailActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("body", body);
        startActivity(intent);
    }

    private void confirmSignOut() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Sign out?")
                .setMessage("You can sign in again any time with your collector ID.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Sign Out", (dialog, which) -> {
                    AppStore.signOut(requireContext());
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .show();
    }
}