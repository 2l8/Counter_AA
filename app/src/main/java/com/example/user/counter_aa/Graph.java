package com.example.user.counter_aa;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.view.MenuItem;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

/**
 * Created by user on 11.04.16.
 */
@EActivity(R.layout.activity_graph)
@OptionsMenu(R.menu.menu_graph)
public class Graph extends Activity{
    DBH db = null;
    private GraphView graphView;
    private LinearLayout graphLayout;

    private DataPoint[] data = null;

    @ViewById(R.id.txtGraphLabel)
    TextView txtGraphLabel;

    @AfterViews
    void init(){

        if (db==null) db = new DBH(this);

        graphView = new GraphView(this);
        graphLayout = (LinearLayout) findViewById(R.id.GraphLayout);
        graphLayout.addView(graphView);

        showPowerGraph();

    }

    @OptionsItem(R.id.mi_graph_Power)
    void showPowerGraph() {
        txtGraphLabel.setText(getText(R.string.graphLabelPower));
        if (calcPower())
            draw(Color.BLUE);
    }

    @OptionsItem(R.id.mi_graph_Energy)
    void showEnergyGraph() {
        txtGraphLabel.setText(getText(R.string.graphLabelEnergy));
        if (calcEnergy())
            draw(Color.RED);
    }



    public boolean calcPower() {
        long minDT = db.getMinDT();
        long maxDT = db.getMaxDT();
        double prev_x = 0;
        double prev_y = 0;
        //double impPower = 3200.0/3600; // 1кВтч/3200 импульсов
        double impPower = 100.0/1; // 100/1кВтч/ импульсов

        Cursor cursor = db.getReadableDatabase().rawQuery("Select dtl from impuls order by dtl asc;", null);
        if (!cursor.moveToFirst())
            return false;

        int cnt =cursor.getCount();
        this.data = new DataPoint[cnt];
        do{
            double dtl = cursor.getLong(cursor.getColumnIndex(db.KEY_DTL));
            double x = (dtl-minDT)/1000; // секунды от начала
            double y = 0 ;
            if (x!=0) y=3600/(200*(x-prev_x));
            if (y>100) y=prev_y;
            this.data[cursor.getPosition()] = new DataPoint(x,y);
            prev_x = x;
            prev_y = y;
        }while (cursor.moveToNext());
        cursor.close();
        return true;
    }

    public boolean calcEnergy() {
        long minDT = db.getMinDT();
        long maxDT = db.getMaxDT();
        double prev_x = 0;
        double prev_y = 0;
        //double impPower = 3200.0/3600; // 1кВтч/3200 импульсов
        double impPower = 100.0/1; // 100/1кВтч/ импульсов

        Cursor cursor = db.getReadableDatabase().rawQuery("Select "+db.KEY_DTL
                                                        +" from "+db.TABLE_NAME
                                                        +" order by "+db.KEY_DTL+" asc;", null);
        if (!cursor.moveToFirst())
            return false;

        int cnt =cursor.getCount();
        this.data = new DataPoint[cnt];
        do{
            double dtl = cursor.getLong(cursor.getColumnIndex(db.KEY_DTL));
            double x = (dtl-minDT)/1000; // секунды от начала
            double y = 0 ;
            if (x!=0) y=prev_y+3600/(200*(x-prev_x));
            if (y>100) y=prev_y;
            this.data[cursor.getPosition()] = new DataPoint(x,y);
            prev_x = x;
            prev_y = y;
        }while (cursor.moveToNext());
        cursor.close();
        return true;
    }


    boolean draw(int color){
        try
        {
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(this.data);
            graphView.getViewport().setScrollable(true);
            graphView.getViewport().setScalable(true);

            series.setDrawDataPoints(true);
            series.setDataPointsRadius(2);
            series.setColor(color);

            graphView.removeAllSeries();
            graphView.addSeries(series);

            graphView.getViewport().setMinX(0);
            graphView.getViewport().setMaxX(this.data.length);
        }
        catch (Exception e){return false;}

        return true;
    }
}
