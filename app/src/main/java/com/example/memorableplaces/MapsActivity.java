package com.example.memorableplaces;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    LocationManager locationManager;
    LocationListener locationListener;
    Geocoder geocoder;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);









    }

    public void centerMapOnLocation(Location location, String title)
    {

        if(location != null)
        {
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(userLocation).title("You Are Here"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);




           if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

               if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
               locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);
           }

       }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);



        Intent intent = getIntent();
        if(intent.getIntExtra("place number",0)==0)
        {
            //zoom in on user location
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {

                    centerMapOnLocation(location,"Your Are Here");

                }

                @Override
                public void onProviderDisabled(@NonNull String provider) {

                }

                @Override
                public void onProviderEnabled(@NonNull String provider) {

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }
            };
            if(ContextCompat.checkSelfPermission(MapsActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10000,0,locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centerMapOnLocation(lastKnownLocation,"Your Location");

            }
            else
            {

                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);
            }


        }
        else
        {
            Location place =new Location(locationManager.GPS_PROVIDER);
            place.setLatitude(MainActivity.locations.get(intent.getIntExtra("place number",0)).latitude);
            place.setLongitude(MainActivity.locations.get(intent.getIntExtra("place number",0)).longitude);
            centerMapOnLocation(place,MainActivity.addresses.get(intent.getIntExtra("place number",0)));
        }




        }

    @Override
    public void onMapLongClick(LatLng latLng) {

        Geocoder geocoder = new Geocoder(getApplicationContext(),Locale.getDefault());
        String address = "";
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            Log.i("address",addressList.get(0).toString());
            if(addressList != null && addressList.size()>0)
            {
                if(addressList.get(0).getFeatureName() != "null")
                {
                    address += addressList.get(0).getFeatureName()+ "";


                }
                address += addressList.get(0).getSubAdminArea();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if(address=="")
        {
            SimpleDateFormat sdf  = new SimpleDateFormat("yyyyMMdd_HHmmss");
            address+= sdf.format(new Date());

        }
        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
        MainActivity.addresses.add(address);
        MainActivity.locations.add(latLng);
        MainActivity.arrayAdapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), "Location Saved", Toast.LENGTH_SHORT).show();

    }
}
