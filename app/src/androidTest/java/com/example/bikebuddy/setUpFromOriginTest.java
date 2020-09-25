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
        mapsActivity.setUpOriginFromLocation();

        BikeBuddyLocation startingOrigin = mapsActivity.getStartingOrigin();
        BikeBuddyLocation theDestination = mapsActivity.getTheDestination();
        if(!mapsActivity.startingLocationNeeded){//if starting location is needed flag is false, the the startingOrigin co ordinates should be the same as the last known location
            assertTrue(( mapsActivity.lastKnownLocation.getLatitude() == startingOrigin.coordinate.latitude )
                    && ( mapsActivity.lastKnownLocation.getLongitude() == startingOrigin.coordinate.longitude));
        }else{
            assertNull(startingOrigin);
        }
    }
    /*
     * Checks if starting location is in NZ, default location is Auckland, New Zealand so test should pass.
     */
    @Test
    public void startingLocationValidNameTest() {

        MapsActivity mapsActivity = rule.getActivity();
        mapsActivity.setUpOriginFromLocation();

        BikeBuddyLocation startingOrigin = mapsActivity.getStartingOrigin();
       assertEquals("NZ", mapsActivity.getStartingOrigin().address.getCountryCode() );
    }
    /*
     *Checks if starting location is in NZ, default location is Auckland, New Zealand so test should fail.
     */
    @Test
    public void startingLocationInValidNameTest() {

        MapsActivity mapsActivity = rule.getActivity();
        mapsActivity.setUpOriginFromLocation();

        BikeBuddyLocation startingOrigin = mapsActivity.getStartingOrigin();
        assertEquals("US", mapsActivity.getStartingOrigin().address.getCountryCode() );
    }
}


