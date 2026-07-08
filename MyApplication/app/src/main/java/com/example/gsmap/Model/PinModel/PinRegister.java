package com.example.gsmap.Model.PinModel;

import android.util.Log;

public class PinRegister {

    public boolean savePin(String walkerId,
                           double latitude,
                           double longitude,
                           String memo) {

        Log.d("PinTest", "walkerId=" + walkerId);
        Log.d("PinTest", "latitude=" + latitude);
        Log.d("PinTest", "longitude=" + longitude);
        Log.d("PinTest", "memo=" + memo);

        return true;
    }
}