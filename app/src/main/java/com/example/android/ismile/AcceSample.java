package com.example.android.ismile;

import static java.lang.Math.sqrt;

/**
 * Created by simon on 22.05.2017.
 */

public class AcceSample {
    private float [] sampleData;

    public AcceSample(float [] data){
        sampleData = data;
    }

    public float min(){
        float min=sampleData[0];
        for (int i=0;i<sampleData.length;i++)
            if(min>sampleData[i])min=sampleData[i];
        return min;
    }
    public float max(){
        float max=sampleData[0];
        for (int i=0;i<sampleData.length;i++)
            if(max<sampleData[i])max=sampleData[i];
        return max;
    }
    public float ave(){
        float sum=0;
        for (int i=0;i<sampleData.length;i++)
            sum+=sampleData[i];
        return sum/sampleData.length;
    }
    public float rms(){
        float sum=0;
        for(int i=0;i<sampleData.length;i++)
            sum+=sampleData[i]*sampleData[i];
        return (float) sqrt(sum);
    }
    public boolean isWake(){
        if(ave() <= 0.014984){
            if(max() <= 0.075303){
                if(min() <= 0.004489)return false;
                else return true;
            }else {
                if(rms() <= 0.019588)return false;
                else return true;
            }
        }else {
            return true;
        }
    }
}
