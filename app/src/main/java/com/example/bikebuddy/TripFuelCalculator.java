package com.example.bikebuddy;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/*
 * Author Theo Brown
 */

public class TripFuelCalculator extends AppCompatActivity {

    /*
     * Returns the Km/L of the completed trip
     */
    public double calculateTripKmL(Trip trip) {
        TextView fuelUsed = (TextView) findViewById(R.id.textFuelUsed);
        return (trip.distance / Double.valueOf(String.valueOf(fuelUsed)));
    }

    /*
     * Returns the predicted maximum range of a vehicle, assuming a full tank and consistent fuel usage.
     * Takes the values from the fields in content_fuel_calculator.xml
     */
    public double calculateMaxRange() {
        TextView fuelTankSize = (TextView) findViewById(R.id.textFuelTankSize);
        TextView avgKML = (TextView) findViewById(R.id.textKPL);
        return (Double.valueOf(String.valueOf(fuelTankSize)) / Double.valueOf(String.valueOf(avgKML)));
    }

    public TextView getMaxRange() {
        return (TextView) findViewById(R.id.textMaxRange);
    }

    /*
     * Outputs the calculated Maximum range to the text field
     */
    public void showMaxRange() {
        TextView output = (TextView) findViewById(R.id.textMaxRange);
        output.setText(String.valueOf(calculateMaxRange()));
    }

    /*
     *Returns false if trip is longer than the max range of the vehicle, returns true otherwise
     */
    public boolean calculateTripSufficientFuel(Trip trip) {
        if (trip.distance != null) { //check if there is a route plotted on the map for us to get a trip distance from.
            if (trip.distance > calculateMaxRange())
                return false;
            else
                return true;
        } else { // If no trip listed, use user entered data instead.
            TextView distance = (TextView) findViewById(R.id.textTripDistance);
            if (Double.valueOf(String.valueOf(distance)) > calculateMaxRange())
                return false;
            else
                return true;
        }
    }
}
