package com.example.android.ismile;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;

import org.json.JSONException;
import java.io.IOException;


import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CltService extends Service implements SensorEventListener{

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private SensorEvent senEvent;
    private ScheduledFuture<?> senSchedule;
    private ScheduledFuture<?> jsonSchedule;
    private DbManager dbManager;

    private Timer timer;

    private UpLoad upLoad;

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    String string;

    public CltService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        Notification.Builder builder=new Notification.Builder(this);
        PendingIntent contentIntent= PendingIntent.getActivity(this,0,
                new Intent(this,MainActivity_alarm.class),0);
        builder.setContentIntent(contentIntent);
        builder.setSmallIcon(R.drawable.ic_ismile);
        builder.setOngoing(true);
        builder.setContentTitle("iSmile");
        builder.setContentText("Good Night! ^-^");
        Notification notification=builder.build();
        startForeground(1,notification);

        senSensorManager.registerListener(this, senAccelerometer , senSensorManager.SENSOR_DELAY_GAME);
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyWakelockTag");
        wakeLock.acquire();


//        ScheduledExecutorService jsonService = Executors.newScheduledThreadPool(3);
//        jsonSchedule = jsonService.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    upLoad.upAccByJson("http://13.228.21.35/jsontest.php");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, 0, 1, TimeUnit.HOURS);

    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        dbManager = new DbManager(this);
        upLoad = new UpLoad(this);

        string=intent.getStringExtra("tips");

        if(string.compareTo("test")==0){
            dbManager.signWriter("20000","20000","20000");
        }else {
            dbManager.signWriter("10000", "10000", "10000");
        }

        timer=new Timer();
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                if (senEvent!=null) dbManager.accWriter(senEvent);
            }
        };
        timer.scheduleAtFixedRate(timerTask,0,100);

        return super.onStartCommand(intent,flags,startId);
    }



    @Override
    public void onDestroy(){
        senSensorManager.unregisterListener(this);
//        jsonSchedule.cancel(true);
        if(string.compareTo("test")==0){
            dbManager.signWriter("-20000","-20000","-20000");
        }else {
            dbManager.signWriter("-10000", "-10000", "-10000");
        }
        timer.cancel();
        wakeLock.release();
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onSensorChanged(SensorEvent event) {
        senEvent = event;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}