package com.example.bikebuddy;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    //        Paris France 48.8566° N, 2.3522° E
//        Wellington NZ 41.2769° S, 174.7731° E
//        Beijing China 39.9042° N, 116.4074° E
    @Test
    public void regionCheckWorks1() {
        //        Portland Oregon, USA 45.5051° N, 122.6750° W
                MockLocationTest locTest = new MockLocationTest();
        locTest.addNewLocation(45.5051, 122.6750, 1f, 0, 0f, 0f, 0);
       MapsActivity ma = new MapsActivity();
        assertEquals("us", ma.getCountryCode());
    }

}