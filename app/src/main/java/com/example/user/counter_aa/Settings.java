package com.example.user.counter_aa;

import android.app.Activity;
import android.widget.EditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by user on 11.04.16.
 */
@EActivity(R.layout.activity_settings)
public class Settings extends Activity {
    DBH db = null;

    @ViewById(R.id.edTickCount) EditText edTickCount;
    @ViewById(R.id.edFlashCount) EditText edFlashCount;


    @AfterViews
    void init(){
        if (db == null)
            db=new DBH(this);

        edTickCount.setText(""+db.getTickCount());
        edFlashCount.setText(""+db.getFlashCount());
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




    @Click(R.id.btnSaveSettings)
    void btnSaveSettings(){
        int tickCount = 0;
        try {
            tickCount = Integer.parseInt(edTickCount.getText().toString());
            db.setTickCount(tickCount);
        } catch(NumberFormatException nfe) {
            System.out.println("Не правильно введен tickCount " + nfe);
        }

        int flashCount = 0;
        try {
            flashCount = Integer.parseInt(edFlashCount.getText().toString());
            db.setFlashCount(flashCount);
        } catch(NumberFormatException nfe) {
            System.out.println("Не правильно введен flashCount " + nfe);
        }

    }


}
