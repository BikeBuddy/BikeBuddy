package com.example.bikebuddy;

/*
 * Author Theo Brown
 */
public class Vehicle {
    private String vehicleID = "";
    private String make = "";
    private String model = "";
    private double fuelTankSize = 0; //size of the fuel tank in Litres
    private double fuelUsed = 0; //The amount of fuel used on the trip. Updates with each trip.
    private double totalFuelUsed = 0; //the total amount of fuel usage logged in the app over time.
    //private double avgMilesPerGallon = 0; //Average MPG
    private double avgKmPerLitre = 0;  //Average KM/L
    private double tachometer = 0;  //The vehicle's Tachometer reading.

    public Vehicle(String vehicleID, String make, String model, double fuelTankSize, double fuelUsed, double avgKmPerLitre, double tachometer) {
        this.vehicleID = vehicleID;
        this.make = make;
        this.model = model;
        this.fuelTankSize = fuelTankSize;
        this.fuelUsed = fuelUsed;
        //this.avgMilesPerGallon = avgMilesPerGallon;
        this.avgKmPerLitre = avgKmPerLitre;
        this.tachometer = tachometer; //the vehicle's tachometer reading

    }

    public void updateKmPerLitre(Double kpl) {
        //TODO
        setAvgKmPerLitre(kpl);
    }

    public String getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(String vehicleID) {
        this.vehicleID = vehicleID;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getFuelTankSize() {
        return fuelTankSize;
    }

    public void setFuelTankSize(double fuelTankSize) {
        this.fuelTankSize = fuelTankSize;
    }

    public double getFuelUsed() {
        return fuelUsed;
    }

    public void setFuelUsed(double fuelUsed) {
        this.fuelUsed = fuelUsed;
    }

//    public double getAvgMilesPerGallon() {
//        return avgMilesPerGallon;
//    }
//
//    public void setAvgMilesPerGallon(double avgMilesPerGallon) {
//        this.avgMilesPerGallon = avgMilesPerGallon;
//    }

    public double getAvgKmPerLitre() {
        return avgKmPerLitre;
    }

    public void setAvgKmPerLitre(double avgKmPerLitre) {
        this.avgKmPerLitre = avgKmPerLitre;
    }

    public double getTachometer() {
        return tachometer;
    }

    public void setTachometer(double tachometer) {
        this.tachometer = tachometer;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleID='" + vehicleID + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", fuelTankSize=" + fuelTankSize +
                ", fuelUsed=" + fuelUsed +
                ", avgKmPerLitre=" + avgKmPerLitre +
                ", tachometer=" + tachometer +
                '}';
    }
}
