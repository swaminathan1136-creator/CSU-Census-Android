package com.csu.census.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csu.census.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HouseholdAdapter extends RecyclerView.Adapter<HouseholdAdapter.VH> {

    public interface Listener {
        void onClick(JSONObject household);
    }

    private List<JSONObject> data;
    private final Listener listener;

    public HouseholdAdapter(List<JSONObject> data, Listener listener) {
        this.data = data != null ? data : new ArrayList<>();
        this.listener = listener;
    }

    public void update(List<JSONObject> list) {
        data = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_household, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        JSONObject o = data.get(position);
        h.title.setText(o.optString("id") + " · " + o.optString("name"));
        h.address.setText(o.optString("address"));
        String status = o.optString("status");
        h.status.setText(status);
        switch (status) {
            case "Submitted":
                h.status.setBackgroundResource(R.drawable.bg_status_submitted);
                h.status.setTextColor(Color.parseColor("#1F8A5B"));
                break;
            case "In Review":
                h.status.setBackgroundResource(R.drawable.bg_status_review);
                h.status.setTextColor(Color.parseColor("#C4781A"));
                break;
            case "Returned":
                h.status.setBackgroundResource(R.drawable.bg_status_returned);
                h.status.setTextColor(Color.parseColor("#C44A5A"));
                break;
            default:
                h.status.setBackgroundResource(R.drawable.bg_status_draft);
                h.status.setTextColor(Color.parseColor("#5A6A7E"));
        }
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(o);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, address, status;

        VH(View v) {
            super(v);
            title = v.findViewById(R.id.tvTitle);
            address = v.findViewById(R.id.tvAddress);
            status = v.findViewById(R.id.tvStatus);
        }
    }
}