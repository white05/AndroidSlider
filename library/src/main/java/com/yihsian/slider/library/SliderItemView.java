package com.yihsian.slider.library;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;


/**
 * Created by Lee on 2018/2/27.
 */

public class SliderItemView extends RelativeLayout {

    //@(#) PUBLIC INTERFACE
    /**
     * Slider View Interface
     */
    public interface SliderViewInterface {
        void onVideoCompletion();
        void onSliderViewClicked(int idx);
    }

    //@(#) STATIC ATTRIBUTES
    private static final String TAG = SliderItemView.class.getSimpleName();
    public static final int ITEM_LOCAL_IMAGE = 0;
    public static final int ITEM_LOCAL_VIDEO = 1;

    //@(#) PRIVATE ATTRIBUTES
    private ImageView imageView;
    private VideoView videoView;
    private LinearLayout imageEffectView;
    private String videoPath;
    private SliderViewInterface sliderViewInterface;
    private File file;
    private int fileType;
    private int pageIdx;
    private ScaleType mScaleType = ScaleType.Fit;

    //@(#) PUBLIC ENUM
    public enum ScaleType{
        CenterCrop, CenterInside, Fit
    }

    // @(#) PUBLIC CONSTRUCTORS

    /**
     * SliderItemView Constructor
     * @param context Context
     */
    public SliderItemView(Context context) {
        super(context);
        init();
    }

    /**
     * SliderItemView Constructor
     * @param context Context
     * @param attrs AttributeSet
     */
    public SliderItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * SliderItemView Constructor
     * @param context Context
     * @param attrs AttributeSet
     * @param defStyleAttr DefStyleAttr
     */
    public SliderItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_slider_item, this);
        this.imageView = (ImageView) findViewById(R.id.itemImage);
        this.videoView = (VideoView)findViewById(R.id.itemVideo);
        this.imageEffectView = (LinearLayout) findViewById(R.id.imageEffectView);
    }

    // @(#) PUBLIC METHODS

    /**
     * Set Slider Item
     * @param filePath File path
     * @param type File type (image or video)
     */
    public void setItem (String filePath, int type) {
        setItem(new File(filePath), type);
    }

    /**
     * Set Slider Item
     * @param file File
     * @param type File type (image or video)
     */
    public void setItem (File file, int type) {
        this.file = file;
        this.fileType = type;

        switch (type) {
            case ITEM_LOCAL_IMAGE:
                imageView.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.INVISIBLE);

                if(file.exists()){
                    RequestCreator rq = Picasso.with(getContext()).load(file);
                    switch (mScaleType){
                        case Fit:
                            rq.fit();
                            break;
                        case CenterCrop:
                            rq.fit().centerCrop();
                            break;
                        case CenterInside:
                            rq.fit().centerInside();
                            break;
                    }
                    rq.into(imageView);
                }
                break;
            case ITEM_LOCAL_VIDEO:
                imageView.setVisibility(View.INVISIBLE);
                videoView.setVisibility(View.VISIBLE);
                videoPath = file.getAbsolutePath();
                videoView.setVideoURI(Uri.parse(videoPath));
                break;
        }
    }

    /**
     * Set Scale Type
     * @param type
     * @return
     */
    public SliderItemView setScaleType(ScaleType type) {
        mScaleType = type;
        return this;
    }

    // @(#) PROTECTED METHODS
    /**
     * SET PAGE INDEX
     * @param idx index of SliderItemView
     */
    protected void setPageIdx (int idx) {
        this.pageIdx = idx;

        imageEffectView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                sliderViewInterface.onSliderViewClicked(pageIdx);
            }
        });
    }

    /**
     * Set Slider View Interface
     * @param viewInterface SliderViewInterface
     */
    protected void setSliderViewInterface(SliderViewInterface viewInterface) {
        this.sliderViewInterface = viewInterface;
    }

    /**
     * play video item
     */
    protected void playVideo() {
        Log.i(TAG, "playVideo:" + videoPath);
        if (videoView.getVisibility() == View.VISIBLE) {
            videoView.requestFocus();
//            videoView.setVideoURI(Uri.parse(videoPath));
            videoView.start();

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (mediaPlayer != null) {
                        mediaPlayer.reset();
                        videoView.setVideoURI(Uri.parse(videoPath));
                        sliderViewInterface.onVideoCompletion();
                        Log.i(TAG, "Video Complete");
                    }
                }
            });

        }
    }

    /**
     * stop video item
     */
    protected void stopVideo() {
        if (videoPath != null) {
            Log.i(TAG, "stopVideo:" + videoPath);
            videoView.pause();
            videoView.stopPlayback();
            videoView.setVideoURI(Uri.parse(videoPath));
        }
    }

    /**
     * get fileType
     */
    protected int getFileType() {
        return this.fileType;
    }

//    public void onDestroy() {
//        Picasso.with(getContext()).load(null).into(imageView);
//    }

}
