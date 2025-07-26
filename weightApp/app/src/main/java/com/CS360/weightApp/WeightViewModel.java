package com.CS360.weightApp;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

// this is the viewmodel that manages weight data and input validation
public class WeightViewModel extends AndroidViewModel {
    private final WeightRepository repo;
    private final MutableLiveData<String> inputError = new MutableLiveData<>(null);
    private final MutableLiveData<List<WeightEntry>> weightsData = new MutableLiveData<>();

    public WeightViewModel(@NonNull Application app, String username) {
        super(app);
        // initialize database with current user
        AppDatabase db = AppDatabase.getInstance(app);
        repo = new WeightRepository(db.weightDao(), Executors.newSingleThreadExecutor(), username);

        // load user's weight data
        repo.getAllWeights().observeForever(weights -> {
            weightsData.postValue(weights);
        });
    }

    // expose user's weight data
    public LiveData<List<WeightEntry>> getWeights() {
        return weightsData;
    }

    // expose error messages
    public LiveData<String> getInputError() {
        return inputError;
    }

    // add new weight for current user
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

    // sort current user's data by date
    public void sortByDate() {
        repo.getWeightsSortedByDate().observeForever(weights -> {
            if (weights != null) weightsData.postValue(weights);
        });
    }

    // sort current user's data by weight
    public void sortByWeight() {
        repo.getWeightsSortedByWeight().observeForever(weights -> {
            if (weights != null) weightsData.postValue(weights);
        });
    }

    // get weekly average for current user
    public String getCurrentWeekAverage() {
        LocalDate now = LocalDate.now();
        LocalDate weekStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        return repo.getPeriodAverageWithDetail(
                weekStart.format(DateTimeFormatter.ISO_LOCAL_DATE),
                weekEnd.format(DateTimeFormatter.ISO_LOCAL_DATE)
        );
    }

    // get monthly average for current user
    public String getCurrentMonthAverage() {
        LocalDate now = LocalDate.now();
        LocalDate monthStart = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate monthEnd = now.with(TemporalAdjusters.lastDayOfMonth());

        return repo.getPeriodAverageWithDetail(
                monthStart.format(DateTimeFormatter.ISO_LOCAL_DATE),
                monthEnd.format(DateTimeFormatter.ISO_LOCAL_DATE)
        );
    }
}