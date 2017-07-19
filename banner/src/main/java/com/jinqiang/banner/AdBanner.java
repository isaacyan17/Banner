package com.jinqiang.banner;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jinqiang.banner.view.BaseViewPager;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


public class AdBanner extends FrameLayout implements ViewPager.OnPageChangeListener {

    private int mIndicatorMargin = Config.PADDING_SIZE;
    private int mIndicatorWidth = Config.INDICATOR_SIZE;
    private int mIndicatorHeight = Config.INDICATOR_SIZE;
    private int mIndicatorSelectedResId = R.drawable.gray_radius;
    private int mIndicatorUnselectedResId = R.drawable.white_radius;

    private int scaleType = 0;
    private BannerScroller mScroller;
    private int bannerStyle = Config.CIRCLE_INDICATOR;
    private boolean isAutoScroll = true; //是否自动滚动
    private int delayTime = Config.TIME; //延迟时间
    private int gravity = -1; //底部指示器位置
    private List<String> titles; //标题
    private List<?> imageUrls; //图片
    private List<View> imageViews;
    private List<ImageView> indicatorImages;
    private Context mContext;
    private int count = 0; //图片总数
    private int currentItem; //当前选中
    private int lastPosition = 1;
    private BaseViewPager mViewPager;
    private TextView bannerTitle, numIndicatorInside, numIndicator;
    private LinearLayout indicator, indicatorInside, titleView;
    private OnBannerClickListener listener;
    private WeakHandler handler = new WeakHandler();//弱引用handler
    private BannerPagerAdapter adapter;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;

    public AdBanner(Context context) {
        this(context,null);
//        this.mContext = context;
//        titles = new ArrayList<>();
//        imageUrls = new ArrayList<>();
//        imageViews = new ArrayList<>();
//        indicatorImages = new ArrayList<>();
//        initView(context);
    }

    public AdBanner(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AdBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        titles = new ArrayList<>();
        imageUrls = new ArrayList<>();
        imageViews = new ArrayList<>();
        indicatorImages = new ArrayList<>();
        initView(context);
    }

    private void initView(Context context) {
        imageViews.clear();
        View view = LayoutInflater.from(context).inflate(R.layout.banner, this, true);
        mViewPager = (BaseViewPager) view.findViewById(R.id.viewpager);
        titleView = (LinearLayout) view.findViewById(R.id.titleView);
        indicator = (LinearLayout) view.findViewById(R.id.indicator);
        indicatorInside = (LinearLayout) view.findViewById(R.id.indicatorInside);
        bannerTitle = (TextView) view.findViewById(R.id.bannerTitle);
        numIndicator = (TextView) view.findViewById(R.id.numIndicator);
        numIndicatorInside = (TextView) view.findViewById(R.id.numIndicatorInside);
//        handleTypedArray(context, attrs);
        initViewPagerScroll();
    }

    private void initViewPagerScroll() {
        try {
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            mScroller = new BannerScroller(mViewPager.getContext());
            mField.set(mViewPager, mScroller);
        } catch (Exception e) {
            Log.e("yjq",e.getMessage());
        }
    }
    public AdBanner isAutoPlay(boolean isAutoScroll){
        this.isAutoScroll = isAutoScroll;
        return this;
    }

    public AdBanner setDelayTime(int delayTime) {
        this.delayTime = delayTime;
        return this;
    }

    public AdBanner setIndicatorGravity(int type) {
        switch (type) {
            case Config.LEFT:
                this.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                break;
            case Config.CENTER:
                this.gravity = Gravity.CENTER;
                break;
            case Config.RIGHT:
                this.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                break;
        }
        return this;
    }
    /**
     * Set the number of pages that should be retained to either side of the
     * current page in the view hierarchy in an idle state. Pages beyond this
     * limit will be recreated from the adapter when needed.
     *
     * @param limit How many pages will be kept offscreen in an idle state.
     * @return Banner
     */
    public AdBanner setOffscreenPageLimit(int limit){
        if (mViewPager!=null){
            mViewPager.setOffscreenPageLimit(limit);
        }
        return this;
    }

    public AdBanner setBannerTitles(List<String> titles) {
        this.titles = titles;
        return this;
    }

    public AdBanner setImages(List<?> imagesUrl) {
        this.imageUrls=imagesUrl;
        return this;
    }

    public AdBanner setBannerStyle(int bannerStyle) {
        this.bannerStyle = bannerStyle;
        return this;
    }

    public AdBanner start(){
        setBannerStyleUI();
        setImageList(imageUrls);
        setData();
        return this;
    }

    private void setBannerStyleUI() {
        switch (bannerStyle) {
            case Config.CIRCLE_INDICATOR:
                indicator.setVisibility(View.VISIBLE);
                break;
            case Config.NUM_INDICATOR:
                numIndicator.setVisibility(View.VISIBLE);
                break;
            case Config.NUM_INDICATOR_TITLE:
                numIndicatorInside.setVisibility(View.VISIBLE);
//                setTitleStyleUI();
                break;
            case Config.CIRCLE_INDICATOR_TITLE:
                indicator.setVisibility(View.VISIBLE);
//                setTitleStyleUI();
                break;
            case Config.CIRCLE_INDICATOR_TITLE_INSIDE:
                indicatorInside.setVisibility(VISIBLE);
//                setTitleStyleUI();
                break;
        }
    }

    private void initImages() {
        imageViews.clear();
        if (bannerStyle == Config.CIRCLE_INDICATOR ||
                bannerStyle == Config.CIRCLE_INDICATOR_TITLE ||
                bannerStyle == Config.CIRCLE_INDICATOR_TITLE_INSIDE) {
            createIndicator();
        } else if (bannerStyle == Config.NUM_INDICATOR_TITLE) {
            numIndicatorInside.setText("1/" + count);
        } else if (bannerStyle == Config.NUM_INDICATOR) {
            numIndicator.setText("1/" + count);
        }
    }

    private void setImageList(List<?> imagesUrl) {
        if (imagesUrl == null || imagesUrl.size() <= 0) {
            Log.e("yjq", "Please set the images data.");
            return;
        }
        count = imagesUrl.size();
        initImages();
        for (int i = 0; i <= count + 1; i++) {
            ImageView imageView = null;
//            if (imageLoader != null) {
//                imageView = imageLoader.createImageView(mContext);
//            }
            if (imageView == null) {
                imageView = new ImageView(mContext);
            }
            if (imageView instanceof ImageView) {
                if (scaleType==0) {
                    ((ImageView) imageView).setScaleType(ImageView.ScaleType.FIT_XY);
                } else {
                    ((ImageView) imageView).setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            }
            Object url = null;
            if (i == 0) {
                url = imagesUrl.get(count - 1);
            } else if (i == count + 1) {
                url = imagesUrl.get(0);
            } else {
                url = imagesUrl.get(i - 1);
            }
            imageViews.add(imageView);
            //  TODO display图片

            Glide
                    .with(mContext)
                    .load((String)url)
                    .centerCrop()
                    .into(imageView);


        }
//        setData();
    }

    private void createIndicator() {
        indicatorImages.clear();
        indicator.removeAllViews();
        indicatorInside.removeAllViews();
        for (int i = 0; i < count; i++) {
            ImageView imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mIndicatorWidth, mIndicatorHeight);
            params.leftMargin = mIndicatorMargin;
            params.rightMargin = mIndicatorMargin;
            if (i == 0) {
                imageView.setImageResource(mIndicatorSelectedResId);
            } else {
                imageView.setImageResource(mIndicatorUnselectedResId);
            }
            indicatorImages.add(imageView);
            if (bannerStyle == Config.CIRCLE_INDICATOR ||
                    bannerStyle == Config.CIRCLE_INDICATOR_TITLE)
                indicator.addView(imageView, params);
            else if (bannerStyle == Config.CIRCLE_INDICATOR_TITLE_INSIDE)
                indicatorInside.addView(imageView, params);
        }
    }

    private void setData() {
        currentItem = 1;
        if (adapter == null) {
            adapter = new BannerPagerAdapter();
            mViewPager.addOnPageChangeListener(this);
        }

            mViewPager.setAdapter(adapter);
            mViewPager.setFocusable(true);
            mViewPager.setCurrentItem(1);
//            mViewPager.addOnPageChangeListener(this);
            if (gravity != -1)
                indicator.setGravity(gravity);
            if (count <= 1)
                mViewPager.setScrollable(false);
            else
                mViewPager.setScrollable(true);
        if (isAutoScroll)
            startAutoScroll();
    }

    /**
     * 更新banner数据
     * @param imageUrls
     */
    public void update(List<?> imageUrls) {
        this.imageUrls.clear();
        this.imageUrls = imageUrls;
        this.count = this.imageUrls.size();
        start();
    }

    public void startAutoScroll() {
        handler.removeCallbacks(task);
        handler.postDelayed(task, delayTime);
    }
    public void stopAutoScroll() {
        handler.removeCallbacks(task);
    }

    private final Runnable task = new Runnable() {

        @Override
        public void run() {
            if (count > 1 && isAutoScroll) {
                currentItem = currentItem % (count + 1) + 1;
                if (currentItem == 1) {
                    mViewPager.setCurrentItem(currentItem, false);
                    handler.postDelayed(task, delayTime);
                }else if(currentItem==count+1){
                    mViewPager.setCurrentItem(currentItem);
                    handler.postDelayed(task, 500);
                }else {
                    mViewPager.setCurrentItem(currentItem);
                    handler.postDelayed(task, delayTime);
                }
            }
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isAutoScroll) {
            int action = ev.getAction();
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL
                    || action == MotionEvent.ACTION_OUTSIDE) {
                startAutoScroll();
            } else if (action == MotionEvent.ACTION_DOWN) {
                stopAutoScroll();
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    /**
     * pagerAdapter
     */
    class BannerPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            container.addView(imageViews.get(position));
            View view = imageViews.get(position);
            if (listener!=null) {
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.OnBannerClick(position);
                    }
                });
            }
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //container.removeView(imageViews.get(position));
            //如果做update操作，使用imageViews.get(position)会出现越界异常
            container.removeView((View) object);
        }

    }

    /**
     * banner点击监听
     */
    public interface OnBannerClickListener {
        public void OnBannerClick(int position);
    }

    public void setOnBannerClickListener(OnBannerClickListener listener) {
        this.listener=listener;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(position);
        }
        if (bannerStyle == Config.CIRCLE_INDICATOR ||
                bannerStyle == Config.CIRCLE_INDICATOR_TITLE ||
                bannerStyle == Config.CIRCLE_INDICATOR_TITLE_INSIDE) {
            indicatorImages.get((lastPosition - 1 + count) % count).setImageResource(mIndicatorUnselectedResId);
            indicatorImages.get((position - 1 + count) % count).setImageResource(mIndicatorSelectedResId);
            lastPosition = position;
        }
        if (position == 0) position = 1;
        int titleSize=titles.size();
        switch (bannerStyle) {
            case Config.CIRCLE_INDICATOR:
                break;
            case Config.NUM_INDICATOR:
                if (position > count) position = count;
                numIndicator.setText(position + "/" + count);
                break;
            case Config.NUM_INDICATOR_TITLE:
                if (position > count) position = count;
                numIndicatorInside.setText(position + "/" + count);
                if (titles != null && titleSize > 0) {
                    if (position > titleSize) position = titleSize;
                    bannerTitle.setText(titles.get(position - 1));
                }
                break;
            case Config.CIRCLE_INDICATOR_TITLE:
                if (titles != null && titleSize> 0) {
                    if (position > titleSize) position = titleSize;
                    bannerTitle.setText(titles.get(position - 1));
                }
                break;
            case Config.CIRCLE_INDICATOR_TITLE_INSIDE:
                if (titles != null && titleSize > 0) {
                    if (position > titleSize) position = titleSize;
                    bannerTitle.setText(titles.get(position - 1));
                }
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }
        currentItem = mViewPager.getCurrentItem();
        switch (state) {
            case 0://No operation
                if (currentItem == 0) {
                    mViewPager.setCurrentItem(count, false);
                } else if (currentItem == count + 1) {
                    mViewPager.setCurrentItem(1, false);
                }
                break;
            case 1://start Sliding
                if (currentItem == count + 1) {
                    mViewPager.setCurrentItem(1, false);
                }else if(currentItem == 0){
                    mViewPager.setCurrentItem(count, false);
                }
                break;
            case 2://end Sliding
                break;
        }
    }
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }

}
