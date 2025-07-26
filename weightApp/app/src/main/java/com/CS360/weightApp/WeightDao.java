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

    // get all entries for specific user
    @Query("SELECT * FROM weights WHERE username = :username ORDER BY dateIso DESC")
    LiveData<List<WeightEntry>> getAllForUser(String username);

    // get date range for specific user
    @Query("SELECT * FROM weights WHERE username = :username AND dateIso BETWEEN :start AND :end ORDER BY dateIso ASC")
    List<WeightEntry> getRangeForUser(String username, String start, String end);

    // get sorted by weight for specific user
    @Query("SELECT * FROM weights WHERE username = :username ORDER BY weight ASC")
    LiveData<List<WeightEntry>> getAllByWeightAscForUser(String username);

    // get averages for specific user
    @Query("SELECT AVG(weight) FROM weights WHERE username = :username AND dateIso BETWEEN :start AND :end")
    Double getAverageForUser(String username, String start, String end);

    // count days with data for specific user
    @Query("SELECT COUNT(DISTINCT dateIso) FROM weights WHERE username = :username AND dateIso BETWEEN :start AND :end")
    int getDaysWithDataCountForUser(String username, String start, String end);
}