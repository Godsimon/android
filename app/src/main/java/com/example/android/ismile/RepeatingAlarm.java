package com.example.android.ismile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;


/**
 * Created by simon on 04.05.2017.
 */

public class RepeatingAlarm extends BroadcastReceiver {
    String music;

    int mHour;
    int hour;

    @Override
    public void onReceive(Context context, Intent intent){
        if(intent.getAction()!=null&&intent.getAction().equals("com.gcc.alarm")){


                music = intent.getStringExtra("music");
                Intent intent1 = new Intent(context, AlarmActivity.class);
                intent1.putExtra("music", music);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);

        }
    }
}
