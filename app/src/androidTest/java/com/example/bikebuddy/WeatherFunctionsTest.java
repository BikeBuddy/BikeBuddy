package com.example.bikebuddy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import static org.junit.Assert.*;

public class WeatherFunctionsTest {

    WeatherFunctions wf = new WeatherFunctions();

    @Test
    public void generateIconsTest() {

        Bitmap test_bmp;
        BitmapDrawable bmd = (BitmapDrawable)InstrumentationRegistry.getInstrumentation().getTargetContext().getDrawable(R.drawable.lighting);

        test_bmp = wf.generateIcons(bmd);
       // assertTrue(test_bmp.getHeight() == 100);
        assertEquals(test_bmp.getHeight(), 100);
    }
}
