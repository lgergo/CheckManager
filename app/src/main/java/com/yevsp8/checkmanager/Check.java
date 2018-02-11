package com.yevsp8.checkmanager;

import java.util.Date;

/**
 * Created by Gergo on 2018. 02. 11..
 */

public class Check {

    private String checkId;
    private Date creationDate;
    private int amount;
    private String paidTo;
    private Date paidDate;
    private boolean isUploaded;

    public Check(String checkId, Date creationDate, int amount, String paidTo, Date paidDate, boolean isUploaded) {
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

    public Date getCreationDate() {
        return creationDate;
    }

    public int getAmount() {
        return amount;
    }

    public String getPaidTo() {
        return paidTo;
    }

    public Date getPaidDate() {
        return paidDate;
    }

    public boolean getIsUploaded() {
        return isUploaded;
    }
}

