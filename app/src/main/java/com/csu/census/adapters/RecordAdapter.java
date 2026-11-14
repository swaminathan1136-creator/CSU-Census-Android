package com.csu.census.adapters;

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

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.VH> {
    private List<JSONObject> data;

    public RecordAdapter(List<JSONObject> data) {
        this.data = data != null ? data : new ArrayList<>();
    }

    public void update(List<JSONObject> list) {
        data = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_record, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        JSONObject o = data.get(position);
        h.title.setText(o.optString("name"));
        h.meta.setText(o.optString("id") + " · " + o.optString("date"));
        h.status.setText(o.optString("status"));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, meta, status;

        VH(View v) {
            super(v);
            title = v.findViewById(R.id.tvTitle);
            meta = v.findViewById(R.id.tvMeta);
            status = v.findViewById(R.id.tvStatus);
        }
    }
}