package com.jinqiang.mybanner;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jinqiang.adapter.BaseRecyclerAdapter;
import com.jinqiang.adapter.SampleAdapter;
import com.jinqiang.banner.AdBanner;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout mSwipeLayout;
    RecyclerView recyclerView;
    String[] images, titles;
    AdBanner mBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        mSwipeLayout.setOnRefreshListener(this);
//
//        recyclerView= (RecyclerView) findViewById(R.id.list);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        BaseRecyclerAdapter adapter = new BaseRecyclerAdapter<>(new SampleAdapter());
//        /**
//         * 将banner添加到recyclerView头部
//         */
//        View header= LayoutInflater.from(this).inflate(R.layout.banner_layout,null);
//        mBanner = (AdBanner) header.findViewById(R.id.banner);
//        //如果你不需要用xml的属性，那么也可以直接创建对象来实现
////        banner=new Banner(this);
//        mBanner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,400));
//        adapter.addHeader(mBanner);
//        recyclerView.setAdapter(adapter);


        String[] strUrl = getResources().getStringArray(R.array.url);
        mBanner = (AdBanner)findViewById(R.id.banner);
//        mBanner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,300));
        mBanner.isAutoPlay(true);
        mBanner.setImages(Arrays.asList(strUrl)).start();
        mBanner.setOnBannerClickListener(new AdBanner.OnBannerClickListener() {
            @Override
            public void OnBannerClick(int position) {

            }
        });
    }

    @Override
    public void onRefresh() {

    }
}
