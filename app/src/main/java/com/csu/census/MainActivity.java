
package com.csu.census;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.csu.census.fragments.DashboardFragment;
import com.csu.census.fragments.HouseholdsFragment;
import com.csu.census.fragments.LeaderboardFragment;
import com.csu.census.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    private int currentTab = R.id.nav_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(
                getWindow(),
                false
        );

        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.WHITE);

        setContentView(R.layout.activity_main);

        View root = findViewById(R.id.mainRoot);
        View container = findViewById(R.id.fragmentContainer);

        bottomNavigation = findViewById(
                R.id.bottomNavigation
        );

        ViewCompat.setOnApplyWindowInsetsListener(
                root,
                (view, insets) -> {

                    Insets statusInsets =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.statusBars()
                            );

                    Insets navigationInsets =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.navigationBars()
                            );

                    container.setPadding(
                            0,
                            statusInsets.top,
                            0,
                            0
                    );

                    ViewGroup.MarginLayoutParams params =
                            (ViewGroup.MarginLayoutParams)
                                    bottomNavigation.getLayoutParams();

                    params.bottomMargin = navigationInsets.bottom;

                    bottomNavigation.setLayoutParams(params);

                    return insets;
                }
        );

        bottomNavigation.setItemIconTintList(
                getResources().getColorStateList(
                        R.color.bottom_nav_color,
                        getTheme()
                )
        );

        bottomNavigation.setItemTextColor(
                getResources().getColorStateList(
                        R.color.bottom_nav_color,
                        getTheme()
                )
        );

        bottomNavigation.setOnItemSelectedListener(item -> {

            switchFragment(item.getItemId());

            return true;
        });

        if (savedInstanceState == null) {

            bottomNavigation.setSelectedItemId(
                    R.id.nav_home
            );

        } else {

            currentTab = savedInstanceState.getInt(
                    "tab",
                    R.id.nav_home
            );

            bottomNavigation.setSelectedItemId(
                    currentTab
            );
        }
    }

    /**
     * Opens a bottom navigation destination.
     * Dashboard quick actions can use this method.
     */
    public void openTab(int menuItemId) {

        if (bottomNavigation.getSelectedItemId() != menuItemId) {

            bottomNavigation.setSelectedItemId(
                    menuItemId
            );

        } else {

            switchFragment(menuItemId);
        }
    }

    private void switchFragment(int id) {

        Fragment fragment;

        if (id == R.id.nav_households) {

            fragment = new HouseholdsFragment();

        } else if (id == R.id.nav_leaderboard) {

            fragment = new LeaderboardFragment();

        } else if (id == R.id.nav_profile) {

            fragment = new ProfileFragment();

        } else {

            fragment = new DashboardFragment();

            id = R.id.nav_home;
        }

        currentTab = id;

        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(
                        R.id.fragmentContainer,
                        fragment
                )
                .commit();
    }

    @Override
    protected void onSaveInstanceState(
            @NonNull Bundle outState
    ) {

        outState.putInt(
                "tab",
                currentTab
        );

        super.onSaveInstanceState(outState);
    }
}
