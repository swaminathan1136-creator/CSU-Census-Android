package com.csu.census;

import android.content.res.ColorStateList;
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
import com.csu.census.fragments.RecordsFragment;
import com.csu.census.fragments.ProfileFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private int currentTab = R.id.nav_home;
    private boolean isProgrammaticSelect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.WHITE);

        setContentView(R.layout.activity_main);

        View root = findViewById(R.id.mainRoot);
        View fragmentContainer = findViewById(R.id.fragmentContainer);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets status = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            Insets nav = insets.getInsets(WindowInsetsCompat.Type.navigationBars());

            fragmentContainer.setPadding(
                    fragmentContainer.getPaddingLeft(),
                    status.top,
                    fragmentContainer.getPaddingRight(),
                    fragmentContainer.getPaddingBottom()
            );

            ViewGroup.MarginLayoutParams lp =
                    (ViewGroup.MarginLayoutParams) bottomNavigation.getLayoutParams();
            lp.bottomMargin = nav.bottom;
            bottomNavigation.setLayoutParams(lp);

            return insets;
        });

        int[][] states = new int[][] {
                new int[] { android.R.attr.state_checked },
                new int[] { }
        };
        int[] colors = new int[] {
                Color.parseColor("#255DB0"),
                Color.parseColor("#94A3B8")
        };
        ColorStateList tint = new ColorStateList(states, colors);
        bottomNavigation.setItemIconTintList(tint);
        bottomNavigation.setItemTextColor(tint);

        bottomNavigation.setOnItemSelectedListener(item -> {
            if (isProgrammaticSelect) {
                isProgrammaticSelect = false;
                switchFragment(item.getItemId());
                return true;
            }
            switchFragment(item.getItemId());
            return true;
        });

        if (savedInstanceState == null) {
            bottomNavigation.setSelectedItemId(R.id.nav_home);
        } else {
            currentTab = savedInstanceState.getInt("currentTab", R.id.nav_home);
            isProgrammaticSelect = true;
            bottomNavigation.setSelectedItemId(currentTab);
        }
    }

    /**
     * Public entry point used by quick actions / other fragments.
     * Switches the fragment AND updates the bottom nav selection.
     */
    public void openTab(int menuItemId) {
        if (menuItemId == currentTab) {
            switchFragment(menuItemId);
            return;
        }
        isProgrammaticSelect = true;
        bottomNavigation.setSelectedItemId(menuItemId);
    }

    private void switchFragment(int menuItemId) {
        Fragment selectedFragment;
        String screenTitle;

        if (menuItemId == R.id.nav_home) {
            selectedFragment = new DashboardFragment();
            screenTitle = "Dashboard";
        } else if (menuItemId == R.id.nav_households) {
            selectedFragment = new HouseholdsFragment();
            screenTitle = "Households";
        } else if (menuItemId == R.id.nav_records) {
            selectedFragment = new RecordsFragment();
            screenTitle = "Records";
        } else if (menuItemId == R.id.nav_profile) {
            selectedFragment = new ProfileFragment();
            screenTitle = "Profile";
        } else {
            selectedFragment = new DashboardFragment();
            screenTitle = "Dashboard";
            menuItemId = R.id.nav_home;
        }

        currentTab = menuItemId;
        setTitle(screenTitle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, selectedFragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("currentTab", currentTab);
        super.onSaveInstanceState(outState);
    }
}