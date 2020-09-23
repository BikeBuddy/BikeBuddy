package com.example.bikebuddy;

import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

public class BikeBuddyLocation {

    LatLng coordinate;
    Marker marker;
    GoogleMap mMap;//reference to the map
    Address address;
    boolean isOrigin;//marker will be different depending if its the start or destination
    Geocoder gc;

    public BikeBuddyLocation(boolean isOrigin, Geocoder gc, LatLng latLong, GoogleMap mMap){
        this.isOrigin = isOrigin;
        this.gc = gc;
        this.mMap = mMap;
        if(address!= null)
             this.address = address;
        coordinate = latLong;
        createMarker();
    }

    public void createMarker(){
        this.marker = mMap.addMarker(new MarkerOptions().position(coordinate).title("Destination"));
        this.marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        this.marker.setDraggable(true);
        if(isOrigin){
            this.marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            this.marker.setTitle("Origin");
        }
        try {
            address = gc.getFromLocation(coordinate.latitude, coordinate.longitude,1).get(0);
            if(address!= null)
                this.marker = mMap.addMarker(new MarkerOptions().position(coordinate).title("Destination").snippet(address.getLocality()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCoordinate(LatLng coordinate){
        this.coordinate = coordinate;
        this.marker.setPosition(coordinate);
        update();
    }

    //when marker was cleared from map, calll this function to redraw
    public synchronized void update(){
            createMarker();
            coordinate = marker.getPosition();
    }

}
