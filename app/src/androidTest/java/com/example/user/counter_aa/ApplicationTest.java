package com.example.user.counter_aa;

import android.app.Application;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void DBHisNotNull(){
        DBH dbh = null;
        if (dbh==null) dbh = new DBH(this.getContext());
        assertNotNull(dbh);
    }

}