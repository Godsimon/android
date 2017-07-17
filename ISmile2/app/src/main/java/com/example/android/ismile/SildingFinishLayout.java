package com.example.android.ismile;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Scroller;

/**
 * Created by simon on 09.05.2017.
 */

public class SildingFinishLayout extends RelativeLayout implements View.OnTouchListener {

    //SildingFinishLayout布局的父布局
    private ViewGroup mParentView;
    //处理滑动逻辑的View
    private View touchView;
    //滑动的最小距离
    private int mTouchSlop;
    //按下的坐标X
    private int downX;
    //按下的坐标Y
    private int downY;
    //临时储存的坐标X
    private int tempX;
    //滑动类
    private Scroller mScroller;
    //SildingFinishLayout的宽度
    private int viewWidth;
    //记录是否在滑动
    private boolean isSilding;

    private OnSildingFinishListener onSildingFinishListener;
    private boolean isFinish;

    public SildingFinishLayout(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }
    public SildingFinishLayout(Context context,AttributeSet attrs,int defStyle){
        super(context,attrs,defStyle);

        mTouchSlop= ViewConfiguration.get(context).getScaledTouchSlop();
        mScroller=new Scroller(context);
    }

    @Override
    protected void onLayout(boolean changed,int l,int t,int r,int b){
        super.onLayout(changed,l,t,r,b);
        if(changed){
            //获取SildingFinishLayout所在布局的父布局
            mParentView=(ViewGroup)this.getParent();
            viewWidth=this.getWidth();
        }
    }

    public void setOnSildingFinishListener(OnSildingFinishListener onSildingFinishListener){
        this.onSildingFinishListener=onSildingFinishListener;
    }

    public void setTouchView(View view){
        this.touchView=view;
        touchView.setOnTouchListener(this);
    }

    public View getTouchView(){
        return touchView;
    }

    private void scrollRight(){
        final int delta=(viewWidth+mParentView.getScrollX());
        mScroller.startScroll(mParentView.getScrollX(),0,-delta+1,0,Math.abs(delta));
        postInvalidate();
    }

    private void scrollOrigin(){
        int delta=mParentView.getScrollX();
        mScroller.startScroll(mParentView.getScrollX(),0,-delta,0,Math.abs(delta));
        postInvalidate();
    }

    private boolean isTouchOnAbsListView(){
        return touchView instanceof ScrollView ? true:false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX=tempX=(int)event.getRawX();
                downY=(int)event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX=(int)event.getRawX();
                int deltaX=tempX-moveX;
                tempX=moveX;
                if(Math.abs(moveX-downX)>mTouchSlop
                        &&Math.abs((int)event.getRawY()-downY)<mTouchSlop){
                    isSilding=true;
                    if(isTouchOnAbsListView()){
                        MotionEvent cancleEvent=MotionEvent.obtain(event);
                        cancleEvent.setAction(MotionEvent.ACTION_CANCEL|
                                (event.getActionIndex()<<MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                        v.onTouchEvent(cancleEvent);
                    }
                }
                if(moveX-downX>=0&&isSilding){
                    mParentView.scrollBy(deltaX,0);
                    if(isTouchOnAbsListView()||isTouchOnAbsListView()){
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                isSilding=false;
                if(mParentView.getScrollX()<=-viewWidth/2){
                    isFinish=true;
                    scrollRight();
                }else{
                    scrollOrigin();
                    isFinish=false;
                }
                break;
        }
        return true;
    }

    @Override
    public void computeScroll(){
        if(mScroller.computeScrollOffset()){
            mParentView.scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            postInvalidate();

            if(mScroller.isFinished()){
                if(onSildingFinishListener!=null&&isFinish){
                    onSildingFinishListener.onSildingFinish();
                }
            }
        }
    }

    public interface OnSildingFinishListener{
        public void onSildingFinish();
    }


}
