package com.CS360.weightApp;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.util.List;

// this is the dao for accessing weight data
@Dao
public interface WeightDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(WeightEntry entry);

    @Update
    void update(WeightEntry entry);

    @Delete
    void delete(WeightEntry entry);

    // this deletes by id and username
    @Query("DELETE FROM weights WHERE id = :id AND username = :username")
    void deleteByIdAndUser(long id, String username);

    // this gets all entries for specific user
    @Query("SELECT * FROM weights WHERE username = :username ORDER BY dateIso DESC")
    LiveData<List<WeightEntry>> getAllForUser(String username);

    // this gets entries within date range
    @Query("SELECT * FROM weights WHERE username = :username AND dateIso BETWEEN :start AND :end ORDER BY dateIso ASC")
    List<WeightEntry> getRangeForUser(String username, String start, String end);

    // this gets entries sorted by weight
    @Query("SELECT * FROM weights WHERE username = :username ORDER BY weight ASC")
    LiveData<List<WeightEntry>> getAllByWeightAscForUser(String username);

    // this calculates average weight
    @Query("SELECT AVG(weight) FROM weights WHERE username = :username AND dateIso BETWEEN :start AND :end")
    Double getAverageForUser(String username, String start, String end);

    // this counts days with data
    @Query("SELECT COUNT(DISTINCT dateIso) FROM weights WHERE username = :username AND dateIso BETWEEN :start AND :end")
    int getDaysWithDataCountForUser(String username, String start, String end);
}