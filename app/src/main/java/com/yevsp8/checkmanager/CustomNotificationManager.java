package com.yevsp8.checkmanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.TimeZone;

import static android.content.Context.ALARM_SERVICE;
import static com.yevsp8.checkmanager.util.Constants.NotificationRequestCode;

/**
 * Created by Gergo on 2018. 03. 25..
 */

public class CustomNotificationManager {

    public void createNotification(Context context, int interval) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.set(Calendar.HOUR_OF_DAY, 16);
        cal.set(Calendar.MINUTE, 0);

        Intent i = new Intent(context, NotificationReceiver.class);
        i.putExtra("title", context.getString(R.string.notification_title));
        i.putExtra("message", context.getString(R.string.notification_message_text));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NotificationRequestCode, i, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (manager != null) {
            manager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY * interval, pendingIntent);
        }
    }

    public void deleteNotification(Context context) {
        Intent i = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NotificationRequestCode, i, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (manager != null) {
            manager.cancel(pendingIntent);
        }
    }
}
