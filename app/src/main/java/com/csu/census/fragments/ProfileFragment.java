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

        View view = inflater.inflate(
            R.layout.fragment_profile,
            container,
            false
        );

        bindProfileDetails(view);
        bindProfileActions(view);

        return view;
    }

    private void bindProfileDetails(View view) {

        String name = AppStore.profile(
            requireContext(),
            "name"
        );

        String email = AppStore.profile(
            requireContext(),
            "email"
        );

        String collectorId = AppStore.activeId(
            requireContext()
        );

        if (name == null || name.trim().isEmpty()) {

            name = "Collector";
        }

        String firstLetter = name.trim()
            .substring(0, 1)
            .toUpperCase();

        ((TextView) view.findViewById(
            R.id.tvProfileAvatar
        )).setText(firstLetter);

        ((TextView) view.findViewById(
            R.id.tvProfileName
        )).setText(name);

        ((TextView) view.findViewById(
            R.id.tvProfileId
        )).setText(
            "CSU DATA COLLECTOR  •  " + collectorId
        );

        ((TextView) view.findViewById(
            R.id.tvProfileEmail
        )).setText(email);
    }

    private void bindProfileActions(View view) {

        view.findViewById(R.id.btnLeaderboardProfile)
            .setOnClickListener(button -> showInfo(
                "Leaderboard",
                "Collector rankings will be displayed when the CSU service is connected."
            ));

        view.findViewById(R.id.btnCertificates)
            .setOnClickListener(button -> showInfo(
                "My Certificates",
                "Your certificates will appear here when they are issued by CSU."
            ));

        view.findViewById(R.id.btnHelp)
            .setOnClickListener(button -> showInfo(
                "Help & Support",
                "Please contact your CSU administrator for assistance."
            ));

        view.findViewById(R.id.btnPrivacy)
            .setOnClickListener(button -> showInfo(
                "Privacy Policy",
                "The production privacy policy will be provided by CSU."
            ));

        view.findViewById(R.id.btnAppInfo)
            .setOnClickListener(button -> showInfo(
                "App Information",
                "CSU Census & Data Collection\n"
                    + "Version 1.0.0\n"
                    + "Offline starter build"
            ));

        view.findViewById(R.id.btnSignOut)
            .setOnClickListener(button -> confirmSignOut());
    }

    private void showInfo(
        String title,
        String message
    ) {

        new AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Close", null)
            .show();
    }

    private void confirmSignOut() {

        new AlertDialog.Builder(requireContext())
            .setTitle("Sign out?")
            .setMessage("You can sign in again any time.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton(
                "Sign Out",
                (dialog, which) -> {

                    AppStore.signOut(requireContext());

                    Intent intent = new Intent(
                        requireContext(),
                        LoginActivity.class
                    );

                    intent.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    );

                    startActivity(intent);
                }
            )
            .show();
    }
}
