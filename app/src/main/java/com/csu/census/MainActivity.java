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
import com.csu.census.fragments.ProfileFragment;
import com.csu.census.fragments.RecordsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private int currentTab = R.id.nav_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.WHITE);
        setContentView(R.layout.activity_main);

        View root = findViewById(R.id.mainRoot);
        View container = findViewById(R.id.fragmentContainer);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets status = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            Insets nav = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            container.setPadding(0, status.top, 0, 0);
            ViewGroup.MarginLayoutParams lp =
                    (ViewGroup.MarginLayoutParams) bottomNavigation.getLayoutParams();
            lp.bottomMargin = nav.bottom;
            bottomNavigation.setLayoutParams(lp);
            return insets;
        });

        ColorStateList tint = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                new int[]{Color.parseColor("#255DB0"), Color.parseColor("#94A3B8")}
        );
        bottomNavigation.setItemIconTintList(tint);
        bottomNavigation.setItemTextColor(tint);

        bottomNavigation.setOnItemSelectedListener(item -> {
            switchFragment(item.getItemId());
            return true;
        });

        if (savedInstanceState == null) {
            bottomNavigation.setSelectedItemId(R.id.nav_home);
        } else {
            currentTab = savedInstanceState.getInt("tab", R.id.nav_home);
            bottomNavigation.setSelectedItemId(currentTab);
        }
    }

    /** Quick actions call this – switches fragment AND selects bottom nav item. */
    public void openTab(int menuItemId) {
        if (bottomNavigation.getSelectedItemId() != menuItemId) {
            bottomNavigation.setSelectedItemId(menuItemId);
        } else {
            switchFragment(menuItemId);
        }
    }

    private void switchFragment(int id) {
        Fragment f;
        if (id == R.id.nav_households) f = new HouseholdsFragment();
        else if (id == R.id.nav_records) f = new RecordsFragment();
        else if (id == R.id.nav_profile) f = new ProfileFragment();
        else {
            f = new DashboardFragment();
            id = R.id.nav_home;
        }
        currentTab = id;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, f)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("tab", currentTab);
        super.onSaveInstanceState(outState);
    }
}