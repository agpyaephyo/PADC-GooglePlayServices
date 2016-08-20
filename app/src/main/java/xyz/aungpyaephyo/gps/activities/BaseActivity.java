package xyz.aungpyaephyo.gps.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;

import xyz.aungpyaephyo.gps.GPSApplication;
import xyz.aungpyaephyo.gps.controllers.ControllerActivityRecognition;
import xyz.aungpyaephyo.gps.utils.GPSConstants;
import xyz.aungpyaephyo.gps.utils.GeofenceUtils;
import xyz.aungpyaephyo.gps.utils.UserActivityUtils;

/**
 * Created by aung on 3/7/16.
 */
public abstract class BaseActivity extends AppCompatActivity implements
        ControllerActivityRecognition,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int ACCESS_PERMISSIONS_LOCATION_REQUEST = 100;
    private static final int ACCESS_PERMISSIONS_LAST_LOCATION = 101;
    private static final int ACCESS_PERMISSIONS_SETUP_GEOFENCES = 102;
    private static final int ACCESS_PERMISSIONS_CURRENT_PLACE_NAME = 103;

    protected static final int REQUEST_CODE_PLACE_PICKER = 1000;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private ActivityDetectionBroadcastReceiver mActivityDetectionBR;

    private ActivityRecognitionResultCallback mActivityRecognitionResultCallback;
    private GeofenceResultCallback mGeofenceResultCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityDetectionBR = new ActivityDetectionBroadcastReceiver();
        mActivityRecognitionResultCallback = new ActivityRecognitionResultCallback();
        mGeofenceResultCallback = new GeofenceResultCallback();
        buildGoogleApiClient();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PLACE_PICKER) {
            if (resultCode == RESULT_OK) {
                Place pickedPlace = PlacePicker.getPlace(getApplicationContext(), data);
                onPlacePicked(pickedPlace);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mActivityDetectionBR,
                new IntentFilter(GPSConstants.BA_ACTIVITIES_DETECTED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mActivityDetectionBR);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ACCESS_PERMISSIONS_LOCATION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    makeLocationRequest();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case ACCESS_PERMISSIONS_LAST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Location location = getLastLocation();
                    onLocationRetrieved(location);
                }
                break;
            }
            case ACCESS_PERMISSIONS_SETUP_GEOFENCES: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupGeofences();
                }
                break;
            }
            case ACCESS_PERMISSIONS_CURRENT_PLACE_NAME: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentPlaceName();
                }
                break;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(GPSApplication.LOG_TAG, "GPS onConnected");

        /* Make location request with specified Priority & Frequency.
        makeLocationRequest();
        */

        Location location = getLastLocation();
        if (location != null) {
            onLocationRetrieved(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(GPSApplication.LOG_TAG, "GPS onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(GPSApplication.LOG_TAG, "GPS onConnectionFailed");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(GPSApplication.LOG_TAG, "GPS onLocationChanged");
        onLocationRetrieved(location);
    }

    //ControllerActivityRecognition
    @Override
    public void requestActivityUpdate() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "Google Api Client is not connected", Toast.LENGTH_SHORT).show();
        } else {
            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                    mGoogleApiClient, GPSConstants.ACTIVITY_DETECTION_INTERVAL,
                    UserActivityUtils.getActivityDetectionPendingIntent()).setResultCallback(mActivityRecognitionResultCallback);
        }
    }

    //ControllerActivityRecognition
    @Override
    public void removeActivityUpdate() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "Google Api Client is not connected", Toast.LENGTH_SHORT).show();
        } else {
            ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                    mGoogleApiClient,
                    UserActivityUtils.getActivityDetectionPendingIntent()).setResultCallback(mActivityRecognitionResultCallback);
        }
    }

    //onResult callback from setting up Geofences.
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            Toast.makeText(this, "Geofences Added", Toast.LENGTH_SHORT).show();
        } else {
            String errorMsg = GeofenceUtils.getErrorString(status.getStatusCode());
            Log.d(GPSApplication.LOG_TAG, errorMsg);
        }
    }

    protected void setupGeofences() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "Google Api Client is not connected", Toast.LENGTH_SHORT).show();
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        ACCESS_PERMISSIONS_SETUP_GEOFENCES);

                return;
            }
            LocationServices.GeofencingApi.addGeofences(mGoogleApiClient,
                    GeofenceUtils.getGeofencingRequest(),
                    GeofenceUtils.getGeofencePendingIntent()).setResultCallback(mGeofenceResultCallback);
        }
    }

    //#### ONLY PRIVATE FUNCTIONS GO BELOW THIS ####

    /**
     * Build GoogleApiClient with required callback listeners.
     */
    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    /**
     * Make location request with Runtime permission checking.
     */
    private void makeLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(GPSConstants.LOCATION_DETECTION_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    ACCESS_PERMISSIONS_LOCATION_REQUEST);

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Get Last Location with Runtime Permission checking.
     *
     * @return
     */
    private Location getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    ACCESS_PERMISSIONS_LAST_LOCATION);

            return null;
        }
        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    protected void startPlacePicker() {
        try {
            Intent intent = new PlacePicker.IntentBuilder().build(this);
            startActivityForResult(intent, REQUEST_CODE_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(GPSApplication.LOG_TAG, e.getMessage());
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(GPSApplication.LOG_TAG, e.getMessage());
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    protected void getCurrentPlaceName() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_PERMISSIONS_CURRENT_PLACE_NAME);
            return;
        }

        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {
                onGetCurrentPlaceName(likelyPlaces);
            }
        });
    }

    /**
     * Placeholder method for manipulating retrieved location object.
     * Not abstract because not every child activity would need Location.
     *
     * @param location
     */
    protected void onLocationRetrieved(Location location) {

    }

    /**
     * Placeholder method for manipulating detected activities.
     *
     * @param detectedActivities
     */
    protected void onActivitiesDetected(ArrayList<DetectedActivity> detectedActivities) {

    }

    protected void onPlacePicked(Place pickedPlace) {

    }

    protected void onGetCurrentPlaceName(PlaceLikelihoodBuffer likelyPlaces) {

    }

    //#### ONLY INNER CLASSES GO BELOW THIS ####
    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<DetectedActivity> detectedActivities = intent.getParcelableArrayListExtra(GPSConstants.IE_DETECTED_ACTIVITIES);
            if (detectedActivities != null) {
                onActivitiesDetected(detectedActivities);
            }
        }
    }

    public static class ActivityRecognitionResultCallback implements ResultCallback<Status> {

        @Override
        public void onResult(@NonNull Status status) {
            if (status.isSuccess()) {
                Toast.makeText(GPSApplication.getContext(), "Successfully added / removed activity detection", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(GPSApplication.getContext(), "Error adding / removing activity detection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class GeofenceResultCallback implements ResultCallback<Status> {

        @Override
        public void onResult(@NonNull Status status) {
            if (status.isSuccess()) {
                Toast.makeText(GPSApplication.getContext(), "Geofences Added", Toast.LENGTH_SHORT).show();
            } else {
                String errorMsg = GeofenceUtils.getErrorString(status.getStatusCode());
                Log.d(GPSApplication.LOG_TAG, errorMsg);
                Toast.makeText(GPSApplication.getContext(), GPSApplication.LOG_TAG + " - " + errorMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
