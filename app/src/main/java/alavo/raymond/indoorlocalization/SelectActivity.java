package alavo.raymond.indoorlocalization;

/**
 * Created by Raymond on 6/16/2016.
 * This class have to main purpose.
 * The first one is to get the localization of the user in latitude and longitude
 * from the GoogleApiClient.
 * The second, is to return those coordinates to the MainActivity class through
 * an intent and using a buttonListener
 */

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class SelectActivity extends AppCompatActivity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private Button startButton;
    private TextView latitude, longitude;

    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    double lat, lon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        //call the method to get a connection to the GoogleApiClient
        buildGoogleApiClient();

        //get references for the button and textViews
        startButton = (Button) findViewById(R.id.startButton);
        latitude = (TextView) findViewById(R.id.lat);
        longitude = (TextView) findViewById(R.id.lon);

        //set a listener on the button
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //call the method when a click is performed
                startLocalization();
            }
        });
    }

    /*
    * This method run the MainActivity Class
    */
    public void startLocalization() {
        //declare the intent
        Intent main = new Intent(this, MainActivity.class);
        main.putExtra("lat", lat);  //put the latitude
        main.putExtra("lon", lon);  //put the longitude
        startActivity(main);        //send the intent
    }

    /*
    * This method is called when the connection is successful
    */
    @Override
    public void onConnected(Bundle bundle) {

        //request the location coordinates
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //Update location every second
        mLocationRequest.setInterval(100);

        //check permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        //get the location
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();      //get the latitude
            lon = mLastLocation.getLongitude();     //get the longitude
            latitude.setText(lat+"");
            longitude.setText(lon+"");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();
    }

    /*
    * If the connection failed retry to connect
    */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }

    /*
    * Configure, connect a GoogleApiClient
     */
    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }
}