package com.csu.census.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class HouseholdsFragment extends Fragment {

    private HouseholdAdapter adapter;
    private TextView emptyState;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_households, container, false);

        recyclerView = view.findViewById(R.id.rvHouseholds);
        emptyState = view.findViewById(R.id.tvEmpty);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new HouseholdAdapter(new ArrayList<>(), this::openDetail);
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.btnAddHousehold)
                .setOnClickListener(v ->
                        startActivity(new Intent(requireContext(), AddHouseholdActivity.class))
                );

        refreshList();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        ArrayList<JSONObject> list = AppStore.households(requireContext());
        adapter.update(list);
        boolean empty = list.isEmpty();
        emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void openDetail(JSONObject household) {
        Intent intent = new Intent(requireContext(), HouseholdDetailActivity.class);
        intent.putExtra("household_id", household.optString("id"));
        startActivity(intent);
    }
}