package com.caitlynwiley.memorableplaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    ArrayList<String> addresses;
    ArrayList<LatLng> locations;
    Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        addresses = new ArrayList<>();
        locations = new ArrayList<>();
        //set first element to be null to account for the spot in the list that says "add new place..."
        locations.add(0, null);

        geocoder = new Geocoder(this, Locale.getDefault());
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // move marker and camera
                mMap.clear();
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
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


        //check the focus extra added to intent to see where to focus
        String focus = getIntent().getStringExtra("FOCUS");
        if (focus.equals("USER")) {
            //focus on user
        } else {
            //focus on the location clicked on
            int position = Integer.parseInt(focus);

        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                // Add a marker
                locations.add(latLng);
                mMap.addMarker(new MarkerOptions().position(latLng).title(getAddress(latLng)));

                // possibly save all locations in this class, and just pass back an array to main activity?
                // main class doesn't need actual location data, just addresses to show in list
            }
        });

        // ask for permission if we don't have it
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, 0);
            return;
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            locationListener.onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    // get the address string (first line) from the location using geocoder
    private String getAddress(LatLng latLng) {
        try {
            List<Address> addrs = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addrs.size() != 0) {
                Address address = addrs.get(0);
                String title = null;
                if (address.getThoroughfare() != null) {
                    title = address.getThoroughfare();
                }
                title = (title == null ? "<No address>" : title);
                addresses.add(title);
                return title;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    // Add list of addresses to intent to send it back to the main activity without restarting the activity
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra("PLACES", addresses);
        super.onBackPressed();
    }
}