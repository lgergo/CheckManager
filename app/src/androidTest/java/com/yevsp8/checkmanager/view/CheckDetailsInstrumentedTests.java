package com.yevsp8.checkmanager.view;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.Button;

import com.yevsp8.checkmanager.R;
import com.yevsp8.checkmanager.data.Check;
import com.yevsp8.checkmanager.data.CheckDAO;
import com.yevsp8.checkmanager.data.CheckDatabase;
import com.yevsp8.checkmanager.util.Constants;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by lgergo on 2018. 04. 29..
 */

@RunWith(AndroidJUnit4.class)
public class CheckDetailsInstrumentedTests extends ActivityInstrumentationTestCase2<CheckDetailsActivity> {

    @Rule
    public ActivityTestRule<CheckDetailsActivity> mActivityTestRule = new ActivityTestRule<CheckDetailsActivity>(CheckDetailsActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent(InstrumentationRegistry.getTargetContext(), CheckDetailsActivity.class);
            intent.putExtra(Constants.SelectedCheckId, "0");
            return intent;
        }
    };
    CheckDetailsActivity activityForTest;
    Context context;
    private CheckDAO dao;
    private CheckDatabase db;

    public CheckDetailsInstrumentedTests() {
        super(CheckDetailsActivity.class);
    }

    public static <T> T getValue(final LiveData<T> liveData) throws InterruptedException {
        final Object[] data = new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);
        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(@Nullable T o) {
                data[0] = o;
                latch.countDown();
                liveData.removeObserver(this);
            }
        };
        liveData.observeForever(observer);
        latch.await(2, TimeUnit.SECONDS);
        return (T) data[0];
    }

    @Before
    public void setup() {
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        activityForTest = getActivity();

        context = InstrumentationRegistry.getTargetContext();
        db = Room.inMemoryDatabaseBuilder(context, CheckDatabase.class).build();
        dao = db.checkDAO();

        dao.insertCheck(new Check("0", 11111111, 2500, "company", "2018/01/01"));
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void checkDetailsActivity_clickDeleteButton_checkDetailsIsDeleted() throws InterruptedException {

        Button delete = mActivityTestRule.getActivity().findViewById(R.id.button_details_delete);
        delete.callOnClick();

        int size = getValue(dao.getCheckList()).size();
        assertEquals(size, 0);
    }

    @Test
    @UiThreadTest
    public void checkDetailsActivity_clickEditButton_editedCheckDetailsIsSaved() throws InterruptedException {

        String id = "1";
        int amount = 1000;
        String paidto = "test company";
        String paiddate = "2018/10/10";

        Button edit = mActivityTestRule.getActivity().findViewById(R.id.button_details_edit_save);
        edit.callOnClick();
        TextInputEditText id_i = mActivityTestRule.getActivity().findViewById(R.id.check_details_id);
        id_i.setText(id);
        TextInputEditText amount_i = mActivityTestRule.getActivity().findViewById(R.id.check_details_amount);
        amount_i.setText(String.valueOf(amount));
        TextInputEditText paidto_i = mActivityTestRule.getActivity().findViewById(R.id.check_details_paidto);
        paidto_i.setText(paidto);
        TextInputEditText paiddate_i = mActivityTestRule.getActivity().findViewById(R.id.check_details_paiddate);
        paiddate_i.setText(paiddate);

        edit.callOnClick();

        Check result = getValue(dao.getCheckById(id));

        assertEquals(id, result.getCheckId());
        assertEquals(amount, result.getAmount());
        assertEquals(paidto, result.getPaidTo());
        assertEquals(paiddate, result.getPaidDate());
    }
}
