package com.example.android.ismile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by simon on 09.05.2017.
 */

public class OutputActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private SeekBar arousal_sb;
    private SeekBar valence_sb;

    private TextView arousal_tv;
    private TextView valence_tv;

    private Button btn_ok;

    private String arousal_qa="0";
    private String valence_qa="0";

    @Override
    protected void onCreate(Bundle savedInstancestate){
        super.onCreate(savedInstancestate);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.arousal_valence);

        arousal_sb=(SeekBar)findViewById(R.id.arousal_seekbar);
        valence_sb=(SeekBar)findViewById(R.id.valence_seekbar);
        arousal_sb.setMax(10);
        valence_sb.setMax(10);
        arousal_sb.setOnSeekBarChangeListener(this);
        valence_sb.setOnSeekBarChangeListener(this);

        arousal_tv=(TextView)findViewById(R.id.arousal_value);
        valence_tv=(TextView)findViewById(R.id.valence_value);

        btn_ok=(Button)findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(OutputActivity.this,MainActivity_alarm.class);
                startActivity(intent);

                new Thread(){
                    @Override
                    public void run(){
                        UpLoad upLoad = new UpLoad(OutputActivity.this);
                        upLoad.upQa(arousal_qa,valence_qa,"http://54.169.225.49/tmood.php");
                    }
                }.start();

                Toast.makeText(getApplicationContext(),arousal_qa+" "+valence_qa,Toast.LENGTH_LONG).show();
            }
        });

    }

    //SeekBar滑动时间监听
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()){
            case R.id.arousal_seekbar:
                arousal_tv.setText(String.valueOf(seekBar.getProgress())+"/10");
                break;
            case R.id.valence_seekbar:
                valence_tv.setText(String.valueOf(seekBar.getProgress())+"/10");
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()){
            case R.id.arousal_seekbar:
                arousal_qa=String.valueOf(seekBar.getProgress());
                break;
            case R.id.valence_seekbar:
                valence_qa=String.valueOf(seekBar.getProgress());
                break;
        }
    }

    //实现后退键跳转到主程序
    public void onBackPressed(){
        Intent intent=new Intent(OutputActivity.this,MainActivity_alarm.class);
        startActivity(intent);
        super.onBackPressed();
    }
}

