package com.yevsp8.checkmanager.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import com.yevsp8.checkmanager.R;
import com.yevsp8.checkmanager.util.Constants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by lgergo on 2018. 04. 20..
 */

@RunWith(AndroidJUnit4.class)
public class SettingsActivityInstrumentedTests extends ActivityInstrumentationTestCase2<SettingsActivity> {

    SettingsActivity activityForTest;

    public SettingsActivityInstrumentedTests() {
        super(SettingsActivity.class);
    }

    @Before
    public void setup() {
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        activityForTest = getActivity();
    }

    @Test
    @UiThreadTest
    public void saveButtonClicked_instrumentedTest() {
        EditText sheetId = activityForTest.findViewById(R.id.editText_settings_sheetId);
        sheetId.setText("test");
        SeekBar sb = activityForTest.findViewById(R.id.seekBar_settings);
        sb.setProgress(5);
        Button saveButton = activityForTest.findViewById(R.id.button_settings_save);

        saveButton.callOnClick();

        SharedPreferences sp = activityForTest.getSharedPreferences(Constants.SharedPreferencesName, Context.MODE_PRIVATE);
        String sheetIdResult = sp.getString(getActivity().getString(R.string.sheetId_value), getActivity().getString(R.string.sheetId_default));
        String notificationResult = sp.getString(getActivity().getString(R.string.notification_interval_value), getActivity().getString(R.string.notification_interval_default));

        assertEquals(sheetId.getText().toString(), sheetIdResult);
        assertEquals(String.valueOf(sb.getProgress()), notificationResult);
    }
}
