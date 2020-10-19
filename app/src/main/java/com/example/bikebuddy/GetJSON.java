package com.example.bikebuddy;


import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

class GetJSON extends AsyncTask<String, Void, String> {
  //  String returnThisString;

    MapsActivity ma;
    GoogleMap gm;


    public GetJSON(MapsActivity ma) {
        this.ma = ma;
    }
    public GetJSON(GoogleMap gm) {
        this.gm = gm;
    }
    public GetJSON() {

    }


    //this method will be called before execution
    //you can display a progress bar or something
    //so that user can understand that he should wait
    //as network operation may take some time
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //          Toast.makeText(getApplicationContext(), "onPreExecute is working", Toast.LENGTH_LONG).show();
    }

    //this method will be called after execution
    //so here we are displaying a toast with the json string
    @Override
    protected void onPostExecute(String jsonString) {
        try {
            addWeather(jsonString);

           // addFutureWeather(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addWeather(String jsonString) throws JSONException {

        JSONObject obj = new JSONObject(jsonString);
        if (obj.has("list")) {
            addFutureWeather(jsonString);
            System.out.println(("not null"));
        } else {


            JSONArray weather = obj.getJSONArray("weather");
            JSONObject weather0 = weather.getJSONObject(0);


            JSONObject coord = obj.getJSONObject("coord");

            double lon = coord.getDouble("lon");
            double lat = coord.getDouble("lat");

            String description = weather0.getString("description"); //description eg clear sky
            String main = weather0.getString("main"); //main eg Clear
            String iconID = weather0.getString("icon"); //weather icon id
            System.out.println((iconID));

            ma.weatherFunctions.addLocationsWeather(lat, lon, iconID, description);//adds weather Icon
        }
    }

    private void addFutureWeather(String jsonString) throws JSONException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm:ss");
        int offsetHours = ma.dateTimeFunctions.offsetHours;
        Calendar weatherCalendar = Calendar.getInstance();
        weatherCalendar.add(Calendar.HOUR, offsetHours);
        long weatherDateTime = weatherCalendar.getTimeInMillis() / 1000; // time in seconds

       // long weatherDateTime = (long) ma.dateTimeFunctions.weatherDateTime;
        //usae this to determine which icon to display

        JSONObject obj = new JSONObject(jsonString);

        JSONArray list = obj.getJSONArray("list");
       // JSONObject list0 = list.getJSONObject(0);
        //JSONObject list5 = list.getJSONObject(0);
        JSONObject listX = list.getJSONObject(0);
        Long dateTime = null;


        for (int i = 0; i < list.length(); i++) {
            JSONObject listItem = list.getJSONObject(i);
            Long dt = listItem.getLong("dt");
            if (weatherDateTime < dt) {
                listX = listItem;
                dateTime = dt;
                // System.out.println(i);
                break;
            }
        }

        JSONArray weather = listX.getJSONArray("weather");
        JSONObject weather0 = weather.getJSONObject(0);

        JSONObject city = obj.getJSONObject("city");
        System.out.println((city));

        //String dt = list0.getString("dt");
        System.out.println((dateTime));
        //String dt5 = list5.getString("dt");
       // System.out.println((dt5));
       // int dt6 = list5.getInt("dt");
       // long dt6 = list5.getLong("dt");
       // System.out.println((dt6)); // in seconds
      //  System.out.println((dt6*1000));

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeZone(TimeZone.getTimeZone("Pacific/Auckland"));
        currentCalendar.setTimeInMillis(dateTime * 1000);
        //SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm:ss");
        System.out.println(currentCalendar.getTimeInMillis());
        String currentDateTime = dateFormat.format(currentCalendar.getTime());
        System.out.println(currentDateTime);
        System.out.println(weatherDateTime); // divide by 1000

//        System.out.println(ma.getDateTimeFunctions().getWeatherDateTime());
//        Date date = null;
//        try {
//            date = dateFormat.parse(ma.getDateTimeFunctions().getWeatherDateTime());
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        System.out.println(date);
//        long weatherDate = date.getTime();
//        System.out.println(weatherDate/1000);
//        Calendar c = Calendar.getInstance();
//        c.setTime(dateFormat.parse(ma.dateTimeFunctions.weatherDateTime));

        JSONObject coord = city.getJSONObject("coord");
        System.out.println((coord));

        double lon = coord.getDouble("lon");
        double lat = coord.getDouble("lat");

        String description = weather0.getString("description"); //description eg clear sky
        String main= weather0.getString("main"); //main eg Clear
        String iconID = weather0.getString("icon"); //weather icon id
        System.out.println((iconID));
       // System.out.println((dt));

        ma.weatherFunctions.addLocationsWeather(lat, lon, iconID, description);//adds weather Icon

    }
    private void addFutureWeatherCount(String jsonString) throws JSONException {
        JSONObject obj = new JSONObject(jsonString);

        JSONArray list = obj.getJSONArray("list");
        JSONArray weather = obj.getJSONArray("weather");
        JSONObject weather0 = weather.getJSONObject(0);


        JSONObject coord = obj.getJSONObject("coord");

        double lon = coord.getDouble("lon");
        double lat = coord.getDouble("lat");

        String description = weather0.getString("description"); //description eg clear sky
        String main= weather0.getString("main"); //main eg Clear
        String iconID = weather0.getString("icon"); //weather icon id
        System.out.println((iconID));

        ma.weatherFunctions.addLocationsWeather(lat, lon, iconID, description);//adds weather Icon

    }


    //in this method we are fetching the json string
    @Override
    protected String doInBackground(String... strings) {
        try {
            //creating a URL
            //URL url = new URL(jsonRequestURL);
            URL url = new URL(strings[0]);

            //Opening the URL using HttpURLConnection
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            //StringBuilder object to read the string from the service
            StringBuilder sb = new StringBuilder();

            //We will use a buffered reader to read the string from service
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            //A simple string to read values from each line
            String json;

            //reading until we don't find null
            while ((json = bufferedReader.readLine()) != null) {
                //appending it to string builder
                sb.append(json + "\n");
            }
            System.out.println((sb.toString().trim()));
            //finally returning the read string
            return sb.toString().trim();
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}



