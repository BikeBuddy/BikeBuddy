package com.example.bikebuddy;

/*
 * Author Theo Brown
 */
public class FuelEconomyCalculator {

    /*
     * Calculates the Miles per Gallon of a trip. Commented out until multi-unit support is introduced.
     */
//    public double calculateMPG(Trip trip, Vehicle v) {
//       return (trip.distance/v.getFuelUsed());
//    }

    /*
     * Returns the Km/L of the completed trip
     */
    public double calculateTripKmL(Trip trip, Vehicle v) {
        return (trip.distance / v.getFuelUsed());
    }

    /*
     * Returns the predicted maximum range of a vehicle, assuming a full tank and consistent fuel usage.
     */
    public double calculateMaxRange(Vehicle v) {
        return (v.getFuelTankSize() / v.getAvgKmPerLitre());
    }

}
