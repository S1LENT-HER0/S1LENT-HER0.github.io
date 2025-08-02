package com.CS360.weightApp;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;
import java.util.concurrent.Executor;

// this is the repo that handles data operations
public class WeightRepository {
    private final WeightDao weightDao;
    private final UserDao userDao;
    private final Executor executor;
    private final String currentUsername;

    // this creates the repository
    public WeightRepository(WeightDao weightDao, UserDao userDao, Executor executor, String username) {
        this.weightDao = weightDao;
        this.userDao = userDao;
        this.executor = executor;
        this.currentUsername = username;
    }

    // this creates new user on background thread
    public LiveData<Boolean> createUser(String username, String password) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        executor.execute(() -> {
            try {
                // this checks if username exists
                int count = userDao.countUsersByUsername(username);
                if (count > 0) {
                    Log.d("UserCreation", "username exists: " + username);
                    result.postValue(false);
                    return;
                }

                // this inserts new user
                User newUser = new User(username, password);
                long userId = userDao.insertUser(newUser);
                result.postValue(userId > 0);
                Log.d("UserCreation", "user created: " + username);
            } catch (Exception e) {
                Log.e("UserCreation", "error creating user", e);
                result.postValue(false);
            }
        });
        return result;
    }

    // this authenticates user on background thread
    public LiveData<Boolean> authenticateUser(String username, String password) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        executor.execute(() -> {
            try {
                // this finds user by username
                User user = userDao.findUserByUsername(username);
                boolean authenticated = user != null && user.password.equals(password);
                result.postValue(authenticated);
                Log.d("Auth", "auth result: " + authenticated);
            } catch (Exception e) {
                Log.e("Auth", "error authenticating", e);
                result.postValue(false);
            }
        });
        return result;
    }

    // this gets all weights for current user
    public LiveData<List<WeightEntry>> getAllWeights() {
        return weightDao.getAllForUser(currentUsername);
    }

    // this gets weights sorted by weight
    public LiveData<List<WeightEntry>> getWeightsSortedByWeight() {
        return weightDao.getAllByWeightAscForUser(currentUsername);
    }

    // this adds a new weight entry
    public void addWeight(float value, String dateIso, boolean goalHit) {
        executor.execute(() -> {
            WeightEntry e = new WeightEntry();
            e.dateIso = dateIso;
            e.weight = value;
            e.goalHit = goalHit;
            e.username = currentUsername;
            weightDao.insert(e);
        });
    }

    // this gets average for current user
    // this gets average for current user on background thread
    public LiveData<Double> getAverageForCurrentUser(String startDate, String endDate) {
        MutableLiveData<Double> result = new MutableLiveData<>();
        executor.execute(() -> {
            try {
                Double avg = weightDao.getAverageForUser(currentUsername, startDate, endDate);
                result.postValue(avg);
            } catch (Exception e) {
                Log.e("repo", "error getting average", e);
                result.postValue(null);
            }
        });
        return result;
    }

    // this counts days with data
    public LiveData<Integer> getDaysWithDataCount(String startDate, String endDate) {
        MutableLiveData<Integer> result = new MutableLiveData<>();
        executor.execute(() -> {
            try {
                int count = weightDao.getDaysWithDataCountForUser(currentUsername, startDate, endDate);
                result.postValue(count);
            } catch (Exception e) {
                Log.e("repo", "error counting days", e);
                result.postValue(0);
            }
        });
        return result;
    }
}