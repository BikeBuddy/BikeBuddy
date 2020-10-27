package com.example.bikebuddy;

public class FetchNearbyPlace {

    //GetJSON gj; //error trying to run multiple executes, so have to create new getJSON each time
    MapsActivity mapsActivity;

    public FetchNearbyPlace(MapsActivity mapsActivity) {
        //error trying to run multiple executes, so have to create new getJSON each time
        this.mapsActivity = mapsActivity;
    }

    //option 1: pass in MapsActivity
    public void fetch(double lat, double lon) {
        // getResources().getString(R.string.google_maps_key)
        String st = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=gas+station&key=" + (mapsActivity.getResources().getString(R.string.google_maps_key));

        //String st = "https://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&APPID=d2222fc373d644fa109aea09a4046a3c";
        //String st = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=1500&type=restaurant&keyword=cruise&key=";
        //String st = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+lat+","+lon+"&radius=1500&type=restaurant&keyword=cruise&key=";
        //String st = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=gas%20of%20station&inputtype=textquery&fields=photos,formatted_address,name,rating,opening_hours,geometry&key=";
        //String st = "https://maps.googleapis.com/maps/api/place/textsearch/xml?query=restaurants+in+Sydney&key=";
        //String st = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=gas+station&key=";
        // gj.execute(st); //error trying to run multiple executes, so have to create new getJSON each time
        new GetPlaceJSON(mapsActivity).execute(st);
    }

    //option 2: extend MapsActivity to access it's methods
    public String fetch2(double lat, double lon) {
        //testToast("test");
        String s = "description";
        return s;
    }
}
