package com.example.gsmap.Model.PinModel;

public class PinRegister {

    public boolean savePin(String walkerId,
                           double latitude,
                           double longitude,
                           String memo) {

        PinSql pinSql = new PinSql();

        return pinSql.savePin(
                walkerId,
                latitude,
                longitude,
                memo
        );
    }

}