package com.csu.census.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csu.census.R;
import com.csu.census.adapters.RecordAdapter;
import com.csu.census.data.AppStore;

public class RecordsFragment extends Fragment {
    private RecordAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle s) {
        View v = inflater.inflate(R.layout.fragment_records, container, false);
        RecyclerView rv = v.findViewById(R.id.recyclerRecords);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RecordAdapter(AppStore.records(requireContext()));
        rv.setAdapter(adapter);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) adapter.update(AppStore.records(requireContext()));
    }
}