package com.CS360.weightApp;

import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

// this is the repo that handles data operations
public class WeightRepository {
    private final WeightDao dao;
    private final Executor io;
    private final String currentUsername;

    public WeightRepository(WeightDao dao, Executor io, String username) {
        this.dao = dao;
        this.io = io;
        this.currentUsername = username;
    }

    // get all weights for current user
    public LiveData<List<WeightEntry>> getAllWeights() {
        return dao.getAllForUser(currentUsername);
    }

    // add weight for current user
    public void addWeight(float value, String dateIso, boolean goalHit) {
        io.execute(() -> {
            WeightEntry e = new WeightEntry();
            e.dateIso = dateIso;
            e.weight = value;
            e.goalHit = goalHit;
            e.username = currentUsername;
            dao.insert(e);
        });
    }

    // delete entry (only if belongs to user)
    public void delete(WeightEntry entry) {
        if (entry.username.equals(currentUsername)) {
            io.execute(() -> dao.delete(entry));
        }
    }

    // get sorted by date for current user
    public LiveData<List<WeightEntry>> getWeightsSortedByDate() {
        return dao.getAllForUser(currentUsername);
    }

    // get sorted by weight for current user
    public LiveData<List<WeightEntry>> getWeightsSortedByWeight() {
        return dao.getAllByWeightAscForUser(currentUsername);
    }

    // get formatted average with details
    public String getPeriodAverageWithDetail(String startDate, String endDate) {
        try {
            Double avg = dao.getAverageForUser(currentUsername, startDate, endDate);
            int days = dao.getDaysWithDataCountForUser(currentUsername, startDate, endDate);

            if (avg != null && avg > 0 && days > 0) {
                return String.format(Locale.getDefault(),
                        "Avg: %.1f lbs (%d days)", avg, days);
            }
        } catch (Exception e) {
            Log.e("Repo", "Avg error", e);
        }
        return "No data for period";
    }
}