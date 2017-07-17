package com.example.android.ismile;


import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MainActivity_alarm extends AppCompatActivity {


    //page1控件
    Button btn_start;
    TimePicker tp;
    Calendar calendar;
    AlarmManager alarmManager;

    //测试控件
    Button btn_upload;
    Button btn_startTest;
    Button btn_stopTest;

    UpLoad upLoad;

    private int mHour;
    private int hourofDay;
    private int min;

    //page2控件
    private TabHost mTabHost;
    private TabWidget mTabWidget;

    //page3控件
    private Switch auto;
    private ListView music_list;
    private ArrayList<String> list=new ArrayList<String>();


    //ViewPager
    private View alarm,mood,settings;
    private CustomViewPager customViewPager;
    private List<View> viewList;


    //闹钟是否设定
    public boolean isSet=false;
    //默认闹钟铃声
    String str="color";
    MediaPlayer mp;

    //用于进程间通讯
    MyHandler myHandler;

    //存储心情数据
    String arousal;
    String valence;

    //底端导航栏
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_alarm:
                    item.setChecked(true);
                    customViewPager.setCurrentItem(0);
                    mp.stop();
                    return false;
                case R.id.navigation_mood:
                    item.setChecked(true);
                    customViewPager.setCurrentItem(1);
                    mp.stop();
                    return true;
                case R.id.navigation_settings:
                    item.setChecked(true);
                    customViewPager.setCurrentItem(2);
                    return false;
            }
            return false;
        }

    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Remove title bar
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //禁止旋转屏幕
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_main);

        mp=new MediaPlayer();

        myHandler=new MyHandler();

        //获取Viewpager实例
        customViewPager=(CustomViewPager) findViewById(R.id.custom_viewpager);
        LayoutInflater inflater=getLayoutInflater();
        alarm=inflater.inflate(R.layout.activity_main_alarm,null);
        mood=inflater.inflate(R.layout.mood_analysis,null);
        settings=inflater.inflate(R.layout.settings_layout,null);


        viewList=new ArrayList<View>();
        viewList.add(alarm);
        viewList.add(mood);
        viewList.add(settings);

        PagerAdapter pagerAdapter=new PagerAdapter() {
            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view==object;
            }

            @Override
            public void destroyItem(ViewGroup container,int position,Object object){
                container.removeView(viewList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container,int position){
                if(position == 1){
                    TabHost tab = (TabHost)viewList.get(1).findViewById(R.id.tabHost);
                    tab.setup();
                    LayoutInflater layoutInflater = getLayoutInflater();
                    layoutInflater.inflate(R.layout.mood_tab1_days, tab.getTabContentView());
                    layoutInflater.inflate(R.layout.mood_tab2_weeks, tab.getTabContentView());
                    layoutInflater.inflate(R.layout.mood_tab3_months, tab.getTabContentView());

                    tab.addTab(tab.newTabSpec("tab1").setIndicator("Days" ).setContent(R.id.mood_tab1_days));
                    tab.addTab(tab.newTabSpec("tab2").setIndicator("Weeks").setContent(R.id.mood_tab2_weeks));
                    tab.addTab(tab.newTabSpec("tab3").setIndicator("Months").setContent(R.id.mood_tab3_months));
                    mTabWidget=tab.getTabWidget();
                    mTabWidget.setVisibility(View.VISIBLE);

                    for (int i=0;i<mTabWidget.getChildCount();i++){
                        TextView tv=(TextView)mTabWidget.getChildAt(i).findViewById(android.R.id.title);
                        tv.setTextSize(15);
                        tv.setTextColor(Color.WHITE);
                    }
                    showCharts();
                }
                container.addView(viewList.get(position));
                return viewList.get(position);
            }
        };
        customViewPager.setAdapter(pagerAdapter);

        //测试控件实现
        btn_upload=(Button)alarm.findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        upLoad = new UpLoad(MainActivity_alarm.this);
                        try {
                            upLoad.upAccByJson("http://54.169.225.49/jsontest.php");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Message msg= new Message();
                        Bundle b=new Bundle();
                        b.putString("Toast","上传结束");
                        msg.setData(b);
                        myHandler.sendMessage(msg);
                    }
                }.start();
            }
        });
        btn_startTest=(Button)alarm.findViewById(R.id.start_test);
        btn_startTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentTest=new Intent(MainActivity_alarm.this,CltService.class);
                intentTest.putExtra("tips","test");
                startService(intentTest);
                Toast.makeText(getApplicationContext(),"开始测试",Toast.LENGTH_LONG).show();
            }
        });
        btn_stopTest=(Button)alarm.findViewById(R.id.stop_test);
        btn_stopTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentTest1=new Intent(MainActivity_alarm.this,CltService.class);
                stopService(intentTest1);
                Toast.makeText(getApplicationContext(),"测试结束",Toast.LENGTH_LONG).show();
            }
        });


        //获取navigation实例
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //page1相关方法
        btn_start=(Button)alarm.findViewById(R.id.main_start);
        tp=(TimePicker)alarm.findViewById(R.id.time_picker);
        calendar=Calendar.getInstance();
        hourofDay=calendar.get(Calendar.HOUR_OF_DAY);
        min=calendar.get(Calendar.MINUTE);
        tp.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener(){
            @Override
            public void onTimeChanged(TimePicker view,int hour,int minute){
                hourofDay=hour;
                min=minute;
            }
        });
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取控件数据，设定闹钟时间
                Calendar cal=Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                cal.set(Calendar.HOUR_OF_DAY,hourofDay);
                cal.set(Calendar.MINUTE,min);
                cal.set(Calendar.SECOND,0);

                //设定闹钟
                Intent intent=new Intent(MainActivity_alarm.this,RepeatingAlarm.class);
                intent.setAction("com.gcc.alarm");
                intent.putExtra("music",str);
                PendingIntent pi=PendingIntent.getBroadcast(MainActivity_alarm.this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);

                //屏蔽错误闹钟
                long time=System.currentTimeMillis();
                java.util.Calendar mCalendar= java.util.Calendar.getInstance();
                mCalendar.setTimeInMillis(time);
                mHour=mCalendar.get(java.util.Calendar.HOUR_OF_DAY);
                if(mHour-hourofDay>0){
                    cal.add(Calendar.DAY_OF_YEAR,1);
                }

                int dayOfYear=0;
                dayOfYear=cal.get(Calendar.DAY_OF_YEAR);

                alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pi);
                Toast.makeText(MainActivity_alarm.this,dayOfYear+":"+hourofDay+":"+min,Toast.LENGTH_SHORT).show();
                isSet=true;


                //跳转到WatingActivity
                Intent intent1=new Intent(MainActivity_alarm.this,WatingActivity.class);
                startActivity(intent1);
            }
        });


        if(getIntent().getStringExtra("music")!=null)str=getIntent().getStringExtra("music");

        //Settings控件获取实例
        auto=(Switch)settings.findViewById(R.id.auto);
        auto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    music_list.setVisibility(View.INVISIBLE);
                    mp.stop();
                }
                else {
                    music_list.setVisibility(View.VISIBLE);
                }
            }
        });

        music_list=(ListView)settings.findViewById(R.id.music_list);
        list.add("Baby");
        list.add("Run");
        list.add("Summer");
        list.add("X");
        list.add("O");
        list.add("Color");
        ArrayAdapter<String> musicAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        music_list.setAdapter(musicAdapter);

        music_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (list.get(position)){
                    case "Baby":
                        if(mp!=null) mp.stop();
                        AssetFileDescriptor file0 = getResources().openRawResourceFd(R.raw.baby);
                        play(file0);
                        str="baby";
                        break;
                    case "Run":
                        if(mp!=null) mp.stop();
                        AssetFileDescriptor file1 = getResources().openRawResourceFd(R.raw.run);
                        play(file1);
                        str="run";
                        break;
                    case "Summer":
                        if(mp!=null) mp.stop();
                        AssetFileDescriptor file2 = getResources().openRawResourceFd(R.raw.summer);
                        play(file2);
                        str="summer";
                        break;
                    case "X":
                        if(mp!=null) mp.stop();
                        AssetFileDescriptor file3 = getResources().openRawResourceFd(R.raw.xx);
                        play(file3);
                        str="X";
                        break;
                    case "O":
                        if(mp!=null) mp.stop();
                        AssetFileDescriptor file4 = getResources().openRawResourceFd(R.raw.oo);
                        play(file4);
                        str="O";
                        break;
                    case "Color":
                        if(mp!=null) mp.stop();
                        AssetFileDescriptor file5 = getResources().openRawResourceFd(R.raw.color);
                        play(file5);
                        str="color";
                        break;
                }
            }
        });
    }

    //播放方法
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

    private LineChart   lineChartDay;
    private LineChart   lineChartWeek;
    private LineChart   lineChartMonth;
    private BarChart    barChartDay;
    private BarChart    barChartWeek;
    private BarChart    barChartMonth;
    private void showCharts() {
        lineChartDay    = (LineChart)mood.findViewById(R.id.day_linechart);
        barChartDay     = (BarChart)mood.findViewById(R.id.day_barchart);
        lineChartWeek    = (LineChart)mood.findViewById(R.id.week_linechart);
        barChartWeek     = (BarChart)mood.findViewById(R.id.week_barchart);
        lineChartMonth    = (LineChart)mood.findViewById(R.id.month_linechart);
        barChartMonth     = (BarChart)mood.findViewById(R.id.month_barchart);
        addLineChart();
        addBarChart();
    }

    private Random random;
    private List<Entry> getAllMood(){ // TODO : select * from sqlite
        List<Entry> ret = new ArrayList<Entry>();
        for(int i=0;i<365;i++){
            random = new Random();

            int r=random.nextInt();
            if(r>0) ret.add(new Entry(i,r%100));
            else ret.add(new Entry(i,(0-r)%100));
        }
        return ret;
    }

    private List<BarEntry> getAllQuality(){ // TODO : select * from sqlite
        List<BarEntry> ret = new ArrayList<BarEntry>();
        for(int i=0;i<365;i++) {
            random = new Random();
            int r=random.nextInt();
            if(r>0) ret.add(new BarEntry(i,r%100));
            else ret.add(new BarEntry(i,(0-r)%100));
        }
        return ret;
    }

    private  void addLineChart(){
        List<Entry> allMood = getAllMood();
        addData2LineChart(lineChartDay,allMood.subList(allMood.size()-7,allMood.size()));
        addData2LineChart(lineChartWeek,allMood.subList(allMood.size()-30,allMood.size()));
        addData2LineChart(lineChartMonth,allMood);

        drawLineChart(lineChartDay,7);
        drawLineChart(lineChartWeek,30);
        drawLineChart(lineChartMonth,365);
    }

    private  void addBarChart(){
        List<BarEntry> allQ    = getAllQuality();
        addData2BarChart(barChartDay,allQ.subList(allQ.size()-7,allQ.size()));
        addData2BarChart(barChartWeek,allQ.subList(allQ.size()-30,allQ.size()));
        addData2BarChart(barChartMonth,allQ);

        drawBarChart(barChartDay,7);
        drawBarChart(barChartWeek,30);
        drawBarChart(barChartMonth,365);
    }

    //添加数据到linechart
    private void addData2LineChart(LineChart lineChart, List<Entry> entries){
        LineDataSet lineDataSet = new LineDataSet(entries,"Mood Index");
        lineDataSet.setValueTextColor(0xffffffff);
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
    //添加数据到barchart
    private  void addData2BarChart(BarChart barChart, List<BarEntry>entries){
        BarDataSet barDataSet = new BarDataSet(entries,"Sleep Quality");
        barDataSet.setValueTextColor(0xffffffff);
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.invalidate();
    }

    //绘制linecha
    private void drawLineChart(LineChart lineChart,float i){
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setDrawGridLines(false);

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setAxisMinimum(0.0f);

        lineChart.getAxisLeft().setAxisMaximum(100.0f);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisLeft().setTextColor(0xffffffff);
        lineChart.getXAxis().setTextColor(0xffffffff);
    }
    //绘制barchart
    private void drawBarChart(BarChart barChart,float i){
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setDrawGridLines(false);

        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setAxisMinimum(0.0f);
        barChart.getAxisLeft().setAxisMaximum(100.0f);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisLeft().setTextColor(0xffffffff);
        barChart.getXAxis().setTextColor(0xffffffff);
    }




    //用于进程间通讯
    class MyHandler extends Handler {
        public MyHandler(){}

        public MyHandler(Looper L){
            super(L);
        }

        //重写handleMessage 用于处理message
        @Override
        public void handleMessage(Message msg){
            Bundle b=msg.getData();
            String toast=b.getString("Toast");
            dialog(toast);
        }
    }

    //对话框
    private void dialog(String string){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage(string);
        builder.setPositiveButton("确定",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface,int which){
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    //重写返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            mp.stop();
            return false;
        }
        return super.onKeyDown(keyCode,event);
    }


    //重写onDestroy方法，activity关闭之后关闭服务
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Intent intent=new Intent(MainActivity_alarm.this,CltService.class);
        stopService(intent);
    }
}
