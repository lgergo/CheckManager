package com.yevsp8.checkmanager.data;

import android.arch.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Gergo on 2018. 02. 25..
 */

public class CheckRepository {

    private final CheckDAO checkDao;

    @Inject
    public CheckRepository(CheckDAO checkDao) {
        this.checkDao = checkDao;
    }

    public LiveData<List<Check>> getDataList() {
        return checkDao.getCheckList();
    }

    public LiveData<Check> getCheck(String checkId) {
        return checkDao.getCheckById(checkId);
    }

    public void deleteCheck(Check check) {
        checkDao.deleteCheck(check);
    }

    public void insertCheck(Check check) {
        checkDao.insertCheck(check);
    }

    public void deleteCheckById(String id) {
        checkDao.deleteCheckById(id);
    }
}
