package com.yevsp8.checkmanager.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import javax.annotation.Nonnull;

/**
 * Created by Gergo on 2018. 03. 23..
 */

@Entity
public class Notification {


    @PrimaryKey(autoGenerate = true)
    @Nonnull
    private int checkId;
    private String title;
    private String message;
    private long from;
    private long to;

    public Notification(String title, String message, long from, long to) {
        this.title = title;
        this.message = message;
        this.from = from;
        this.to = to;
    }

    public int getCheckId() {
        return checkId;
    }

    public void setCheckId(@Nonnull int checkId) {
        this.checkId = checkId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public long getFrom() {
        return from;
    }

    public long getTo() {
        return to;
    }
}
