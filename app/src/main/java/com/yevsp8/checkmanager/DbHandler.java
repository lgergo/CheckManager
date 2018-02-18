package com.yevsp8.checkmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Gergo on 2018. 02. 13..
 */

public class DbHandler extends SQLiteOpenHelper {

    public final static String DB_NAME = "check_database";
    public final static int DB_VERSION = 1;
    public final static String TABLE_CHECK = "checks";

    public static DbHandler handler = null;

    private DbHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static DbHandler getInstance(Context context) {
        if (handler == null) {
            handler = new DbHandler(context);
        }
        return handler;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_CHECK + "(" +
                "_id    VARCHAR(13) PRIMARY KEY, " +
                "created   INTEGER," +
                "amount     INTEGER," +
                "paid_to    VARCHAR(255)," +
                "paid_date  INTEGER," +
                "is_uploaded    BOOLEAN" +
                ")");
    }

    public void generateDemoData() {
        Date date = Calendar.getInstance().getTime();
        for (int i = 0; i < 5; i++) {
            Check c = new Check(
                    Integer.toString(i),
                    date.getTime(),
                    i * 1000,
                    i + ". szervezet",
                    date.getTime(),
                    false
            );
            insertCheck(c);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CHECK);
        onCreate(sqLiteDatabase);
    }

    public void insertCheck(Check check) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("_id", check.getCheckId());
        values.put("created", check.getCreationDate());
        values.put("amount", check.getAmount());
        values.put("paid_to", check.getPaidTo());
        values.put("paid_date", check.getPaidDate());
        values.put("is_uploaded", check.getIsUploaded());
        db.insert(TABLE_CHECK, null, values);
        db.close();
    }

    //TODO meghívás sikeres szinkronizáció után
    public void deleteCheck(String checkId) {
        SQLiteDatabase db = getWritableDatabase();
        String[] args = {checkId};
        db.delete(TABLE_CHECK, "_id=?", args);
        db.close();
    }

    public Cursor getNotUploadedCheckList() {
        //TODO rossz lekérdezés
        SQLiteDatabase db = getReadableDatabase();
        String table = TABLE_CHECK;
        String[] columns = null;
        String selection = null;//"is_uploaded=?";
        String[] selectionArgs = null;//new String[]{"false"};
        Cursor result = db.query(table, columns, selection, selectionArgs, null, null, null);

        result.moveToFirst();
        db.close();
        return result;
    }
}
