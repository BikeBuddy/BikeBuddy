package com.example.bikebuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private  MarkerQueue mapMarkers;
    private GoogleMap mMap;
    //push test PK
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        Toast.makeText(this, "Hello?", Toast.LENGTH_LONG).show();
        mapMarkers = new MarkerQueue(false); //
    }

    //
    private class MarkerQueue{
        private  Queue<Marker>  markers;
        private  int markerLimit;  //the limit of markers which are generated from long press


        public MarkerQueue(boolean flag){
            markers = new LinkedList<Marker>();
            if(flag){
                markerLimit=2;
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
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
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
                locations.add(latLng);
                mapMarkers.addMarker(mMap.addMarker(new MarkerOptions().position(latLng).title("Your marker title").snippet("Your marker snippet")));
                if(locations.size()>1){
                    PolylineOptions places = new PolylineOptions();
                    places.add(locations.get(0)).add(locations.get(1)).width(2f).color(Color.RED);
                    mMap.addPolyline(places);
                }
            }
        });
    }



}