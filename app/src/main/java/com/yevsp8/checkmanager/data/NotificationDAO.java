package com.yevsp8.checkmanager.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Gergo on 2018. 03. 23..
 */

@Dao
public interface NotificationDAO {

    @Query("SELECT * FROM NOTIFICATION")
    LiveData<List<Notification>> getNotificationList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNotification(Notification notification);

    @Delete
    void deleteNotification(Notification notification);
}
