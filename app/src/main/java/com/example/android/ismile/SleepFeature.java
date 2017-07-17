package com.example.android.ismile;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import static android.content.ContentValues.TAG;

/**
 * Created by simon on 22.05.2017.
 */

public class SleepFeature {
    private DbHelper dbHelper;
    SQLiteDatabase db;
    public Cursor curcor;

    public static int winSize = 30;

    public SleepFeature(Context context){
        dbHelper = new DbHelper(context);
        db = dbHelper.getReadableDatabase();

        String[] projection  = {dbHelper.SN,dbHelper.Time,dbHelper.x,dbHelper.y,dbHelper.z};

//        String selection = dbHelper.SN + " = ?";
//        String[] selectionArgs = { "3b7323c87d93" };

        String sortOrder = dbHelper.Time + " DESC";

        curcor = db.query(dbHelper.TABLE_ACC,projection,null,null,null,null,sortOrder);
        curcor.moveToFirst();
    }

    public long wakeNum(){

        long sum=0;
        float [] sample = new float[winSize];
        AcceSample acceSample = new AcceSample(sample);

        for(long i=0;i<curcor.getCount();i++){
            sample[(int) (i%winSize)]=(float) sqrt(pow(curcor.getFloat(curcor.getColumnIndex(dbHelper.x)),2)+
                    pow(curcor.getFloat(curcor.getColumnIndex(dbHelper.y)),2)+
                    pow(curcor.getFloat(curcor.getColumnIndex(dbHelper.z)),2));
            if((i+1)%winSize==0) {
                acceSample = new AcceSample(sample);
                if(acceSample.isWake())sum++;
            }
            curcor.moveToNext();
        }

        curcor.close();

        Log.d(TAG, "onSensorChanged: "+sum+"--"+curcor.getCount());

        //db.execSQL("DELETE FROM "+dbHelper.TABLE_NAME_ONE);

        return sum;
    }
}
