package com.CS360.weightApp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WeightsActivity extends AppCompatActivity {

    RecyclerView recycler;
    WeightAdapter adapter;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weights);

        // hook up the recycler view
        recycler = findViewById(R.id.weightRecyclerView);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        // get the database ready and load the adapter
        db = new DBHelper(this);
        adapter = new WeightAdapter(this, db.getAllItems());
        recycler.setAdapter(adapter);
    }

    // this is triggered when I hit "Add Entry"
    public void addEntry(View view) {
        EditText weightInput = findViewById(R.id.weightInput);
        String entry = weightInput.getText().toString();

        if (!entry.isEmpty()) {
            // add it to the DB and update the list
            db.insertItem(entry);
            adapter.updateCursor(db.getAllItems());
            weightInput.setText(""); // clear the box
        } else {
            Toast.makeText(this, "Enter your weight", Toast.LENGTH_SHORT).show();
        }
    }
}
