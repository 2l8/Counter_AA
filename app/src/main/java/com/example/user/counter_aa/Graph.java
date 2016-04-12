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
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
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

    private final int TYPE_BAR = 1101;
    private final int TYPE_LINE = 1102;

    private final int GRAPH_POWER = 1201;
    private final int GRAPH_ENERGY = 1202;
    private final int GRAPH_DURTY = 1203;

    @InstanceState
    int GraphState=1301;

    @ViewById(R.id.txtGraphLabel)
    TextView txtGraphLabel;

    @AfterViews
    void init(){

        if (db==null) db = new DBH(this);

        graphView = new GraphView(this);
        graphLayout = (LinearLayout) findViewById(R.id.GraphLayout);
        graphLayout.addView(graphView);

        switch (GraphState){
            case GRAPH_ENERGY:
                showEnergyGraph();
                break;
            case GRAPH_POWER:
                showPowerGraph();
                break;
            case GRAPH_DURTY:
                showDurtyDataGraph();
                break;
            default: showPowerGraph();
        }

    }

    @OptionsItem(R.id.mi_graph_Power)
    void showPowerGraph() {
        GraphState = GRAPH_POWER;
        txtGraphLabel.setText(getText(R.string.graphLabelPower));
        if (calcPower())
            draw(TYPE_LINE, Color.BLUE);
        else
            Toast.makeText(this, "Нет данных", Toast.LENGTH_SHORT).show();
    }

    @OptionsItem(R.id.mi_graph_Energy)
    void showEnergyGraph() {
        GraphState = GRAPH_ENERGY;

        txtGraphLabel.setText(getText(R.string.graphLabelEnergy));

        if (calcEnergy())
        //    draw(R.color.EnergyLine);
            draw(TYPE_LINE, Color.RED);
        else
            Toast.makeText(this, "Нет данных", Toast.LENGTH_SHORT).show();

    }

    @OptionsItem(R.id.mi_graph_DurtyData)
    void showDurtyDataGraph() {
        GraphState = GRAPH_DURTY;

        txtGraphLabel.setText(getText(R.string.graphLabelDurty));

        if (calcDurtyData()){
//            draw(R.color.DurtyLine);
            draw(TYPE_BAR, Color.GREEN);
            drawFilter(1500, Color.YELLOW);
        }
        else
            Toast.makeText(this, "Нет данных", Toast.LENGTH_SHORT).show();
    }


    //Расчет мощности потребления по имеющимся данным
    public boolean calcPower() {
        long minDT = db.getMinDT();
        long maxDT = db.getMaxDT();
        int tickCount = db.getTickCount();
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
            if (x!=0) y=3600/(tickCount*(x-prev_x));
            if (y>100) y=prev_y;
            this.data[cursor.getPosition()] = new DataPoint(x,y);
            prev_x = x;
            prev_y = y;
        }while (cursor.moveToNext());
        cursor.close();
        return true;
    }

    //Расчет количества потребленной ЭЭ за время измерений
    public boolean calcEnergy() {
        long minDT = db.getMinDT();
        long maxDT = db.getMaxDT();
        int tickCount = db.getTickCount();
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
            if (x!=0) y=prev_y+3600/(tickCount*(x-prev_x));
            if (y>100) y=prev_y;
            this.data[cursor.getPosition()] = new DataPoint(x,y);
            prev_x = x;
            prev_y = y;
        }while (cursor.moveToNext());
        cursor.close();
        return true;
    }

    //Подготова поступивших данных (пары [время: уровень сигнала])
    public boolean calcDurtyData() {
        long minDT = db.getMinDT();

        //double impPower = 3200.0/3600; // 1кВтч/3200 импульсов
        double impPower = 100.0/1; // 100/1кВтч/ импульсов

        Cursor cursor = db.getReadableDatabase().rawQuery(""
                +" Select "+db.KEY_DTL+", "+db.KEY_AMPL
                +" from "+db.TABLE_NAME
                +" order by "+db.KEY_DTL+" asc;", null);
        if (!cursor.moveToFirst())
            return false;

        int cnt =cursor.getCount();
        this.data = new DataPoint[cnt];
        do{
            double dtl = cursor.getLong(cursor.getColumnIndex(db.KEY_DTL));
            double x = (dtl-minDT)/1000; // секунды от начала
            double y = cursor.getInt(cursor.getColumnIndex(db.KEY_AMPL));
            this.data[cursor.getPosition()] = new DataPoint(x,y);
        }while (cursor.moveToNext());
        cursor.close();
        return true;
    }

    //Отрисовка данных на графике
    boolean draw(int GraphType, int color){
        try
        {
            graphView.getViewport().setScrollable(true);
            graphView.getViewport().setScalable(true);
            graphView.removeAllSeries();


            if (GraphType == TYPE_BAR) {
                BarGraphSeries<DataPoint> series = new BarGraphSeries<>(this.data);
                series.setColor(color);
                graphView.addSeries(series);
            }

            if (GraphType == TYPE_LINE){
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(this.data);
                series.setDrawDataPoints(true);
                series.setDataPointsRadius(2);
                series.setColor(color);
                graphView.addSeries(series);
            }

            graphView.getViewport().setMinX(0);
            graphView.getViewport().setMaxX(Math.ceil(this.data[this.data.length-1].getX()));
        }
        catch (Exception e){return false;}

        return true;
    }

    boolean drawFilter(int value, int color){
        try
        {
            DataPoint[] filterData = new DataPoint[2];
            DataPoint BeginPoint = new DataPoint(0,value);
            DataPoint EndPoint = new DataPoint(Math.ceil(this.data[this.data.length - 1].getX()),value);
            filterData[0]=BeginPoint;
            filterData[1]=EndPoint;

            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(filterData);
            series.setDrawDataPoints(true);
            series.setDataPointsRadius(2);
            series.setThickness(1);
            series.setColor(color);
            graphView.addSeries(series);

      }
        catch (Exception e){return false;}

        return true;
    }
}
