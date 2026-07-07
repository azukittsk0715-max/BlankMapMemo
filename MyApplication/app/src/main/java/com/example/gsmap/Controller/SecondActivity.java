package com.example.gsmap.Controller;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gsmap.R;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ 別のXMLを読み込む
        setContentView(R.layout.activity_second);


    }
}