package com.yevsp8.checkmanager.data;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by lgergo on 2018. 04. 07..
 */

@RunWith(AndroidJUnit4.class)
public class CheckDAOInstrumentedTests {

    private CheckDAO dao;
    private CheckDatabase db;

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
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        db = Room.inMemoryDatabaseBuilder(context, CheckDatabase.class).build();
        dao = db.checkDAO();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void getCheckList_InstrumentedTest() throws InterruptedException {
        Check c = new Check("1111", Calendar.getInstance().getTimeInMillis(), 1000, "TestCompany", "2018.01.01");
        dao.insertCheck(c);

        List<Check> results = getValue(dao.getCheckList());

        assertNotNull(results);
        assertTrue(results.size() == 1);
        assertThat(results.get(0).getCheckId(), is(c.getCheckId()));
    }

    @Test
    public void getCheckById_InstrumentedTest() throws InterruptedException {
        Check c = new Check("1112", Calendar.getInstance().getTimeInMillis(), 1000, "TestCompany", "2018.01.01");
        dao.insertCheck(c);

        Check result = getValue(dao.getCheckById("1112"));

        assertNotNull(result);
        assertEquals(result.getCheckId(), c.getCheckId());
    }

    @Test
    public void insertCheck_InstrumentedTest() throws InterruptedException {
        Check c1 = new Check("1111", Calendar.getInstance().getTimeInMillis(), 1000, "TestCompany", "2018.01.01");
        Check c2 = new Check("2222", Calendar.getInstance().getTimeInMillis(), 1000, "TestCompany", "2018.01.01");

        dao.insertCheck(c1);
        dao.insertCheck(c2);

        assertTrue(getValue(dao.getCheckList()).size() == 2);
    }

    @Test
    public void deleteCheck_InstrumentedTest() throws InterruptedException {
        Check c = new Check("1111", Calendar.getInstance().getTimeInMillis(), 1000, "TestCompany", "2018.01.01");
        dao.insertCheck(c);

        dao.deleteCheck(c);

        assertTrue(getValue(dao.getCheckList()).size() == 0);
    }

    @Test
    public void deleteCheckById_WithGoodId_InstrumentedTest() throws InterruptedException {
        Check c = new Check("1111", Calendar.getInstance().getTimeInMillis(), 1000, "TestCompany", "2018.01.01");
        dao.insertCheck(c);

        dao.deleteCheckById("1111");

        assertTrue(getValue(dao.getCheckList()).size() == 0);
    }

    @Test
    public void deleteCheckById_WithBadId_InstrumentedTest() throws InterruptedException {
        Check c = new Check("1111", Calendar.getInstance().getTimeInMillis(), 1000, "TestCompany", "2018.01.01");
        dao.insertCheck(c);

        dao.deleteCheckById("0");

        assertTrue(getValue(dao.getCheckList()).size() == 1);
    }

}
