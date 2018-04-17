package com.example.saurabh.testmap;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
//import android.graphics.Color;
//import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
//import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;



import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Locale;

//import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private int Mocking = 0;

    private GoogleMap mMap;
    //public Marker m1;
    LatLng newlatlng;
    MockLocationProvider mockGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!isMockSettingsON()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("In order to use this app you must enable mock location do you want to enable it now?").setTitle("Mock location is not enable");
            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                    startActivity(i);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        createLocationRequest();


        //Toolbar tb = findViewById(R.id.toolbar);
        //setSupportActionBar(tb);
        //tb.setSubtitle("Your Location");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.current_location);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        enableMyLocationIfPermitted();

        mMap.getUiSettings().setZoomControlsEnabled(true);
        // mMap.setMinZoomPreference(11);

/*
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("OnlongClick"));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                CameraPosition newposition = mMap.getCameraPosition();
                Toast.makeText(MainActivity.this,newposition.toString(),Toast.LENGTH_SHORT).show();
                newlatlng = latLng;

            }
        });
        */

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("OnlongClick"));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                CameraPosition newposition = mMap.getCameraPosition();
                Toast.makeText(MainActivity.this,newposition.toString(),Toast.LENGTH_SHORT).show();
                newlatlng = latLng;

            }
        });




    }

    private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }

    private void showDefaultLocation() {
        Toast.makeText(this, "Location permission not granted, " +
                        "showing default location",
                Toast.LENGTH_SHORT).show();
        LatLng redmond = new LatLng(47.6739881, -122.121512);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(redmond));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Got permission location ",Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this,"No location permissions",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }




    private void askRunTimePermissions() {
        ActivityCompat.requestPermissions(this
                ,new  String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_FINE_LOCATION);
    }




    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    String TAG = "MainActivity";

    /* use for test */
    final float dummyLat = -10;
    final float dummylong = 10;
    GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    public void btmocklocation(View view) {



        if (isMockSettingsON()) {
            mockGPS = new MockLocationProvider(LocationManager.GPS_PROVIDER, this);


            double lat = newlatlng.latitude;
            double lon = newlatlng.longitude;
            double alt = 0;
            float accurate = 0;


            mockGPS.pushLocation(lat, lon, alt, accurate);
            Mocking = 1;
        }


    }




    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();

    }


    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }



    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(0);
        mLocationRequest.setSmallestDisplacement(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private boolean isMockSettingsON() {
        boolean isMockLocation = false;
        try
        {
            //if marshmallow
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                AppOpsManager opsManager = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);
                isMockLocation = (opsManager.checkOp(AppOpsManager.OPSTR_MOCK_LOCATION, android.os.Process.myUid(), BuildConfig.APPLICATION_ID)== AppOpsManager.MODE_ALLOWED);
            }
            else
            {
                // in marshmallow this will always return true
                isMockLocation = !android.provider.Settings.Secure.getString(this.getContentResolver(), "mock_location").equals("0");
            }
        }
        catch (Exception e)
        {
            return isMockLocation;
        }

        return isMockLocation;

    }




    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


    }



    public void stopmock(View view) {

        if(Mocking==1) {

            mockGPS.shutdown();
            Mocking = 0;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location.getLatitude() == dummyLat && location.getLongitude() == dummylong) {
            Toast.makeText(this,"Mock location application is working",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,String.format(Locale.US,"setting mock to Latitude=%f, Longitude=%f Altitude=%f Accuracy=%f",
                    location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getAccuracy()),Toast.LENGTH_SHORT).show();
        }
    }
    }


