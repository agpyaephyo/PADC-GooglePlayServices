package xyz.aungpyaephyo.gps;

import android.app.Application;
import android.content.Context;

/**
 * Created by aung on 3/5/16.
 */
public class GPSApplication extends Application {

    public static final String LOG_TAG = GPSApplication.class.getSimpleName();

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
