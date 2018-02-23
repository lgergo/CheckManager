package com.yevsp8.checkmanager;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Gergo on 2018. 02. 17..
 */

public class GoogleApiProvider implements EasyPermissions.PermissionCallbacks {

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {SheetsScopes.SPREADSHEETS};
    private static GoogleApiProvider provider;
    String mOutputText;
    GoogleAccountCredential mCredential;
    MainActivity baseActivity;
    private String spreadsheetId = "1BWj04i6jH6jgA95ExEx3ke0ENo7LuAFHuofeU0lcjKs";
    private String sheetName = "Cég1!";
    //TODO kiemelés vhova
    private String accountName = "lg.gergo@gmail.com";
    private GoogleApiProvider(final MainActivity activity) {
        baseActivity = activity;
        mCredential = GoogleAccountCredential.usingOAuth2(
                baseActivity.getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
    }

    public static GoogleApiProvider getInstance(final MainActivity activity) {
        if (provider == null)
            provider = new GoogleApiProvider(activity);
        return provider;
    }

    private void initializeAccountForApiCall() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (!isDeviceOnline()) {
            mOutputText = "No network connection available.";
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        }
        baseActivity.updateGoogleApiTextView(mOutputText);
    }

    public void insertData(String checkid, String amount, String paidto, String paiddate) {

        initializeAccountForApiCall();
        if (mCredential.getSelectedAccountName() != null) {
            new UpdateRequestTask(mCredential).execute();
        }
    }

    public void createEmptyCompanyTemplate(String paidToCompany)
    {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        com.google.api.services.sheets.v4.Sheets mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                transport, jsonFactory, mCredential)
                    .setApplicationName("Create sheet")
                    .build();
        initializeAccountForApiCall();
        //TODO accout name
        mCredential.setSelectedAccountName(accountName);
        if (/*mCredential.getSelectedAccountName() != null &&*/ paidToCompany != null) {
            Spreadsheet requestBody = new Spreadsheet();

            Sheets sheetsService = new Sheets.Builder(transport, jsonFactory, mCredential)
                    .setApplicationName("Google-SheetsSample/0.1")
                    .build();

            try {
                Sheets.Spreadsheets.Create request = sheetsService.spreadsheets().create(requestBody);

                Spreadsheet response = request.execute();

                //TODO sheet létezésének ellenőrzése

                if (response != null) {
                    String range = sheetName + "A15:D15";
                    ValueRange valueRange = new ValueRange();
                    valueRange.setMajorDimension(MajorDimension.COLUMNS.toString());
                    valueRange.setRange(range);
                    valueRange.setValues(
                            Arrays.asList(
                                    Arrays.asList((Object) "hónap", "csekk sorszám", "összeg", "befizetés dátuma")
                            ));


                    String range2 = sheetName + "A16:A28";
                    ValueRange valueRange2 = new ValueRange();
                    valueRange2.setMajorDimension(MajorDimension.ROWS.toString());
                    valueRange2.setRange(range2);
                    valueRange2.setValues(
                            Arrays.asList(
                                    Arrays.asList((Object) "január", "február", "március", "április", "május", "június", "július", "augusztus", "szeptember", "október", "november", "december")
                            ));

                    List<ValueRange> data = new ArrayList<>();
                    data.add(valueRange);
                    data.add(valueRange2);

                    BatchUpdateValuesRequest updateRequestBody = new BatchUpdateValuesRequest();
                    // updateRequestBody.setValueInputOption(valueInputOption);
                    updateRequestBody.setData(data);

                    Sheets.Spreadsheets.Values.BatchUpdate batchUpdateRequest = mService.spreadsheets().values().batchUpdate(spreadsheetId, updateRequestBody);
                    BatchUpdateValuesResponse updateValueResponse = batchUpdateRequest.execute();

                    //TODO for debug
                    baseActivity.updateGoogleApiTextView("sikeres sheet generálás");
                }

                //TODO for debug
                baseActivity.updateGoogleApiTextView("null response");
            } catch (Exception ex) {
                Log.e("credentials", ex.getMessage());
            }
        }
    }
    
    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    public void getResultsFromApi() {

        initializeAccountForApiCall();
        if (mCredential.getSelectedAccountName() != null) {
            new MakeRequestTask(mCredential).execute();
        }
    }

//    public void checkIfRowIsEmptyByMonth(String month)
//    {
//        String range = sheetName + "B2:B13";
//        String majorDim = MajorDimension.ROWS.toString();
//        List<String> results = new ArrayList<String>();
//        ValueRange response = this.mService.spreadsheets().values()
//                .get(spreadsheetId, range).setMajorDimension(majorDim)
//                .execute();
//        List<List<Object>> values = response.getValues();
//        if (values != null) {
//            for (int i = 0; i < values.size(); i++) {
//                for (int j = 0; j < values.get(i).size(); j++) {
//                    results.add(values.get(i).get(j).toString());
//                }
//            }
//        }
//       //TODO check if contains month + egymás után ha több év van ?
//
//        return results;
//    }

    /**
     * Check that Google Play services APK is installed and up to date.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(baseActivity);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(baseActivity);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                baseActivity,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(baseActivity.getApplicationContext(), Manifest.permission.GET_ACCOUNTS)) {
            //String accountName = baseActivity.getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
            } else {
                // Start a dialog from which the user can choose an account
                baseActivity.startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
                //TODO pickerből account visszanyerése
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    baseActivity,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) baseActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     *
     * @param requestCode  The request code passed in
     *                     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    enum MajorDimension {COLUMNS, ROWS}

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Sheets API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Sheets API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of names and majors of students in a sample spreadsheet:
         * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
         *
         * @return List of names and majors
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            String range = sheetName + "F1:H4";
            String majorDim = GoogleApiActivity.MajorDimension.COLUMNS.toString();
            List<String> results = new ArrayList<String>();
            ValueRange response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range).setMajorDimension(majorDim)
                    .execute();
            List<List<Object>> values = response.getValues();   // belső lista a majorDiemnsion
            if (values != null) {
                for (int i = 0; i < values.size(); i++) {
                    for (int j = 0; j < values.get(i).size(); j++) {
                        results.add(values.get(i).get(j).toString());
                    }
                }
            }
            return results;
        }

        @Override
        protected void onPreExecute() {
            mOutputText = "";
            baseActivity.updateProgressBar.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            baseActivity.updateProgressBar.hide();
            if (output == null || output.size() == 0) {
                mOutputText = "No results returned.";
            } else {
                output.add(0, "Data retrieved using the Google Sheets API:");
                mOutputText = TextUtils.join("\n", output);
                baseActivity.updateGoogleApiTextView(mOutputText);
            }
        }

        @Override
        protected void onCancelled() {
            baseActivity.updateProgressBar.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    baseActivity.startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            GoogleApiActivity.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText = "The following error occurred:\n"
                            + mLastError.getMessage();
                }
            } else {
                mOutputText = "Request cancelled.";
            }
        }
    }


    private class UpdateRequestTask extends AsyncTask<Void, Void, Boolean> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        UpdateRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Sheets API Update")
                    .build();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                updateDataThroughApi();
                return true;
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return false;
            }
        }

        private Boolean updateDataThroughApi() throws IOException {
            //TODO vhonnan az eredetit
            Object a1 = "Test Row 1 Column A";
            Object b1 = "Test Row 1 Column B";

            String range = sheetName + "F1:H4";
            String majorDim = GoogleApiActivity.MajorDimension.ROWS.toString();
            ValueRange valueRange = new ValueRange();
            valueRange.setMajorDimension(majorDim);
            valueRange.setRange(range);
            valueRange.setValues(
                    Arrays.asList(
                            Arrays.asList(a1, b1)
                    ));

            UpdateValuesResponse response = this.mService.spreadsheets().values()
                    .update(spreadsheetId, range, valueRange).setValueInputOption("RAW")
                    .execute();

            //TODO update sikeressége ???
            return response.getUpdatedCells() == 2;
        }

        @Override
        protected void onPreExecute() {
            mOutputText = "";
            //mProgress.show();
        }

        @Override
        protected void onPostExecute(Boolean output) {
            //mProgress.hide();
            if (!output) {
                mOutputText = "No results returned.";
            } else {
                mOutputText = "Update was successful";
                baseActivity.updateGoogleApiTextView(mOutputText);
            }
        }

        @Override
        protected void onCancelled() {
            //mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    baseActivity.startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            GoogleApiActivity.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText = "The following error occurred:\n"
                            + mLastError.getMessage();
                }
            } else {
                mOutputText = "Request cancelled.";
            }
        }
    }
}
