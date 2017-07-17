package com.example.android.ismile;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by siat on 2017/4/30.
 */

public class DbHelper extends SQLiteOpenHelper {

    public static final String TABLE_ACC = "accTable";
    public static final String SN = "SN";
    public static final String Time = "Time";
    public static final String x = "x";
    public static final String y = "y";
    public static final String z = "z";

    public static final String TABLE_MOOD_2 = "moodTable";
    public static final String Arousal = "Arousal";
    public static final String Valence = "Valence";

    private static final String TEXT_TYPE = " TEXT";
    private static final String FLOAT_TYPE = " FLOAT";
    private static final String INT_TYPE = " INT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ACC =
            "CREATE TABLE IF NOT EXISTS " + TABLE_ACC + "("+
                    SN + TEXT_TYPE + COMMA_SEP +
                    Time + TEXT_TYPE + COMMA_SEP +
                    x + FLOAT_TYPE + COMMA_SEP +
                    y + FLOAT_TYPE + COMMA_SEP +
                    z + FLOAT_TYPE + ")";

    private static final String SQL_CREATE_MOOD =
            "CREATE TABLE IF NOT EXISTS " + TABLE_MOOD_2 + "("+
                    SN + TEXT_TYPE + COMMA_SEP +
                    Time + TEXT_TYPE + COMMA_SEP +
                    Arousal + INT_TYPE + COMMA_SEP +
                    Valence + INT_TYPE + ")";

    private static final String SQL_DELETE_ACC = "DROP TABLE IF EXISTS " + TABLE_ACC;
    private static final String SQL_DELETE_MOOD = "DROP TABLE IF EXISTS " + TABLE_MOOD_2;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "smile.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ACC);
        db.execSQL(SQL_CREATE_MOOD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ACC);
        db.execSQL(SQL_DELETE_MOOD);
        onCreate(db);
    }
}