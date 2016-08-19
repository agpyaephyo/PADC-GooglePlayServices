package xyz.aungpyaephyo.gps.utils;

import android.content.res.Resources;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

import xyz.aungpyaephyo.gps.GPSApplication;
import xyz.aungpyaephyo.gps.R;

/**
 * Created by aung on 3/7/16.
 */
public class GPSConstants {

    public static final String PACKAGE_NAME = "xyz.aungpyaephyo.gps";
    public static final String BA_ACTIVITIES_DETECTED = PACKAGE_NAME + ".BA_ACTIVITIES_DETECTED";
    public static final String IE_DETECTED_ACTIVITIES = PACKAGE_NAME + ".IE_DETECTED_ACTIVITIES";

    public static final int LOCATION_DETECTION_INTERVAL = 5000; //ms
    public static final int ACTIVITY_DETECTION_INTERVAL = 5000; //ms

    public static final double LAT_MY_HOME = 16.895775;
    public static final double LNG_MY_HOME = 96.153431;

    public static final double LAT_MY_GYM = 16.8674854;
    public static final double LNG_MY_GYM = 96.154611;

    public static final int GEOFENCE_RADIUS = 100; //radius is in meter.
    public static final int GEOFENCE_EXPIRATION = 24 * 60 * 60 * 1000; //in ms - 24 hour.

    public static final HashMap<String, LatLng> GEOFENCES = new HashMap<>();
    static {
        Resources resources = GPSApplication.getContext().getResources();
        GEOFENCES.put(resources.getString(R.string.geofence_my_home), new LatLng(LAT_MY_HOME, LNG_MY_HOME));
        GEOFENCES.put(resources.getString(R.string.geofence_my_gym), new LatLng(LAT_MY_GYM, LNG_MY_GYM));
    }
}
