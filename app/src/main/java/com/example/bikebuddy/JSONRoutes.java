package com.example.bikebuddy;


import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import java.util.ArrayList;
import java.util.List;

//@Author PK
public class JSONRoutes {
    private String key;
    private GoogleMap mMap;//class has reference to the main map fragment
    private ArrayList<LatLng> locations;
    protected MapsActivity mapsActivity;
    private Trip newTrip;

    public JSONRoutes(String key, GoogleMap mMap){
        this.key = key;
        this.mMap = mMap;
        locations = new ArrayList<>();
    }


    //parses a Json response into a Trip object and returns it
    public Trip parseJsonToDirections(String jsonString)throws JSONException, UnsupportedOperationException{
        JSONObject recievedJsonDirections = new JSONObject(jsonString);
        String responseStatus = recievedJsonDirections.getString("status");
        // String responseStatus = status.getString("value");
        if(responseStatus.equals("OK")) {
            return parseDirectionsToTrip(recievedJsonDirections);
        }
        //else if google directions is unable to find a route for the specified locations
        else if(responseStatus.equals("NOT_FOUND") || responseStatus.equals("ZERO_RESULTS")) {
            Toast.makeText(mapsActivity.getApplicationContext(), "A route can not be generated from the locations you have chosen", Toast.LENGTH_LONG);
        }
        return null;
    }

    //Parses JSON Object to a Trip class
    public Trip parseDirectionsToTrip(JSONObject recievedJsonDirections) throws JSONException {
        JSONArray jsonRoutes = recievedJsonDirections.getJSONArray("routes");
        JSONObject jsonRoute = jsonRoutes.getJSONObject(0);
        JSONObject jsonPolyline = jsonRoute.getJSONObject("overview_polyline");
        JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
        JSONObject jsonLeg = jsonLegs.getJSONObject(0);
        JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
        JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
        newTrip = new Trip();
        newTrip.distance = jsonDistance.getInt("value");
        newTrip.duration = jsonDuration.getInt("value");
        newTrip.startLocation = locations.get(0);
        newTrip.endLocation = locations.get(locations.size() - 1);
        newTrip.start = jsonLeg.getString("start_address");
        newTrip.end = jsonLeg.getString("end_address");
        newTrip.encodedPolyLine = jsonPolyline.getString("points");
        newTrip.decodePolyLine();
        return newTrip;
    }

    //shows the polyline(route) of a trip onto the map
    //@param: Trip, the trip to be shown on the map
    public void showTrip(Trip aTrip){
        PolylineOptions places = new PolylineOptions();
        for (LatLng point : aTrip.points)
            places.add(point).width(20f).color(Color.RED);
        mMap.addPolyline(places);
        LatLng midPoint = aTrip.points.get(aTrip.points.size() / 2);
        mMap.addMarker(new MarkerOptions().position(midPoint)
                .snippet(aTrip.getTripDuration())
                .title(aTrip.getTripDistance()).icon(null).flat(true)
        ).showInfoWindow();//BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)

    }

    public void executeResponse(String jsonString)throws UnsupportedOperationException{
        try {
            if(!hasMultipleLegs()){
                LatLng start = locations.get(0);
                LatLng destination = locations.get(1);
                Trip trip = parseJsonToDirections(jsonString);
                if(trip != null)
                    showTrip(trip);
            }else{
                Trip trip = parseJsonToDirections(jsonString);
                if(trip != null)
                    showTrip(trip);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //   Toast.makeText(mapsActivity, jsonString, Toast.LENGTH_LONG).show();
    }

    //takes in two LatLong locations, sends a request to google maps, parses the repsonse to a Trip and shows it on the map
    public void getDirections() throws UnsupportedOperationException{
        String apiUrl1 = "https://maps.googleapis.com/maps/api/directions/json?origin=";
        String apiUrl2 = "&key=" + key;
        String startAndEnd = getStartLocation().latitude + "," + getStartLocation().longitude + "&destination=" + getDestination().latitude + "," + getDestination().longitude;
        if(hasMultipleLegs()){
            startAndEnd += "&waypoints=";
            int lastLeg = locations.size() - 1;
            for(int i = 1; i < lastLeg; i++){//iteration starts at second index position and ends at second to last
                LatLng leg = locations.get(i);
                String waypoint = "via:" + leg.latitude +"%2C"+ leg.longitude;
                if(i < lastLeg){//%7C is not added to last waypoint
                    waypoint += "%7C";
                }
                startAndEnd += waypoint;
            }
        }
        String jsonRequestURL = apiUrl1 + startAndEnd + apiUrl2;
        getDirectionsResponse(jsonRequestURL);
    }

    public void getDirectionsResponse(final String jsonRequestURL)throws UnsupportedOperationException{
        class GetJSON extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected void onPostExecute(String jsonString)throws UnsupportedOperationException{
                executeResponse(jsonString);
                //  Toast.makeText(mapsActivity, jsonString, Toast.LENGTH_LONG).show();
                // showTrip(parseJsonToDirections(jsonString,start,destination));
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

    public boolean hasMultipleLegs(){
        if(locations.size()>2){
            return true;
        }else{
            return false;
        }
    }

    public LatLng getStartLocation(){
        return locations.get(0);
    }

    public LatLng getDestination(){
        return locations.get(locations.size()-1);
    }


    public void setLocations(ArrayList<LatLng> locations){
        this.locations = locations;
    }

    public Trip getTrip(){
        return newTrip;
    }

}