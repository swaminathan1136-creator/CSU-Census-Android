package com.csu.census.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HouseholdsFragment extends Fragment {
    private HouseholdAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle s) {
        View v = inflater.inflate(R.layout.fragment_households, container, false);
        RecyclerView rv = v.findViewById(R.id.recyclerHouseholds);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new HouseholdAdapter(AppStore.households(requireContext()), h -> {
            Intent i = new Intent(requireContext(), HouseholdDetailActivity.class);
            i.putExtra("household_id", h.optString("id"));
            startActivity(i);
        });
        rv.setAdapter(adapter);
        FloatingActionButton fab = v.findViewById(R.id.fabAdd);
        if (fab != null) {
            fab.setOnClickListener(b ->
                    startActivity(new Intent(requireContext(), AddHouseholdActivity.class)));
        }
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) adapter.update(AppStore.households(requireContext()));
    }
}