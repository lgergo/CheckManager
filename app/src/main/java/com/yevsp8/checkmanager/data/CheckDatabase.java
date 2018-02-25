package com.yevsp8.checkmanager.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by Gergo on 2018. 02. 25..
 */

@Database(entities = {Check.class}, version = 1)
public abstract class CheckDatabase extends RoomDatabase {

    public abstract CheckDAO checkDAO();
}
