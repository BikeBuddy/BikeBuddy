package com.example.bikebuddy;

import com.google.android.gms.maps.model.Marker;
import java.util.LinkedList;
import java.util.Queue;

//manages markers added by long press
//@author PK
public class MarkerQueue{
    private Queue<Marker> markers;
    private  int markerLimit;  //the limit of markers which are generated from long press

    public MarkerQueue(boolean flag){
        markers = new LinkedList<Marker>();
        if(flag){
            markerLimit=1;
        }else{
            markerLimit =2;
        }
    }

    public void addMarker(Marker marker){
        if(markerLimit<= markers.size()){
            Marker oldMarker = markers.remove();//.poll();
            oldMarker.setVisible(false);
        }markers.add(marker);
    }

    public void setMarkerLimit(int limit){
        this.markerLimit = limit;
    }
    public Marker getMarker(){
        return markers.poll();
    }
    public Queue<Marker>  getMarkers(){
        return markers;
    }
}