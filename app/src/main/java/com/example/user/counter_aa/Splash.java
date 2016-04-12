package com.example.user.counter_aa;

import android.app.Activity;
import android.os.SystemClock;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;

/**
 * Created by user on 11.04.16.
 */
@EActivity(R.layout.activity_splash)
public class Splash extends Activity {
    @AfterViews
    protected void init()
    {
        waitInBackGrond();

    }

    @Background
    void waitInBackGrond(){
        SystemClock.sleep(2000);
        Main_.intent(this).start();
    }

}
