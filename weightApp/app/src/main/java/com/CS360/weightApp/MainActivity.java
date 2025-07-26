package com.CS360.weightApp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText usernameInput, passwordInput;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        db = new DBHelper(this);
    }

    public void onLogin(View view) {
        String user = usernameInput.getText().toString();
        String pass = passwordInput.getText().toString();

        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Fill in both fields.", Toast.LENGTH_SHORT).show();
        } else if (db.checkLogin(user, pass)) {
            // pass username to weights activity
            Intent intent = new Intent(this, WeightsActivity.class);
            intent.putExtra("username", user);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid login info.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onCreateAccount(View view) {
        String user = usernameInput.getText().toString();
        String pass = passwordInput.getText().toString();

        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Fill in both fields.", Toast.LENGTH_SHORT).show();
        } else if (db.insertUser(user, pass)) {
            Toast.makeText(this, "Account created. Please log in.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Username taken.", Toast.LENGTH_SHORT).show();
        }
    }
}