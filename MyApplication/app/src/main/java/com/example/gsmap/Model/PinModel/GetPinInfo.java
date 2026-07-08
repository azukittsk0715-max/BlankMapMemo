package com.example.gsmap.Model.PinModel;

import org.json.JSONArray;

public class GetPinInfo {

    public JSONArray getPins(String walkerId) {

        PinSql pinSql = new PinSql();

        return pinSql.getPins(walkerId);

    }

}