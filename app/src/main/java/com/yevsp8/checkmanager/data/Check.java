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
    private String paidDate;

    public Check(@NonNull String checkId, long creationDate, int amount, String paidTo, String paidDate) {
        this.checkId = checkId;
        this.creationDate = creationDate;
        this.amount = amount;
        this.paidTo = paidTo;
        this.paidDate = paidDate;
    }

    @NonNull
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

    public String getPaidDate() {
        return paidDate;
    }
}

