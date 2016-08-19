package xyz.aungpyaephyo.gps.utils;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import xyz.aungpyaephyo.gps.GPSApplication;
import xyz.aungpyaephyo.gps.R;
import xyz.aungpyaephyo.gps.intentservices.GeofenceTransitionsIntentService;

/**
 * Created by aung on 3/7/16.
 */
public class GeofenceUtils {

    public static String getErrorString(int errorCode) {
        Resources resources = GPSApplication.getContext().getResources();
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return resources.getString(R.string.geofence_not_available);
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return resources.getString(R.string.geofence_registered_too_many);
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return resources.getString(R.string.geofence_provided_too_many_pending_intent);
            default:
                return resources.getString(R.string.geofence_unknown_error);
        }
    }

    public static String getGeofenceTransitionDetails(List<Geofence> geofenceList) {
        StringBuilder geofenceDetails = new StringBuilder();

        for (Geofence geofence : geofenceList) {
            geofenceDetails.append(geofence.getRequestId() + ", ");
        }

        return geofenceDetails.toString();
    }

    public static String getGeofenceTransition(int geofenceTransition) {
        Resources resources = GPSApplication.getContext().getResources();
        switch (geofenceTransition) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return resources.getString(R.string.geofence_transition_enter);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return resources.getString(R.string.geofence_transition_exit);
            default:
                return resources.getString(R.string.geofence_transition_unknown);
        }
    }

    public static GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(populateDummyGeofenceList());
        return builder.build();
    }

    public static PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(GPSApplication.getContext(), GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(GPSApplication.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    //#### ONLY PRIVATE HELPER FUNCTIONS GOES BELOW THIS POINT ####

    private static List<Geofence> populateDummyGeofenceList() {
        List<Geofence> geofenceList = new ArrayList<>();
        for (Map.Entry<String, LatLng> entry : GPSConstants.GEOFENCES.entrySet()) {
            geofenceList.add(new Geofence.Builder()
                    .setRequestId(entry.getKey())
                    .setCircularRegion(entry.getValue().latitude, entry.getValue().longitude, GPSConstants.GEOFENCE_RADIUS)
                    .setExpirationDuration(GPSConstants.GEOFENCE_EXPIRATION)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }

        return geofenceList;
    }
}
