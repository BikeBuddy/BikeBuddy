package com.example.bikebuddy;

public class FetchWeather
        //extends MapsActivity
{

    //GetJSON gj; //error trying to run multiple executes, so have to create new getJSON each time
    MapsActivity ma;

    public FetchWeather(MapsActivity ma) {
        //gj = new GetJSON(ma); //error trying to run multiple executes, so have to create new getJSON each time
        this.ma = ma;
    }

    //option 1: pass in MapsActivity
    public String fetch(double lat, double lon) {

        String st = "https://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&APPID=d2222fc373d644fa109aea09a4046a3c";

       // gj.execute(st); //error trying to run multiple executes, so have to create new getJSON each time
        new GetJSON(ma).execute(st);

        String s = "description";

        return s;
    }

    //option 2: extend MapsActivity to access it's methods
    public String fetch2(double lat, double lon){
      //testToast("test");

        String s = "description";
        return s;
    }
}
