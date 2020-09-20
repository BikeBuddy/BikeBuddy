package com.example.bikebuddy;

import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

public class BikeBuddyLocation implements GoogleMap.OnMarkerDragListener{

    LatLng coordinate;
    Marker marker;
    GoogleMap mMap;
    Address address;
    boolean isOrigin;    //if its a destination
    Geocoder gc;

//    public BikeBuddyLocation(boolean isOrigin, Geocoder gc){
//        this.isOrigin = isOrigin;
//        MarkerOptions searchedLocationMarker = new MarkerOptions().position(autoCompleteLatLng).title(place.getAddress());
//        marker = new MarkerOptions().
//        this.gc =gc;
//    }

    public BikeBuddyLocation(boolean isOrigin, Geocoder gc, LatLng autoCompleteLatLang, GoogleMap mMap){
        this.isOrigin = isOrigin;
        this.gc = gc;
        this.mMap = mMap;
        try {
            if(address!= null)
                 this.address = address;
            coordinate= autoCompleteLatLang;
            createMarker();
            update();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void createMarker() throws IOException {
        address= gc.getFromLocation(coordinate.latitude, coordinate.longitude,1).get(0);
        if(address!= null)
            this.marker = mMap.addMarker(new MarkerOptions().position(coordinate).title("Destination").snippet(address.getLocality()));
        else{
            this.marker = mMap.addMarker(new MarkerOptions().position(coordinate).title("Destination"));
        }
        this.marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        this.marker.setDraggable(true);
        if(isOrigin){
            this.marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            this.marker.setTitle("Origin");
        }
    }

    public void setCoordinate(LatLng coordinate)throws IOException {
        this.coordinate = coordinate;
        this.marker.setPosition(coordinate);
        update();
    }

    public void update(){
        try {
            if(marker==null)//if map was cleared
                createMarker();
            coordinate = marker.getPosition();
            address= gc.getFromLocation(marker.getPosition().latitude, marker.getPosition().latitude,1).get(0);
            if(address!=null)
                this.marker.setSnippet(address.getLocality());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        update();
    }
}
