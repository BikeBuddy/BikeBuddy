package com.example.bikebuddy;

import android.widget.Button;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MapsActivityTest {
//    private boolean showLogo = true;
//    Button button;
//    MapsActivity map = new MapsActivity();

    @Before
    public void setUp() throws Exception {
//        showLogo = true;
//        button = (Button) map.findViewById(R.id.button1);
    }

    @After
    public void tearDown() throws Exception {
//        showLogo = false;
    }

    @Test
    public void toggleWeatherTest() {
        MapsActivity map = new MapsActivity();
        boolean expected = !map.getShowLogo();
        System.out.println(map.getShowLogo());
        map.toggleWeather();
        System.out.println(map.getShowLogo());
        assertEquals(expected, map.getShowLogo());
    }
}