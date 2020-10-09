package com.example.bikebuddy;

/*
 * Author Theo Brown
 */
public class FuelEconomyActivity {

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

    /*
     *Returns false if trip is longer than the max range of the vehicle, returns true otherwise
     */
    public boolean calculateTripSufficientFuel(Trip trip, Vehicle v) {
        //FIXME
        if (trip.distance > v.calculateMaxRange())
            return false;
        else
            return true;
    }
}
