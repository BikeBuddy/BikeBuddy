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
    public void toggleRouteButtonTest() {

        MapsActivity mapsActivity = rule.getActivity();

        assertEquals(mapsActivity.findViewById(R.id.route_button).getVisibility(), View.INVISIBLE);

        mapsActivity.toggleRouteButton();

        assertEquals(mapsActivity.findViewById(R.id.route_button).getVisibility(), View.INVISIBLE);
    }


}
