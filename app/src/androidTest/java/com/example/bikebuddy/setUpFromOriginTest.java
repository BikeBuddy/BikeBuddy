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





//
//    @Test
//    public void durationCalcTest() {
//        MapsActivity mapsActivity = rule.getActivity();
//
//        for(int i =0; i< 1000; i++)
//            assertEquals(mapsActivity);
//
//        mapsActivity.toggleRouteButton();
//
//        assertEquals(mapsActivity.findViewById(R.id.route_button).getVisibility(), View.INVISIBLE);
//    }
//


/*        Bitmap testBitmap;

        MapsActivity act = rule.getActivity();
        act.initWeatherFunctions();

        //valid IconName
        testBitmap = act.weatherFunctions.generateIcon("test");
        assertNotNull(testBitmap);
        assertEquals(testBitmap.getHeight(), 100);
        assertEquals(testBitmap.getWidth(), 100);

        //invalid IconName (returns default icon - red circle)
        testBitmap = act.weatherFunctions.generateIcon("notAnIcon");
        assertNotNull(testBitmap);
        assertEquals(testBitmap.getHeight(), 100);
        assertEquals(testBitmap.getWidth(), 100);*/
}

