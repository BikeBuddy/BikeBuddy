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

        if (mapsActivity.dateTimeFunctions.offsetHours == 0) {
            //do currentweather call
            String st = "https://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&APPID=d2222fc373d644fa109aea09a4046a3c";
            new GetJSON(mapsActivity).execute(st);
        } else if (mapsActivity.dateTimeFunctions.offsetHours > 0) {


            //do future weather call, gets offset hours in JSOn function
            String sth = "https://api.openweathermap.org/data/2.5/forecast?lat="+lat+"&lon="+lon+"&APPID=d2222fc373d644fa109aea09a4046a3c";
            new GetJSON(mapsActivity).execute(sth);
        }

        int cnt = 3;
        String st = "https://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&APPID=d2222fc373d644fa109aea09a4046a3c";
        String sth = "https://api.openweathermap.org/data/2.5/forecast?lat="+lat+"&lon="+lon+"&APPID=d2222fc373d644fa109aea09a4046a3c";
        String stc = "https://api.openweathermap.org/data/2.5/forecast/hourly?lat="+lat+"&lon="+lon+"&cnt="+cnt+"&APPID=d2222fc373d644fa109aea09a4046a3c";

       // gj.execute(st); //error trying to run multiple executes, so have to create new getJSON each time
       // new GetJSON(mapsActivity).execute(st);
       // new GetJSON(mapsActivity).execute(sth);
        //new GetJSON(mapsActivity).execute(stc);

    }

    //option 2: extend MapsActivity to access it's methods
    public String fetch2(double lat, double lon){
      //testToast("test");

        String s = "description";
        return s;
    }
}
