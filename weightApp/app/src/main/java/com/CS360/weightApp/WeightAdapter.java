package com.CS360.weightApp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.WeightViewHolder> {

    private Context context;
    private Cursor cursor;

    public WeightAdapter(Context ctx, Cursor c) {
        context = ctx;
        cursor = c;
    }

    // this fires when a new row view needs to be made
    @Override
    public WeightViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new WeightViewHolder(row);
    }

    // fill in a row with the data from the cursor
    @Override
    public void onBindViewHolder(WeightViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            String item = cursor.getString(cursor.getColumnIndexOrThrow("item_name"));
            holder.weightText.setText(item);
        }
    }

    // tells RecyclerView how many items there are
    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    // when I add or change stuff, this makes the adapter refresh
    public void updateCursor(Cursor newCursor) {
        if (cursor != null) cursor.close();
        cursor = newCursor;
        notifyDataSetChanged();
    }

    // describes what one row looks like
    static class WeightViewHolder extends RecyclerView.ViewHolder {
        TextView weightText;

        public WeightViewHolder(View itemView) {
            super(itemView);
            weightText = itemView.findViewById(android.R.id.text1);
        }
    }
}
