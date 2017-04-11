package com.langoor.app.blueshak.item;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.divrt.co.R;
import com.langoor.app.blueshak.services.model.ProductModel;
import com.langoor.app.blueshak.services.model.SalesModel;
import com.langoor.app.blueshak.view.CustomSliderTextView;
import java.util.ArrayList;

public class ImageViewActivty extends AppCompatActivity {
    private static final String TAG = "ImageViewActivty";
    static Context context;
    static Activity activity;
    static final String  PRODUCTDETAIL_BUNDLE_KEY_POSITION = "ProductModelWithDetails";
    static final String  PRODUCTDETAIL_BUNDLE_KEY_FLAG = "ProductModelWithFlag";
    ProductModel productModel=null;
    private SliderLayout mProductSlider;

    public static Intent newInstance(Context context, ProductModel product){
        Intent mIntent = new Intent(context, ImageViewActivty.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(PRODUCTDETAIL_BUNDLE_KEY_POSITION, product);
        mIntent.putExtras(bundle);
        return mIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_view_activty);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context=this;
        activity=this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        if(getIntent().hasExtra(PRODUCTDETAIL_BUNDLE_KEY_POSITION))
            productModel=(ProductModel)getIntent().getExtras().getSerializable(PRODUCTDETAIL_BUNDLE_KEY_POSITION);
        mProductSlider = (SliderLayout) findViewById(R.id.product_detail_slider);
        if(productModel!=null)
            setImageOnView();

    }
    private void setImageOnView(){

        mProductSlider.removeAllSliders();
        ArrayList<String> displayImageURL = new ArrayList<String>();
        displayImageURL.clear();
        displayImageURL.addAll(productModel.getImage());

        for(int i=0;i<displayImageURL.size()&&displayImageURL.get(i)!=null;i++){
            CustomSliderTextView textSliderView = new CustomSliderTextView(context);
            // initialize a SliderLayout
            textSliderView
                    .description(1+"")
                    .image(displayImageURL.get(i).toString())
                    .setScaleType(BaseSliderView.ScaleType.FitCenterCrop);
                   /* .setOnSliderClickListener(this);*/
                    /*.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView slider) {
                            Toast.makeText(context,"onSliderClick",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(uri, "image*//*");
                            context.startActivity(intent);
                        }
                    });*/
            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", 1 + "");
            mProductSlider.addSlider(textSliderView);
        }

        mProductSlider.setPresetTransformer(SliderLayout.Transformer.ZoomOutSlide);
        mProductSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
      /*  if(mPagerIndicator!=null){mProductSlider.setCustomIndicator(mPagerIndicator);}*/
        mProductSlider.setCustomAnimation(new DescriptionAnimation());
        mProductSlider.setDuration(4000);
      /*  mProductSlider.addOnPageChangeListener(context);*/
    }

}
