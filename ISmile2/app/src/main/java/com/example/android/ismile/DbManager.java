package com.example.android.ismile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.SensorEvent;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by siat on 2017/6/2.
 */

public class  DbManager {
    public DbHelper dbHelper;
    public SQLiteDatabase db;
    public DbManager(Context context){
        dbHelper=new DbHelper(context);
    }

    //write mood value to table mood in local db
    public void moodWriter(String arousal,String valence){
        String time = getTime();
        final ContentValues values = new ContentValues();
        values.put(dbHelper.SN, android.os.Build.SERIAL);
        values.put(dbHelper.Time, time);
        values.put(dbHelper.Arousal,arousal);
        values.put(dbHelper.Valence,valence);

        db = dbHelper.getWritableDatabase();
        long newRowId = db.insert(dbHelper.TABLE_MOOD_2, null, values);
    }

    //write acc value to table acc in local db
    public void accWriter(SensorEvent senEvent){
        String time = getTime();

        final ContentValues values = new ContentValues();
        values.put(dbHelper.SN, android.os.Build.SERIAL);
        values.put(dbHelper.Time, time);
        values.put(dbHelper.x,senEvent.values[0]);
        values.put(dbHelper.y,senEvent.values[1]);
        values.put(dbHelper.z,senEvent.values[2]);

        db = dbHelper.getWritableDatabase();
        long newRowId = db.insert(dbHelper.TABLE_ACC, null, values);
    }

    //写入标记位
    public void signWriter(String x,String y,String z){
        String time = getTime();

        final ContentValues values = new ContentValues();
        values.put(dbHelper.SN, android.os.Build.SERIAL);
        values.put(dbHelper.Time, time);
        values.put(dbHelper.x,x);
        values.put(dbHelper.y,x);
        values.put(dbHelper.z,x);

        db = dbHelper.getWritableDatabase();
        long newRowId = db.insert(dbHelper.TABLE_ACC, null, values);
    }

    //get current time
    public String getTime(){
        long curTime = System.currentTimeMillis();
        Date date = new Date(curTime);
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        final String time = dateFormat.format(date);
        try {
            dateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public Cursor getAccCursor(){
        db = dbHelper.getReadableDatabase();

        String[] projection = {dbHelper.SN,dbHelper.Time,dbHelper.x,dbHelper.y,dbHelper.z};

        String sortOrder = dbHelper.Time;

        Cursor cursor = db.query(dbHelper.TABLE_ACC,projection,null,null,null,null,sortOrder);

        return cursor;
    }

    public Cursor getMoodCursor(){
        db=dbHelper.getReadableDatabase();

        String[] projection = {dbHelper.SN,dbHelper.Time,dbHelper.Arousal,dbHelper.Valence};

        String sortOrder=dbHelper.Time;

        Cursor cursor=db.query(dbHelper.TABLE_MOOD_2,projection,null,null,null,null,sortOrder);

        return cursor;
    }

    public int getMoodValue(){
        db = dbHelper.getReadableDatabase();

        String[] projection  = {dbHelper.SN,dbHelper.Time,dbHelper.Arousal,dbHelper.Valence};

        String sortOrder = dbHelper.Time;

        Cursor cursor = db.query(dbHelper.TABLE_MOOD_2,projection,null,null,null,null,sortOrder);
        Log.d("cursor", "getMoodValue: "+cursor.getCount());
        cursor.moveToLast();

        return cursor.getInt(2);
    }

    public void accClear(){
        db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM "+dbHelper.TABLE_ACC);
    }

}