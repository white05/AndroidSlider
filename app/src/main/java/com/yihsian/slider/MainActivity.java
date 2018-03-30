package com.yihsian.slider;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.yihsian.slider.library.SliderItemView;
import com.yihsian.slider.library.SliderLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SliderLayout sliderLayout = (SliderLayout) findViewById(R.id.sliderLayout);

        String rootPath = "/storage/sdcard/Android/data/com.yutouch.liondemo2/files/template/";

        sliderLayout.setSliderInterface(new SliderLayout.SliderInterface() {

            @Override
            public void onSliderClicked(int idx) {
                Log.d("CLICKED", "*****Clicked " + idx + "********");
            }
        });

        SliderItemView view01 = new SliderItemView(this);
        view01.setScaleType(SliderItemView.ScaleType.CenterInside);
        view01.setItem(rootPath + "5a9531062e3efHVQdJ.mp4" , SliderItemView.ITEM_LOCAL_VIDEO);
        sliderLayout.addSlider(view01);

        SliderItemView view02 = new SliderItemView(this);
        view02.setScaleType(SliderItemView.ScaleType.CenterInside);
        view02.setItem(rootPath + "5a95061cf33e77neWQ.jpg" , SliderItemView.ITEM_LOCAL_IMAGE);
        sliderLayout.addSlider(view02);

        SliderItemView view03 = new SliderItemView(this);
        view03.setScaleType(SliderItemView.ScaleType.CenterInside);
        view03.setItem(rootPath + "5a8fdc3e2e5f8P5kZZ.jpg" , SliderItemView.ITEM_LOCAL_IMAGE);
        sliderLayout.addSlider(view03);
    }
}
