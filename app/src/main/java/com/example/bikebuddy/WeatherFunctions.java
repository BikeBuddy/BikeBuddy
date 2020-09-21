package com.example.bikebuddy;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

public class WeatherFunctions {

    MapsActivity ma;
    GoogleMap gm;

/*    public WeatherFunctions() {

    }*/

    public WeatherFunctions(MapsActivity ma, GoogleMap gm) {
        this.ma = ma;
        this.gm = gm;
    }
//    Resources res = ma.getResources();
//    TypedArray icons = res.obtainTypedArray(R.array.weather_icons);




/*   // public Bitmap generateIcons(BitmapDrawable bitmapdraw) {
   public Bitmap generateIcons(HashMap weatherIcons) {

        for (int i = 0; i < icons.length(); i++) {
            Drawable dr = icons.getDrawable(i);
            String name = dr.


            weatherIcons.put(i.)


        }

   //public Bitmap generateIcons(String iconName) {
        // custom the size of the weather icon
        int height = 100;
        int width = 100;
       // BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.lighting);

        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        return smallMarker;
    }*/

    public Bitmap generateIcon(String iconName) {

       String uri = "@drawable/i"+iconName;
       int imageResource = ma.getResources().getIdentifier(uri, null, ma.getPackageName());

       Drawable drwa = ma.getResources().getDrawable(imageResource);
    // custom the size of the weather icon
    int height = 100;
    int width = 100;
     //BitmapDrawable bitmapdraw=(BitmapDrawable)ma.getResources().getDrawable(R.drawable.iconName);
        BitmapDrawable bitmapdraw=(BitmapDrawable)drwa;

        Bitmap b=bitmapdraw.getBitmap();

         Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        return smallMarker;
}

    public void addLocationsWeather(double lat, double lon, String iconId, String description) {
        //option 1 using address object, creates address object with everything else set to null, uses URL as description
        // allows to use the same locationslist but the addresses are nto that useful, could reverse locate to get full address.
        //  Address a = new Address(Locale.ENGLISH);
        //  a.setLatitude(lat);
        //  a.setLongitude(lon);
        //  a.setUrl(description);
        // locationsList.add(a);

        //option 2 just creates a marker with lat, lon, description in a seperate array to the locations
        //similar to the previous displaylocations method
        LatLng latLng = new LatLng(lat, lon);
        //Marker marker =

        //option 3 similar to above but doesn't store the markers just displays them (this could have issues with the weather toggle)
        //LatLng latLng = new LatLng(lat, lon);
       /* mMap.addMarker(new MarkerOptions().position(latLng).title(description).snippet("000")
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));*/

/*              gm.addMarker(new MarkerOptions().position(latLng).title(description).snippet("000")
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));*/
        gm.addMarker(new MarkerOptions().position(latLng).title(description).snippet("000")
                .icon(BitmapDescriptorFactory.fromBitmap(generateIcon(iconId))));

    }

}
