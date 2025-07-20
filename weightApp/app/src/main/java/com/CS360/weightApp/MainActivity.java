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

        // link up the input boxes for username and password
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        db = new DBHelper(this); // this sets up the database helper
    }

    // handles logging in when I hit the button
    public void onLogin(View view) {
        String user = usernameInput.getText().toString();
        String pass = passwordInput.getText().toString();

        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Fill in both fields.", Toast.LENGTH_SHORT).show();
        } else if (db.checkLogin(user, pass)) {
            // if login is good, head to SMS screen
            startActivity(new Intent(this, SmsPermissionActivity.class));
        } else {
            Toast.makeText(this, "Invalid login info.", Toast.LENGTH_SHORT).show();
        }
    }

    // handles new account creation
    public void onCreateAccount(View view) {
        String user = usernameInput.getText().toString();
        String pass = passwordInput.getText().toString();

        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "You need to enter both fields.", Toast.LENGTH_SHORT).show();
        } else if (db.insertUser(user, pass)) {
            Toast.makeText(this, "Account created. Now log in.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Username taken. Try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
