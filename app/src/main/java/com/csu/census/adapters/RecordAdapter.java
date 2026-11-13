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

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.Holder> {

    private final List<JSONObject> data;

    public RecordAdapter(List<JSONObject> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_record, parent, false);
        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        JSONObject record = data.get(position);
        holder.title.setText(record.optString("id") + " • " + record.optString("name"));
        holder.meta.setText(record.optString("date"));

        String status = record.optString("status", "Submitted");
        holder.status.setText(status);

        if ("Returned".equals(status)) {
            holder.status.setBackgroundResource(R.drawable.bg_status_returned);
            holder.status.setTextColor(Color.parseColor("#C44A5A"));
        } else if ("In Review".equals(status)) {
            holder.status.setBackgroundResource(R.drawable.bg_status_review);
            holder.status.setTextColor(Color.parseColor("#C4781A"));
        } else {
            holder.status.setBackgroundResource(R.drawable.bg_status_submitted);
            holder.status.setTextColor(Color.parseColor("#1F8A5B"));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView meta;
        final TextView status;

        Holder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvRecordTitle);
            meta = itemView.findViewById(R.id.tvRecordMeta);
            status = itemView.findViewById(R.id.tvRecordStatus);
        }
    }
}