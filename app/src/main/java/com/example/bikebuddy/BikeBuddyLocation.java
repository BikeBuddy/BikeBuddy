package com.example.bikebuddy;

import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

/**
 * Class which encapulates and manages objects from the google maps API
 */
public class BikeBuddyLocation {

    LatLng coordinate;//the lat long of the location
    Marker marker;
    GoogleMap mMap;//reference to the map
    Address address; //address of the location ie local details such as city name
    private boolean isOrigin;//marker will be different depending if its the start or destination
    private boolean isDestination;
    Geocoder gc; //used to obtain the address from the coordinate
    private boolean isVisible;

    public boolean isOrigin() {
        return isOrigin;
    }

    public void setOrigin(boolean origin) {
        isOrigin = origin;
    }

    public boolean isDestination() {
        return isDestination;
    }

    public void setDestination(boolean destination) {
        isDestination = destination;
    }

    public BikeBuddyLocation(boolean isOrigin, Geocoder gc, LatLng autoCompleteLatLang, GoogleMap mMap){
        this.isOrigin = isOrigin;
        this.gc = gc;
        this.mMap = mMap;
        coordinate= autoCompleteLatLang;
        isDestination = false;
        isVisible = true;
    }

    //creates the marker based on the objects set coordinate, the colour of the marker depends on if it is a destination or origin
    public void createMarker(){
        this.marker = mMap.addMarker(new MarkerOptions().position(coordinate));
        this.marker.setDraggable(true);
        if(isOrigin){
            this.marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            this.marker.setTitle("Origin");
        }else if (isDestination){
            this.marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            this.marker.setTitle("Destination");
        }else{
            this.marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            this.marker.setTitle("Leg");
            this.marker.setSnippet("sdkfjsd \n asd");

        }
        this.marker.setVisible(isVisible);
        this.marker.showInfoWindow();

//        try {// The snippet will include the city name if the geo coder recieves atleast once response from the places api
////            address= gc.getFromLocation(coordinate.latitude ,coordinate.longitude,1).get(0);
//            if(address!= null)
//                this.marker.setSnippet(address.getLocality() );
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    //either from long pressing the map, or using the auto complete search. An already existing destination/origin will be updated via this function.
    public void setCoordinate(LatLng coordinate){
        this.coordinate = coordinate;
    }

    //when marker was dragged from map, the object updates based on the marker position
    public void update(){
        coordinate = marker.getPosition();
        createMarker();
    }

    public void setAsDestination(){
        this.isDestination = true;
        if(isOrigin){
            isOrigin=false;
        }
    }
    public void setAsOrigin(){
        this.isOrigin = true;
        if(isDestination){
            this.isDestination = false;
        }
    }

    public void setInvisible(){
        this.isVisible = false;
    }

}
