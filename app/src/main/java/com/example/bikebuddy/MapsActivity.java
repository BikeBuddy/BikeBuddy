package com.example.bikebuddy;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
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

    // Gets the code of the country where the device is located. This will only get a location on devices with a SIM card currently.
    //FIXME This may cause problems if we have users in Europe, doing rides from country to country.
    public String getCountryCode() {
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCodeValue = tm.getNetworkCountryIso();
        //check if country code is provided, otherwise return null to enable a global search.
        if (countryCodeValue != null) {
            return countryCodeValue.toLowerCase();
        } else
            return null;
//        return "nz";  //FIXME Hard coded to return "nz" currently for testing and development purposes
    }

    // initialise autocomplete search bar
    private void initAutoComplete() {
        final AutocompleteSupportFragment autocompleteSupportFragment =
                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // restrict place field results to ID, Address, LatLng, and Name (basic data, no extra fees)
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME));

        // Restricts search results to the user's geo-located country.
        autocompleteSupportFragment.setCountry(getCountryCode());

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


    @RequiresApi(api = Build.VERSION_CODES.M)
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