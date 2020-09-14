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

    private  MarkerQueue mapMarkers;// the markers present on map via long press
    private GoogleMap mMap;
    Geocoder gc;
    private JSONRoutes jsonRoutes;

   // @Override
    protected void onCreate(Bundle savedInstanceState){
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
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------");
        mapMarkers = new MarkerQueue(false); //

    }


    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;
        jsonRoutes = new JSONRoutes(getResources().getString(R.string.google_maps_key), mMap);
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

                }
            }
        });
    }
}