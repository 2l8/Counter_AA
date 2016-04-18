package com.example.user.counter_aa;
import android.app.Activity;
import android.widget.Button;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_noicemeter)
public class Noice extends Activity implements NoiceMeter.NoiceMeterListener {
    String LOG_TAG = "myLogs";
    private NoiceMeter nMeter = null;
    private DBH dbh=null;


    @ViewById(R.id.txtNmValue)
    TextView txtNmValue;

    @ViewById(R.id.btnNmStartStop)
    Button btnNmStartStop;


    @AfterViews
    void init(){
        if (nMeter==null)
            nMeter= new NoiceMeter();
        nMeter.addListener(this);
        if (dbh==null) dbh = new DBH(getApplicationContext());
    }


    @Click(R.id.btnNmStartStop)
    void btnNmStartStopClick(){
        if(btnNmStartStop.getText().toString()==getText(R.string.start)){
            nMeter.initMediaRecoder();
            nMeter.runRecording();
            btnNmStartStop.setText(getText(R.string.stop));
        }
        else{
            nMeter.stop_rec();
            btnNmStartStop.setText(getText(R.string.start));
        }
    }


    @Override
    public void onImpuls(final int amplitude) {
        int minAmpl = 1000;
        int maxAmpl = 25000; //выше - ошибка
        if ((amplitude>minAmpl)&&(amplitude<maxAmpl))
            dbh.impuls(amplitude);

        txtNmValue.post(new Runnable() {
            public void run() {
                txtNmValue.setText("" + amplitude + " dB");

            }
        });
    }
}
//http://www.ssaurel.com/blog/create-a-real-time-line-graph-in-android-with-graphview/