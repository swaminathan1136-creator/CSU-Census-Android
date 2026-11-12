
package com.csu.census.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csu.census.R;

import org.json.JSONObject;

import java.util.List;

public class RecordAdapter
    extends RecyclerView.Adapter<RecordAdapter.Holder> {

    private final List<JSONObject> data;

    public RecordAdapter(List<JSONObject> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(
        @NonNull ViewGroup parent,
        int viewType
    ) {

        View itemView = LayoutInflater.from(parent.getContext())
            .inflate(
                R.layout.item_record,
                parent,
                false
            );

        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(
        @NonNull Holder holder,
        int position
    ) {

        JSONObject record = data.get(position);

        holder.title.setText(
            record.optString("id")
                + " • "
                + record.optString("name")
        );

        holder.meta.setText(
            record.optString("date")
        );

        holder.status.setText(
            record.optString("status")
        );
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

            title = itemView.findViewById(
                R.id.tvRecordTitle
            );

            meta = itemView.findViewById(
                R.id.tvRecordMeta
            );

            status = itemView.findViewById(
                R.id.tvRecordStatus
            );
        }
    }
}
