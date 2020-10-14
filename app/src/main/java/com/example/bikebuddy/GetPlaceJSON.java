


package com.example.bikebuddy;


        import android.os.AsyncTask;
        import android.text.style.ScaleXSpan;
        import android.widget.Toast;

        import com.google.android.gms.maps.GoogleMap;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.BufferedReader;
        import java.io.InputStreamReader;
        import java.net.HttpURLConnection;
        import java.net.URL;

        import static android.widget.Toast.LENGTH_LONG;

class GetPlaceJSON extends AsyncTask<String, Void, String> {
    //  String returnThisString;

    MapsActivity ma;
    GoogleMap gm;


    public GetPlaceJSON(MapsActivity ma) {
        this.ma = ma;
    }
    public GetPlaceJSON(GoogleMap gm) {
        this.gm = gm;
    }
    public GetPlaceJSON() {

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
            System.out.print("onPostExecute gary");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addWeather(String jsonString) throws JSONException {
        System.out.print(jsonString);
        System.out.print("add weather gary");
        JSONObject obj = new JSONObject(jsonString);

        JSONArray results = obj.getJSONArray("results");
        System.out.print("inside addWeather");
        System.out.print(results);
        String item0 = results.getString(0);
        JSONObject insideItem0 = new JSONObject(item0);
        //String business_status_s = insideItem0.getString("place_id");
        String geometry = insideItem0.getString("geometry");
        JSONObject geometry1  = new JSONObject(geometry);
        //get lat , lng
        String location = geometry1.getString("location");
        JSONObject location_obj = new JSONObject(location);
        Double lat = location_obj.getDouble("lat");
        Double lon = location_obj.getDouble("lng");


        Toast toast = Toast.makeText(ma, Double.toString(lat) , Toast.LENGTH_LONG);
        toast.show();

//        JSONObject weather0 = weather.getJSONObject(0);
//
//
//        JSONObject coord = obj.getJSONObject("coord");
//
//        double lon = coord.getDouble("lon");
//        double lat = coord.getDouble("lat");
//
//        String description = weather0.getString("description"); //description eg clear sky
//        String main= weather0.getString("main"); //main eg Clear
//        String iconID = weather0.getString("icon"); //weather icon id
//        System.out.println((iconID));
//
        //ma.weatherFunctions.addLocationsWeather(lat, lon, iconID, description);//adds weather Icon

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
            System.out.print("do in background running");
            //finally returning the read string
            return sb.toString().trim();
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}



