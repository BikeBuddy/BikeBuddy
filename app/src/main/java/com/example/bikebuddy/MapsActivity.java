package com.example.bikebuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import android.location.Geocoder;
import android.location.Address;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private  MarkerQueue mapMarkers;
    private GoogleMap mMap;
    private String jsonString;
    Geocoder gc;
    JSONObject mapsJson;

   // @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        GoogleMapOptions options = new GoogleMapOptions();
        mapFragment.newInstance(options);
        options.mapToolbarEnabled(true);
        // Make Toast
        gc = new Geocoder(this);
        mapMarkers = new MarkerQueue(false); //
    }

    private class MarkerQueue{
        private  Queue<Marker>  markers;
        private  int markerLimit;  //the limit of markers which are generated from long press
        public MarkerQueue(boolean flag){
            markers = new LinkedList<Marker>();
            if(flag){
                markerLimit=1;
            }else{
                markerLimit =2;
            }
        }
        public void addMarker(Marker marker){
            if(markerLimit<= markers.size()){
                Marker oldMarker = markers.remove();//.poll();
                oldMarker.setVisible(false);
            }markers.add(marker);
        }
        public void setMarkerLimit(int limit){
            this.markerLimit = limit;
        }
        public Marker getMarker(){
            return markers.poll();
        }
        public Queue<Marker>  getMarkers(){
            return markers;
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        final ArrayList<LatLng> locations = new ArrayList<LatLng>();
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mapMarkers.addMarker(mMap.addMarker(new MarkerOptions().position(latLng).title("Your marker title").snippet("Your marker snippet")));
                if(mapMarkers.getMarkers().size()>1){
                    // places.add().add(locations.get(1)).width(2f).color(Color.RED);
                    LatLng one = mapMarkers.getMarker().getPosition();
                    LatLng two = mapMarkers.getMarker().getPosition();
                    Address location1 = null;
                    Address location2 = null;
                    try {
                       location1  = gc.getFromLocation(one.latitude,one.longitude,1).get(0);
                       location2 = gc.getFromLocation(two.latitude,two.longitude,1).get(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    getDirections(one, two);
                }
            }
        });
    }

    public class Trip {
        Integer distance;
        Integer duration;
        LatLng startLocation;
        String start;
        String end;
        LatLng endLocation;
        String encodedPolyLine;
        Polyline tripPolyline;
        ArrayList<LatLng> points;
        private void decodePolyLine() {
            int len = encodedPolyLine.length();
            int index = 0;
            points = new ArrayList<LatLng>();
            int lat = 0;
            int lng = 0;
            while (index < len) {
                int b;
                int shift = 0;
                int result = 0;
                do {
                    b = encodedPolyLine.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int decodedLat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += decodedLat;
                shift = 0;
                result = 0;
                do {
                    b = encodedPolyLine.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int decodedLong = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += decodedLong;
                points.add(new LatLng(lat / 100000d, lng / 100000d
                ));
            }
        }
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

    //    setContentDescription("Duration: " + (aTrip.duration));
    }

    //this method is actually fetching the json string
    private void getDirections(final LatLng one, final LatLng two) {
        String apiUrl1 = "https://maps.googleapis.com/maps/api/directions/json?origin=";
        String apiUrl2 = "&key=";// add the key here
        String startAndEnd =  one.latitude + "," + one.longitude + "&destination=" + two.latitude +  "," + two.longitude;
        final String  jsonRequestURL =  apiUrl1 + startAndEnd + apiUrl2;


        class GetJSON extends AsyncTask<Void, Void, String> {
            String returnThisString;
            //this method will be called before execution
            //you can display a progress bar or something
            //so that user can understand that he should wait
            //as network operation may take some time
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Toast.makeText(getApplicationContext(), "onPreExecute is working", Toast.LENGTH_LONG).show();
            }

            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected  void onPostExecute(String s) {
                returnThisString =s;
                jsonString = new String(s);
                try {
                    Trip newTrip = parseJsonToDirections(s,one, two);
                    showTrip(newTrip);
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
                    jsonString =sb.toString().trim();
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
      //  return getJSON.returnThisString;
    }


}