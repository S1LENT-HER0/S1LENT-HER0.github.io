// WeightViewModelFactory.java
package com.CS360.weightApp;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

// this creates viewmodel with username parameter
public class WeightViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final String username;

    // this initializes the factory
    public WeightViewModelFactory(Application application, String username) {
        this.application = application;
        this.username = username;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new WeightViewModel(application, username);
    }
}