package com.example.bikebuddy;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class WeatherFunctions {



    public Bitmap generateIcons(BitmapDrawable bitmapdraw) {
   //public Bitmap generateIcons(String iconName) {
        // custom the size of the weather icon
        int height = 100;
        int width = 100;
       // BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.lighting);

        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        return smallMarker;
    }

}
