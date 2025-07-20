package com.CS360.weightApp;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.util.List;

// this is the dao for accessing weight data
@Dao
public interface WeightDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(WeightEntry entry);

    @Delete
    void delete(WeightEntry entry);

    @Query("SELECT * FROM weights ORDER BY dateIso DESC")
    LiveData<List<WeightEntry>> getAll();

    @Query("SELECT * FROM weights WHERE dateIso BETWEEN :start AND :end ORDER BY dateIso ASC")
    List<WeightEntry> getRange(String start, String end);
}
