package xyz.aungpyaephyo.gps.utils;

import com.google.firebase.analytics.FirebaseAnalytics;

import xyz.aungpyaephyo.gps.GPSApplication;

/**
 * Created by aung on 8/19/16.
 */
public class FAUtils {

    public static final String ACTION_TAP_GET_CURRENT_PLACE_NAME = "Get Current Place Name";

    private static FAUtils objInstance;

    private FirebaseAnalytics mFirebaseAnalytics;

    private FAUtils() {
        initFA();
    }

    public static FAUtils getInstance() {
        if (objInstance == null) {
            objInstance = new FAUtils();
        }

        return objInstance;
    }

    private void initFA() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(GPSApplication.getContext());
    }

    public void logAppEvent(String eventName) {
        mFirebaseAnalytics.logEvent(eventName, null);
    }
}
