package com.example.bikebuddy;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import android.os.Handler;
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private boolean showLogo = true;
    private Marker marker;
    private Marker marker1;
    private ArrayList<City> city = new ArrayList<City>();
    private ArrayList<Marker> markerArray = new ArrayList<Marker>();
    public static String sky = "Raining";
    public static String cityName = "Hong Kong";
    public static double lon =114.1694;
    public static double lat =22.3193;
    private float zoomLevel = 10.0f;
    private LatLng currentLocation;
    private ArrayList<String> cityToFetch = new ArrayList();
    //push test PK
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        generateCities();

        // set onClick listener for "Show Weather" button to show/hide markers on the map when pressed
        final Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                System.out.println("button clicked");
//                System.out.println(showLogo);
//                System.out.println(city.get(0).getLat());
                toggleWeather();
            }
        });

        // fetch data from openWeatherMap
        FetchWeather fw = new FetchWeather();
        cityToFetch.add("rome,italy");
        cityToFetch.add("auckland,newzealand");

        for(String fetchCity : cityToFetch){
            fw.getJSON("http://api.openweathermap.org/data/2.5/weather?q=" + fetchCity +"&APPID=d2222fc373d644fa109aea09a4046a3c");
        }
//        fw.getJSON("http://api.openweathermap.org/data/2.5/weather?q=" + cityToFetch.get(0)+"&APPID=d2222fc373d644fa109aea09a4046a3c");
//        fw.getJSON("http://api.openweathermap.org/data/2.5/weather?q="+ cityToFetch.get(1)+"&APPID=d2222fc373d644fa109aea09a4046a3c");

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //print the global variable and check if it is the most update one from openWeatherApi
                System.out.println("MapsActivity global variable: ");
                System.out.println(lon);
                System.out.println(lat);
                System.out.println(sky);
                System.out.println(cityName);
                mMap.clear();
                displayCities(generateIcons());
            }
        }, 5000);
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
//        currentLocation = new LatLng(city.get(0).getLat(), city.get(0).getLng());
        // custom the size of the weather icon
        Bitmap smallMarker = generateIcons();

        // Add a marker on map
        displayCities(smallMarker);

//      setOnnCameraIdle didn't work
//        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
//            @Override
//            public void onCameraIdle() {
//                System.out.print("onCameraIdle works ");
//                // custom the size of the weather icon
//                Bitmap smallMarker = generateIcons1();
//
//                // Add a marker on map
//                displayCities(smallMarker);
//            }
//        });
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel));
    }

    //get method for showLogo
    public boolean getShowLogo() {
        return showLogo;
    }

    //toggle the "showLogo" variable to show or hide the markers on the map
    public void toggleWeather() {

        if(showLogo == false){
            showLogo = true;
        }else{
            showLogo = false;
        }
        checkWeatherIconDisplay();
    }

    // set the markers visible or invisible
    public void checkWeatherIconDisplay() {
        for(Marker m : markerArray){
            m.setVisible((showLogo));
        }
    }

    private void displayCities(Bitmap smallMarker){
        for(City c : city){
            //LatLng cityLatLng = new LatLng(c.getLat(),c.getLng());
            LatLng cityLatLng = new LatLng(lat,lon);
            marker = mMap.addMarker(new MarkerOptions().position(cityLatLng).title(cityName).snippet(sky)
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(cityLatLng));
            markerArray.add(marker);
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    System.out.println("marker clicked");
                    return false;
                }
            });
        }
    }
    public void generateCities() {
        // city.add(new City(-36.848461, 174.763336, "Auckland"));
        city.add(new City(-37.78333,175.28333, "Halminton"));
    }
    public Bitmap generateIcons() {
        // custom the size of the weather icon
        int height = 100;
        int width = 100;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.lighting);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return smallMarker;
    }
}