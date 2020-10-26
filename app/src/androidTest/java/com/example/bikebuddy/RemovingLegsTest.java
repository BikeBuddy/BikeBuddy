package com.example.bikebuddy;

import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class RemovingLegsTest {

    @Rule
    public ActivityTestRule<MapsActivity> rule = new ActivityTestRule<>(MapsActivity.class);

    @Test
    public void addMultipleLegsTest() throws Throwable {
        final MapsActivity map = rule.getActivity();
        rule.runOnUiThread(new Runnable() {
            public void run() {
                TripManager tripManager = map.getTripManager();

                System.out.println("Testing removing legs from a journey");
                LatLng leg1 = new LatLng(13,140);
                LatLng leg2 = new LatLng(13,145);
                LatLng leg3 = new LatLng(13,144);

                tripManager.setAutoLatLang(leg1);
                tripManager.setAutoLatLang(leg2);
                //note that leg 3 will be at index 1 in the list, destination always remains at the last index after the additon of a leg
                tripManager.setAutoLatLang(leg3);

                tripManager.removeLeg(0);
                //removing the starting origin should set the first stop (leg3) to starting location, set the last location in the list to destination(leg2)
                assertEquals(2,tripManager.getLocations().size());
                assertFalse(tripManager.startingLocationNeeded);
                assertEquals(tripManager.getStartingOrigin().getCoordinate(), leg3);
                assertEquals(tripManager.getTheDestination().getCoordinate(), leg2);
            }
        });
    }
}
