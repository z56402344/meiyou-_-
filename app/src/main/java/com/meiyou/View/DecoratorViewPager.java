package com.meiyou.View;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

//解决Viewpager抢焦点问题
public class DecoratorViewPager extends ViewPager {
    private static final String TAG = DecoratorViewPager.class.getSimpleName();

    public DecoratorViewPager(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public DecoratorViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private float mDx;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
        case MotionEvent.ACTION_DOWN:
            mDx = ev.getRawX();
            break;
        case MotionEvent.ACTION_MOVE:
            float dx = Math.abs(ev.getRawX() - mDx);
//            Logger.i(TAG," ev.getRawX() >>> "+ev.getRawX()+"   ev.getRawY() >>> "+ev.getRawY()+"  mDx>>> "+mDx);
            if (dx> 50){
//              Logger.i(TAG,"Viewpage抢焦点");
                getParent().requestDisallowInterceptTouchEvent(true);//重点
            }
            break;
        case MotionEvent.ACTION_UP:
            break;
        }

        return super.dispatchTouchEvent(ev);
    }

}
