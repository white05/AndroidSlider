# AndroidSlider
video &amp; image slider for Android

### Download
Step 1. Add it in your root build.gradle at the end of repositories:
```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
Step 2. Add the dependency
```
dependencies {
  compile 'com.github.white05:AndroidSlider:v1.0'
}
```

### How to use
#### Add slider items
```
//Type Video 
SliderItemView view01 = new SliderItemView(this);
view01.setScaleType(SliderItemView.ScaleType.CenterInside);
view01.setItem("video file path" , SliderItemView.ITEM_LOCAL_VIDEO);
sliderLayout.addSlider(view01);

//Type Image
SliderItemView view02 = new SliderItemView(this);
view02.setScaleType(SliderItemView.ScaleType.CenterInside);
view02.setItem("image file path" , SliderItemView.ITEM_LOCAL_IMAGE);
sliderLayout.addSlider(view02);
```
#### Set Slider OnClickedListener
```
sliderLayout.setSliderInterface(new SliderLayout.SliderInterface() {
    @Override
    public void onSliderClicked(int idx) {
        Log.d("CLICKED", "*****Clicked " + idx + "********");
    }
});
```
