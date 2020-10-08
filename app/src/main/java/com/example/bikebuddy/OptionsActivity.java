package com.example.bikebuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OptionsActivity extends AppCompatActivity {
        MapsActivity mapPage;

        protected void onCreate(Bundle saveInstanceState){
            //mapPage
            super.onCreate(saveInstanceState);
            setContentView(R.layout.activity_options);
            Button backButton = (Button) findViewById(R.id.back);
            backButton.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent mapView = new Intent(getApplicationContext(),MapsActivity.class);
                    startActivity(mapView);
                }
            });
        }

}



