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

//@Author PK
public class JSONRoutes {
    String key;
    GoogleMap mMap;//class has reference to the main map fragment
    Trip tmpTrip; // The current trip
    public JSONRoutes(String key, GoogleMap mMap){
        this.key = key;
        this.mMap = mMap;
    }


    //parses a Json response into a Trip object and returns it
    public Trip parseJsonToDirections(String jsonString, LatLng start, LatLng Destination) throws JSONException {
        JSONObject recievedJsonDirections = new JSONObject(jsonString);
        JSONArray jsonRoutes =recievedJsonDirections.getJSONArray("routes");
        JSONObject jsonRoute = jsonRoutes.getJSONObject(0);
        JSONObject jsonPolyline = jsonRoute.getJSONObject("overview_polyline");
        JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
        JSONObject jsonLeg = jsonLegs.getJSONObject(0);
        JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
        JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
        Trip newTrip = new Trip();
        newTrip.distance =  jsonDistance.getInt("value");
        newTrip.duration =  jsonDuration.getInt("value");
        newTrip.startLocation = start;
        newTrip.endLocation = Destination;
        newTrip.start = jsonLeg.getString("start_address");
        newTrip.end = jsonLeg.getString("end_address");
        newTrip.encodedPolyLine = jsonPolyline.getString("points");
        newTrip.decodePolyLine();
        tmpTrip = newTrip;
        return newTrip;
    }

    //shows the polyline(route) of a trip onto the map
    //@param: Trip, the trip to be shown on the map
    public void showTrip(Trip aTrip){
        PolylineOptions places = new PolylineOptions();
        for(LatLng point : aTrip.points)
            places.add(point).width(20f).color(Color.RED);
        mMap.addPolyline(places);
        LatLng midPoint = aTrip.points.get(aTrip.points.size()/2);
        mMap.addMarker(new MarkerOptions().position(midPoint)
                .snippet(aTrip.getTripDuration())
                .title(aTrip.getTripDistance())).showInfoWindow();
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