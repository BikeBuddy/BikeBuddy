package com.example.bikebuddy;

import android.os.Handler;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateTimeFunctions {

    MapsActivity mapsActivity;
   // GoogleMap googleMap;

    //Thread thread;
    Handler handler;
    TextView currentDateTimeDisplay;
    TextView weatherDateTimeDisplay;

    private Calendar weatherCalendar;
    private Calendar currentCalendar;

    public String getWeatherDateTime() {
        return weatherDateTime;
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    public String weatherDateTime;
    private String currentDateTime;
    private SimpleDateFormat dateFormat;

    int offsetHours;

    TextView dateTimeDisplay;



    //public DateTimeFunctions(MapsActivity mapsActivity, GoogleMap googleMap, Handler handler, TextView currentDateTimeDisplay) {
   public DateTimeFunctions(MapsActivity mapsActivity, Handler handler, TextView currentDateTimeDisplay, TextView weatherDateTimeDisplay) {
        this.mapsActivity = mapsActivity;
       // this.googleMap = googleMap;
        this.handler = handler;
        this.currentDateTimeDisplay = currentDateTimeDisplay;
        this.weatherDateTimeDisplay = weatherDateTimeDisplay;
        //this.dateTimeDisplay = currentDateTimeDisplay;

      // currentCalendar = Calendar.getInstance();
       //weatherCalendar = Calendar.getInstance();

        dateFormat = new SimpleDateFormat("dd/MM HH:mm:ss");

        offsetHours = 0;

        initDateTime();



    }

   public void updateDateTime() {
        currentCalendar = Calendar.getInstance();
        weatherCalendar = Calendar.getInstance();
        weatherCalendar.add(Calendar.HOUR, offsetHours);
       // Calendar c = Calendar.getInstance();
       // SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm:ss");
        currentDateTime = dateFormat.format(currentCalendar.getTime());
        weatherDateTime = dateFormat.format(weatherCalendar.getTime());
        //String date = dateFormat.format(c.getTime());
        weatherDateTimeDisplay.setText("Weather date/time: " + weatherDateTime);
        currentDateTimeDisplay.setText("Current date/time: " + currentDateTime);
//        calendar = Calendar.getInstance();
//        dateFormat = new SimpleDateFormat("dd/MM HH:mm:ss");
//
//        date = dateFormat.format(calendar.getTime());
//        dateTimeDisplay.setText("Weather date/time: " + date);

    //mapsActivity.getLocationsWeather();
    }


    //date time display stuff
//    private TextView dateTimeDisplay;
//    private Calendar calendar;
//    private SimpleDateFormat dateFormat;
//    private String date;


    public void addHour() {
       // weatherCalendar.add(Calendar.HOUR, 1);
        //updateDateTime();
        //max future weather available is 5 days, 3 hourly
        if (offsetHours < 120)
            offsetHours +=3;
        updateDateTime();
        //mapsActivity.createLocationsList();
        mapsActivity.getLocationsWeather();

    }
    public void minusHour() {

       // weatherCalendar.add(Calendar.HOUR, -1);
       // updateDateTime();
        if (offsetHours > 0)
            offsetHours -= 3;
        updateDateTime();
     //   mapsActivity.createLocationsList();
        mapsActivity.getLocationsWeather();

    }
    public void resetHour() {

        // weatherCalendar.add(Calendar.HOUR, -1);
        // updateDateTime();
        offsetHours =0;
        updateDateTime();
     //   mapsActivity.createLocationsList();
        mapsActivity.getLocationsWeather();

    }

//Handler mhandler = new Handler();

    public void initDateTime() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(1000);
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                               updateDateTime();
                                //  dateTimeDisplay = (TextView).findViewById(R.id.dateTimeDisplay);
//                                Calendar c = Calendar.getInstance();
//                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm:ss");
//                                String date = dateFormat.format(c.getTime());
//                                // int min = c.get(Calendar.MINUTE);
//                                // int hour=c.get(Calendar.HOUR);
//                                // int sec = c.get(Calendar.SECOND);
//                                //  dateTimeDisplay.setText(String.valueOf(hour)+":"+String.valueOf(min)+":"+String.valueOf(sec));
//                                dateTimeDisplay.setText("Weather date/time: " + date);
                                //dateTimeDisplay.setText("Weather date/time: " + date);
                                //calendar = Calendar.getInstance();
//                                dateFormat = new SimpleDateFormat("dd/MM HH:mm:ss");
//
//                                date = dateFormat.format(calendar.getTime());
//                                dateTimeDisplay.setText("Weather date/time: " + date);
//                                currentCalender = Calendar.getInstance();
//                                weatherCalender = Calendar.getInstance();
//                                // Calendar c = Calendar.getInstance();
//                                // SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm:ss");
//                                currentDateTime = dateFormat.format(currentCalender.getTime());
//                                weatherDateTime = dateFormat.format(weatherCalender.getTime());
//                                //String date = dateFormat.format(c.getTime());
//                                weatherDateTimeDisplay.setText("Weather date/time: " + weatherDateTime);
//                                currentDateTimeDisplay.setText("Current date/time: " + currentDateTime);
                            }
                        });
                    } catch (Exception e) {

                }
                }
            }
        }).start();}




}
