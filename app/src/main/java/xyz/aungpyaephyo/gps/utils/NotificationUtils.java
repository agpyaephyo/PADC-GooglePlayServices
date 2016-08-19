package xyz.aungpyaephyo.gps.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import xyz.aungpyaephyo.gps.GPSApplication;
import xyz.aungpyaephyo.gps.R;
import xyz.aungpyaephyo.gps.activities.MainActivity;

/**
 * Created by aung on 3/7/16.
 */
public class NotificationUtils {

    private static final int NOTIFICATION_ID = 4001;

    public static void showNotification(String notificationText) {
        Context context = GPSApplication.getContext();

        //Notification Title
        String title = context.getString(R.string.app_name);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setColor(context.getResources().getColor(R.color.color_primary))
                .setSmallIcon(R.mipmap.google_play_services)
                .setContentTitle(title)
                .setContentText(notificationText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationText));

        //Open the app when user tap on notification
        Intent resultIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
