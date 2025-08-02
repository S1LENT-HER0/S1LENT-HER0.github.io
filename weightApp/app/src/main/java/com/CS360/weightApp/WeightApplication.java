package com.CS360.weightApp;

import android.app.Application;

public class WeightApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize database
        AppDatabase.getInstance(this);
    }
}