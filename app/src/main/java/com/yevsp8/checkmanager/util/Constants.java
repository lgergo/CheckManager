package com.yevsp8.checkmanager.util;

/**
 * Created by Gergo on 2018. 03. 10..
 */

public class Constants {

    public static final String DateTimePattern = "yyyy/MM/dd";
    public static final String LongDateTimePattern = "yyyyMMdd_HHmmss";

    //GoogleApi
    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    public static final int UPDATED_CELL_COUNT = 3;
    public static final int CREATED_COMPANY_CELL_COUNT = 16;
    public static final String PREF_ACCOUNT_NAME = "accountName";
    public static final int NotificationRequestCode = 111;
    public static final int Max_Notification_Day_Interval = 30;
    public static final String Levensthein_Value = "3";
    public static final String Levensthein_Default = "0";
    public static final String FirstStart_Default = "1";
    public static final String FirstStart_Value = "0";
    public static final int REQUEST_TAKE_PHOTO = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    public static final String SharedPreferencesName = "App";
    public static final int NotificationHour = 12;
    public static final int NotificationMinute = 0;

    //intents
    public static final String RecognisedTextsArray = "result_array";
    public static final String SelectedCheckId = "selected_check_id";
    public static final String GoogleApiCallType = "callType";
    public static final String WhiteListAmount = "0123456789*";
    public static final String WhiteListId = "0123456789";
    public static final String NotificationTitle = "title";
    public static final String NotificationMessage = "message";

    //image processing
    private static final double height = 10.6;
    public static final double Check_Amount_Top_DistFrom_Top = 0.9 / height;
    public static final double Check_Amount_Bottom_DistFrom_Top = 1.32 / height;
    public static final double Check_PaidTo_Top_DistFrom_Top = 8.1 / height;
    public static final double Check_PaidTo_Bottom_DistFrom_Top = 9.58 / height;
    public static final double Check_ID_Top_DistFrom_Top = 9.7 / height;
    public static final double Check_ID_Bottom_DistFrom_Top = 10.3 / height;
    private static final double width = 6.0;
    public static final double Check_Amount_Left_DistFrom_Left = 0.5 / width;
    public static final double Check_Amount_Right_DistFrom_Left = 3.8 / width;
    public static final double Check_ID_Left_DistFrom_Left = 0.4 / width;
    public static final double Check_ID_Right_DistFrom_Left = 5.8 / width;
    public static final double Check_PaidTo_Left_DistFrom_Left = 0.2 / width;
    public static final double Check_PaidTo_Right_DistFrom_Left = 5.8 / width;

}
