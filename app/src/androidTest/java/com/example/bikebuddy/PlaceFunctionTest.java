package com.example.bikebuddy;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import androidx.test.rule.ActivityTestRule;

import static org.junit.Assert.*;

public class PlaceFunctionTest {

    @Rule
    public ActivityTestRule<MapsActivity> rule = new ActivityTestRule<>(MapsActivity.class);

    private Object GoogleMap;

    @Test
    public void addLocationsPlace() throws Throwable {
        final MapsActivity map = rule.getActivity();
        final LatLng[] lat = new LatLng[2];
        //try {
            rule.runOnUiThread(new Runnable() {
                public void run() {
                    Marker marker;
                    ArrayList<Marker> markerArray = new ArrayList<Marker>();
                    //MapsActivity map = new MapsActivity();
                    GoogleMap googleMap = map.getmMap();

                    PlaceFunction place = new PlaceFunction(map, googleMap);
                    //GetAllInter getAll = mock(GetAllInter.class);
//                    PlaceFunction place = mock(PlaceFunction.class);
                    //run addLocationPlace method
                    place.addLocationsPlace(34, 141);

                    //after running addLocationPlace method, there should be one marker stored in the ArrayList of the PlaceFunction
                    //global variable
                    markerArray = place.getMarkerArray();

                    //take the first element of the ArrayList
                    lat[0] = markerArray.get(0).getPosition();

                    // create a marker

                    LatLng latLng= new LatLng(34, 141);
                    marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("gary creates gas station").snippet("000")
                            .icon(BitmapDescriptorFactory.fromBitmap(place.generateIcon())));
                    lat[1] = latLng;
                    //check the first element of the ArrayList against the marker created
                    assertEquals(lat[0],lat[1]);
                }
            });

    }
}