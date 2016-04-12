package com.example.user.counter_aa;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.TextView;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
public class Main extends Activity {
//    protected DBH DB = null;

    @ViewById(R.id.txtMain)
    TextView txtHW;

    @AfterViews
    protected void init() {
        txtHW.setText("newText");
    }

    @Click(R.id.btnSettings)
    void btnSettingsClick() {
        Settings_.intent(this).start();
    }

    @Click(R.id.btnGraph)
    void btnGraphClick() {
        Graph_.intent(this).start();
    }

    @Click(R.id.btnNoiceMeter)
    void btnNoiceMeterClick() {
        Noice_.intent(this).start();
    }

    @Override
    public void onBackPressed() {
    }
}
