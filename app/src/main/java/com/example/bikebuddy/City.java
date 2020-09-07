package com.example.bikebuddy;

public class City {
    private double lat;
    private double lng;
    private String name;

    public City(double lat, double lng, String name) {
        this.lat = lat;
        this.lng = lng;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public double getLat(){
        return this.lat;
    }

    public double getLng(){
        return this.lng;
    }
}
