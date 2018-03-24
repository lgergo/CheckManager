package com.yevsp8.checkmanager;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AddSheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.yevsp8.checkmanager.di.ApplicationModule;
import com.yevsp8.checkmanager.di.CheckManagerApplicationComponent;
import com.yevsp8.checkmanager.di.ContextModule;
import com.yevsp8.checkmanager.di.DaggerCheckManagerApplicationComponent;
import com.yevsp8.checkmanager.util.Enums.APICallType;
import com.yevsp8.checkmanager.view.BaseActivity;
import com.yevsp8.checkmanager.viewModel.CheckViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class GoogleApiActivity extends BaseActivity
        implements EasyPermissions.PermissionCallbacks {

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    static final int UPDATED_CELL_COUNT = 3;
    static final int CREATED_COMPANY_CELL_COUNT = 16;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    APICallType type;
    @Inject
    HttpTransport transport;
    @Inject
    JsonFactory jsonFactory;
    @Inject
    GoogleAccountCredential mCredential;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    CheckViewModel viewModel;
    ProgressDialog progress;
    private String spreadsheetId;// = "1BWj04i6jH6jgA95ExEx3ke0ENo7LuAFHuofeU0lcjKs";
    private TextView mOutputText;
    private int requestCode = -1;
    private String[] checkDetailsdataArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_api);

        CheckManagerApplicationComponent component = DaggerCheckManagerApplicationComponent.builder()
                .contextModule(new ContextModule(this))
                .applicationModule(new ApplicationModule(getApplication()))
                .build();
        component.injectGooglaApiActivity(this);

        type = (APICallType) getIntent().getSerializableExtra("callType");
        checkDetailsdataArray = getIntent().getStringArrayExtra("result_array");
        spreadsheetId = getValueFromSharedPreferences(R.string.sheetId_value, R.string.sheetId_default);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CheckViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar_checkDetails);
        setSupportActionBar(toolbar);

        mOutputText = findViewById(R.id.googleApiResult_textView);

        callGoogleApi(type);

//      Intent resultIntent = new Intent();
        //resultIntent.putExtra("result", mOutputText.getText());
//        setResult(requestCode);
//        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */

    public void callGoogleApi(APICallType type) {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            mOutputText.setText(R.string.no_network);
        } else {
            switch (type) {
                case Get_data:
                    new GetDataTask(transport, jsonFactory, mCredential).execute();
                    break;
                case Update_data:
                    new UpdateRequestTask(transport, jsonFactory, mCredential).execute(checkDetailsdataArray);
                    break;
            }
        }
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
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                callGoogleApi(type);
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.request_contactAccess),
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mOutputText.setText(
                            getString(R.string.googlePlay_install));
                } else {
                    callGoogleApi(type);
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);

                        callGoogleApi(type);
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    callGoogleApi(type);
                }
                break;
        }
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

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
                apiAvailability.isGooglePlayServicesAvailable(this);
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
                apiAvailability.isGooglePlayServicesAvailable(this);
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
                GoogleApiActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private boolean createCompanyTemplateForSheet(String companyName, com.google.api.services.sheets.v4.Sheets mService) throws Exception {
        if (companyName != null) {
            try {
                BatchUpdateSpreadsheetRequest batchUpdateSpreadsheetRequest = new BatchUpdateSpreadsheetRequest();
                List<Request> requests = new ArrayList<>();

                //create sheet
                Request addSheetRequest = new Request();
                AddSheetRequest addSheet = new AddSheetRequest();
                SheetProperties prop = new SheetProperties();
                prop.setTitle(companyName);
                addSheet.setProperties(prop);
                addSheetRequest.setAddSheet(addSheet);
                requests.add(addSheetRequest);

                batchUpdateSpreadsheetRequest.setRequests(requests);
                Sheets.Spreadsheets.BatchUpdate createSheetRequest = mService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateSpreadsheetRequest);
                BatchUpdateSpreadsheetResponse createSheetResponse = createSheetRequest.execute();
                if (createSheetResponse.getReplies().size() < 1) {
                    return false;
                }

                //update with template
                String range = companyName + "!A1:D1";
                ValueRange valueRange = new ValueRange();
                valueRange.setMajorDimension(MajorDimension.ROWS.toString());
                valueRange.setRange(range);
                valueRange.setValues(
                        Arrays.asList(
                                Arrays.asList((Object) getResources().getStringArray(R.array.headers))//(Object) "hónap", "csekk sorszám", "összeg", "befizetés dátuma")
                        ));
                String range2 = companyName + "!A2:A14";
                ValueRange valueRange2 = new ValueRange();
                valueRange2.setMajorDimension(MajorDimension.COLUMNS.toString());
                valueRange2.setRange(range2);
                valueRange2.setValues(
                        Arrays.asList(
                                Arrays.asList((Object) getResources().getStringArray(R.array.months))//(Object) "január", "február", "március", "április", "május", "június", "július", "augusztus", "szeptember", "október", "november", "december")
                        ));

                List<ValueRange> data = new ArrayList<>();
                data.add(valueRange);
                data.add(valueRange2);

                BatchUpdateValuesRequest updateRequestBody = new BatchUpdateValuesRequest();
                updateRequestBody.setValueInputOption("RAW");
                updateRequestBody.setData(data);

                Sheets.Spreadsheets.Values.BatchUpdate batchUpdateRequest = mService.spreadsheets().values().batchUpdate(spreadsheetId, updateRequestBody);
                BatchUpdateValuesResponse updateValueResponse = batchUpdateRequest.execute();
                return updateValueResponse.getTotalUpdatedCells() == CREATED_COMPANY_CELL_COUNT;

            } catch (UserRecoverableAuthIOException e) {
                startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            }
        }
        return false;
    }

    enum MajorDimension {COLUMNS, ROWS}

    //region AsyncTasks

    private class GetDataTask extends AsyncTask<String, Void, List<String>> {
        com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        GetDataTask(HttpTransport transport, JsonFactory jsonFactory, GoogleAccountCredential credential) {
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Sheets API Android Quickstart")
                    .build();
        }

        @Override
        protected List<String> doInBackground(String... params) {
            try {
                return getDataFromApi(params[0]);
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private List<String> getDataFromApi(String name) throws Exception {
            String range = name + "F1:H4";
            String majorDim = MajorDimension.COLUMNS.toString();
            List<String> results = new ArrayList<>();
            ValueRange response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range).setMajorDimension(majorDim)
                    .execute();
            List<List<Object>> values = response.getValues();
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
            mOutputText.setText("");
            requestCode = -1;
            progress = new ProgressDialog(GoogleApiActivity.this);
            progress.setMessage("Feltöltés folyamatban...");
            progress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            if (output == null || output.size() == 0) {
                mOutputText.setText(R.string.no_results);
                requestCode = 1;
            } else {
                output.add(0, "Data retrieved using the Google Sheets API:");
                mOutputText.setText(TextUtils.join("\n", output));
                requestCode = 1;
            }
            progress.dismiss();
        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            GoogleApiActivity.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText(R.string.universal_error
                            + mLastError.getMessage());
                    requestCode = 1;
                }
            } else {
                mOutputText.setText(R.string.request_cancelled);
                requestCode = 1;
            }
            progress.dismiss();
        }
    }

    private class UpdateRequestTask extends AsyncTask<String[], Void, Boolean> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        UpdateRequestTask(HttpTransport transport, JsonFactory jsonFactory, GoogleAccountCredential credential) {
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Sheets API Update")
                    .build();
        }

        @Override
        protected Boolean doInBackground(String[]... params) {
            try {
                return updateDataApi(params[0]);
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return false;
            }
        }

        private Boolean updateDataApi(String[] checkDetails) throws Exception {
            Object a1 = checkDetails[0];
            Object a2 = checkDetails[1];
            Object a3 = checkDetails[2];

            String title;
            String dataRange;

            Spreadsheet spreadsheet = this.mService.spreadsheets().get(spreadsheetId).execute();
            List<Sheet> sheets = spreadsheet.getSheets();
            int i = 0;
            while (i < sheets.size() && !sheets.get(i).getProperties().getTitle().equals(checkDetails[3])) {
                i++;
            }
            if (i == sheets.size()) {
                if (!createCompanyTemplateForSheet(checkDetails[3], this.mService)) {
                    return false;
                }
            }

            dataRange = "B" + checkDetails[4] + ":D" + checkDetails[4]; //TODO új év esetén range ??
            title = checkDetails[3] + "!"; //TODO közelítő szöveg egyezés ???

            String range = title + dataRange;
            String majorDim = MajorDimension.ROWS.toString();
            ValueRange valueRange = new ValueRange();
            valueRange.setMajorDimension(majorDim);
            valueRange.setRange(range);
            valueRange.setValues(
                    Arrays.asList(
                            Arrays.asList(a1, a2, a3)
                    ));
            try {
                UpdateValuesResponse response = this.mService.spreadsheets().values()
                        .update(spreadsheetId, range, valueRange).setValueInputOption("RAW")
                        .execute();
                return response.getUpdatedCells() == UPDATED_CELL_COUNT;
            } catch (UserRecoverableAuthIOException e) {
                startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            }
            return false;
        }

        @Override
        protected void onPreExecute() {
            mOutputText.setText("");
            requestCode = -1;
            progress = new ProgressDialog(GoogleApiActivity.this);
            progress.setMessage(getString(R.string.progress_dialog_upload));
            progress.show();
        }

        @Override
        protected void onPostExecute(Boolean output) {
            if (output) {
                mOutputText.setText(R.string.succesful_update);
                requestCode = 0;
            } else {
                mOutputText.setText(R.string.unsuccesful_update);
                requestCode = 1;
            }
            viewModel.deleteCheckById(checkDetailsdataArray[0]);
            progress.dismiss();
        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            GoogleApiActivity.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText(getString(R.string.universal_error)
                            + mLastError.getMessage());
                    requestCode = 1;
                }
            } else {
                mOutputText.setText(R.string.request_cancelled);
                requestCode = 1;
            }
            progress.dismiss();
        }
    }
}
