package com.example.bikebuddy;

public class FetchWeather
        //extends MapsActivity
{

    GetJSON gj;

    public FetchWeather(MapsActivity ma) {
        gj = new GetJSON(ma);
    }

    //option 1: pass in MapsActivity
    public String fetch(double lat, double lon) {

        String st = "https://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&APPID=d2222fc373d644fa109aea09a4046a3c";
        System.out.println(st);

        gj.execute(st);

        String s = "description";
      //  ma.lat = 0;
       // ma.testToast("test");

        return s;
    }

//fw.getJSON("http://api.openweathermap.org/data/2.5/weather?q=auckland,newzealand&APPID=d2222fc373d644fa109aea09a4046a3c");

    //option 2: extend MapsActivity to access it's methods
    public String fetch2(double lat, double lon){

        //testToast("test");

        String s = "description";
        return s;
    }
}
