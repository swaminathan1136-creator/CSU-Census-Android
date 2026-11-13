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

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(
                R.layout.fragment_records,
                container,
                false
        );

        RecyclerView recordsRecyclerView = view.findViewById(R.id.rvRecords);
        recordsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        RecordAdapter adapter = new RecordAdapter(AppStore.records(requireContext()));
        recordsRecyclerView.setAdapter(adapter);

        boolean isEmpty = adapter.getItemCount() == 0;

        view.findViewById(R.id.tvRecordsEmpty)
                .setVisibility(isEmpty ? View.VISIBLE : View.GONE);

        recordsRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        return view;
    }
}