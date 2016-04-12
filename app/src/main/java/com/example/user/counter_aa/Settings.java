package com.example.user.counter_aa;

import android.app.Activity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

/**
 * Created by user on 11.04.16.
 */
@EActivity(R.layout.activity_settings)
public class Settings extends Activity {
    DBH db = null;


    @AfterViews
    void init(){
        if (db == null)
            db=new DBH(this);
    }

    @Click(R.id.btnDeleteRecords)
    void btnDeleteRecordsClick(){
        db.deleteRecordsFromTable();
    }

    @Click(R.id.btnDropTable)
    void btnDropTableClick(){
        db.dropTable();
    }

    @Click(R.id.btnCreateTable)
    void btnCreateTable(){
        db.createTable();
    }



}
