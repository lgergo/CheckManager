package com.yevsp8.checkmanager;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.yevsp8.checkmanager.view.MainActivity;

import static com.yevsp8.checkmanager.util.Constants.NotificationRequestCode;

/**
 * Created by Gergo on 2018. 03. 18..
 */

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent repeatingIntent = new Intent(context, MainActivity.class);
        repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pending = PendingIntent.getActivity(context, NotificationRequestCode, repeatingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pending)
                .setSmallIcon(R.drawable.ic_home_24dp)
                .setContentTitle("Checkmanager")
                .setContentText("Be nem fizetett csekkjei vannak erre a hónapra")
                .setAutoCancel(true);

        manager.notify(NotificationRequestCode, builder.build());

    }
}
