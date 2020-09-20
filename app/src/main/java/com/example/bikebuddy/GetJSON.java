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
           // super.onPostExecute(jsonString);
            System.out.println("jsonString");
            System.out.println(jsonString);
            System.out.println("endjsonString");
            //this.ma.weatherstring = jsonString;


            addWeather(jsonString);
             // Toast.makeText(MapsActivity, "JSON RESPONSE"+ jsonString, Toast.LENGTH_LONG).show();
            //showTrip(parseJsonToDirections(jsonString,start,destination));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addWeather(String jsonString) throws JSONException {
        JSONObject obj = new JSONObject(jsonString);
        //System.out.println((obj.getJSONArray("coord")));
        JSONArray weather = obj.getJSONArray("weather");
        JSONObject weather0 = weather.getJSONObject(0);
        String description = weather0.getString("description");

        JSONObject coord = obj.getJSONObject("coord");

        double lon = coord.getDouble("lon");
        double lat = coord.getDouble("lat");

        //or
        //String lon = coord.getString("lon");
       // String lat = coord.getString("lat");

       // String description = weather.getString(2); //description eg clear sky
       // String description = weather.getString(1); //main eg Clear
     //   String iconID = weather.getString(3);
        System.out.println((lon));
        System.out.println((lat));
        System.out.println((description));

        ma.weatherstring = "description";

    }


    //in this method we are fetching the json string
    @Override
    protected String doInBackground(String... strings) {
        try {
            //creating a URL
            //URL url = new URL(jsonRequestURL);
            URL url = new URL(strings[0]);
            System.out.println(strings[0]);
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

            //finally returning the read string
            //jsonString =sb.toString().trim();
            System.out.println(sb.toString().trim());
            return sb.toString().trim();
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}



