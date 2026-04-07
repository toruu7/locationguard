package com.locationguard;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final int PERM_REQUEST = 100;
    private TextView statusText;
    private EditText triggerWordInput;
    private EditText adminNumberInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.statusText);
        triggerWordInput = findViewById(R.id.triggerWordInput);
        adminNumberInput = findViewById(R.id.adminNumberInput);
        Button saveBtn = findViewById(R.id.saveBtn);

        loadSettings();

        saveBtn.setOnClickListener(v -> {
            String trigger = triggerWordInput.getText().toString().trim().toUpperCase();
            String admin = adminNumberInput.getText().toString().trim();
            if (trigger.isEmpty() || admin.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            getSharedPreferences("locationguard", MODE_PRIVATE).edit()
                .putString("trigger_word", trigger)
                .putString("admin_number", admin)
                .apply();
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            statusText.setText("Ready. Send '" + trigger + "' from " + admin);
        });
    }

    private void loadSettings() {
        String trigger = getSharedPreferences("locationguard", MODE_PRIVATE)
            .getString("trigger_word", "LOC");
        String admin = getSharedPreferences("locationguard", MODE_PRIVATE)
            .getString("admin_number", "");
        statusText.setText("Ready. Send '" + trigger + "' from " + admin);
    }
}
