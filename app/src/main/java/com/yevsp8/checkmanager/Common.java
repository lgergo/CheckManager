package com.yevsp8.checkmanager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Gergo on 2018. 02. 14..
 */

public class Common {

    public static long dateToLong(Date date) {
        return date.getTime();
    }

    public static Date longToDate(long value) {
        return new Date(value);
    }

    public static String dateToStringFormatted(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        return sdf.format(date);
    }

    public static String longDateToString(long dateValue) {
        Date result = new Date(dateValue);
        return dateToStringFormatted(result);
    }

}
