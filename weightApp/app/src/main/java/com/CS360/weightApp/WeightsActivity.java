package com.CS360.weightApp;

import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatDelegate;


// this is the main screen to show and add weights
public class WeightsActivity extends AppCompatActivity {

    private WeightViewModel vm;
    private WeightAdapter adapter;
    private EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (ThemePrefs.isDark(this)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // this sets up the view and connects to viewmodel
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weights);

        input = findViewById(R.id.weightInput);
        RecyclerView rv = findViewById(R.id.weightRecyclerView);

        adapter = new WeightAdapter();
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        vm = new ViewModelProvider(this).get(WeightViewModel.class);

        vm.getWeights().observe(this, list -> adapter.submitList(list));

        vm.getInputError().observe(this, msg -> {
            if (msg != null) {
                Snackbar.make(rv, msg, Snackbar.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.addEntryButton).setOnClickListener(v -> {
            vm.addWeight(input.getText().toString());
            input.setText("");
        });

        // this sets up the dark mode toggle switch
        Switch themeSwitch = findViewById(R.id.themeSwitch);
        themeSwitch.setChecked(ThemePrefs.isDark(this));

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ThemePrefs.setDark(this, isChecked);
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            recreate(); // this applies the theme change
        });

    }
}
