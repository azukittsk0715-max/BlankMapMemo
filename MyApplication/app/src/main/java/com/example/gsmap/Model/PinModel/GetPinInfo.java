package com.example.gsmap.Model.PinModel;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetPinInfo {

    private static final String URL_STRING =
            "http://172.21.33.121:7070/pin?walker_id=";

    public JSONArray getPins(String walkerId){

        try{

            URL url = new URL(URL_STRING + walkerId);

            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    connection.getInputStream()
                            )
                    );

            StringBuilder result = new StringBuilder();

            String line;

            while((line = reader.readLine()) != null){
                result.append(line);
            }

            reader.close();

            return new JSONArray(result.toString());

        }catch(Exception e){

            e.printStackTrace();
        }

        return new JSONArray();
    }

}