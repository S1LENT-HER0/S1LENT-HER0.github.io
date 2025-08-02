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

// this is the viewmodel that manages weight data
public class WeightViewModel extends AndroidViewModel {
    private final WeightRepository repo;
    private final MutableLiveData<String> inputError = new MutableLiveData<>(null);
    private final MutableLiveData<List<WeightEntry>> weightsData = new MutableLiveData<>();

    // this creates the viewmodel
    public WeightViewModel(@NonNull Application app, String username) {
        super(app);
        AppDatabase db = AppDatabase.getInstance(app);
        repo = new WeightRepository(
                db.weightDao(),
                db.userDao(),
                Executors.newSingleThreadExecutor(),
                username
        );

        // this loads the user's weight data
        repo.getAllWeights().observeForever(weights -> {
            weightsData.postValue(weights);
        });
    }

    // this gets the weight data
    public LiveData<List<WeightEntry>> getWeights() {
        return weightsData;
    }

    // this gets error messages
    public LiveData<String> getInputError() {
        return inputError;
    }

    // this adds a new weight entry
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

    // this sorts by date
    public void sortByDate() {
        repo.getAllWeights().observeForever(weights -> {
            if (weights != null) weightsData.postValue(weights);
        });
    }

    // this sorts by weight
    public void sortByWeight() {
        repo.getWeightsSortedByWeight().observeForever(weights -> {
            if (weights != null) weightsData.postValue(weights);
        });
    }

    // this calculates weekly average
    public LiveData<String> getCurrentWeekAverage() {
        MutableLiveData<String> result = new MutableLiveData<>();
        LocalDate now = LocalDate.now();
        LocalDate weekStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        String start = weekStart.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String end = weekEnd.format(DateTimeFormatter.ISO_LOCAL_DATE);

        LiveData<Double> avgLiveData = repo.getAverageForCurrentUser(start, end);
        LiveData<Integer> daysLiveData = repo.getDaysWithDataCount(start, end);

        avgLiveData.observeForever(avg -> {
            daysLiveData.observeForever(days -> {
                result.postValue(formatAverage(avg, days));
            });
        });

        return result;
    }


    // this calculates monthly average
    public LiveData<String> getCurrentMonthAverage() {
        MutableLiveData<String> result = new MutableLiveData<>();
        LocalDate now = LocalDate.now();
        LocalDate monthStart = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate monthEnd = now.with(TemporalAdjusters.lastDayOfMonth());

        String start = monthStart.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String end = monthEnd.format(DateTimeFormatter.ISO_LOCAL_DATE);

        LiveData<Double> avgLiveData = repo.getAverageForCurrentUser(start, end);
        LiveData<Integer> daysLiveData = repo.getDaysWithDataCount(start, end);

        avgLiveData.observeForever(avg -> {
            daysLiveData.observeForever(days -> {
                result.postValue(formatAverage(avg, days));
            });
        });

        return result;
    }

    // this formats the average string
    private String formatAverage(Double avg, int days) {
        if (avg != null && avg > 0 && days > 0) {
            return String.format(Locale.getDefault(),
                    "avg: %.1f lbs (%d days)", avg, days);
        }
        return "no data for period";
    }
}