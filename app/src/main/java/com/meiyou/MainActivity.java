package com.meiyou;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.view.menu.BaseMenuPresenter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 仿美柚日历效果(学习)
 */
public class MainActivity extends AppCompatActivity {

    private ListView mLv;
    private HeadView mHead = new HeadView();
    private MainAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        mLv = (ListView) findViewById(R.id.mLv);
    }

    private void initData() {
        mAdapter = new MainAdapter();
        mLv.setAdapter(mAdapter);
        mHead.initViewPager(mLv);
    }


    private class MainAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View v, ViewGroup viewGroup) {
            ViewHolder holder = new ViewHolder();
            if (v  == null){
                holder = new ViewHolder();
                v = View.inflate(viewGroup.getContext(),R.layout.item_data,null);
                v.setTag(holder);
                holder.init(v);
            }else{
                holder = (ViewHolder) v.getTag();
            }
            holder.updata(i);
            return v;
        }

        private class ViewHolder{
            private TextView mTvIcon;

            public void init(View v){
                mTvIcon = (TextView) v.findViewById(R.id.mTvIcon);
            }

            public void updata(int i){
                mTvIcon.setText(i+"条目");
            }

        }
    }
}
