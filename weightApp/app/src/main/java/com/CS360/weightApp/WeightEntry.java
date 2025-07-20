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
    public String dateIso;  // this uses yyyy-MM-dd format

    public float weight;

    public boolean goalHit;
}
