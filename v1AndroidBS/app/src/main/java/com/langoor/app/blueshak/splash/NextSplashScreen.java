package com.langoor.app.blueshak.splash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.langoor.app.blueshak.MainActivity;
import com.divrt.co.R;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.login.LoginActivity;
import com.langoor.app.blueshak.view.CustomSliderTextView;

public class NextSplashScreen  extends Activity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{


	SliderLayout mProductSlider;
	PagerIndicator mPagerIndicator;

    TextView skipText;

	Context context;
	static Activity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		activity = this;
		context = this;
		/*if(GlobalFunctions.getSharedPreferenceString(activity, GlobalVariables.SHARED_PREFERENCE_PERMISSION)!=null){
			if (GlobalFunctions.getSharedPreferenceString(activity,
					GlobalVariables.SHARED_PREFERENCE_PERMISSION).equalsIgnoreCase("true")) {
				Intent i = new Intent(NextSplashScreen.this,
						SplashScreenActivity.class);
				startActivity(i);
				finish();
			}
		}*/
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.next_splash_screen);
		skipText = (TextView) findViewById(R.id.tutorial_skip_text);
		mProductSlider = (SliderLayout) findViewById(R.id.splash_slider);

		mPagerIndicator = (PagerIndicator) findViewById(R.id.splash_custom_indicator);
        setImageOnView(this);

        skipText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				nextScreen();
            }
        });
	}

    public void setImageOnView(Context context){

        mProductSlider.removeAllSliders();
        int[] displaypictures = {R.drawable.splash_1,R.drawable.splash_2, R.drawable.splash_3};
        for(int i=0;i<displaypictures.length;i++){
            CustomSliderTextView textSliderView = new CustomSliderTextView(context);
            // initialize a SliderLayout
            textSliderView
                    .description(1 + "")
                    .image(displaypictures[i])
                    .setScaleType(BaseSliderView.ScaleType.CenterInside)
                    .setOnSliderClickListener(this);
            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", 1 + "");

            mProductSlider.addSlider(textSliderView);
        }

        mProductSlider.setPresetTransformer(SliderLayout.Transformer.ZoomOutSlide);
        mProductSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mProductSlider.stopAutoCycle();
        if(mPagerIndicator!=null){mProductSlider.setCustomIndicator(mPagerIndicator);mProductSlider.setCurrentPosition(0);}
        mProductSlider.setCustomAnimation(new DescriptionAnimation());
        mProductSlider.setDuration(4000);
        mProductSlider.addOnPageChangeListener(this);
    }


    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		/*mProductSlider.startAutoCycle();*/

	}



	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
        mProductSlider.stopAutoCycle();
		super.onStop();
	}

	
	private void nextScreen(){
		GlobalFunctions.setSharedPreferenceInt(context,GlobalVariables.SHARED_PREFERENCE_FIRST_TIME,1);
		Intent i = new Intent(NextSplashScreen.this,MainActivity.class);
		startActivity(i);
		closeThisActivity();
	}

	@Override
	public void onPageScrolled(int i, float v, int i1) {
	/*	if(i==2 || i1==2)
			nextScreen();*/
	}

	@Override
	public void onPageSelected(int i) {

	}

	@Override
	public void onPageScrollStateChanged(int i) {

	}

	@Override
	public void onSliderClick(BaseSliderView baseSliderView) {

	}

	public static void closeThisActivity(){
		if(activity!=null){activity.finish();}
	}
}
