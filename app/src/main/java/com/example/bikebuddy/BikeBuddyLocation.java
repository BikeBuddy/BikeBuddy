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

    LatLng coordinate;
    Marker marker;
    GoogleMap mMap;//reference to the map
    Address address;
    boolean isOrigin;//marker will be different depending if its the start or destination
    Geocoder gc;

    public BikeBuddyLocation(boolean isOrigin, Geocoder gc, LatLng autoCompleteLatLang, GoogleMap mMap){
        this.isOrigin = isOrigin;
        this.gc = gc;
        this.mMap = mMap;
        coordinate= autoCompleteLatLang;
    }

    //creates the marker based on the objects set coordinate, the colour of the marker depends on if it is a destination or origin
    public void createMarker(){
        this.marker = mMap.addMarker(new MarkerOptions().position(coordinate));
        this.marker.setDraggable(true);
        if(isOrigin){
            this.marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            this.marker.setTitle("Origin");
        }else{
            this.marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            this.marker.setTitle("Destination");
        }
        this.marker.showInfoWindow();

        try {// The snippet will include the city name if the geo coder recieves atleast once response from the places api
            address= gc.getFromLocation(coordinate.latitude ,coordinate.longitude,1).get(0);
            if(address!= null)
                this.marker.setSnippet(address.getLocality() );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //either from long pressing the map, or using the auto complete search. An already existing destination/origin will be updated via this function.
    public void setCoordinate(LatLng coordinate){
        this.coordinate = coordinate;
    }

    //when marker was dragged from map,
    public void update(){
        coordinate = marker.getPosition();
        createMarker();
    }

}
