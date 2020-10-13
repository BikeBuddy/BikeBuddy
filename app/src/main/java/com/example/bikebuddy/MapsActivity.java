package com.example.bikebuddy;

import androidx.annotation.NonNull;


import android.app.Activity;
import android.content.Context;


import android.location.Address;
import android.location.Geocoder;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.inputmethod.InputMethodManager;


import com.google.android.gms.common.api.Status;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import java.util.Locale;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.internal.NavigationMenuItemView;
import com.google.android.material.navigation.NavigationView;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback , GoogleMap.OnMarkerDragListener {

    private static final String TAG = MapsActivity.class.getSimpleName();

    public WeatherFunctions weatherFunctions;
    public FetchWeather fetchWeather;
    HashMap<String, String> weatherIcons;

    private GoogleMap mMap;

    private float zoomLevel = 10.0f;
    private LatLng currentLocation;//current location the camera is centered on
    private List<Address> locationsList;//locations for weather icons
    private Bitmap smallMarker; //weather icons

    // init data for autocomplete to store
    private LatLng autoCompleteLatLng;

    private Geocoder gc;//used to obtain the address of a location based on the lat long coordinates

    private JSONRoutes jsonRoutes;// send requests and show routes on map with this object--PK
    private CameraPosition cameraPosition;
    private FusedLocationProviderClient fusedLocationProviderClient;



    // A default location (Auckland, New Zealand) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-36.8483, 174.7625);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    protected Location lastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Locations for route planning/generating
    private BikeBuddyLocation startingOrigin;
    private BikeBuddyLocation theDestination;


    // Boolean for telling route initialization the user has no location
    Boolean startingLocationNeeded = false;

    // side menu things
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    boolean routeStarted = false;//flag determined if a poly line between start and destination markers is drawn or not after map has been cleared

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     //    Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.activity_maps);
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        gc = new Geocoder(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        gc = new Geocoder(this);

        // set onClick listener for "Show Weather" button to show/hide markers on the map when pressed
        final Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                weatherFunctions.toggleWeather();
                System.out.print("hello");
            }
        });
        // add listener for weather toggle button within the side menu
        final Button sideWeatherButton = (Button) findViewById(R.id.side_menu_weather);
        sideWeatherButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                weatherFunctions.toggleWeather();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        initFetchWeather();
        initWeatherFunctions();

        HashMap<String, Drawable> weatherIcons = new HashMap<String, Drawable>();

        this.jsonRoutes = new JSONRoutes(getResources().getString(R.string.google_maps_key), mMap); //jsonRoutes needs reference to mMap
        // stock google maps UI buttons
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // call initialisations
        initPlaces();
        initAutoComplete();
        initSideMenu();


        this.mMap.setOnCameraIdleListener(onCameraIdleListener);

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        this.mMap.setInfoWindowAdapter(customInfoWindowAdapter);

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        //sets origin to gps location
        setUpOriginFromLocation();

        //action listener for draggable markers
        mMap.setOnMarkerDragListener(this);

        //ActionListener for long press --PK
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            public void onMapLongClick(LatLng latLng) {
                setAutoCompleteLatLang(latLng);
                mMap.clear();
                updateMap();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });
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
                setAutoCompleteLatLang(autoCompleteLatLng);

                // go to found location
                mMap.animateCamera(CameraUpdateFactory.newLatLng(autoCompleteLatLng));
            }
            @Override
            public void onError(@NonNull Status status) {

            }
        }));

    }



    //Toggles the buttons visibility
    public void toggleRouteButton() {
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
        // locations set, show route
        if(startingOrigin !=null || theDestination!=null){
            try {
                routeStarted = true; //sets flag so that the polyline for the route will be redrawn if map is cleared
                mMap.clear();
                updateMap();//adds polyline and markers onto map
            }catch (Exception e){
                System.err.println(e);
            }
        }else if(theDestination == null){
            Toast.makeText(this, "Please Select Destination", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Please Select Origin", Toast.LENGTH_LONG).show();
        }

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


    // Use a custom info window adapter to handle multiple lines of text in the
    // info window contents.
    private GoogleMap.InfoWindowAdapter customInfoWindowAdapter =
            new GoogleMap.InfoWindowAdapter() {
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
            };

    //when the camera goes idle, new weather icons are drawn on the map
    private GoogleMap.OnCameraIdleListener onCameraIdleListener =
        new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                zoomLevel = mMap.getCameraPosition().zoom;
                currentLocation = mMap.getCameraPosition().target;

                //creates new list of locations based on camera centre position.
                locationsList = getAddressListFromLatLong(currentLocation.latitude, currentLocation.longitude);

                getLocationsWeather();
            }
        };


    //updates the snippet, Address etc when start and destination markers are dragged
    public void onMarkerDragEnd(Marker marker) {
        startingOrigin.update();
        if(theDestination!=null ) {
            theDestination.update();
        }
        mMap.clear();//clears the old poly line if there was one
        startingOrigin.createMarker();
        if(theDestination!=null)
            theDestination.createMarker();
    }

    //LatLng which are generated by long press on the map or from the address entered from the search bar will be input into this function
    //for the input latLang, sets the origin if not already set, if the origin is set,the latLang is used to set the destination
    public void setAutoCompleteLatLang(LatLng latLang){
        autoCompleteLatLng = latLang;
        if(startingOrigin==null){
            startingOrigin = new BikeBuddyLocation(true,gc,latLang, mMap);
            startingOrigin.createMarker();
            startingLocationNeeded = false;
        }else if(theDestination==null){
            theDestination = new BikeBuddyLocation(false,gc,latLang, mMap);
            theDestination.createMarker();
        }else{//once both origin and destination has been set, all input LatLng will be used to update the destination
            theDestination.setCoordinate(latLang);
            theDestination.createMarker();
        }
        if(theDestination != null && findViewById(R.id.route_button).getVisibility() == View.INVISIBLE)//if the destination has been selected for the first time, then the button will become visible
            toggleRouteButton();
    }




    //Gets 20 locations which are within view in the camera
    public  List<Address> getAddressListFromLatLong(double lat, double lng) {

        Geocoder geocoder = gc;
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocation(lat, lng, 20);
            // 20 is no of address you want to fetch near by the given lat-long
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return addressList;
    }

    // Pulls weather data from the weather service api and generates weather icons onto the map
    public void getLocationsWeather() {
        if (locationsList != null ){
        //had to change to iterator in order to delete
        Iterator<Address> it = locationsList.iterator();
            while (it.hasNext()) {
                Address a = it.next();
                fetchWeather.fetch(a.getLatitude(), a.getLongitude());
                if(it.hasNext())
                    it.remove();
            }
        }
        mMap.clear();
        updateMap();
    }

    public void initWeatherFunctions() {
       this.weatherFunctions = new WeatherFunctions(this, this.mMap);
    }

    public void initFetchWeather() {
        this.fetchWeather = new FetchWeather(this);
    }


    //sets the starting location to gps location, otherwise sets startingLocationNeeded flag to true
    public void setUpOriginFromLocation(){
        if(lastKnownLocation==null){
            startingLocationNeeded =true;
        }else{
            LatLng startLatLong = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
            startingOrigin = new BikeBuddyLocation(true,gc, startLatLong, mMap);
            startingLocationNeeded = false;
        }
    }


    //redraws all the markers and polyline onto map
    public void updateMap(){
        if(startingOrigin!=null)
            startingOrigin.createMarker();
        if(theDestination!=null)
            theDestination.createMarker();
        if(routeStarted)
            jsonRoutes.getDirections(startingOrigin.coordinate, theDestination.coordinate);
    }
    public void onMarkerDragStart(Marker marker) {    }
    public void onMarkerDrag(Marker marker) {    }

    public BikeBuddyLocation getStartingOrigin() {
        return startingOrigin;
    }

    public BikeBuddyLocation getTheDestination() {
        return theDestination;
    }

    public void initSideMenu()
    {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
    }

    // side menu button listener
    public void openSideMenu(View view) {
        if(view.getId() == R.id.side_menu_button)
        {
            // set side menu as active and clickable
            drawerLayout.openDrawer(Gravity.LEFT);
            navigationView.bringToFront();

            updateSideMenu();
        }
    }

    public void updateSideMenu()
    {
        // update route information
        Menu navMenu = navigationView.getMenu();
        navMenu.findItem(R.id.duration).setTitle("Duration: "+"20 minutes");
        navMenu.findItem(R.id.distance).setTitle("Distance: "+"12km");

        SubMenu markerList = navMenu.findItem(R.id.marker_list).getSubMenu();
        markerList.clear();
        // update marker list with current markers
        /**
         *  Currently hard coded in 3 empty markers.
         *  Once access to marker array, loop through and create entry for each marker.
         */
        markerList.add("Marker 1");
        markerList.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_marker));
        markerList.add("Marker 2");
        markerList.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_marker));
        markerList.add("Marker 3");
        markerList.getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_marker));
    }

    public void sideMenuClear(View view) {
        if(view.getId() == R.id.side_menu_clear)
        {
            /**
             * still needs to actually delete markers, currently just clears for current draw
             */
            mMap.clear();
            updateSideMenu();
        }
    }

    public void sideMenuMapStyle(View view) {
        if(view.getId() == R.id.side_menu_map) {
            // change to next map type
            int mapType = mMap.getMapType() + 1;
            // reset back to type 1 if end of types reached
            if(mapType > 4)
            {
                mapType = 1;
            }
            mMap.setMapType(mapType);
        }
    }

}

