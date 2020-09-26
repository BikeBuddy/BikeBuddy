package com.example.bikebuddy;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class WeatherFunctionsTest {

    @Rule
    public ActivityTestRule<MapsActivity> rule = new ActivityTestRule<>(MapsActivity.class);


    /*
    This tests that the generateIcon() method returns a Bitmap and that it
    is scaled to 100x100 pixels.
     */
    @Test
    public void generateIconTest() {

        Bitmap testBitmap;

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
        assertEquals(testBitmap.getWidth(), 100);
    }


    @Test
    public void toggleWeatherTest() {

        MapsActivity act = rule.getActivity();
        act.initWeatherFunctions();

  
        boolean expected = !act.weatherFunctions.isShowMarker();
        System.out.println(act.weatherFunctions.isShowMarker());
        act.weatherFunctions.toggleWeather();
        System.out.println(act.weatherFunctions.isShowMarker());
        assertEquals(expected, act.weatherFunctions.isShowMarker());
    }
}
