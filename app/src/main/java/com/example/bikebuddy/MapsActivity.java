package com.example.bikebuddy;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private static final String TAG = MapsActivity.class.getSimpleName();

    public WeatherFunctions weatherFunctions;
    public FetchWeather fetchWeather;

    public FetchNearbyPlace fetchNearbyPlace;
    HashMap<String, String> weatherIcons;
    public PlaceFunction placeFunctions;

    public DateTimeFunctions dateTimeFunctions;

    private GoogleMap mMap;

    private float zoomLevel = 10.0f;
    private LatLng currentLocation;//current location the camera is centered on
    private LatLng updateLocation; // location where the map last updated
    private float updateZoom = 0.0f;
    private List<Address> locationsList;//locations for weather icons

    long idleUpdateTimer = 0; // timer for camera idle updates (when map and markers were last redrawn)

    private Geocoder geocoder;//used to obtain the address of a location based on the lat long coordinates

    private CameraPosition cameraPosition;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private TextView currentDateTimeDisplay;
    private TextView maxRange;
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

    private TripManager tripManager;
    private Button routeButton;

    // side menu things
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    boolean darkModeActive = false;


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

        geocoder = new Geocoder(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        routeButton = (Button) findViewById(R.id.route_button);

        tripManager = new TripManager(this);

        // add listener for weather toggle button within the side menu
        final ImageButton sideWeatherButton = (ImageButton) findViewById(R.id.side_menu_weather);
        sideWeatherButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                weatherFunctions.toggleWeather();
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        Button gasButton = (Button) findViewById(R.id.fuel_display_stations);
        gasButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                System.out.println("gas station button clicked");
                fetchNearbyPlace.fetch(currentLocation.latitude, currentLocation.longitude);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        initMapStyle(true);

        initFetchWeather();
        initWeatherFunctions();
        initFetchNearbyPlace();
        initPlaceFunctions();
        initDateTimeFunctions();

        HashMap<String, Drawable> weatherIcons = new HashMap<String, Drawable>();

        tripManager.setUpMapObjects(mMap); //jsonRoutes needs reference to mMap
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

        //action listener for draggable markers
        mMap.setOnMarkerDragListener(this);

        //sets origin to gps location
        tripManager.setUpOriginFromLocation();
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
                LatLng autoCompleteLatLng = place.getLatLng();
                tripManager.setAutoLatLang(autoCompleteLatLng);

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
        if (routeButt.getVisibility() == View.INVISIBLE) {
            routeButt.setVisibility(View.VISIBLE);
        } else {
            routeButt.setVisibility(View.INVISIBLE);
        }
    }

    public void initRoute(View view) {
        setMaxRange();
        tripManager.showRoute();
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
        } catch (SecurityException e) {
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
        } catch (SecurityException e) {
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

                    // only update map drawables if map is moved or zoomed past these amounts
                    if ((zoomLevel - updateZoom) > 1.0f || (zoomLevel - updateZoom) < -1.0f
                            || currentLocation.latitude - updateLocation.latitude > 0.1
                            || currentLocation.latitude - updateLocation.latitude < -0.1
                            || currentLocation.longitude - updateLocation.longitude > 0.1
                            || currentLocation.longitude - updateLocation.longitude < -0.1) {
                        updateZoom = zoomLevel;
                        updateLocation = currentLocation;

                        new getAddressListFromLatLong().execute();

                        idleUpdateTimer = System.nanoTime();
                    }
                }
            };


    //updates the snippet, Address etc when start and destination markers are dragged
    public void onMarkerDragEnd(Marker marker) {
        tripManager.updateMap();
    }

    //Gets 20 locations which are within view in the camera
    class getAddressListFromLatLong extends AsyncTask<Void, Void, List<Address>> {

        @Override
        protected void onPostExecute(List<Address> tempLocationsList) {
            locationsList = tempLocationsList;
            getLocationsWeather();
        }

        @Override
        protected List<Address> doInBackground(Void... voids) {
            try {
                return geocoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 20);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }
    }

    // Pulls weather data from the weather service api and generates weather icons onto the map
    public void getLocationsWeather() {
        if (locationsList != null) {
            for (Address address : locationsList) {
                fetchWeather.fetch(address.getLatitude(), address.getLongitude());
            }
        }
        if (tripManager.getTripDetails() != null) {
            LatLng pointA = tripManager.getTripDetails().firstQuarterPoint;
            LatLng pointB = tripManager.getTripDetails().thirdQuaterPoint;
            fetchWeather.fetch(pointA.latitude, pointA.longitude);
            fetchWeather.fetch(pointB.latitude, pointB.longitude);
        }
        mMap.clear();
        tripManager.updateMap();
    }

    public void initPlaceFunctions() {
        this.placeFunctions = new PlaceFunction(this, this.mMap);
    }

    public void initWeatherFunctions() {
        this.weatherFunctions = new WeatherFunctions(this, this.mMap);
    }

    public void initFetchWeather() {
        this.fetchWeather = new FetchWeather(this);
    }


    public void initFetchNearbyPlace() {
        this.fetchNearbyPlace = new FetchNearbyPlace(this);
    }

    //sets the starting location to gps location, otherwise sets startingLocationNeeded flag to true

    public void onMarkerDragStart(Marker marker) {
    }

    public void onMarkerDrag(Marker marker) {
    }


    // Weather Date/Time stuff
    public void initDateTimeFunctions() {
        //date time display stuff
        currentDateTimeDisplay = findViewById(R.id.currentDateTimeDisplay);
        TextView weatherDateTimeDisplay = findViewById(R.id.weatherDateTimeDisplay);
        Handler handler = new Handler();
        this.dateTimeFunctions = new DateTimeFunctions(this, handler, currentDateTimeDisplay, weatherDateTimeDisplay);
        // this.dateTimeFunctions = new DateTimeFunctions(this, mMap, handler, currentDateTimeDisplay);
        currentDateTimeDisplay.bringToFront();
        weatherDateTimeDisplay.bringToFront();
    }

    public void dateTimeFunctionsPlusHour(View view) {
        dateTimeFunctions.addHour();
    }

    public void dateTimeFunctionsMinusHour(View view) {
        dateTimeFunctions.minusHour();
    }

    public void dateTimeFunctionsResetHour(View view) {
        dateTimeFunctions.resetHour();
    }


    public void initMapStyle(boolean lightMode) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success;
            if (lightMode) {
                success = mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.map_style_light_json));
            } else {
                success = mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.map_style_dark_json));
            }
            if (!success) {
                Log.e("MapsActivityRaw", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivityRaw", "Can't find style.", e);
        }
    }


    public void initSideMenu() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
    }

    // side menu button listener
    public void openSideMenu(View view) {
        if (view.getId() == R.id.side_menu_button) {
            // set side menu as active and clickable
            drawerLayout.openDrawer(Gravity.LEFT);
            navigationView.bringToFront();
            updateSideMenu();
        }
    }

    public void updateSideMenu() {
        // update route information
        Menu navMenu = navigationView.getMenu();

        if (tripManager.getTripDetails() != null && tripManager.getLocations().size() > 0) {// if there are locations, pulls and displays the distance and duration to the side menu

            navMenu.findItem(R.id.duration).setTitle(tripManager.getTripDetails().getTripDuration());
            navMenu.findItem(R.id.distance).setTitle(tripManager.getTripDetails().getTripDistance());
        } else { //if no locations, show default text output.
            navMenu.findItem(R.id.duration).setTitle("Duration: " + "0 Minutes");
            navMenu.findItem(R.id.distance).setTitle("Distance: " + "0 Kilometers");
        }
        SubMenu markerList = navMenu.findItem(R.id.marker_list).getSubMenu();
        markerList.clear();

        // update marker list with current markers
        int index = 0;
        if (tripManager.getLocations().size() > 0) {
            for (BikeBuddyLocation location : tripManager.getLocations()) {
                markerList.add(location.getAddress());
                markerList.getItem(index).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_marker_white));
                if (index > 3)
                    markerList.getItem(index).setVisible(false);
                index++;
            }
        } else {
            markerList.add("No Locations Selected");
        }
    }

    public void sideMenuClear(View view) {
        if (view.getId() == R.id.side_menu_clear) {

            tripManager.routeStarted = false;
            for (int i = 0; i < tripManager.getLocations().size(); i++) {
                tripManager.removeLeg(i);
            }
            tripManager.getLocations().clear();
            tripManager.resetOriginAndDestination();
            tripManager.updateMap();
            updateSideMenu();
            findViewById(R.id.route_button).setVisibility(view.INVISIBLE);
            mMap.clear();
        }
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    public void sideMenuMapStyle(View view) {
        if (view.getId() == R.id.side_menu_map) {
            // change to next map type
            int mapType = mMap.getMapType() + 1;
            // reset back to type 1 if end of types reached
            if (mapType > 4) {
                mapType = 1;
            }
            mMap.setMapType(mapType);
        }
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    public void toggleFuelInfo(View view) {
        if (view.getId() == R.id.side_menu_fuel || view.getId() == R.id.fuel_close) {
            View fuelInf = findViewById(R.id.fuel_info_window);
            if (fuelInf.getVisibility() == View.INVISIBLE) {
                fuelInf.setVisibility(View.VISIBLE);
            } else {
                fuelInf.setVisibility(View.INVISIBLE);
            }
        }
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    public void toggleWeatherTime(View view) {
        View weatherTime = findViewById(R.id.weatherDateTimeDisplay);
        if (weatherTime.getVisibility() == view.INVISIBLE) {
            weatherTime.setVisibility(View.VISIBLE);
            findViewById(R.id.currentDateTimeDisplay).setVisibility(View.VISIBLE);
            findViewById(R.id.weatherDateTimeMinus).setVisibility(View.VISIBLE);
            findViewById(R.id.weatherDateTimePlus).setVisibility(View.VISIBLE);
            findViewById(R.id.weatherDateTimeReset).setVisibility(View.VISIBLE);
        } else {
            weatherTime.setVisibility(View.INVISIBLE);
            findViewById(R.id.currentDateTimeDisplay).setVisibility(View.INVISIBLE);
            findViewById(R.id.weatherDateTimeMinus).setVisibility(View.INVISIBLE);
            findViewById(R.id.weatherDateTimePlus).setVisibility(View.INVISIBLE);
            findViewById(R.id.weatherDateTimeReset).setVisibility(View.INVISIBLE);
        }
        dateTimeFunctions.resetHour();
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    public void toggleDarkMode(View view) {
        darkModeActive = !darkModeActive;
        if (darkModeActive) {
            // dark mode
            findViewById(R.id.side_menu_header).setBackgroundColor(Color.parseColor("#FF1E1E1E"));
            findViewById(R.id.nav_view).setBackground(ContextCompat.getDrawable(this, R.drawable.night_background));
            findViewById(R.id.side_menu_clear).setBackground(ContextCompat.getDrawable(this, R.drawable.black_border));
            findViewById(R.id.side_menu_fuel).setBackground(ContextCompat.getDrawable(this, R.drawable.black_border));
            findViewById(R.id.side_menu_weather).setBackground(ContextCompat.getDrawable(this, R.drawable.black_border));
            findViewById(R.id.side_menu_darkMode).setBackground(ContextCompat.getDrawable(this, R.drawable.black_border));
            findViewById(R.id.side_menu_map).setBackground(ContextCompat.getDrawable(this, R.drawable.black_border));
            findViewById(R.id.side_menu_time).setBackground(ContextCompat.getDrawable(this, R.drawable.black_border));

            initMapStyle(false);

        } else {
            // light mode
            findViewById(R.id.side_menu_header).setBackgroundColor(Color.parseColor("#515151"));
            findViewById(R.id.nav_view).setBackground(ContextCompat.getDrawable(this, R.drawable.light_background));
            findViewById(R.id.side_menu_clear).setBackground(ContextCompat.getDrawable(this, R.drawable.grey_border));
            findViewById(R.id.side_menu_fuel).setBackground(ContextCompat.getDrawable(this, R.drawable.grey_border));
            findViewById(R.id.side_menu_weather).setBackground(ContextCompat.getDrawable(this, R.drawable.grey_border));
            findViewById(R.id.side_menu_darkMode).setBackground(ContextCompat.getDrawable(this, R.drawable.grey_border));
            findViewById(R.id.side_menu_map).setBackground(ContextCompat.getDrawable(this, R.drawable.grey_border));
            findViewById(R.id.side_menu_time).setBackground(ContextCompat.getDrawable(this, R.drawable.grey_border));

            initMapStyle(true);

        }
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    public GoogleMap getmMap() {
        return this.mMap;
    }

    public TripManager getTripManager() {
        return tripManager;
    }

    public Button getRouteButton() {
        return routeButton;
    }

    //Takes the input maximum range from the user and stores it for use.
    public void setMaxRange() {
        TextView maxRange = (TextView) findViewById(R.id.textMaxRange);
        try {
            Integer range = Integer.parseInt(maxRange.getText().toString());
            //check for invalid inputs.
            if (range < 1 || range > 600)
                throw new NumberFormatException();
            else
                tripManager.setMaxFuelRange(range);
        } catch (NumberFormatException ex) {
            tripManager.setMaxFuelRange(100);
        }
    }
}
