package com.example.bikebuddy;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)

public class DateTimeFunctionsTests {

    @Rule
    public ActivityTestRule<MapsActivity> rule = new ActivityTestRule<>(MapsActivity.class);


    @Test
    public void AddHourTest() throws Throwable {
        final MapsActivity map = rule.getActivity();
        rule.runOnUiThread(new Runnable() {
            public void run() {

                DateTimeFunctions dtf = new DateTimeFunctions(map);

                //testing initial case
                assertEquals(0, dtf.offsetHours);

                //testing addHour
                dtf.addHour();
                assertEquals(1, dtf.offsetHours );
                dtf.addHour();
                assertEquals(2, dtf.offsetHours );
            }
        });
    }

    @Test
    public void MinusHourTest() throws Throwable {
        final MapsActivity map = rule.getActivity();
        rule.runOnUiThread(new Runnable() {
            public void run() {

                DateTimeFunctions dtf = new DateTimeFunctions(map);

                dtf.offsetHours = 10;
                //testing initial case
                assertEquals(10, dtf.offsetHours);

                //testing minusHour
                dtf.minusHour();
                assertEquals(9, dtf.offsetHours);
                dtf.minusHour();
                assertEquals(8, dtf.offsetHours);
            }
        });
    }

    @Test
    public void ResetHourTest() throws Throwable {
        final MapsActivity map = rule.getActivity();
        rule.runOnUiThread(new Runnable() {
            public void run() {

                DateTimeFunctions dtf = new DateTimeFunctions(map);

                dtf.offsetHours = 10;
                //testing initial case
                assertEquals(10, dtf.offsetHours);

                //testing reset
                dtf.resetHour();
                assertEquals(0, dtf.offsetHours);
            }
        });
    }

    //the upper limit of offSet hours should be 120
    @Test
    public void HourUpperLimitTest() throws Throwable {
        final MapsActivity map = rule.getActivity();
        rule.runOnUiThread(new Runnable() {
            public void run() {

                DateTimeFunctions dtf = new DateTimeFunctions(map);

                dtf.offsetHours = 119;
                //testing initial case
                assertEquals(119, dtf.offsetHours);

                //testing edge case
                dtf.addHour();
                assertEquals(120, dtf.offsetHours);

                //testing no change
                dtf.addHour();
                assertEquals(120, dtf.offsetHours);
            }
        });
    }

    //the lower limit of offset hours is 0
    @Test
    public void HourLowerLimitTest() throws Throwable {
        final MapsActivity map = rule.getActivity();
        rule.runOnUiThread(new Runnable() {
            public void run() {

                DateTimeFunctions dtf = new DateTimeFunctions(map);
                dtf.offsetHours = 1;
                //testing initial case
                assertEquals(1, dtf.offsetHours);

                //testing edge case
                dtf.minusHour();
                assertEquals(0, dtf.offsetHours);

                //testing no change
                dtf.minusHour();
                assertEquals(0, dtf.offsetHours);
            }
        });
    }
}
