package com.example.bikebuddy;

public class FetchWeather
        //extends MapsActivity
{

    //GetJSON gj; //error trying to run multiple executes, so have to create new getJSON each time
    MapsActivity mapsActivity;

    public FetchWeather(MapsActivity mapsActivity) {
     //error trying to run multiple executes, so have to create new getJSON each time
        this.mapsActivity = mapsActivity;
    }

    //option 1: pass in MapsActivity
    public void fetch(double lat, double lon) {

        String st = "https://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&APPID=d2222fc373d644fa109aea09a4046a3c";

       // gj.execute(st); //error trying to run multiple executes, so have to create new getJSON each time
        new GetJSON(mapsActivity).execute(st);

    }

    //option 2: extend MapsActivity to access it's methods
    public String fetch2(double lat, double lon){
      //testToast("test");

        String s = "description";
        return s;
    }
}
