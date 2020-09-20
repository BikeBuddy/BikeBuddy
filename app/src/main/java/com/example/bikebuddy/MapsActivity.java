package com.example.bikebuddy;

import androidx.annotation.NonNull;



import android.content.Context;


import android.location.Address;
import android.location.Geocoder;
import android.view.inputmethod.InputMethodManager;


import com.google.android.gms.common.api.Status;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener  {

    private static final String TAG = MapsActivity.class.getSimpleName();

    private Geocoder gc;
    private GoogleMap mMap;
    private JSONRoutes jsonRoutes;// send requests and show routes on map with this object--PK
    private CameraPosition cameraPosition;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Address addressFromLongPress;  //The long click listener will set the location pressed to this variable --PK
    private MarkerQueue markerFromLongPress; //the marker from long press

    // A default location (Auckland, New Zealand) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-36.8483, 174.7625);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Locations for route planning
    private LatLng startingLocation = null;
    private LatLng destination = null;
    private LatLng autoCompleteLatLng = null;

    // Boolean for telling route initialization the user has no location
    Boolean startingLocationNeeded = false;

    // Markers for route locations
//    MarkerOptions startMarker = null;
//    MarkerOptions destMarker = null;

    BikeBuddyLocation startingOrigin;
    BikeBuddyLocation theDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.activity_maps);
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        gc = new Geocoder(this);

    }


    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }


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

                // go to found location
                mMap.animateCamera(CameraUpdateFactory.newLatLng(autoCompleteLatLng));
                // make marker
                MarkerOptions searchedLocationMarker = new MarkerOptions().position(autoCompleteLatLng).title(place.getAddress());
                mMap.addMarker(searchedLocationMarker);

               toggleRouteButton();
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        }));

    }

    private void setAutoCompleteLatLang(LatLng latLang){
        autoCompleteLatLng = latLang;
        if(startingLocation==null){
            startingLocation = latLang;
        }else if(destination==null){
            destination= latLang;
        }else{
            startingLocation=null;
            destination=null;
            startingLocation = latLang;
        }
    }

    private void toggleRouteButton() {
        // make route button visible
        View routeButt = findViewById(R.id.route_button);
        if(routeButt.getVisibility() == View.INVISIBLE)
        {
            routeButt.setVisibility(View.VISIBLE);
        }
        else
        {
            routeButt.setVisibility(View.INVISIBLE);
        }
    }


    public void initRoute(View view) {

        if(view.getId() == R.id.route_button) {
            // if need to set starting locale
            if(startingLocationNeeded)
            {
                // replace selected location marker with destination marker
                mMap.clear();
                mMap.addMarker(destMarker);

                // set searched location as start
                /** allow for using directly selected locations too **/
                if (autoCompleteLatLng != null) {
                    startingLocation = autoCompleteLatLng;
                    // place marker

                   // startMarker = new MarkerOptions().position(startingLocation).title("Start");
                 //   mMap.addMarker(startMarker);
                    // debug toast
                    startingOrigin = new BikeBuddyLocation(true, gc, startingLocation, mMap);
                    Toast.makeText(this, "Start: " + startingLocation + "\nDestination: " + destination, Toast.LENGTH_LONG).show();
                }
                startingLocationNeeded = false;
            }
            // standard init / location setting
            else {
                // clear markers
                startMarker = null;
                destMarker = null;
                mMap.clear();

                // set searched location as destination
                /** allow for using directly selected locations too **/
                if (autoCompleteLatLng != null) {
                    destination = autoCompleteLatLng;
                    // place marker
                 //   destMarker = new MarkerOptions().position(destination).title("Destination");
                //    mMap.addMarker(destMarker);
                    theDestination = new BikeBuddyLocation(false, gc, destination, mMap);
                }
                // set user's location as start if known
                if (lastKnownLocation != null) {
                    startingLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    // place marker
                 //   startMarker = new MarkerOptions().position(startingLocation).title("Start");
                //    mMap.addMarker(startMarker);
                    // debug toast
                    startingOrigin = new BikeBuddyLocation(true, gc, startingLocation, mMap);
                    Toast.makeText(this, "Start: " + startingLocation + "\nDestination: " + destination, Toast.LENGTH_LONG).show();
                }
                else
                {
                    // if a starting location already selected
                    if(startingLocation != null)
                    {
                    //  startMarker = new MarkerOptions().position(startingLocation).title("Start");
                   //   mMap.addMarker(startMarker);
                        startingOrigin = new BikeBuddyLocation(true, gc, startingLocation, mMap);
                        // debug toast
                        Toast.makeText(this, "Start: " + startingLocation + "\nDestination: " + destination, Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(this, "User Location not found.\nPlease select starting location.", Toast.LENGTH_LONG).show();
                        startingLocationNeeded = true;
                    }
                }
            }
            // locations set, show route
            if(startingOrigin !=null || theDestination!=null)
            {
                try {
                    Toast.makeText(this, "start is : "+ startingLocation.toString()+" DEST IS"+destination.toString(), Toast.LENGTH_LONG).show();
                    jsonRoutes.getDirections(startingOrigin.coordinate, theDestination.coordinate);
                }catch (Exception e){
                    System.err.println(e);
                }
            }
            // hide button
          //  toggleRouteButton();
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.mMap = googleMap;
        this.jsonRoutes = new JSONRoutes(getResources().getString(R.string.google_maps_key), mMap); //jsonRoutes needs reference to mMap
        // stock google maps UI buttons
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // call initialisations
        initPlaces();
        initAutoComplete();

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        this.mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.map), false);

                TextView title = infoWindow.findViewById(R.id.title);
                title.setText(marker.getTitle());

                TextView snippet = infoWindow.findViewById(R.id.snippet);
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        mMap.setOnMarkerDragListener(this);

        markerFromLongPress = new MarkerQueue(startingLocationNeeded);//allows 1 marker on map if start location isnt needed, allows 2 if its needed
        //ActionListener for long press
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            public void onMapLongClick(LatLng latLng) {
                try {
                    BikeBuddyLocation aPlace;
                    if(startingOrigin==null) {
                        startingOrigin = new BikeBuddyLocation(true, gc, latLng, mMap);
                        startingLocationNeeded = false;
                    }
                    else if(theDestination== null) {
                        theDestination = new BikeBuddyLocation(false, gc, latLng, mMap);
                        aPlace = theDestination;
                    }else{
                        theDestination.setCoordinate(latLng);
                    }
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
               //     addressFromLongPress = gc.getFromLocation(latLng.latitude,latLng.longitude,1).get(0);
               //     Marker aMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(addressFromLongPress.getLocality()));
               //     markerFromLongPress.addMarker(aMarker);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onMarkerDragStart(Marker marker) {
    }
    @Override
    public void onMarkerDrag(Marker marker) {
    }

    public void onMarkerDragEnd(Marker marker) {
       startingOrigin.update();
       theDestination.update();
    }





    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            } else {
                //location services denied, move camera to default location
                mMap.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                mMap.getUiSettings().setMyLocationButtonEnabled(false);

            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                getDeviceLocation();
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                //getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}