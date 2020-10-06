package com.example.bikebuddy;

public class Vehicle {
    private String vehicleID = "";
    private String make = "";
    private String model = "";
    private double fuelTankSize = 0; //size of the fuel tank in Litres
    private double avgMilesPerGallon = 0; //Average MPG
    private double avgKmPerLitre = 0;  //Average KM/L
    private double tachometer = 0;

    public Vehicle(String vehicleID, String make, String model, int fuelTankSize, int avgMilesPerGallon, int avgKmPerLitre, int tachometer) {
        this.vehicleID = vehicleID;
        this.make = make;
        this.model = model;
        this.fuelTankSize = fuelTankSize;
        this.avgMilesPerGallon = avgMilesPerGallon;
        this.avgKmPerLitre = avgKmPerLitre;
        this.tachometer = tachometer;
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

    public void setFuelTankSize(int fuelTankSize) {
        this.fuelTankSize = fuelTankSize;
    }

    public double getAvgMilesPerGallon() {
        return avgMilesPerGallon;
    }

    public void setAvgMilesPerGallon(int avgMilesPerGallon) {
        this.avgMilesPerGallon = avgMilesPerGallon;
    }

    public double getAvgKmPerLitre() {
        return avgKmPerLitre;
    }

    public void setAvgKmPerLitre(int avgKmPerLitre) {
        this.avgKmPerLitre = avgKmPerLitre;
    }

    public double getTachometer() {
        return tachometer;
    }

    public void setTachometer(int tachometer) {
        this.tachometer = tachometer;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleID='" + vehicleID + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", fuelTankSize=" + fuelTankSize +
                ", avgMilesPerGallon=" + avgMilesPerGallon +
                ", avgKmPerLitre=" + avgKmPerLitre +
                ", tachometer=" + tachometer +
                '}';
    }
}
