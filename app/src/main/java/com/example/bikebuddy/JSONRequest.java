package com.example.bikebuddy;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class JSONRequest {

    String key;
    GoogleMap mMap;//class has reference to the main map fragment

    public JSONRequest(GoogleMap mMap, MapAction action){
        this.mMap = mMap;
        this.action = action;
    }

    //takes in two LatLong locations, sends a request to google maps, parses the repsonse to a Trip and shows it on the map
    public void getDirections(final LatLng start, final LatLng destination) {
        String apiUrl1 = "https://maps.googleapis.com/maps/api/directions/json?origin=";
        String apiUrl2 = "&key=" +   key;
        String startAndEnd =  start.latitude + "," + start.longitude + "&destination=" + destination.latitude +  "," + destination.longitude;
        final String  jsonRequestURL =  apiUrl1 + startAndEnd + apiUrl2;

        class GetJSON extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected void onPostExecute(String jsonString) {
                try {
                    showTrip(parseJsonToDirections(jsonString,start,destination));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {
                try {

                    URL url = new URL(jsonRequestURL);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();//returning the recieved json response to postExecutue
                } catch (Exception e) {
                    return null;
                }
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }
}
