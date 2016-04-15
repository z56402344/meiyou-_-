package com.meiyou;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.meiyou.View.DecoratorViewPager;

/**
 * Created by duguang on 16-4-15.
 */
public class HeadView implements AbsListView.OnScrollListener, Handler.Callback{

    private Context mCxt;
    private ListView mLv;
    private View mHead;
    private RelativeLayout mRlGoal;
    private DecoratorViewPager mVp;
    private View mBottom;

    public void initViewPager(ListView lv){
        if (lv == null)return;
        mLv = lv;
        mCxt = lv.getContext();

        mHead = View.inflate(mCxt,R.layout.item_head,null);
        mRlGoal  = (RelativeLayout) mHead.findViewById(R.id.mRlGoal);
        mVp = (DecoratorViewPager) mHead.findViewById(R.id.mVp);

        mLv.addHeaderView(mHead);
        FrameLayout llBottom = new FrameLayout(mCxt);
        ViewGroup.LayoutParams lp = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        llBottom.setLayoutParams(lp);
        mBottom = new View(mCxt);
        lp = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,30);
        mBottom.setLayoutParams(lp);
        llBottom.addView(mBottom);
        mLv.addFooterView(llBottom);

        mLv.setOnScrollListener(this);
    }

    private Handler mHdler = new Handler(Looper.getMainLooper(), this);

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
        case IA_ScrollGoal: {
            int ms = msg.arg1 > 0 ? msg.arg1 : -msg.arg1;
            ms = 200 * ms / 50;
            mLv.smoothScrollBy(msg.arg1, ms);
            if (msg.arg2>0) { // 第一次动画
                mTopLine = msg.arg2;
            }
            break;
        }
        case IA_ScrollGoalInit: {
            mLv.smoothScrollBy(msg.arg1, 20);
            mHdler.sendMessageDelayed(Message.obtain(mHdler, IA_ScrollGoal, -msg.arg1, msg.arg2), 1000);
            break; }
        }
        return true;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int i) {
        switch (i) {
        case SCROLL_STATE_IDLE:
            checkHead(view);
            break;
        case SCROLL_STATE_TOUCH_SCROLL:
        case SCROLL_STATE_FLING:
            keepHead();
            mHdler.removeMessages(IA_ScrollGoal);
            break;
        }
    }

    private int mLastCnt = 0;
    private int mLastSpace = 0;
    private int mTopLine = 0; // 10dp0
    private static final int IA_ScrollGoal = 1001;
    private static final int IA_ScrollGoalInit = 1002;

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mLastCnt!=totalItemCount) {
            mLastCnt = totalItemCount;
            if (totalItemCount>0) {
                if (mTopLine==0) {
                    mTopLine = -1;
                    float density = view.getResources().getDisplayMetrics().density;
//				mTopLine = (int) (density * 10);
//				int ms = (int) ((175 - 10) * density);
                    // 第一次动画 有item时才启动 把偏移+topline都算好
                    mHdler.sendMessageDelayed(Message.obtain(mHdler, IA_ScrollGoalInit, (int) ((175 - 10) * density), (int) (density * 10)),10);
//				ms = 200 * ms / 50 + 200;
                } else if (mTopLine>0) {
                    checkHead(view);
                }
            }
            // 10dp
            float density = view.getResources().getDisplayMetrics().density;
            int space = 0;
            if (totalItemCount==0||firstVisibleItem>0) {
            } else if (totalItemCount<3) {
                // 只有目标一个item 整个list高度
                space = view.getHeight()-(int)(density*10);
                // 此情况不会出现 显示添加课程的提示了
            } else {
                // 至少有1个课程 且所有item项不够屏幕高
                // 不足一屏
                firstVisibleItem = view.getHeight() - (int)(density*10);
                View child = view.getChildAt(1);
                // 课程数=totalItemCount-(1个头布局+1个底布局)
                totalItemCount = (child!=null)?child.getHeight()*(totalItemCount-2):0;
                space = firstVisibleItem-totalItemCount;
                if (space<0) {
                    space = 0;
                }
            }
            if (space!=mLastSpace) {
                mLastSpace = space;
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)mBottom.getLayoutParams();
                lp.height = space;
                mBottom.setLayoutParams(lp);
                mBottom.requestLayout();
                // 使用view来做 padding部分不能点住滑动
//				view.setPadding(view.getPaddingLeft(),view.getPaddingTop(),view.getPaddingRight(),space);
//				view.requestLayout();
//				view.invalidate();
            }
        } else {
        }
        if (totalItemCount>0)
            keepHead();
    }

    private boolean checkHead(AbsListView view) {
        int t = view.getFirstVisiblePosition();
        if (t != 0) return false;
        View v = view.getChildAt(0);
        if (v == null) return false;
        t = v.getTop();
        if (t == 0) {
            keepHead();
            return false;
        }
        int b = v.getBottom() - mTopLine;
        if (b <= 0) return false;
        // 露出多 跳回0否则跳到1
        mHdler.removeMessages(IA_ScrollGoal);
        mHdler.sendMessageDelayed(Message.obtain(mHdler, IA_ScrollGoal, (t + b > 0 ? t : b), 0), 100);
        return true;
    }

    private void keepHead() {
        if (mHead == null) return;
        int t = mHead.getTop();
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mRlGoal.getLayoutParams();
        if (lp.topMargin == -t) return;
        lp.topMargin = -t;
        mRlGoal.setLayoutParams(lp);
    }
}
