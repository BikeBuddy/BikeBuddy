package com.example.bikebuddy;

import android.location.Geocoder;
import android.location.Location;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class RouteManager {

    protected MapsActivity mapsActivity;
    protected RouteFetcher routeFetcher;  // send requests and show routes on map with this --PK
    protected GoogleMap mMap;

    // init data for autocomplete to store
    protected LatLng inputLatLong;
    protected Geocoder gc;

    // Locations for route planning
    protected BikeBuddyLocation startingOrigin;//BikeBuddyLocation contained location , address & marker data.
    protected BikeBuddyLocation theDestination;
    // Boolean for telling route initialization the user has no location
    Boolean startingLocationNeeded = false;

    boolean routeStarted = false;//flag determined if a poly line between start and destination markers is drawn or not after map has been cleared

    public RouteManager(MapsActivity mapsActivity, GoogleMap mMap,Geocoder geocoder)
    {
        this.mMap = mMap;
        this.mapsActivity = mapsActivity;
        gc = geocoder;
    }


    //sets the starting location to gps location, otherwise sets startingLocationNeeded flag to true
    public void setUpOriginFromLocation(Location lastKnownLocation){
        if(lastKnownLocation==null){
           startingLocationNeeded = true;
        }else{
            LatLng startLatLong = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
            //  startingOrigin = new BikeBuddyLocation(true,gc, startLatLong, mMap);
            //    startingLocationNeeded = false;
        }
    }


    //for the input latLang, sets the origin if not already set, if the origin is set,the latLang is used to set the destination
    public void recieveLatLong(LatLng latLang){
        inputLatLong = latLang;
        if(startingOrigin==null){
            startingOrigin = new BikeBuddyLocation(true,gc,latLang, mMap);
            startingOrigin.createMarker();
            startingLocationNeeded = false;
        }else if(theDestination==null){
            theDestination = new BikeBuddyLocation(false,gc,latLang, mMap);
            theDestination.createMarker();
        }else{
            theDestination.setCoordinate(latLang);
        }
    }

    public void initRoute(View view) {
        // locations set, show route
        if(startingOrigin !=null || theDestination!=null){
            try {
                routeStarted = true; //sets flag so that the polyline for the route will be redrawn if map is cleared
         //       Toast.makeText(this, "start is : "+ startingOrigin.coordinate.toString()+ " DEST IS"+theDestination.coordinate.toString(), Toast.LENGTH_LONG).show();
                mMap.clear();
                updateMap();//adds polyline and markers onto map
            }catch (Exception e){
                System.err.println(e);
            }
        }else if(theDestination == null){
            Toast.makeText(mapsActivity, "Please Select Destination", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(mapsActivity, "Please Select Origin", Toast.LENGTH_LONG).show();
        }
    }


    //redraws all the markers and polyline onto map
    public void updateMap(){
        if(startingOrigin!= null)
            startingOrigin.update();
        if(theDestination!= null)
            theDestination.update();
        if(routeStarted)
            routeFetcher.getDirections(startingOrigin.coordinate, theDestination.coordinate);
    }




    public BikeBuddyLocation getStartingOrigin() {
        return startingOrigin;
    }

    public BikeBuddyLocation getTheDestination() {
        return theDestination;
    }
}
