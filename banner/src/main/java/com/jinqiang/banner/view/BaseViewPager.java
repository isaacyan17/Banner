package com.jinqiang.banner.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author jingqiang
 * @desc TODO
 */
public class BaseViewPager extends ViewPager{
    private boolean scrollable = true;

    public BaseViewPager(Context context) {
        super(context);
    }

    public BaseViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return this.scrollable && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return this.scrollable && super.onInterceptTouchEvent(ev);
    }

    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }
}
