package com.yevsp8.checkmanager.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by Gergo on 2018. 03. 23..
 */

@Database(entities = {Notification.class}, version = 1)
public abstract class NotificationDatabase extends RoomDatabase {

    public abstract NotificationDAO notificationDAO();
}
