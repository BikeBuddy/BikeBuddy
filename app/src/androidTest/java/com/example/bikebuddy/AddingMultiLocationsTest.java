package com.example.bikebuddy;

import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;




public class AddingMultiLocationsTest {

    @Rule
    public ActivityTestRule<MapsActivity> rule = new ActivityTestRule<>(MapsActivity.class);

    private Object GoogleMap;

    @Test
    public void addMultipleLegsTest() throws Throwable {
        final MapsActivity map = rule.getActivity();
        rule.runOnUiThread(new Runnable() {
            public void run() {
            TripManager tripManager = map.getTripManager();

                System.out.println("Testing adding multiple legs");
                LatLng leg1 = new LatLng(13,140);
                LatLng leg2 = new LatLng(13,145);
                LatLng leg3 = new LatLng(13,144);

                tripManager.setAutoLatLang(leg1);
                tripManager.setAutoLatLang(leg2);
                tripManager.setAutoLatLang(leg3);

                assertFalse(tripManager.startingLocationNeeded);
                assertEquals(3,tripManager.getLocations().size());
                assertEquals(tripManager.getStartingOrigin().getCoordinate(), leg1);
                //adding a leg after destination should not change the destination so destination should still equal leg2
                assertEquals(tripManager.getTheDestination().getCoordinate(), leg2);
            }
        });
    }
}