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
        if(ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startListening();
        }
    }

    public void updateLocationInfo(Location location) throws IOException {
        Log.i("LocationInfo",location.toString());
        TextView latTextView = (TextView) findViewById(R.id.latTextView);
        TextView lonTextView = (TextView) findViewById(R.id.lonTextView);
        TextView accuracyTextView = (TextView) findViewById(R.id.accuracyTextView);
        TextView altTextView = (TextView) findViewById(R.id.altTextView);
        TextView speedTextView = (TextView) findViewById(R.id.speedTextView);
        TextView addressTextView = (TextView) findViewById(R.id.addressTextView);
        Log.i("Lattitude",String.valueOf(location.getLatitude()));
        Log.i("Longitude",String.valueOf(location.getLongitude()));
        latTextView.setText("Latitude: "+String.valueOf(location.getLatitude()));
        lonTextView.setText("Longitude: "+String.valueOf(location.getLongitude()));
        accuracyTextView.setText("Accuracy: "+String.valueOf(location.getAccuracy()));
        altTextView.setText("Altitude: "+String.valueOf(location.getAltitude()));
        speedTextView.setText("Speed: "+String.valueOf(location.getSpeed()));
        Geocoder geoCode = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            String address = "Could not find address";
            List<Address> addressList =
            geoCode.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if(addressList !=null && addressList.size()>0){
                address="Address: ";
              Log.i("PlaceInfo",addressList.get(0).toString());
                if(addressList.get(0).getSubThoroughfare()!=null){
                    address+= addressList.get(0).getSubThoroughfare()+" ";
                }
                if(addressList.get(0).getThoroughfare()!=null){
                    address+= addressList.get(0).getThoroughfare()+"\n";
                }
                if(addressList.get(0).getLocality()!=null){
                    address+= addressList.get(0).getLocality()+"\n";
                }
                if(addressList.get(0).getPostalCode()!=null){
                    address+= addressList.get(0).getPostalCode()+"\n";
                }
                if(addressList.get(0).getCountryName()!=null){
                    address+= addressList.get(0).getCountryName()+"\n";
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

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        startListening();
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try {
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
        if (Build.VERSION.SDK_INT <23){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
            else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
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
