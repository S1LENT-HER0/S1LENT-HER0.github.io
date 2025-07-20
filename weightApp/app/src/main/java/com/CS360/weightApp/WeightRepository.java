package com.CS360.weightApp;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;

// this is the repo that handles data operations
public class WeightRepository {

    private final WeightDao dao;
    private final Executor io;

    public WeightRepository(WeightDao dao, Executor io) {
        this.dao = dao;
        this.io = io;
    }

    public LiveData<List<WeightEntry>> getAllWeights() {
        return dao.getAll();
    }

    public void addWeight(float value, String dateIso, boolean goalHit) {
        io.execute(() -> {
            WeightEntry e = new WeightEntry();
            e.dateIso = dateIso;
            e.weight = value;
            e.goalHit = goalHit;
            dao.insert(e);
        });
    }

    public void delete(WeightEntry entry) {
        io.execute(() -> dao.delete(entry));
    }
}
