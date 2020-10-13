package com.example.bikebuddy;

import android.location.Geocoder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class TripManager {
    private JSONRoutes jsonRoutes;// send requests and show routes on map with this object--PK
    private Geocoder gc;//used to obtain the address of a location based on the lat long coordinates
    private MapsActivity mapsActivity;
    // Locations for route planning/generating
    private BikeBuddyLocation startingOrigin;
    private BikeBuddyLocation theDestination;
    // Boolean for telling route initialization the user has no location
    Boolean startingLocationNeeded = false;
    boolean routeStarted = false;//flag determined if a poly line between start and destination markers is drawn or not after map has been cleared
 //   private Button  addMarkerButton;
    private Button  removeMarkerButton;
    private Integer clickedMarker;

    // init data for autocomplete to store
    private LatLng autoCompleteLatLng;
    private GoogleMap mMap;
    private ArrayList<BikeBuddyLocation> locations;

    public TripManager(MapsActivity activity, Geocoder gc){
        this.mapsActivity = activity;
        this.gc = gc;
        locations = new ArrayList<>();
        initMarkerButtons();
    }

    public void initMarkerButtons(){
//        addMarkerButton = (Button) mapsActivity.findViewById(R.id.addLegButton);
//        addMarkerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(clickedMarker != null){
//                    showMarkerButtons(false);
//                }
//            }
//        });
        removeMarkerButton = (Button) mapsActivity.findViewById(R.id.undoMarkerButton);
        removeMarkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clickedMarker != null){
                    removeLeg(clickedMarker);
                    showMarkerButtons(false);
                    updateMap();
                }
            }
        });
    }

    public void setUpMapObjects(GoogleMap googleMap){
        this.mMap = googleMap;
        setJSONRoutes(mapsActivity.getResources().getString(R.string.google_maps_key), mMap);
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            public void onMapLongClick(LatLng latLng) {
                setAutoLatLang(latLng);
                mMap.clear();
                updateMap();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//                Integer markerID = getMarkerIDByLatLong(latLng);
//                Toast.makeText(mapsActivity.getApplicationContext(),"tag "+ markerID.toString(),Toast.LENGTH_LONG );
//                setFocusedMarker(markerID);
//                //MarkerID will have a null value, if a weather marker is clicked
//                //The bounds for markerID are so that the option to remove or add legs only appears for legs and not the start or destination
//                if(markerID!=null && markerID >0 && markerID < getLocations().size()-1){
//                    showMarkerButtons(true);
//                }

                //  else if(tripManager.getStartingOrigin()==null){
                //       tripManager.showMarkerButtons(false);
                //  }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                updateMarkerTags();
                Integer markerTag = (Integer) marker.getTag();
                if(markerTag != null){
                    setFocusedMarker(markerTag);
                    try {
                        sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    showMarkerButtons(true);
                    return true;
                }
                return false;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                showMarkerButtons(false);
            }
        });

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                showMarkerButtons(false);
            }

        });
    }


    public void showMarkerButtons(boolean show){
        if(show){
      //      addMarkerButton.setVisibility(View.VISIBLE);
            removeMarkerButton.setVisibility(View.VISIBLE);
        }else{
    //        addMarkerButton.setVisibility(View.INVISIBLE);
            removeMarkerButton.setVisibility(View.INVISIBLE);
        }

    }

    public void setJSONRoutes(String key, GoogleMap mMap){
        jsonRoutes = new JSONRoutes(key, mMap);
        this.mMap = mMap;
        jsonRoutes.mapsActivity = mapsActivity;
    }

    //LatLng which are generated by long press on the map or from the address entered from the search bar will be input into this function
    //for the input latLang, sets the origin if not already set, if the origin is set,the latLang is used to set the destination
    public void setAutoLatLang(LatLng latLang){
        autoCompleteLatLng = latLang;
        if(startingOrigin==null){
            setStartingOrigin(new BikeBuddyLocation(true,gc,latLang, mMap));
            startingOrigin.createMarker();
            startingLocationNeeded = false;
        }else if(theDestination==null){
            setDestination(theDestination = new BikeBuddyLocation(false,gc,latLang, mMap));
            theDestination.setAsDestination();
            theDestination.createMarker();
        }else{//once both origin and destination has been set, all input LatLng will be used to update the destination
            BikeBuddyLocation leg = new BikeBuddyLocation(false,gc, latLang ,mMap);
            if(locations.size() < 3)
                addLeg(leg, 1);
            else
                addLeg(leg, locations.size()-1);
            leg.createMarker();
        }
        if(theDestination != null && mapsActivity.getRouteButton().getVisibility() == View.INVISIBLE)//if the destination has been selected for the first time, then the button will become visible
            mapsActivity.toggleRouteButton();
        updateMarkerTags();
    }

    public void showRoute(){
        // locations set, show route
        //if(startingOrigin !=null && theDestination!=null){
        if(locations.size()>1){
            try {
                routeStarted = true; //sets flag so that the polyline for the route will be redrawn if map is cleared
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

    public LatLng getAutoCompleteLatLang(){
        return autoCompleteLatLng;
    }
    //redraws all the markers and polyline onto map
    public void updateMap(){
        ArrayList<LatLng> latLngLocations = new ArrayList<>();//list will be used by jsonRoutes to send a request to google directions
        for(BikeBuddyLocation location: locations){//updates/redraws all the location markers on map
            location.update();
            latLngLocations.add(location.coordinate);
        }
        if(locations.size()>1 && routeStarted){
            jsonRoutes.setLocations(latLngLocations);
            jsonRoutes.getDirections();
            Toast.makeText(mapsActivity, "if block in update map", Toast.LENGTH_SHORT).show();
        }
    }
    //sets the starting location to gps location, otherwise sets startingLocationNeeded flag to true
    public void setUpOriginFromLocation(){
        if(mapsActivity.lastKnownLocation==null){
            startingLocationNeeded =true;
        }else{
            LatLng startLatLong = new LatLng(mapsActivity.lastKnownLocation.getLatitude(),mapsActivity.lastKnownLocation.getLongitude());
            startingOrigin = new BikeBuddyLocation(true,gc, startLatLong, mMap);
            startingLocationNeeded = false;
        }
    }
    public BikeBuddyLocation getStartingOrigin() {
        return startingOrigin;
    }

    public BikeBuddyLocation getTheDestination() {
        return theDestination;
    }

    public void setStartingOrigin(BikeBuddyLocation startingOrigin){
        locations.add(0, startingOrigin);
        this.startingOrigin = startingOrigin;
        jsonRoutes.setStart(startingOrigin.coordinate);
    }

    //last index is replaced by input BikeBikeBuddyLocation, it does NOT add to the list
    public void setDestination(BikeBuddyLocation destination){
        locations.add(destination);
        theDestination = destination;
        theDestination.setAsDestination();
        jsonRoutes.setDestination(destination.coordinate);
    }

    public void addLeg(BikeBuddyLocation leg, int legNumber){
        locations.add(legNumber, leg);
        jsonRoutes.addLeg(leg.coordinate, legNumber);
    }

    public void updateMarkerTags(){
        for(int i=0 ; i<locations.size() ; i++){
            locations.get(i).marker.setTag(i);
        }
    }
    public void removeLeg(int leg){
        boolean removeDestination = false;
        if(locations.get(leg).isDestination())//if user wants to remove destination
            removeDestination = true;
        boolean removeOrigin = false;
        if(locations.get(leg).isOrigin())
            removeOrigin = true;
        if(leg >= 0 && leg < locations.size()){
            locations.get(leg).setInvisible();//setting the marker invisible before removing from array, else it still appears on map
            locations.remove(leg);
            updateMarkerTags();//tags are updated according to the new order of
        }
        if(removeOrigin || removeDestination){
            resetOriginAndDestination();
        }
        mMap.clear();
        updateMap();
    }

    public void resetOriginAndDestination(){
        if(locations.isEmpty()){//if all markers were removed
            startingOrigin = null; //removing the old reference
            theDestination = null;
            startingLocationNeeded = true;
        }else if(locations.size() == 1){//if only one marker remains
            startingOrigin = locations.get(0);
            startingOrigin.setAsOrigin();
            theDestination = null;
        }else{//if there are atleast 2 markers
            BikeBuddyLocation lastLocation = locations.get(locations.size()-1);
            BikeBuddyLocation firstLocation  = locations.get(0);
            firstLocation.setAsOrigin();
            lastLocation.setAsDestination();
        }
    }

    public void setFocusedMarker(Integer markerTag){
        this.clickedMarker = markerTag;
    }

    public ArrayList<BikeBuddyLocation> getLocations(){
        return locations;
    }

    public int getMarkerIDByLatLong(LatLng latLng){
        updateMarkerTags();
        for(BikeBuddyLocation location: locations){
            if(location.coordinate.equals(latLng)){
                return (Integer) location.marker.getTag();
            }
        }
        return 0;
    }

    public Trip getTripDetails(){
        return jsonRoutes.getTrip();
    }

}
