package com.example.android.ismile;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by simon on 06.05.2017.
 */

public class AlarmActivity extends AppCompatActivity {

    MediaPlayer mp;
    Button btn_snooze;
    String music;

    PowerManager pm;
    PowerManager.WakeLock mWakelock;

    //判断是否非正常死亡
    boolean notRight=true;

    UpLoad upLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                |WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                |WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                |WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
                |WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        );

        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.wake_up);

        //强制屏幕亮起
        pm=(PowerManager)getSystemService(Context.POWER_SERVICE);
        mWakelock=pm.newWakeLock(
                PowerManager.ACQUIRE_CAUSES_WAKEUP| PowerManager.SCREEN_DIM_WAKE_LOCK, "SimpleTimer");
        mWakelock.acquire();

        mp=new MediaPlayer();

        music=getIntent().getStringExtra("music");
        switch (music) {
            case "color":
                if(mp!=null) mp.stop();
                AssetFileDescriptor file0 = getResources().openRawResourceFd(R.raw.color);
                play(file0);
                break;
            case "summer":
                if(mp!=null) mp.stop();
                AssetFileDescriptor file1 = getResources().openRawResourceFd(R.raw.summer);
                play(file1);
                break;
            case "run":
                if(mp!=null) mp.stop();
                AssetFileDescriptor file2 = getResources().openRawResourceFd(R.raw.run);
                play(file2);
                break;
            case "baby":
                if(mp!=null) mp.stop();
                AssetFileDescriptor file3 = getResources().openRawResourceFd(R.raw.baby);
                play(file3);
                break;
            case "X":
                if(mp!=null) mp.stop();
                AssetFileDescriptor file4 = getResources().openRawResourceFd(R.raw.xx);
                play(file4);
                break;
            case "O":
                if(mp!=null) mp.stop();
                AssetFileDescriptor file5 = getResources().openRawResourceFd(R.raw.oo);
                play(file5);
                break;
        }


        //延长闹钟
        btn_snooze=(Button)findViewById(R.id.wakeup_snooze);
        btn_snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm();
                finish();
                Intent intent=new Intent(AlarmActivity.this,WatingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
        //滑动部分实现取消闹钟
        SildingFinishLayout mSildingFinishLayout=(SildingFinishLayout)
                findViewById(R.id.sildingFinishLayout);
        mSildingFinishLayout.setOnSildingFinishListener(new SildingFinishLayout.OnSildingFinishListener() {
            @Override
            public void onSildingFinish() {
                cancleAlarm();
                finish();

                //关闭加速度传感器服务
                Intent intent1=new Intent(AlarmActivity.this,CltService.class);
                stopService(intent1);

                notRight=true;

                Intent intent=new Intent(AlarmActivity.this,OutputActivity.class);
                startActivity(intent);

                //最后关闭闹钟传输剩余数据
//                upLoad = new UpLoad(AlarmActivity.this);
//                try {
//                    upLoad.upAccByJson("http://13.228.21.35/jsontest.php");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        });

        mSildingFinishLayout.setTouchView(mSildingFinishLayout);

    }

    public void play(AssetFileDescriptor file){
        mp=new MediaPlayer();
        try{
            mp.setDataSource(file.getFileDescriptor(),file.getStartOffset(),
                    file.getLength());
            mp.prepare();
            file.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        mp.setVolume(0.5f,0.5f);
        mp.setLooping(true);
        mp.start();
    }

    //闹钟部分实现
    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(mp!=null){
            if(mp.isPlaying()){
                mp.stop();
            }
            mp.release();
        }
        if(notRight){
            cancleAlarm();
            //关闭加速度传感器服务
            Intent intent1=new Intent(AlarmActivity.this,CltService.class);
            stopService(intent1);
        }
        mWakelock.release();
    }


    private void cancleAlarm(){
        Intent intent=new Intent(AlarmActivity.this,RepeatingAlarm.class);
        intent.setAction("com.gcc.alarm");
        PendingIntent sender=PendingIntent.getBroadcast(AlarmActivity.this,0,intent,0);
        AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);
        am.cancel(sender);
    }

    private void alarm(){
        AlarmManager am=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        // 触发闹钟的时间（毫秒）
        long triggerTime = System.currentTimeMillis() + 300000;
        Intent intent = new Intent(this, RepeatingAlarm.class);
        intent.setAction("com.gcc.alarm");
        PendingIntent op = PendingIntent.getBroadcast(this, 0, intent, 0);
        // 启动一次只会执行一次的闹钟
        am.set(AlarmManager.RTC_WAKEUP, triggerTime, op);
        Toast.makeText(AlarmActivity.this,"延迟5分钟",Toast.LENGTH_LONG).show();
    }

    //屏蔽back键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            return false;
        }
        return super.onKeyDown(keyCode,event);
    }

}
