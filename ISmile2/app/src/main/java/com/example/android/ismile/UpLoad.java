package com.example.android.ismile;

import android.content.Context;
import android.database.Cursor;
import android.hardware.SensorEvent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static android.R.string.ok;

/**
 * Created by siat on 2017/5/22.
 */

public class UpLoad {
    public URL url;
    public DbHelper dbHelper;
    public DbManager dbManager;

    //construction method
    public UpLoad(Context context){
        dbHelper = new DbHelper(context);
        dbManager = new DbManager(context);
    }
    //upload data
    public void upData(String data,String urlStr){
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        final String finalData = data;
        new Thread(new Runnable() {
            @Override
            public void run() {
                URLConnection conn = null;
                OutputStreamWriter wr = null;
                try {
                    conn = url.openConnection();
                    conn.setDoOutput(true);
                    wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(finalData);
                    wr.flush();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //upload mood values to url
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void upMood(String urlStr) throws JSONException, IOException {

        Cursor cursor = dbManager.getMoodCursor();

        cursor.moveToFirst();
        JSONArray jsonArray = new JSONArray();

        for (int i=0;i<cursor.getCount();i++){
            final JSONObject jsonObject = new JSONObject();

            for (int j=0;j<cursor.getColumnCount();j++){
                jsonObject.put(cursor.getColumnName(j),cursor.getString(j));
            }
            jsonArray.put(jsonObject);

            if (jsonArray.length()==600){
                String jsonData  = null;
                try {
                    jsonData = URLEncoder.encode("data", "UTF-8")
                            + "=" + URLEncoder.encode(jsonArray.toString(),"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                upData(jsonData,urlStr);
                Log.d("jsondatasize", "upAccByJson: "+ jsonData.length()+"/"+jsonArray.length());
                jsonArray = new JSONArray("[]");
            }

            cursor.moveToNext();
        }

        String jsonData  = null;
        try {
            jsonData = URLEncoder.encode("data", "UTF-8")
                    + "=" + URLEncoder.encode(jsonArray.toString(),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        upData(jsonData,urlStr);


        Log.d("jsonArray", "upAccByJson: "+jsonArray);
        cursor.close();
        dbManager.accClear();
    }

    //upload quest answer to url
    public void upQa(String arousal_qa,String valence_qa,String urlStr){
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String time=getTime();

        String data  = "";
        try {
            data = URLEncoder.encode("SN", "UTF-8")
                    + "=" + URLEncoder.encode(android.os.Build.SERIAL, "UTF-8");
            data += "&" + URLEncoder.encode("Time", "UTF-8")
                    + "=" + URLEncoder.encode(time, "UTF-8");
            data += "&" + URLEncoder.encode("Arousal", "UTF-8")
                    + "=" + URLEncoder.encode(arousal_qa, "UTF-8");
            data += "&" + URLEncoder.encode("Valence", "UTF-8")
                    + "=" + URLEncoder.encode(valence_qa, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        upData(data,urlStr);
    }

    //upload acc values to url
    public void upAcc(SensorEvent senEvent,String urlStr){

        String time = getTime();

        String data  = null;
        try {
            data = URLEncoder.encode(dbHelper.SN, "UTF-8")
                    + "=" + URLEncoder.encode(android.os.Build.SERIAL, "UTF-8");
            data += "&" + URLEncoder.encode(dbHelper.Time, "UTF-8")
                    + "=" + URLEncoder.encode(time, "UTF-8");
            data += "&" + URLEncoder.encode(dbHelper.x, "UTF-8")
                    + "=" + URLEncoder.encode(String.valueOf(senEvent.values[0]), "UTF-8");
            data += "&" + URLEncoder.encode(dbHelper.y, "UTF-8")
                    + "=" + URLEncoder.encode(String.valueOf(senEvent.values[1]), "UTF-8");
            data += "&" + URLEncoder.encode(dbHelper.z, "UTF-8")
                    + "=" + URLEncoder.encode(String.valueOf(senEvent.values[2]), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        upData(data,urlStr);
    }

    public void upAccByJson(String urlStr) throws JSONException, IOException {

        Cursor cursor = dbManager.getAccCursor();

        cursor.moveToFirst();
        JSONArray jsonArray = new JSONArray();

        for (int i=0;i<cursor.getCount();i++){
            final JSONObject jsonObject = new JSONObject();

            for (int j=0;j<cursor.getColumnCount();j++){
                jsonObject.put(cursor.getColumnName(j),cursor.getString(j));
            }
            jsonArray.put(jsonObject);

            if (jsonArray.length()==600){
                String jsonData  = null;
                try {
                    jsonData = URLEncoder.encode("data", "UTF-8")
                            + "=" + URLEncoder.encode(jsonArray.toString(),"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                upData(jsonData,urlStr);
                Log.d("jsondatasize", "upAccByJson: "+ jsonData.length()+"/"+jsonArray.length());
                jsonArray = new JSONArray("[]");
            }

            cursor.moveToNext();
        }

        String jsonData  = null;
        try {
            jsonData = URLEncoder.encode("data", "UTF-8")
                    + "=" + URLEncoder.encode(jsonArray.toString(),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        upData(jsonData,urlStr);


        Log.d("jsonArray", "upAccByJson: "+jsonArray);
        cursor.close();
        dbManager.accClear();
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


}

