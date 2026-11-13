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

public class HouseholdAdapter
        extends RecyclerView.Adapter<HouseholdAdapter.Holder> {

    public interface Listener {
        void onClick(JSONObject item);
    }

    private final ArrayList<JSONObject> data;
    private final Listener listener;

    public HouseholdAdapter(List<JSONObject> data, Listener listener) {
        this.data = new ArrayList<>(data);
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_household, parent, false);
        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        JSONObject household = data.get(position);

        holder.name.setText(
                household.optString("id") + " • " + household.optString("name")
        );
        holder.address.setText(household.optString("address"));

        String status = household.optString("status");
        int members = household.optInt("members");
        holder.status.setText(status + "  ·  " + members + " members");

        applyStatusStyle(holder.status, status);

        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onClick(household);
            }
        });
    }

    private void applyStatusStyle(TextView statusView, String status) {
        switch (status) {
            case "Submitted":
                statusView.setBackgroundResource(R.drawable.bg_status_submitted);
                statusView.setTextColor(Color.parseColor("#1F8A5B"));
                break;
            case "In Review":
                statusView.setBackgroundResource(R.drawable.bg_status_review);
                statusView.setTextColor(Color.parseColor("#C4781A"));
                break;
            case "Returned":
                statusView.setBackgroundResource(R.drawable.bg_status_returned);
                statusView.setTextColor(Color.parseColor("#C44A5A"));
                break;
            default:
                statusView.setBackgroundResource(R.drawable.bg_status_draft);
                statusView.setTextColor(Color.parseColor("#4A6FA5"));
                break;
        }
    }

    public void update(List<JSONObject> items) {
        data.clear();
        data.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView address;
        final TextView status;

        Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvHouseholdName);
            address = itemView.findViewById(R.id.tvHouseholdAddress);
            status = itemView.findViewById(R.id.tvHouseholdStatus);
        }
    }
}