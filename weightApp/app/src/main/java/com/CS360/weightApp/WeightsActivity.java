package com.CS360.weightApp;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

// this is the main weights activity
public class WeightsActivity extends AppCompatActivity {
    private WeightViewModel vm;
    private WeightAdapter adapter;
    private EditText input;
    private TextView averagesText;
    private TextView trendVisualization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // this applies theme preference
        if (ThemePrefs.isDark(this)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weights);

        // this prevents keyboard from pushing content up
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // this gets username from intent
        String username = getIntent().getStringExtra("username");
        if (username == null) {
            finish();
            return;
        }

        // this initializes viewmodel
        WeightViewModelFactory factory = new WeightViewModelFactory(getApplication(), username);
        vm = new ViewModelProvider(this, factory).get(WeightViewModel.class);

        // this sets up views
        setupViews();
        setupWeightInput();
        setupListeners();
    }

    // this sets up all view references
    private void setupViews() {
        input = findViewById(R.id.weightInput);
        RecyclerView rv = findViewById(R.id.weightRecyclerView);
        averagesText = findViewById(R.id.averagesText);
        trendVisualization = findViewById(R.id.trendVisualization);

        adapter = new WeightAdapter();
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    // this handles the weight input field
    private void setupWeightInput() {
        RecyclerView recyclerView = findViewById(R.id.weightRecyclerView);
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        // this makes sure the view scrolls to show input field
        input.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && layoutManager != null) {
                recyclerView.postDelayed(() ->
                        layoutManager.scrollToPositionWithOffset(0, 0), 100);
            }
        });

        // this hides keyboard when done is pressed
        input.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                hideKeyboard();
                return true;
            }
            return false;
        });
    }

    // this sets up all click listeners
    private void setupListeners() {
        // this observes weight data changes
        vm.getWeights().observe(this, weights -> {
            adapter.submitList(weights);
            updateTrendVisualization(weights);
        });

        vm.getInputError().observe(this, msg -> {
            if (msg != null) Snackbar.make(findViewById(R.id.weightRecyclerView), msg, Snackbar.LENGTH_SHORT).show();
        });

        // this sets up button click listeners
        findViewById(R.id.addEntryButton).setOnClickListener(v -> {
            vm.addWeight(input.getText().toString());
            input.setText("");
            hideKeyboard();
        });

        findViewById(R.id.sortDateBtn).setOnClickListener(v -> vm.sortByDate());
        findViewById(R.id.sortWeightBtn).setOnClickListener(v -> vm.sortByWeight());
        // this sets up averages button click listener
        findViewById(R.id.showAveragesBtn).setOnClickListener(v -> {
            vm.getCurrentWeekAverage().observe(this, weekAvg -> {
                vm.getCurrentMonthAverage().observe(this, monthAvg -> {
                    String text = (weekAvg != null ? weekAvg : "no weekly data") + "\n" +
                            (monthAvg != null ? monthAvg : "no monthly data");
                    averagesText.setText(text);
                });
            });
        });



        // this sets up theme switch
        Switch themeSwitch = findViewById(R.id.themeSwitch);
        themeSwitch.setChecked(ThemePrefs.isDark(this));
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ThemePrefs.setDark(this, isChecked);
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            recreate();
        });
    }

    // this hides the soft keyboard
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    // this updates the trend visualization
    private void updateTrendVisualization(List<WeightEntry> weights) {
        if (weights == null || weights.isEmpty()) {
            trendVisualization.setText("");
            return;
        }

        // this creates text-based bar chart
        float maxWeight = weights.stream().max(Comparator.comparing(e -> e.weight)).get().weight;
        StringBuilder trend = new StringBuilder("trend:\n");
        for (WeightEntry entry : weights) {
            int bars = (int)((entry.weight/maxWeight)*20);
            String date = LocalDate.parse(entry.dateIso).format(DateTimeFormatter.ofPattern("MMM dd"));
            trend.append(String.format("%s: %s\n", date, new String(new char[bars]).replace('\0','=')));
        }
        trendVisualization.setText(trend.toString());
    }
}