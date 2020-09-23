package com.example.bikebuddy;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TripTest {



    @Test
    public void getDurationTest() {
        Trip testTrip = new Trip();
        for(int i = 1; i<6000; i++ ){
            testTrip.duration = i;
            Assert.assertEquals( ((float)(i/600)), testTrip.getDuration(), 0.1f);
        }
    }

    @Test
    public void getTripDuration() {

    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetDuration() {
    }
}