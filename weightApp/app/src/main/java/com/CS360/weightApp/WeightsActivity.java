package com.CS360.weightApp;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatDelegate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class WeightsActivity extends AppCompatActivity {
    private WeightViewModel vm;
    private WeightAdapter adapter;
    private EditText input;
    private TextView averagesText;
    private TextView trendVisualization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // apply theme preference
        if (ThemePrefs.isDark(this)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weights);

        // get username from intent
        String username = getIntent().getStringExtra("username");
        if (username == null) {
            finish();
            return;
        }

        // initialize viewmodel with username
        WeightViewModelFactory factory = new WeightViewModelFactory(getApplication(), username);
        vm = new ViewModelProvider(this, factory).get(WeightViewModel.class);

        // initialize views
        input = findViewById(R.id.weightInput);
        RecyclerView rv = findViewById(R.id.weightRecyclerView);
        averagesText = findViewById(R.id.averagesText);
        trendVisualization = findViewById(R.id.trendVisualization);

        adapter = new WeightAdapter();
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // observe weight data changes
        vm.getWeights().observe(this, weights -> {
            adapter.submitList(weights);
            updateTrendVisualization(weights);
        });

        vm.getInputError().observe(this, msg -> {
            if (msg != null) Snackbar.make(rv, msg, Snackbar.LENGTH_SHORT).show();
        });

        // setup button click listeners
        findViewById(R.id.addEntryButton).setOnClickListener(v -> {
            vm.addWeight(input.getText().toString());
            input.setText("");
        });
        findViewById(R.id.sortDateBtn).setOnClickListener(v -> vm.sortByDate());
        findViewById(R.id.sortWeightBtn).setOnClickListener(v -> vm.sortByWeight());
        findViewById(R.id.showAveragesBtn).setOnClickListener(v -> {
            String text = vm.getCurrentWeekAverage() + "\n" + vm.getCurrentMonthAverage();
            averagesText.setText(text);
        });

        // theme switch setup
        Switch themeSwitch = findViewById(R.id.themeSwitch);
        themeSwitch.setChecked(ThemePrefs.isDark(this));
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ThemePrefs.setDark(this, isChecked);
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            recreate();
        });
    }

    private void updateTrendVisualization(List<WeightEntry> weights) {
        if (weights == null || weights.isEmpty()) {
            trendVisualization.setText("");
            return;
        }

        // create text-based bar chart
        float maxWeight = weights.stream().max(Comparator.comparing(e -> e.weight)).get().weight;
        StringBuilder trend = new StringBuilder("Trend:\n");
        for (WeightEntry entry : weights) {
            int bars = (int)((entry.weight/maxWeight)*20);
            String date = LocalDate.parse(entry.dateIso).format(DateTimeFormatter.ofPattern("MMM dd"));
            trend.append(String.format("%s: %s\n", date, new String(new char[bars]).replace('\0','=')));
        }
        trendVisualization.setText(trend.toString());
    }
}