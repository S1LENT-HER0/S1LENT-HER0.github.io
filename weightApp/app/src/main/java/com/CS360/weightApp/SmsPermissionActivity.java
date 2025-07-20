package com.CS360.weightApp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SmsPermissionActivity extends AppCompatActivity {

    private static final int SMS_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_perms);

        // grabbing the toggle and request button
        Switch smsToggle = findViewById(R.id.smsToggle);
        Button requestBtn = findViewById(R.id.requestPermissionButton);

        // when I hit the button, check the toggle and ask for permission if needed
        requestBtn.setOnClickListener(v -> {
            if (smsToggle.isChecked()) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    // ask for SMS permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_REQUEST_CODE);
                } else {
                    // if I already have permission, just send the text
                    sendSMS();
                }
            } else {
                // if I don't want SMS, just move on
                startActivity(new Intent(this, WeightsActivity.class));
            }
        });
    }

    private void sendSMS() {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(null, null, "You've hit your goal! Congratulations!", null, null);
            Toast.makeText(this, "SMS sent!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Couldn’t send SMS.", Toast.LENGTH_SHORT).show();
        } finally {
            startActivity(new Intent(this, WeightsActivity.class));
        }
    }

    // this handles the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == SMS_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sendSMS();
        } else {
            Toast.makeText(this, "SMS permission denied.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, WeightsActivity.class));
        }
    }
}
