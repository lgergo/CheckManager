package com.yevsp8.checkmanager.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by Gergo on 2018. 02. 11..
 */

@Entity
public class Check {

    @PrimaryKey
    @NonNull
    private String checkId;

    private long creationDate;
    private int amount;
    private String paidTo;
    private long paidDate;
    private boolean isUploaded;

    public Check(String checkId, long creationDate, int amount, String paidTo, long paidDate, boolean isUploaded) {
        this.checkId = checkId;
        this.creationDate = creationDate;
        this.amount = amount;
        this.paidTo = paidTo;
        this.paidDate = paidDate;
        this.isUploaded = isUploaded;
    }

    public String getCheckId() {
        return checkId;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public int getAmount() {
        return amount;
    }

    public String getPaidTo() {
        return paidTo;
    }

    public long getPaidDate() {
        return paidDate;
    }

    public boolean getIsUploaded() {
        return isUploaded;
    }
}

