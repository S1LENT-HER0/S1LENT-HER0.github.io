package com.CS360.weightApp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import java.util.concurrent.Executors;

// this is the login screen
public class MainActivity extends AppCompatActivity {
    private EditText usernameInput, passwordInput;
    private WeightRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // this initializes ui elements
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);

        // this sets up database and repository
        AppDatabase db = AppDatabase.getInstance(this);
        repository = new WeightRepository(
                db.weightDao(),
                db.userDao(),
                Executors.newSingleThreadExecutor(),
                null
        );
    }

    // this handles login button click
    public void onLogin(View view) {
        String user = usernameInput.getText().toString().trim();
        String pass = passwordInput.getText().toString().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            showToast("fill in both fields");
            return;
        }

        Log.d("MainActivity", "attempting login for: " + user);
        repository.authenticateUser(user, pass).observe(this, authenticated -> {
            if (authenticated != null && authenticated) {
                startWeightsActivity(user);
            } else {
                showToast("invalid login info");
            }
        });
    }

    // this handles account creation
    public void onCreateAccount(View view) {
        String user = usernameInput.getText().toString().trim();
        String pass = passwordInput.getText().toString().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            showToast("fill in both fields");
            return;
        }

        Log.d("MainActivity", "attempting to create user: " + user);
        repository.createUser(user, pass).observe(this, success -> {
            if (success != null && success) {
                showToast("account created");
                startWeightsActivity(user);
            } else {
                showToast("username taken");
            }
        });
    }

    // this starts weights activity
    private void startWeightsActivity(String username) {
        Intent intent = new Intent(this, WeightsActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }

    // this shows toast messages
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}