package com.example.bikebuddy;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class setUpFromOriginTest {

    @Rule
    public ActivityTestRule<MapsActivity> rule = new ActivityTestRule<>(MapsActivity.class);

    /*
    This tests that the generateIcon() method returns a Bitmap and that it
    is scaled to 100x100 pixels.
     */
    @Test
    public void startingLocationSetTest() {
        MapsActivity mapsActivity = rule.getActivity();
        mapsActivity.getTripManager().setUpOriginFromLocation();
        BikeBuddyLocation startingOrigin = mapsActivity.getTripManager().getStartingOrigin();
        BikeBuddyLocation theDestination = mapsActivity.getTripManager().getTheDestination();
        if(!mapsActivity.getTripManager().startingLocationNeeded){//if starting location is needed flag is false, the the startingOrigin co ordinates should be the same as the last known location
            assertTrue(( mapsActivity.lastKnownLocation.getLatitude() == mapsActivity.getTripManager().getStartingOrigin().getCoordinate().latitude )
                    && ( mapsActivity.lastKnownLocation.getLongitude() == mapsActivity.getTripManager().getStartingOrigin().getCoordinate().longitude));
        }else{
            assertNull(startingOrigin);
        }
    }
}


