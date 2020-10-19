package com.example.bikebuddy;

import android.widget.TextView;

/*
 * Author Theo Brown
 *
 * Deprecated class until vehicle data is saved and can be accessed properly. Use TripFuelCalculator.java in the meantime.
 */
@Deprecated
public class FuelCalculator {

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
        if (trip.distance > calculateMaxRange(v))
            return false;
        else
            return true;
    }
}
