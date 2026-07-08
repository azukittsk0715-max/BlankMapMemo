package com.example.gsmap.Model.PinModel;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gsmap.R;

public class PinAddActivity extends AppCompatActivity {

    private EditText editMemo;
    private Button btnComplete;

    private double latitude;
    private double longitude;
    private String walkerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_add);

        editMemo = findViewById(R.id.editMemo);
        btnComplete = findViewById(R.id.btnComplete);

        latitude = getIntent().getDoubleExtra("LATITUDE", 0);
        longitude = getIntent().getDoubleExtra("LONGITUDE", 0);
        walkerId = getIntent().getStringExtra("WALKER_ID");

        btnComplete.setOnClickListener(v -> addPin());
    }

    private void addPin() {

        if (latitude == 0 && longitude == 0) {
            PinErrorHandler.showError(this, 1);
            return;
        }

        if (walkerId == null || walkerId.isEmpty()) {
            PinErrorHandler.showError(this, 3);
            return;
        }

        String memo = editMemo.getText().toString().trim();

        if (memo.length() > 100) {
            PinErrorHandler.showError(this, 2);
            return;
        }

        PinRegister register = new PinRegister();

        boolean success = register.savePin(
                walkerId,
                latitude,
                longitude,
                memo
        );

        if (success) {

            Toast.makeText(
                    this,
                    "ピンを追加しました",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {
            PinErrorHandler.showError(this, 5);
        }
    }
}