package alavo.raymond.indoorlocalization;

/**
 * Created by Raymond on 6/16/2016.
 * This class ask the permission to the user and display the map.
 *
 * It defines Click Listener and the StepListener
 */

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;

public class MainActivity extends FragmentActivity
        implements
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private boolean mPermissionDenied = false;

    double lat, lon;

    GoogleMap mMap;
    SensorManager sm;
    StepDetection sd;
    StepPosition sp;
    DevicePosition dp;
    GoogleMapDrw gm;
    boolean isWalking = false;
    Location location = new Location("");
    LatLng lastKnown;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get reference to the fragment in the activity_main layout
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        //place the map in the application
        mapFragment.getMapAsync(this);

        //the latitude and longitude through the intent from the select_activity
        Intent dr = getIntent();
        lat = dr.getDoubleExtra("lat",0);
        lon = dr.getDoubleExtra("lon",0);

        //define a LatLng object with the latitude and longitude obtained
        lastKnown = new LatLng(lat, lon);
    }

    /*
    Method form the interface onMapReadyCallBack, used when the map
    is ready to be used
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        //call the enableLocation method to have permission to
        //access the user location.
        enableMyLocation();

        //instantiate a GoogleMapDrw object to show the actual location
        gm = new GoogleMapDrw(map, lastKnown);

        /*
        *   this method allows to start the localization when the user
        *   click on his actual position
         */
        map.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
                location.setLatitude(arg0.latitude);
                location.setLongitude(arg0.longitude);

                //initialize the current position in the StepPosition Class
                sp.setmCurrentLocation(location);

                //we start to record the movement of the user on a new segment
                if (!isWalking) {
                    isWalking = true;
                    gm.newSegment();        //start a new segment
                    gm.newPoint(arg0, 0);   //add a new point to the segment

                }
                //we stop to record the movement and add a end marker
                else {
                    isWalking = false;
                    gm.endSegment();
                }
            }
        });

        //start the sensor manager
        SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //instantiate the step detection object
        sd = new StepDetection(sm);

        //start the step Listener
        sd.setStepListener(mStepDetectionListener);

        //instantiate the device position object
        dp = new DevicePosition(sm);

        //instantiate the step position object
        sp = new StepPosition();

        //set the new position
        sp.setmCurrentLocation(location);
    }

    /*
    *   Methods that allows to detect a step from the user
     */
    private StepDetection.StepDetectionListener mStepDetectionListener = new StepDetection.StepDetectionListener() {

        //this listener will detect a new step
        public void newStep(float stepSize) {
            //find the new position of the user
            Location newloc = sp.computeNextStep(stepSize, dp.orientationVals[0]);

            //if the user is still walking
            if (isWalking) {
                //add the new position on the map
                gm.newPoint(new LatLng(newloc.getLatitude(), newloc.getLongitude()),dp.orientationVals[0]);
            }
        }
    };

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    /*
    *Request permission to have access to the localization
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }


    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

}