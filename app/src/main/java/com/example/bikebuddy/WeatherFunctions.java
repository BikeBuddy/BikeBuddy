package com.example.bikebuddy;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class WeatherFunctions {

    MapsActivity mapsActivity;
    GoogleMap googleMap;

    // default constructor for testing
    public WeatherFunctions() {

    }

    public WeatherFunctions(MapsActivity mapsActivity, GoogleMap googleMap) {
        this.mapsActivity = mapsActivity;
        this.googleMap = googleMap;
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
       // BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.itest);

        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        return smallMarker;
    }*/

    /*
    This method takes an iconName and returns the corresponding Bitmap
    */
    public Bitmap generateIcon(String iconName) {

        String drawableIcon = "@drawable/"+iconName;
        String defaultIcon = "@drawable/idefault";
        //custom size of the weather icon
        int height = 100;
        int width = 100;

        int imageResource;
        Drawable drawable;

        //if iconName is not valid, drawable will throw a ResourceNotFoundException.
        try {
            imageResource = mapsActivity.getResources().getIdentifier(drawableIcon, null, mapsActivity.getPackageName());
            drawable = mapsActivity.getResources().getDrawable(imageResource);
       } catch (Resources.NotFoundException e) {
            //In case of exception, set weather marker to default icon
            imageResource = mapsActivity.getResources().getIdentifier(defaultIcon, null, mapsActivity.getPackageName());
            drawable = mapsActivity.getResources().getDrawable(imageResource);
        }

        BitmapDrawable bitmapDrawable=(BitmapDrawable)drawable;
        Bitmap bitmap=bitmapDrawable.getBitmap();
        Bitmap weatherIcon = Bitmap.createScaledBitmap(bitmap, width, height, false);

        return weatherIcon;
    }

/*    public void addLocationsWeather(double lat, double lon, String iconId, String description) {
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
       *//* mMap.addMarker(new MarkerOptions().position(latLng).title(description).snippet("000")
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));*//*

*//*              gm.addMarker(new MarkerOptions().position(latLng).title(description).snippet("000")
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));*//*
        googleMap.addMarker(new MarkerOptions().position(latLng).title(description)
                .icon(BitmapDescriptorFactory.fromBitmap(generateIcon(iconId))));

    }*/

}
