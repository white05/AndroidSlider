package com.yihsian.slider.library;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;

import com.viewpagerindicator.CirclePageIndicator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Lee on 2018/3/2.
 */

public class SliderLayout extends RelativeLayout {

    /**
     * SliderInterface
     */
    public interface SliderInterface {
        void onSliderClicked(int idx);
    }

    // @(#) STATIC ATTRIBUTES
    private static final String TAG = SliderLayout.class.getSimpleName();
    private static final boolean DEBUG = false;

    // @(#) PRIVATE ATTRIBUTES
    private ViewPager sliderPager;
    private CirclePageIndicator indicator;
    private List<SliderItemView> itemViewList;
    private SliderAdapter sliderAdpater;
    private SliderInterface sliderInterface;
    private Timer sliderTimer;
    private Boolean videoIsPlaying = false;
    private Handler handler;
    private SliderItemView currentView;
    private int currentPage = 0;
    private int SCROLLER_DURATION = 300;
    private int PAGER_DURATION = 5000;
    private boolean isAutoCycling = true;
    private int totalPage = 0;


    // @(#) PUBLIC CONSTRUCTORS

    /**
     * SliderLayout Constructor
     * @param context Context
     */
    public SliderLayout(Context context) {
        super(context);
        init();
    }

    /**
     * SliderLayout Constructor
     * @param context Context
     * @param attrs AttributeSet
     */
    public SliderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * SliderLayout Constructor
     * @param context Context
     * @param attrs AttributeSet
     * @param defStyleAttr DefStyleAttr
     */
    public SliderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Initialize SliderLayout
     */
    private void init() {
        //Layout
        inflate(getContext(), R.layout.layout_slider, this);
        this.sliderPager = (ViewPager) findViewById(R.id.viewPager);
        this.indicator = (CirclePageIndicator) findViewById(R.id.indicator);

        setScroller();

        //Parameters
        handler = new Handler();
        itemViewList = new ArrayList<>();
        sliderAdpater = new SliderAdapter();
        sliderPager.setAdapter(sliderAdpater);

        //Indicator
        if (itemViewList.size() == 1) indicator.setVisibility(INVISIBLE);
        indicator.setViewPager(sliderPager);
        final float density = getResources().getDisplayMetrics().density;
        indicator.setRadius(5 * density);

        //Timer
        resumeTimer();

        //Indicator OnPageChangeListener
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                debug("onPageScrolled");
            }

            @Override
            public void onPageSelected(int position) {
                debug("onPageSelected:" + position);
                Log.i(TAG, "currentPage:" + position);
                int lastPage = currentPage;
                currentPage = position;

                if (currentPage != lastPage && videoIsPlaying) {
                    SliderItemView lastItemView = (SliderItemView) sliderAdpater.getView(lastPage);
                    videoIsPlaying = false;
                    lastItemView.stopVideo();
                    resumeTimer();
                }

                currentView = (SliderItemView) sliderAdpater.getView(currentPage);

                if (currentView.getFileType() == SliderItemView.ITEM_LOCAL_VIDEO) {
                    cancelTimer();
                    videoIsPlaying = true;
                    currentView.playVideo();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                debug("onPageScrollStateChanged");
            }
        });
    }

    // @(#) PUBLIC METHODS

    /**
     * Set SliderInterface
     * @param sliderInterface SliderInterface
     */
    public void setSliderInterface(SliderInterface sliderInterface) {
        this.sliderInterface = sliderInterface;
    }

    /**
     * Add Slider
     * @param view SliderItemView
     */
    public void addSlider (SliderItemView view) {

        //Set Slider view interface
        view.setSliderViewInterface(new SliderItemView.SliderViewInterface() {
            @Override
            public void onVideoCompletion() {
                Log.d(TAG, "Video Completion");
                nextPage();
                resumeTimer();
            }

            @Override
            public void onSliderViewClicked(int idx) {
                if (sliderInterface != null) sliderInterface.onSliderClicked(idx);
            }
        });
        totalPage++;
        view.setPageIdx(totalPage);

        if (totalPage == 1 && view.getFileType() == SliderItemView.ITEM_LOCAL_VIDEO) {
            cancelTimer();
            videoIsPlaying = true;
            view.playVideo();
        }

        itemViewList.add(view);
        sliderAdpater.addView(view);
        sliderAdpater.notifyDataSetChanged();

    }

    /**
     * Set slider layout's time duration
     * @param t time in millisecond
     */
    public void setDuration(int t) {
        SCROLLER_DURATION = t;
    }

    /**
     * Set auto cycle or not
     * @param tag is auto cycle or not
     */
    public void setAutoCycle(boolean tag) {
        debug("setAutoCycle: " + tag);
        isAutoCycling = tag;
    }

    /**
     * Set image scale type
     * @param type SliderItemView.ScaleType
     */
    public void setScaleType(SliderItemView.ScaleType type) {
        for(SliderItemView view: itemViewList) {
            view.setScaleType(type);
        }
    }

    /**
     * Remove all slider
     */
    public void removeAllSliders() {
        if(sliderAdpater !=null){
            sliderAdpater.removeAllSliders();
            currentPage = 0;
            totalPage = 0;
        }
    }

    //@(#) PRIVATE METHODS
    private void setScroller(){
        try {
            Interpolator sInterpolator = new AccelerateInterpolator();
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            mScroller.set(sliderPager, new PagerScroller(getContext(),sInterpolator,SCROLLER_DURATION));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void nextPage() {
        if (isAutoCycling) {
            debug("nextPage");
            final Runnable Update = new Runnable() {
                public void run() {
                    currentPage ++;
                    if (currentPage == itemViewList.size()) {
                        currentPage = 0;
                    }
                    sliderPager.setCurrentItem(currentPage, true);
                }
            };
            handler.post(Update);
        }

    }

    private void cancelTimer() {
        if (sliderTimer != null) {
            debug("cancelTimer");
            sliderTimer.cancel();
        }
    }

    private void resumeTimer() {
        debug("resumeTimer");
        sliderTimer = new Timer();
        sliderTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                nextPage();
            }
        }, PAGER_DURATION, PAGER_DURATION);
    }


    /*
     * End delegation
     */

    private void debug(String message) {
        if (DEBUG) {
            Log.d(TAG, message);
        }
    }
}
