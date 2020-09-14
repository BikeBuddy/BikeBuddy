package com.example.bikebuddy;

import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

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

public class JSONRoutes {
    String key;
    GoogleMap mMap;//class has reference to the map

    public JSONRoutes(String key, GoogleMap mMap){
        this.key = key;
        this.mMap = mMap;
    }

    public Trip parseJsonToDirections(String s, LatLng locOne, LatLng locTwo) throws JSONException {
        JSONObject recievedJsonDirections = new JSONObject(s);
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
        newTrip.startLocation = locOne;
        newTrip.endLocation = locTwo;
        newTrip.start = jsonLeg.getString("start_address");
        newTrip.end = jsonLeg.getString("end_address");
        newTrip.encodedPolyLine = jsonPolyline.getString("points");
        newTrip.decodePolyLine();
        System.out.println("99999423423423999999999999999999999943534534523999999999999999999999999999999999999999999999999999999999"+ newTrip.distance.toString());
        return newTrip;
    }

    public void showTrip(Trip aTrip){
        PolylineOptions places = new PolylineOptions();
        for(LatLng point : aTrip.points)
            places.add(point).width(20f).color(Color.RED);
        mMap.addPolyline(places);
        LatLng midPoint = aTrip.points.get(aTrip.points.size()/2);
        mMap.addMarker(new MarkerOptions().position(midPoint)
                .snippet("duration :" + (aTrip.duration/60) +" minutes")
                .title("duration :" +  (aTrip.duration/60) +" minutes")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
    }

    //this method is actually fetching the json string
    public void getDirections(final LatLng one, final LatLng two) {

        String apiUrl1 = "https://maps.googleapis.com/maps/api/directions/json?origin=";
        String apiUrl2 = "&key=" +   key;// add the key here
        String startAndEnd =  one.latitude + "," + one.longitude + "&destination=" + two.latitude +  "," + two.longitude;

        final String  jsonRequestURL =  apiUrl1 + startAndEnd + apiUrl2;
        System.out.println("0000000000000000000000000000000000000000000000000000000000" + jsonRequestURL);
        class GetJSON extends AsyncTask<Void, Void, String> {
            String returnThisString;
            //this method will be called before execution
            //you can display a progress bar or something
            //so that user can understand that he should wait
            //as network operation may take some time
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
       //       ..  Toast.makeText(getApplicationContext(), "onPreExecute is working", Toast.LENGTH_LONG).show();
            }

            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected void onPostExecute(String s) {
                try {
                    showTrip(parseJsonToDirections(s,one,two));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    //creating a URL
                    URL url = new URL(jsonRequestURL);
                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();
                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    //A simple string to read values from each line
                    String json;
                    //reading until we don't find null
                    while ((json = bufferedReader.readLine()) != null) {
                        //appending it to string builder
                        sb.append(json + "\n");
                    }

                    //finally returning the read string
                    //jsonString =sb.toString().trim();
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }
}
