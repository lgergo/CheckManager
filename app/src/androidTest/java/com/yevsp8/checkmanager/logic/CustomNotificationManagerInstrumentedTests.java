package com.yevsp8.checkmanager.logic;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.yevsp8.checkmanager.R;
import com.yevsp8.checkmanager.util.Constants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

/**
 * Created by lgergo on 2018. 04. 21..
 */

@RunWith(AndroidJUnit4.class)
public class CustomNotificationManagerInstrumentedTests {

    private Context context;
    private CustomNotificationManager notificationManager;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getTargetContext();
        notificationManager = new CustomNotificationManager();
    }

    @Test
    public void CustomNotificationManager_instrumentedTest_createNotification() {
        notificationManager.createNotification(context, 1);

        Intent i = new Intent(context, NotificationReceiver.class);
        i.putExtra(Constants.NotificationTitle, context.getString(R.string.notification_title));
        i.putExtra(Constants.NotificationMessage, context.getString(R.string.notification_message_text));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Constants.NotificationRequestCode, i, PendingIntent.FLAG_UPDATE_CURRENT);

        assertNotNull(pendingIntent);
    }
}
