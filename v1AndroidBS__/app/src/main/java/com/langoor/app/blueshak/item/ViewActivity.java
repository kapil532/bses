package com.langoor.app.blueshak.item;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.daimajia.slider.library.SliderLayout;
import com.divrt.co.R;
import com.langoor.app.blueshak.services.model.ImageModel;
import com.langoor.app.blueshak.services.model.ProductModel;
import com.squareup.picasso.Picasso;

public class ViewActivity extends AppCompatActivity {
    private static final String TAG = "ImageViewActivty";
    static Context context;
    static Activity activity;
    static final String  PRODUCTDETAIL_BUNDLE_KEY_POSITION = "ProductModelWithDetails";
    static final String  PRODUCTDETAIL_BUNDLE_KEY_FLAG = "ProductModelWithFlag";
    ImageModel imageModel=null;
    private SliderLayout mProductSlider;
    private ImageView image;

    public static Intent newInstance(Context context, ImageModel product){
        Intent mIntent = new Intent(context, ViewActivity.class);
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
        setContentView(R.layout.activity_view);
        image=(ImageView)findViewById(R.id.image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context=this;
        activity=this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        if(getIntent().hasExtra(PRODUCTDETAIL_BUNDLE_KEY_POSITION))
            imageModel=(ImageModel)getIntent().getExtras().getSerializable(PRODUCTDETAIL_BUNDLE_KEY_POSITION);
        mProductSlider = (SliderLayout) findViewById(R.id.product_detail_slider);
        if(imageModel!=null)
            setImageOnView();

    }
    private void setImageOnView(){
        String image_=imageModel.getImage();
        if(!TextUtils.isEmpty(image_)&&image_!=null){
            Picasso.with(context)
                    .load(image_)
                    .placeholder(R.drawable.placeholder_background)
                    .into(image);
        }
    }
}
