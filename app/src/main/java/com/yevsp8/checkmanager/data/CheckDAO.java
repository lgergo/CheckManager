package com.yevsp8.checkmanager.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Gergo on 2018. 02. 25..
 */

@Dao
public interface CheckDAO {

    @Query("SELECT * FROM `Check`")
    LiveData<List<Check>> getCheckList();

    @Query("SELECT * FROM `CHECK` WHERE checkId = :id")
    LiveData<Check> getCheckById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCheck(Check item);

    @Delete
    void deleteCheck(Check check);
}
