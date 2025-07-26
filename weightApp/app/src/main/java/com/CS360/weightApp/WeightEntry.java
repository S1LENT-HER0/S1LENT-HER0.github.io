package com.CS360.weightApp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

// this is the data model for a weight entry
@Entity(tableName = "weights")
public class WeightEntry {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String dateIso;

    public float weight;
    public boolean goalHit;

    @NonNull
    public String username; // Make sure this field exists
}