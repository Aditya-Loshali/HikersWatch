package com.example.adilos.hikerswatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    LocationManager locationManager;
    LocationListener locationListener;

    public void startListening(){
        //check if the android package manager has granted the permission required by app in the app manifest
        if(ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //when permission acquired , get the systems location service as a location manager
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //the app receives the result after permissions are requested
        //if the result granted are not none , again check the permission with package manager
        // to check if the result is registered with it or not
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startListening();
        }
    }

    public void updateLocationInfo(Location location) throws IOException {
        //Log.i("LocationInfo",location.toString());
        TextView latTextView = (TextView) findViewById(R.id.latTextView);
        TextView lonTextView = (TextView) findViewById(R.id.lonTextView);
        TextView accuracyTextView = (TextView) findViewById(R.id.accuracyTextView);
        TextView altTextView = (TextView) findViewById(R.id.altTextView);
        TextView speedTextView = (TextView) findViewById(R.id.speedTextView);
        TextView addressTextView = (TextView) findViewById(R.id.addressTextView);

        Log.i("Latitude",String.valueOf(location.getLatitude()));
        Log.i("Longitude",String.valueOf(location.getLongitude()));

        //update the text views using the location object and it's functions
        latTextView.setText("Latitude: "+String.valueOf(location.getLatitude()));
        lonTextView.setText("Longitude: "+String.valueOf(location.getLongitude()));
        accuracyTextView.setText("Accuracy: "+String.valueOf(location.getAccuracy()));
        altTextView.setText("Altitude: "+String.valueOf(location.getAltitude()));
        speedTextView.setText("Speed: "+String.valueOf(location.getSpeed()));

        //generate GeoCoder object which helps to to generate human readable address
        // in the current location's default format using Locale.getDefault
        Geocoder geoCode = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            String address = "Could not find address";
            //generate the local address using current latitude and longitude
            //with the help of GeoCoder which provide the functionality to reverse geocode
            //to obtain formatted address

            List<Address> addressList =
            geoCode.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if(addressList !=null && addressList.size()>0){
                address="Address: ";
              Log.i("PlaceInfo",addressList.get(0).toString());
                if(addressList.get(0).getSubThoroughfare()!=null){
                    //road number
                    address+= addressList.get(0).getSubThoroughfare()+" ";
                }
                if(addressList.get(0).getThoroughfare()!=null){
                    //road name
                    address+= addressList.get(0).getThoroughfare()+"\n";
                }
                if(addressList.get(0).getLocality()!=null){
                    //locality name
                    address+= ", "+addressList.get(0).getLocality()+"\n";
                }
                if(addressList.get(0).getPostalCode()!=null){
                    //postal code
                    address+= ", "+addressList.get(0).getPostalCode()+"\n";
                }
                if(addressList.get(0).getCountryName()!=null){
                    //country name
                    address+= ", "+addressList.get(0).getCountryName()+"\n";
                }
            }
            addressTextView.setText(address);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //to hide the actionbar when the app starts , until its runtime
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //calls the function to check for location permission and then set the location service
        startListening();

        //create a new location listener which listen's/detect's the changes in location
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try {
                    //if location changes , call the function to update the location information
                    updateLocationInfo(location);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        // if Android Build level is less than 23(Marshmallow) , simply request the location updates
        if (Build.VERSION.SDK_INT <23){
            //parameters ( get fine location using GPS , time , distance , Location Listener )
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }else {
            //if Android build is greater than 23 , then , first check and request permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                //if package manager has not granted the permission to access location , request permissions (Permission type, permission code/id )
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
            else{
                //parameters ( get fine location using GPS , time , distance , Location Listener )
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                //get current location at starting
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                try {
                    updateLocationInfo(location);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
