package com.example.bikebuddy;

import android.graphics.Bitmap;
import android.view.View;

import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class RouteTests {

    @Rule
    public ActivityTestRule<MapsActivity> rule = new ActivityTestRule<>(MapsActivity.class);

    /*
    This tests that the generateIcon() method returns a Bitmap and that it
    is scaled to 100x100 pixels.
     */
    @Test
    public void setAutoCompleteLatLangTest() {

        MapsActivity mapsActivity = rule.getActivity();

        BikeBuddyLocation startingOrigin = mapsActivity.getStartingOrigin();
        BikeBuddyLocation theDestination = mapsActivity.getTheDestination();


        assertNull(startingOrigin);
        assertNull(theDestination);
        mapsActivity.setAutoCompleteLatLang(new LatLng(0, 0));

        assertNotNull(mapsActivity.getStartingOrigin());
        //  assertNotNull(theDestination);
    }

    @Test
    public void toggleRouteButtonTest() {

        MapsActivity mapsActivity = rule.getActivity();

        assertEquals(mapsActivity.findViewById(R.id.route_button).getVisibility(), View.VISIBLE);

        mapsActivity.toggleRouteButton();

        assertEquals(mapsActivity.findViewById(R.id.route_button).getVisibility(), View.INVISIBLE);
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

