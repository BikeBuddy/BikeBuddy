package com.example.bikebuddy;

import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchWeather {
    //this method is actually fetching the json string
    public void getJSON(final String urlWebService) {
        /*
         * As fetching the json string is a network operation
         * And we cannot perform a network operation in main thread
         * so we need an AsyncTask
         * The constrains defined here are
         * Void -> We are not passing anything
         * Void -> Nothing at progress update as well
         * String -> After completion it should return a string and it will be the json string
         * */
        class GetJSON extends AsyncTask<Void, Void, String> {
            //this method will be called before execution
            //you can display a progress bar or something
            //so that user can understand that he should wait
            //as network operation may take some time
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                Toast.makeText(getApplicationContext(), "onPreExecute is working", Toast.LENGTH_LONG).show();
            }

            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                //display the fetched data from openWeatherMap as a toast
//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                try {
                    // covert and extract from Json string from openWeatherMap and obtain
                    // city, lon, lat, weather and icon id
                    JSONObject obj = new JSONObject(s);
                    System.out.println(obj);
                    // obtain city name
                    JSONArray weather = obj.getJSONArray("weather");
                    System.out.println("city name: ");
                    String cityName = obj.getString("name");
                    System.out.println(cityName);

                    //obtain lon and lat
                    JSONObject weatherArray0 = weather.getJSONObject(0);
                    JSONObject coord = obj.getJSONObject("coord");
                    System.out.println(coord);
                    String lon = coord.getString("lon");
                    String lat = coord.getString("lat");

                    // update static variable "lon" in MapsActivity -- FAILED
                    System.out.println("debug");

                    System.out.println(MapsActivity.lon);
                    MapsActivity.lon = Double.parseDouble(lon);
                    System.out.println(MapsActivity.lon);
                    //print lon and lat
                    System.out.println("lon");
                    System.out.println(lon);
                    System.out.println("lat");
                    System.out.println(lat);

                    //obtain weather and icon id (ie. clear sky, sunny etc.)
                    String sky = weatherArray0.getString("description");
                    String icon = weatherArray0.getString("icon");
                    System.out.println("sky");
                    System.out.println(sky);

                    // print icon id
                    System.out.println("icon");
                    System.out.println(icon);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {

                try {
                    //creating a URL
                    URL url = new URL(urlWebService);

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
                        sb.append(json + "\n" + "gary");
                    }
                    System.out.println("printing sb");
                    System.out.println(sb);
                    //finally returning the read string
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        //creating asynctask object and executing it
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }
}
