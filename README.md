##Android广告图控件


####介绍
作用于Android的广告轮播控件，实现本地或网络图片播放和循环播放，线程控制避免引起过多线程不能及时回收的问题。目前可以支持的功能有：

* 开启或关闭自动轮播功能(默认开启)
* 设置轮播延迟时间
* 自由开始或结束轮播
* 设置指示器或标题的位置
* 图片点击监听
* 多种banner样式

欢迎fork。

------


####使用步骤
* 下载并添加至本地工程,引用lib
```
compile project(':banner')
```

布局文件
```
<?xml version="1.0" encoding="utf-8"?>
<com.jinqiang.banner.AdBanner xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/banner"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    >

</com.jinqiang.banner.AdBanner>
```

项目中，如果XML布局在本文件中,直接设置数据并启动
```
        String[] strUrl = getResources().getStringArray(R.array.url);
        mBanner = (AdBanner)findViewById(R.id.banner);
        //设置banner的宽高或者在布局文件中定义。
        //mBanner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,300));
        //默认自动轮播
        //mBanner.isAutoPlay(true);
        mBanner.setImages(Arrays.asList(strUrl)).start();
```

如果加在ListView或RecyclerViewL中，请以View的形式注入
```
View header= LayoutInflater.from(this).inflate(R.layout.banner_layout,null);
```

同时注意你的AndroidManifest.xml中是否添加了权限
```
<!-- 图片从网络获取 -->
<uses-permission android:name="android.permission.INTERNET" />
```

* 设置样式
```
        //设置样式
        banner.setBannerStyle(Config.CIRCLE_INDICATOR_TITLE_INSIDE);
        //设置图片集合
        banner.setImages(Arrays.asList(images));
        //设置标题集合（当banner样式有显示title时）
        banner.setBannerTitles(Arrays.asList(titles));
        //设置自动轮播，默认为true
        banner.isAutoScroll(true);
        //设置轮播时间
        banner.setDelayTime(3000);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
```

####依赖框架
* Glide
```
 compile 'com.github.bumptech.glide:glide:3.7.0'
```


---

####TODO
* 图片加载方式从控件中抽取出来，由开发者自定义
* 增加其他样式

