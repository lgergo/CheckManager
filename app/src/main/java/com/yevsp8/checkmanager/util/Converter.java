package com.yevsp8.checkmanager.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Gergo on 2018. 02. 14..
 */

public class Converter {

    public static long dateToLong(Date date) {
        return date.getTime();
    }

    public static Date longToDate(long value) {
        return new Date(value);
    }

    public static String dateToStringFormatted(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DateTimePattern, Locale.getDefault());
        return sdf.format(date);
    }

    public static String longDateToString(long dateValue) {
        Date result = new Date(dateValue);
        return dateToStringFormatted(result);
    }

    public static long stringDateToLong(String date) {
        SimpleDateFormat f = new SimpleDateFormat(Constants.DateTimePattern, Locale.getDefault());
        try {
            Date d = f.parse(date);
            return d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getTodayStringFormatted() {
        Date d = Calendar.getInstance(Locale.getDefault()).getTime();
        return dateToStringFormatted(d);
    }
}
