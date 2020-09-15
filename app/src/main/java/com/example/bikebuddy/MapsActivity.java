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
    private static String sky = "Raining";
    private static String cityName = "Hong Kong";
    public static double lon =0;
    private static double lat =0;


    //push test PK
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Make Toast
//        Toast.makeText(this, "What a stick did you see that", Toast.LENGTH_LONG).show();

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
        fw.getJSON("http://api.openweathermap.org/data/2.5/weather?q=rome,italy&APPID=d2222fc373d644fa109aea09a4046a3c");
        fw.getJSON("http://api.openweathermap.org/data/2.5/weather?q=auckland,newzealand&APPID=d2222fc373d644fa109aea09a4046a3c");
//        System.out.println("mainActivity sky");
//        System.out.println(sky);

        // check if static variable cityName, lon, lat, sky changed after fetching
//        MapsActivity.sky = sky;
//        MapsActivity.cityName = cityName;
//        MapsActivity.lon = Double.parseDouble(lon);
//        MapsActivity.lat = Double.parseDouble(lat);
//        System.out.println("MapsActivity sky: ");
//        System.out.println(MapsActivity.sky);
//        System.out.println("MapsActivity cityName: ");
//        System.out.println(MapsActivity.cityName);
//        try {
//            Thread.sleep(20000);
//            System.out.println("MapsActivity lon: ");
//            System.out.println(lon);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("MapsActivity lon: ");
                System.out.println(lon);
            }
        }, 5000);

//        System.out.println("MapsActivity lat: ");
//        System.out.println(MapsActivity.lat);
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

        // custom the size of the weather icon
        Bitmap smallMarker = generateIcons();

        // Add a marker on map
        displayCities(smallMarker);
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
            LatLng cityLatLng = new LatLng(c.getLat(),c.getLng());
            marker = mMap.addMarker(new MarkerOptions().position(cityLatLng).title("Marker in Halminton").snippet("Population: 300,000")
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