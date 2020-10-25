package com.example.bikebuddy;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static java.lang.Thread.*;

public class AsyncGeoCoder extends AsyncTask<String, Void, String> {

    private static String key;
    private BikeBuddyLocation location;
    private String url;

    public AsyncGeoCoder(BikeBuddyLocation location){
        this.url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + location.getCoordinate().latitude + ","
                + location.getCoordinate().longitude + "&key=" + key;
        this.location = location;
    }

    public static void setKey(String key){
        AsyncGeoCoder.key = key;
    }

    //Extracts the address details returned by google maps API
    public void executeRequest(String jsonString){
        try {
            JSONObject fetchedPlace = new JSONObject(jsonString);
            String status = fetchedPlace.getString("status");
            if(status.equals("OK")){
                JSONArray results = fetchedPlace.getJSONArray("results");
                JSONObject result = results.getJSONObject(0);
                String address = result.getString("formatted_address");
                JSONArray addressComponents = results.getJSONObject(0).getJSONArray("address_components");
                if(addressComponents.length() > 3) {//if the full address is long, just the first 4 address details are used
                    String stNumber = addressComponents.getJSONObject(0).getString("short_name");
                    String street = addressComponents.getJSONObject(1).getString("short_name");
                    String suburb = addressComponents.getJSONObject(2).getString("short_name");
                    String city = addressComponents.getJSONObject(3).getString("short_name");
                    address = stNumber + " " + street + ", " + suburb + ", " + city;
                }
                System.out.println("\n \n \n" + address + "\n \n \n");
                location.setAddress(address);
                location.updateSnippet();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void onPostExecute(String jsonString) {
        executeRequest(jsonString);
    }


    protected String doInBackground(String... strings) {
        try {
            URL url = new URL(this.url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            StringBuilder sb = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String json;
            while ((json = bufferedReader.readLine()) != null) {
                sb.append(json + "\n");
            }
            //finally returning the read string
            return sb.toString().trim();
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

}