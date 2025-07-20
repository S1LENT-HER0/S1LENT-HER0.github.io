package com.CS360.weightApp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// this is the adapter for displaying weight entries
public class WeightAdapter extends ListAdapter<WeightEntry, WeightAdapter.ViewHolder> {

    // this sets up the adapter with diff logic
    public WeightAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<WeightEntry> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<WeightEntry>() {
                @Override
                public boolean areItemsTheSame(@NonNull WeightEntry oldItem, @NonNull WeightEntry newItem) {
                    return oldItem.id == newItem.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull WeightEntry oldItem, @NonNull WeightEntry newItem) {
                    return oldItem.weight == newItem.weight &&
                            oldItem.dateIso.equals(newItem.dateIso) &&
                            oldItem.goalHit == newItem.goalHit;
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // this inflates the row layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // this binds each row to the data
        WeightEntry entry = getItem(position);
        holder.bind(entry);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView line1;
        private final TextView line2;

        ViewHolder(View itemView) {
            super(itemView);
            line1 = itemView.findViewById(android.R.id.text1);
            line2 = itemView.findViewById(android.R.id.text2);
        }

        void bind(WeightEntry entry) {
            line1.setText("Weight: " + entry.weight + " lbs");

            // this formats the date to a friendlier form
            String date = LocalDate.parse(entry.dateIso)
                    .format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));

            String goal = entry.goalHit ? " 🎯 goal hit!" : "";
            line2.setText(date + goal);
        }
    }
}
