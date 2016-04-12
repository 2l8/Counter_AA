package com.example.user.counter_aa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by user on 11.04.16.
 */

public class DBH extends SQLiteOpenHelper {
    static final String LOG_TAG = "myLogs";
    static final String TABLE_NAME = "IMPULS";
    static final String DB_NAME     = "energy.db";
    static final int DB_VERSION     = 1;

    static final String KEY_DTL = "dtl";
    static final String KEY_AMPL = "amplitude";

    static final String sql_CREATE_TABLE     = "create table "+TABLE_NAME+" "
                                +" (_id integer primary key autoincrement not null"
                                +", "+KEY_DTL+" long not null"
                                +", "+KEY_AMPL+"  integer"
                                +");";

    static final String sql_NEW_IMPULS       ="";

    static final String sql_DROP_TABLE       ="DROP TABLE  "+TABLE_NAME+";";

    static final String sql_DELETE_DATA      ="DELETE from "+TABLE_NAME+";";

    static final String sql_GET_DATA         = "";
    static final String sql_GET_MAX_DTL      = "SELECT MAX("+KEY_DTL+") AS MAX_DTL FROM "+TABLE_NAME;
    static final String sql_GET_MIN_DTL      = "SELECT MIN("+KEY_DTL+") AS MIN_DTL FROM "+TABLE_NAME;



    public DBH(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBH(Context con) {
        super(con, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(sql_CREATE_TABLE);
        }
        catch(Exception e) {
            e.printStackTrace(System.out);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


    public void impuls(int amplitude){
        ContentValues val = new ContentValues();

        val.put(KEY_DTL, Calendar.getInstance().getTimeInMillis());
        val.put(KEY_AMPL, (int)amplitude);

        long id =this.getWritableDatabase().insert(TABLE_NAME, null, val);
        Log.d(LOG_TAG, "insert - " + id);
    }

    public long getMinDT(){
        long minDT=0;
        try{
            Cursor cursor = this.getReadableDatabase().rawQuery(sql_GET_MIN_DTL, null);
            if (cursor!=null){
                if (cursor.moveToFirst()){
                    minDT = cursor.getLong(cursor.getColumnIndex("MIN_DTL"));
                }
                cursor.close();
            }
        }
        catch (Exception e){
            e.printStackTrace(System.out);
        }
         return minDT;
    }


    public long getMaxDT(){
        long maxDT=0;
        try {
            Cursor cursor = this.getWritableDatabase().rawQuery(sql_GET_MAX_DTL, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    maxDT = cursor.getLong(cursor.getColumnIndex("MAX_DTL"));
                }
                cursor.close();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return maxDT;
    }


    public void deleteRecordsFromTable(){
        this.getWritableDatabase().execSQL(sql_DELETE_DATA);
    }


    public void dropTable(){
        this.getWritableDatabase().execSQL(sql_DROP_TABLE);
    }


    public void createTable(){
        this.getWritableDatabase().execSQL(sql_CREATE_TABLE);
    }



}