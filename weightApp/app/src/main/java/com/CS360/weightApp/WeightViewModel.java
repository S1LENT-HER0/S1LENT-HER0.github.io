package com.CS360.weightApp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executors;

// this is the viewmodel that manages weight data and input validation
public class WeightViewModel extends AndroidViewModel {

    private final WeightRepository repo;
    private final MutableLiveData<String> inputError = new MutableLiveData<>(null);

    public WeightViewModel(@NonNull Application app) {
        super(app);
        AppDatabase db = AppDatabase.getInstance(app);
        repo = new WeightRepository(db.weightDao(), Executors.newSingleThreadExecutor());
    }

    public LiveData<List<WeightEntry>> getWeights() {
        return repo.getAllWeights();
    }

    public LiveData<String> getInputError() {
        return inputError;
    }

    public void addWeight(String rawValue) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            inputError.postValue("weight is required");
            return;
        }
        try {
            float w = Float.parseFloat(rawValue);
            if (w <= 0 || w > 1500) {
                inputError.postValue("enter a realistic weight");
                return;
            }
            String today = LocalDate.now().toString();
            repo.addWeight(w, today, false);
            inputError.postValue(null);
        } catch (NumberFormatException e) {
            inputError.postValue("invalid number");
        }
    }
}
