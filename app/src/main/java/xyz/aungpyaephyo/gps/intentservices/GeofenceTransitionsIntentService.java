package xyz.aungpyaephyo.gps.intentservices;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import xyz.aungpyaephyo.gps.GPSApplication;
import xyz.aungpyaephyo.gps.utils.GeofenceUtils;
import xyz.aungpyaephyo.gps.utils.NotificationUtils;

/**
 * Created by aung on 3/7/16.
 */
public class GeofenceTransitionsIntentService extends IntentService {

    public GeofenceTransitionsIntentService() {
        super(GeofenceTransitionsIntentService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceUtils.getErrorString(geofencingEvent.getErrorCode());
            Log.d(GPSApplication.LOG_TAG, errorMessage);
            return;
        }

        int geofenceTransitionType = geofencingEvent.getGeofenceTransition();
        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

        String geofenceDetails = GeofenceUtils.getGeofenceTransitionDetails(triggeringGeofences);
        String geofenceEvent = GeofenceUtils.getGeofenceTransition(geofenceTransitionType) + " - " + geofenceDetails;

        if (geofenceTransitionType == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
            NotificationUtils.showNotification(geofenceEvent);
        } else {
            NotificationUtils.showNotification(geofenceEvent); //Need to see the error in development. TODO remove that in production.
            //Log.d(GPSApplication.LOG_TAG, geofenceEvent);
        }
    }
}
