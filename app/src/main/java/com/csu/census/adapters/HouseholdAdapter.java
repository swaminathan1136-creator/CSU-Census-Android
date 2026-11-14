
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

import java.util.List;

public class HouseholdAdapter
        extends RecyclerView.Adapter<HouseholdAdapter.Holder> {

    public interface Listener {
        void onClick(JSONObject household);
    }

    private final List<JSONObject> households;
    private final Listener listener;

    public HouseholdAdapter(
            List<JSONObject> households,
            Listener listener
    ) {
        this.households = households;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_household, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull Holder holder,
            int position
    ) {
        JSONObject household = households.get(position);

        String name = household.optString("name", "Household");
        String id = household.optString("id", "");
        String address = household.optString("address", "");
        String ward = household.optString("ward", "-");
        String status = household.optString("status", "Draft");
        int members = household.optInt("members", 0);

        holder.name.setText(name);
        holder.reference.setText(id);
        holder.address.setText(address);
        holder.meta.setText(
                "Ward: " + ward + "     " + members + " Members"
        );
        holder.status.setText(status);

        int statusColor;
        int statusBackground;

        if (status.equalsIgnoreCase("Submitted")) {
            statusColor = Color.rgb(0, 145, 105);
            statusBackground = Color.rgb(224, 245, 237);
        } else if (status.equalsIgnoreCase("In Review")) {
            statusColor = Color.rgb(230, 123, 35);
            statusBackground = Color.rgb(255, 240, 224);
        } else if (status.equalsIgnoreCase("Returned")) {
            statusColor = Color.rgb(222, 70, 105);
            statusBackground = Color.rgb(255, 232, 239);
        } else {
            statusColor = Color.rgb(0, 102, 196);
            statusBackground = Color.rgb(224, 239, 253);
        }

        holder.status.setTextColor(statusColor);
        holder.status.setBackgroundColor(statusBackground);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(household);
            }
        });
    }

    @Override
    public int getItemCount() {
        return households.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        TextView name;
        TextView reference;
        TextView address;
        TextView meta;
        TextView status;

        Holder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tvHouseholdName);
            reference = itemView.findViewById(R.id.tvHouseholdReference);
            address = itemView.findViewById(R.id.tvHouseholdAddress);
            meta = itemView.findViewById(R.id.tvHouseholdMeta);
            status = itemView.findViewById(R.id.tvHouseholdStatus);
        }
    }
}
