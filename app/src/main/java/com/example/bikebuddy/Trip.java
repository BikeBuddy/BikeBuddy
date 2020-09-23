package com.example.bikebuddy;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

//Route object
//@author PK
public class Trip {
    Integer distance;
    Integer duration;
    LatLng startLocation;
    String start;
    String end;
    LatLng endLocation;
    String encodedPolyLine;
    ArrayList<LatLng> points;// the LatLng coordinates throughout the route

    public String getTripDistance(){
        String tripDistance = "Distance: ";
        if(distance>1000){
            int KMs = distance/1000;
            int meters = (distance % 1000);
            tripDistance += KMs + "KM & " + meters + "meters";
        }else{
            tripDistance += distance + " meters";
        }
        return tripDistance;
    }

    public Integer getDuration(){
        if(duration!=null){
           return  (duration/61);
        }
        return null;
    }

    public String getTripDuration(){
        int durationMins = getDuration();
        String tripDuration ="Duration: ";
        if(durationMins>60){
            int minutes = (60 % durationMins);
            int hours = (durationMins/60);
            tripDuration += hours + "h "+ minutes + "mins";
        }else{
            tripDuration += durationMins  + "mins";
        }
        return tripDuration;
    }


    public void decodePolyLine() {
        int len = encodedPolyLine.length();
        int index = 0;
        points = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;
        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = encodedPolyLine.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int decodedLat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += decodedLat;
            shift = 0;
            result = 0;
            do {
                b = encodedPolyLine.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int decodedLong = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += decodedLong;
            points.add(new LatLng(lat / 100000d, lng / 100000d
            ));
        }
    }
}