package com.example.bikebuddy;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "";
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


    }

    // init data for autocomplete to store
    private LatLng autoCompleteLatLng;
    // initialise places API
    private void initPlaces() {
        // Initialize Places.
        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);
    }
    // initialise autocomplete search bar
    private void initAutoComplete() {
        final AutocompleteSupportFragment autocompleteSupportFragment =
                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // restrict place field results to ID, Address, LatLng, and Name (basic data, no extra fees)
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME));

        // restrict results to nz --- could be changed to grab the user's geolocated country.
        autocompleteSupportFragment.setCountry("nz");

        autocompleteSupportFragment.setOnPlaceSelectedListener((new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // grab found location data from 'place'
                // currently just grabbing LatLng for marker making
                autoCompleteLatLng = place.getLatLng();

                // display found lat long (for debugging)
                //Toast.makeText(MapsActivity.this, "LAT"+autoCompleteLatLng.latitude+"\nLONG"+autoCompleteLatLng.longitude, Toast.LENGTH_LONG).show();

                // remove existing markers (get rid of this in final so markers added by other things aren't removed)
                mMap.clear();
                // go to found location
                mMap.animateCamera(CameraUpdateFactory.newLatLng(autoCompleteLatLng));
                // make marker
                MarkerOptions searchedLocationMarker = new MarkerOptions().position(autoCompleteLatLng).title(place.getAddress());
                mMap.addMarker(searchedLocationMarker);
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        }));

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // stock google maps UI buttons
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // call initialisations
        initPlaces();
        initAutoComplete();

        // start the camera above nz
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(-42, 172)));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(5));
    }
}