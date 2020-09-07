package com.example.bikebuddy;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private boolean showLogo = true;
    private Marker marker;
    private Marker marker1;
    private ArrayList<City> city = new ArrayList<City>();
    private ArrayList<Marker> markerArray = new ArrayList<Marker>();

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
        Toast.makeText(this, "What a stick did you see that", Toast.LENGTH_LONG).show();

        city.add(new City(-37.78333,175.28333, "Halminton"));
        city.add(new City(-36.848461, 174.763336, "Auckland"));
        System.out.println(city.get(0).getLat());
        final Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("button clicked");
                System.out.println(showLogo);
                System.out.println(city.get(0).getLat());
                if(showLogo == false){
                    showLogo = true;
                    for(Marker m : markerArray){
                        m.setVisible((showLogo));
                    }
//                    marker.setVisible(showLogo);
//                    marker1.setVisible(showLogo);
//                    System.out.println(city.get(0).getName());
                }else{
                    showLogo = false;
                    for(Marker m : markerArray){
                        m.setVisible((showLogo));
                    }
//                    marker.setVisible(showLogo);
//                    marker1.setVisible(showLogo);
                }
            }

        });
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
        int height = 100;
        int width = 100;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.lighting);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        // Add a marker in Sydney and move the camera
        for(City c : city){
            LatLng cityLatLng = new LatLng(c.getLat(),c.getLng());
            System.out.println("debug");
            System.out.println(c.getLat());
            System.out.println(c.getLng());
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
//        LatLng hamilton = new LatLng(-37.78333 ,175.28333);
//        marker = mMap.addMarker(new MarkerOptions().position(hamilton).title("Marker in Auckland").snippet("Population: 1,500,000")
//                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
//                marker.setVisible(showLogo);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(hamilton));

//        LatLng auckland = new LatLng(-36.848461, 174.763336);
//        marker1 = mMap.addMarker(new MarkerOptions().position(auckland).title("Marker in Hamilton").snippet("Population: 400,000")
//                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
//                marker.setVisible(showLogo);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(auckland));
//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                System.out.println("marker clicked");
//                return false;
//            }
//        });
    }
}