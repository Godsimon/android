package com.example.android.ismile;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by simon on 09.05.2017.
 */

public class WatingActivity extends AppCompatActivity {
    Button btn_change;

    //判断activity是否非正常死亡
    boolean notRight=true;

    @Override
    protected void onCreate(Bundle savedInstancestate){
        super.onCreate(savedInstancestate);


        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.wating);

        //打开加速度传感器服务
        Intent intent2=new Intent(WatingActivity.this,CltService.class);
        intent2.putExtra("tips","android");
        startService(intent2);

        btn_change=(Button)findViewById(R.id.change);
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(WatingActivity.this,MainActivity_alarm.class);
                startActivity(intent);

                cancleAlarm();

                //关闭加速度传感器服务
                Intent intent1=new Intent(WatingActivity.this,CltService.class);
                stopService(intent1);
            }
        });

        //滑动部分实现
        SildingFinishLayout mSildingFinishLayout=(SildingFinishLayout)
                findViewById(R.id.sildingFinishLayout);
        mSildingFinishLayout.setOnSildingFinishListener(new SildingFinishLayout.OnSildingFinishListener() {
            @Override
            public void onSildingFinish() {
                cancleAlarm();

                Intent intent=new Intent(WatingActivity.this,OutputActivity.class);
                startActivity(intent);

                //关闭加速度传感器服务
                Intent intent1=new Intent(WatingActivity.this,CltService.class);
                stopService(intent1);

                notRight=false;

                finish();
            }
        });

        mSildingFinishLayout.setTouchView(mSildingFinishLayout);

    }

    private void cancleAlarm(){
        Intent intent=new Intent(WatingActivity.this,RepeatingAlarm.class);
        intent.setAction("com.gcc.alarm");
        PendingIntent sender=PendingIntent.getBroadcast(WatingActivity.this,0,intent,0);
        AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);
        am.cancel(sender);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(notRight){
            cancleAlarm();
            //关闭加速度传感器服务
            Intent intent1=new Intent(WatingActivity.this,CltService.class);
            stopService(intent1);
        }
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
