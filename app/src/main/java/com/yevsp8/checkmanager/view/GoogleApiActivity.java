package com.yevsp8.checkmanager.view;

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
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.UpdateSpreadsheetPropertiesRequest;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.yevsp8.checkmanager.R;
import com.yevsp8.checkmanager.di.ApplicationModule;
import com.yevsp8.checkmanager.di.CheckManagerApplicationComponent;
import com.yevsp8.checkmanager.di.ContextModule;
import com.yevsp8.checkmanager.di.DaggerCheckManagerApplicationComponent;
import com.yevsp8.checkmanager.util.Constants;
import com.yevsp8.checkmanager.util.Converter;
import com.yevsp8.checkmanager.util.Enums.APICallType;
import com.yevsp8.checkmanager.viewModel.CheckViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class GoogleApiActivity extends BaseActivity
        implements EasyPermissions.PermissionCallbacks {

    @Inject
    Converter converter;
    @Inject
    HttpTransport transport;
    @Inject
    JsonFactory jsonFactory;
    @Inject
    GoogleAccountCredential mCredential;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private APICallType type;
    private CheckViewModel viewModel;
    private ProgressDialog progress;
    private String spreadsheetId;
    private int levensthein;
    private TextView mOutputText;
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

        type = (APICallType) getIntent().getSerializableExtra(Constants.GooglaApiCallType);
        checkDetailsdataArray = getIntent().getStringArrayExtra(Constants.RecognisedTextsArray);
        spreadsheetId = getValueFromSharedPreferences(R.string.sheetId_value, R.string.sheetId_default);
        levensthein = Integer.parseInt(getValueFromSharedPreferences(R.string.levenshtein_value, R.string.levenshtein_default));

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CheckViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar_checkDetails);
        setSupportActionBar(toolbar);

        mOutputText = findViewById(R.id.googleApiResult_textView);

        callGoogleApi(type);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void callGoogleApi(APICallType type) {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            mOutputText.setText(R.string.no_network);
        } else {
            switch (type) {
                case Update_data:
                    new UpdateRequestTask(transport, jsonFactory, mCredential).execute(checkDetailsdataArray);
                    break;
                case ConnectionTest:
                    new ConnectionTestTask(transport, jsonFactory, mCredential).execute();
                    break;
                case CreateSpreadSheet:
                    new CreateSpreadSheetTask(transport, jsonFactory, mCredential).execute();
            }
        }
    }

    @AfterPermissionGranted(Constants.REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(Constants.PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                callGoogleApi(type);
            } else {
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        Constants.REQUEST_ACCOUNT_PICKER);
            }
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.request_contactAccess),
                    Constants.REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mOutputText.setText(
                            getString(R.string.googlePlay_install));
                } else {
                    callGoogleApi(type);
                }
                break;
            case Constants.REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(Constants.PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);

                        callGoogleApi(type);
                    }
                }
                break;
            case Constants.REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    callGoogleApi(type);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr != null) {
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        }
        return false;
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    private void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                GoogleApiActivity.this,
                connectionStatusCode,
                Constants.REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private boolean createCompanyTemplateForSheet(String companyName, boolean isNewYear, com.google.api.services.sheets.v4.Sheets mService) throws Exception {
        if (companyName != null) {
            try {

                int row = 1;
                if (isNewYear) {
                    String range = companyName + "!A1";
                    ValueRange requestBody = new ValueRange();
                    requestBody.setMajorDimension(MajorDimension.ROWS.toString());
                    requestBody.setRange(range);
                    String valueInputOption = "RAW";
                    String insertDataOption = "OVERWRITE";

                    Sheets.Spreadsheets.Values.Append request =
                            mService.spreadsheets().values().append(spreadsheetId, range, requestBody);
                    request.setValueInputOption(valueInputOption);
                    request.setInsertDataOption(insertDataOption);

                    AppendValuesResponse response = request.execute();
                    String res = response.getUpdates().getUpdatedRange();
                    String[] result = res.split("!");
                    res = result[1].substring(1);
                    row = Integer.parseInt(res) + 1;
                } else {
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
                }
                //update with template
                String range = companyName + "!A" + String.valueOf(row) + ":D" + String.valueOf(row);
                ValueRange valueRange = new ValueRange();
                valueRange.setMajorDimension(MajorDimension.ROWS.toString());
                valueRange.setRange(range);
                valueRange.setValues(
                        Arrays.asList(
                                Arrays.asList((Object[]) getResources().getStringArray(R.array.headers))
                        ));
                String range2 = companyName + "!A" + String.valueOf(row + 1) + ":D" + String.valueOf(row + 13); // "!A2:A14";
                ValueRange valueRange2 = new ValueRange();
                valueRange2.setMajorDimension(MajorDimension.COLUMNS.toString());
                valueRange2.setRange(range2);
                valueRange2.setValues(
                        Arrays.asList(
                                Arrays.asList((Object[]) getResources().getStringArray(R.array.months))
                        ));

                List<ValueRange> data = new ArrayList<>();
                data.add(valueRange);
                data.add(valueRange2);

                BatchUpdateValuesRequest updateRequestBody = new BatchUpdateValuesRequest();
                updateRequestBody.setValueInputOption("RAW");
                updateRequestBody.setData(data);

                Sheets.Spreadsheets.Values.BatchUpdate batchUpdateRequest = mService.spreadsheets().values().batchUpdate(spreadsheetId, updateRequestBody);
                BatchUpdateValuesResponse updateValueResponse = batchUpdateRequest.execute();
                return updateValueResponse.getTotalUpdatedCells() == Constants.CREATED_COMPANY_CELL_COUNT;

            } catch (UserRecoverableAuthIOException e) {
                startActivityForResult(e.getIntent(), Constants.REQUEST_AUTHORIZATION);
            }
        }
        return false;
    }

    enum MajorDimension {COLUMNS, ROWS}

    //region AsyncTasks

    private class ConnectionTestTask extends AsyncTask<Void, Void, Boolean> {
        com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        ConnectionTestTask(HttpTransport transport, JsonFactory jsonFactory, GoogleAccountCredential credential) {
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Sheets API Test")
                    .build();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                return testConnection();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private Boolean testConnection() throws Exception {
            Spreadsheet spreadsheet = this.mService.spreadsheets().get(spreadsheetId).execute();
            List<Sheet> sheets = spreadsheet.getSheets();
            return sheets.size() > 0;
        }

        @Override
        protected void onPreExecute() {
            mOutputText.setText("");
            if (spreadsheetId.length() == 0) {
                cancel(true);
            }
            progress = new ProgressDialog(GoogleApiActivity.this);
            progress.setMessage(getString(R.string.googleApi_connectionTest_progressDialogText));
            progress.show();
        }

        @Override
        protected void onPostExecute(Boolean output) {
            if (output) {
                mOutputText.setText(R.string.connection_test_successful);
            } else {
                mOutputText.setText(R.string.connection_test_unsuccesful);
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
                            Constants.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText(R.string.connection_test_unsuccesful);
                }
            } else {
                mOutputText.setText(R.string.sheetId_isRequired);
            }
            progress.dismiss();
        }
    }

    private class CreateSpreadSheetTask extends AsyncTask<Void, Void, Boolean> {
        com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;
        private String tempSpreadSheetId;

        CreateSpreadSheetTask(HttpTransport transport, JsonFactory jsonFactory, GoogleAccountCredential credential) {
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Sheets API Create Spreadsheet")
                    .build();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                return createSpreadSheet();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private Boolean createSpreadSheet() throws Exception {
            Spreadsheet requestBody = new Spreadsheet();
            Sheets.Spreadsheets.Create request = this.mService.spreadsheets().create(requestBody);
            Spreadsheet response = request.execute();
            String sheetId = response.getSpreadsheetId();
            if (sheetId.length() > 0) {
                saveToSharedPreferences(R.string.sheetId_value, sheetId);
                tempSpreadSheetId = sheetId;
            }
            List<Request> reqList = new ArrayList<>();
            reqList.add(new Request().setUpdateSpreadsheetProperties(new UpdateSpreadsheetPropertiesRequest()
                    .setProperties(new SpreadsheetProperties()
                            .setTitle(getString(R.string.google_spreadsheet_file_name)))
                    .setFields("title")));

            BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(reqList);
            BatchUpdateSpreadsheetResponse batchResponse = this.mService.spreadsheets().batchUpdate(tempSpreadSheetId, body).execute();
            return batchResponse.getReplies().size() > 0;
        }

        @Override
        protected void onPreExecute() {
            mOutputText.setText("");
            progress = new ProgressDialog(GoogleApiActivity.this);
            progress.setMessage(getString(R.string.googleApi_generate_progressDialogText));
            progress.show();
        }

        @Override
        protected void onPostExecute(Boolean output) {
            if (output) {
                mOutputText.setText(R.string.create_spreadsheet_successful);
                spreadsheetId = tempSpreadSheetId;
            } else {
                mOutputText.setText(R.string.create_spreadsheet_unsuccessful);
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
                            Constants.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText(R.string.connection_test_unsuccesful);
                }
            } else {
                mOutputText.setText(R.string.create_spreadsheet_unsuccessful);
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
            while (i < sheets.size() &&
                    !converter.insideLevenshteinDistance(sheets.get(i).getProperties().getTitle(),
                            checkDetails[3],
                            levensthein
                    )) {
                i++;
            }
            String newSheetTitle;
            if (i == sheets.size()) {
                if (!createCompanyTemplateForSheet(checkDetails[3], false, this.mService)) {
                    return false;
                } else {
                    newSheetTitle = checkDetails[3];
                }
            } else {
                newSheetTitle = sheets.get(i).getProperties().getTitle();
                if (checkDetails[4].equals("1")) {
                    createCompanyTemplateForSheet(newSheetTitle, true, this.mService);
                }
            }

            dataRange = "B" + checkDetails[4] + ":D" + checkDetails[4];
            title = newSheetTitle + "!";

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
                return response.getUpdatedCells() == Constants.UPDATED_CELL_COUNT;
            } catch (UserRecoverableAuthIOException e) {
                startActivityForResult(e.getIntent(), Constants.REQUEST_AUTHORIZATION);
            }
            return false;
        }

        @Override
        protected void onPreExecute() {
            mOutputText.setText("");
            if (spreadsheetId.length() == 0) {
                cancel(true);
            }
            progress = new ProgressDialog(GoogleApiActivity.this);
            progress.setMessage(getString(R.string.progress_dialog_upload));
            progress.show();
        }

        @Override
        protected void onPostExecute(Boolean output) {
            if (output) {
                mOutputText.setText(R.string.successful_update);
            } else {
                mOutputText.setText(R.string.unsuccesful_update);
            }
            viewModel.deleteCheckById(checkDetailsdataArray[0]);
            saveToSharedPreferences(R.string.last_sync_value, Converter.getTodayStringFormatted());
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
                            Constants.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText(R.string.unsuccesful_update);
                }
            } else {
                mOutputText.setText(R.string.sheetId_isRequired);
            }
            progress.dismiss();
        }
    }
}
