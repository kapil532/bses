package com.langoor.app.blueshak.profile;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.divrt.co.R;
import com.google.android.gms.vision.text.Line;
import com.langoor.app.blueshak.ImageCashing.ImageLoader;
import com.langoor.app.blueshak.MainActivity;
import com.langoor.app.blueshak.PickLocation;
import com.langoor.app.blueshak.bookmarks.BookMarkActivty;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.helper.RoundedImageView;
import com.langoor.app.blueshak.register.TermsConditions;
import com.langoor.app.blueshak.reviews_rating.ReviewsRatings;
import com.langoor.app.blueshak.services.ServerResponseInterface;
import com.langoor.app.blueshak.services.ServicesMethodsManager;
import com.langoor.app.blueshak.services.model.ErrorModel;
import com.langoor.app.blueshak.services.model.FilterModel;
import com.langoor.app.blueshak.services.model.ItemListModel;
import com.langoor.app.blueshak.services.model.OTPCheckerModel;
import com.langoor.app.blueshak.services.model.OTPResendModel;
import com.langoor.app.blueshak.services.model.ProfileDetailsModel;
import com.langoor.app.blueshak.services.model.ReviewsRatingsListModel;
import com.langoor.app.blueshak.services.model.ShopModel;
import com.langoor.app.blueshak.services.model.StatusModel;
import com.langoor.app.blueshak.services.model.UserDetailsModel;
import com.langoor.app.blueshak.settings.SettingsActivty;
import com.langoor.app.blueshak.shop.ShopActivity;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by NanduYoga on 05/05/16.
 */
public class ProfileActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    String TAG = "ProfileActivity";
    private Toolbar toolbar;
    public static FragmentManager mainActivityFM;
    static Context mainContext;
    static Window mainWindow;
    private  MenuItem item;
    static GlobalFunctions globalFunctions = new GlobalFunctions();
    static GlobalVariables globalVariables = new GlobalVariables();
    static Context context;
    private RoundedImageView rounde_image;
    private  LinearLayout help_cetre;
    static Activity activity;
    private ProfileDetailsModel profileDetailsModel=new ProfileDetailsModel();
    private  TextView name,listing_count,likes_count,buying_content_count,sold_count;
    private ImageView rating1,rating2,rating3,rating4,rating5,online_status,email_verified,facebook_verified,phone_number_verified;
    private  LinearLayout profile_content,settings_content,listing_content,sold_content;
    private CardView buying_content_card;
    private  boolean mail_verified=false;
    private  boolean number_verified=false;
    private ImageLoader imageLoader;
    private  String user_id;
    private RatingBar ratingBar1;
    private LinearLayout likes_content,Reviews;
    private  ShopModel shopModel=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile_screen);
        mainContext = this;
        mainActivityFM = getSupportFragmentManager();
        mainWindow = getWindow();
        context=this;
        activity=this;
        imageLoader=new ImageLoader(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ratingBar1=(RatingBar)findViewById(R.id.ratingBar1);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("Profile");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.brandColor)));
        }
        name = (TextView) findViewById(R.id.profile_name);
        rounde_image=(RoundedImageView)findViewById(R.id.rounde_image);
        rating1=(ImageView)findViewById(R.id.rating_image1);
        rating2=(ImageView)findViewById(R.id.rating_image2);
        rating3=(ImageView)findViewById(R.id.rating_image3);
        rating4=(ImageView)findViewById(R.id.rating_image4);
        rating5=(ImageView)findViewById(R.id.rating_image5);
        likes_count=(TextView)findViewById(R.id.likes_count);
        likes_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(activity, ReviewsRatings.class));
            }
        });
        listing_count=(TextView)findViewById(R.id.listing_count);
        buying_content_count=(TextView)findViewById(R.id.buying_content_count);
        sold_count=(TextView)findViewById(R.id.sold_count);
        profile_content=(LinearLayout)findViewById(R.id.profile_content);
        help_cetre=(LinearLayout)findViewById(R.id.help_cetre);

        email_verified=(ImageView)findViewById(R.id.email_verified);
        online_status=(ImageView)findViewById(R.id.online_status);
        facebook_verified=(ImageView)findViewById(R.id.facebook_verified);
        phone_number_verified=(ImageView)findViewById(R.id.phone_number_verified);

        likes_content=(LinearLayout)findViewById(R.id.likes_content);
        likes_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, BookMarkActivty.class));
            }
        });
        Reviews=(LinearLayout)findViewById(R.id.Reviews);
        Reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              proceedToReviews();
            }
        });
        final SwipeRefreshLayout.OnRefreshListener swipeRefreshListner = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
               /* swipeRefreshLayout.setRefreshing(true);*/
                setThisPage();
            }
        };
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */


        help_cetre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= TermsConditions.newInstance(context,GlobalVariables.TYPE_ADD_TEMS);
                startActivity(i);
            }
        });


        profile_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   Intent i= ShopActivity.newInstance(context,profileDetailsModel,null);
                startActivity(i);*/
               /* getShopDetails(context,user_id);*/
                proceedToShop();
            }
        });
        settings_content=(LinearLayout)findViewById(R.id.settings_content);
        settings_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(context, SettingsActivty.class));
            }
        });
        listing_content=(LinearLayout)findViewById(R.id.listing_content);
        listing_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   Intent i= ShopActivity.newInstance(context,profileDetailsModel,null);
                startActivity(i);*/
              /*  getShopDetails(context,user_id);*/
                proceedToShop();
            }
        });
        sold_content=(LinearLayout)findViewById(R.id.sold_content);
        sold_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Intent i= ShopActivity.newInstance(context,profileDetailsModel,null);
                startActivity(i);*/
             /*   getShopDetails(context,user_id);*/
                proceedToShop();
            }
        });
      /*  buying_content_card=(CardView)findViewById(R.id.buying_content_card);
        buying_content_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             *//*   Intent i= ShopActivity.newInstance(context,profileDetailsModel,null);
                startActivity(i);*//*
                getShopDetails(context,user_id);
            }
        });*/
        email_verified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mail_verified)
                    verifyEmail();
            }
        });
        phone_number_verified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!number_verified)
                    verifyPhoneNumber();
            }
        });
    }
    @Override
    public void onRefresh() {
        if(!GlobalFunctions.isNetworkAvailable(context)){
            Snackbar.make(name, "Please check your internet connection", Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {

                        }
                    }).show();
        }else{
            setThisPage();
        }

    }
    @Override
    public void onResume() {
        if(!GlobalFunctions.isNetworkAvailable(context)){
            Snackbar.make(name, "Please check your internet connection", Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {

                        }
                    }).show();

        }else
            setThisPage();
        super.onResume();

    }
    public void setThisPage(){
        getUserDetails(context);
    }
    private void getUserDetails(Context context){
        GlobalFunctions.showProgress(context,"Loading...");
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.getUserDetails(context, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                Log.d(TAG, "onSuccess Response"+arg0.toString());
                profileDetailsModel = (ProfileDetailsModel) arg0;
                setValues(profileDetailsModel);
            }

            @Override
            public void OnFailureFromServer(String msg) {
                GlobalFunctions.hideProgress();

                Log.d(TAG, msg);
            }

            @Override
            public void OnError(String msg) {
                GlobalFunctions.hideProgress();
                Log.d(TAG, msg);
            }
        }, "Profile");

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        this.item = item;
        int id = item.getItemId();
        if (id == R.id.profile_edit) {
            Intent intent=ProfileEditActivity.newInstance(context,profileDetailsModel);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setValues(ProfileDetailsModel model){
        if(model!=null){
            user_id=model.getId();
            name.setText(model.getName());
            listing_count.setText(model.getListings());
            likes_count.setText(model.getLikes());
            buying_content_count.setText(model.getBuying());
            sold_count.setText(model.getSold());
            String avatar=model.getAvatar();
            if(!TextUtils.isEmpty(avatar)){
               /* imageLoader.DisplayImage(avatar,rounde_image);*/
                Picasso.with(activity)
                        .load(avatar)
                        .placeholder(R.drawable.placeholder_background)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .fit().centerCrop()
                        .into(rounde_image);
            }else{
                rounde_image.setImageResource(R.drawable.placeholder_background);
            }
            if(!TextUtils.isEmpty(model.getFb_id())){facebook_verified.setImageResource(R.drawable.facebook_verified);}else{facebook_verified.setImageResource(R.drawable.facebook_not_verified);}
            if(model.is_active()){online_status.setVisibility(View.VISIBLE);}
            if(model.is_email_verify()){mail_verified=true;email_verified.setImageResource(R.drawable.email);}else{email_verified.setImageResource(R.drawable.phone_gray);}
            if(model.is_otp_verified()){number_verified=true;phone_number_verified.setImageResource(R.drawable.phone_number_verified);} else{phone_number_verified.setImageResource(R.drawable.email_gray);}
            /*if(model.getReviews()!=null)
                setReviewsRatings(model.getReviews());*/
            String rating=profileDetailsModel.getReviews();
            if(rating!=null&&!TextUtils.isEmpty(rating)&&!rating.equalsIgnoreCase("null"))
                ratingBar1.setRating(Float.parseFloat(rating));

            GlobalFunctions.setSharedPreferenceString(context, GlobalVariables.SHARED_PREFERENCE_FULLNAME, model.getName());
            GlobalFunctions.setSharedPreferenceString(context, GlobalVariables.SHARED_PREFERENCE_PHONE, model.getPhone());
            GlobalFunctions.setSharedPreferenceString(context, GlobalVariables.SHARED_PREFERENCE_ADDRESS, model.getAddress());
            GlobalFunctions.setSharedPreferenceString(context, GlobalVariables.SHARED_PREFERENCE_AVATAR, model.getAvatar());
            getShopDetails(context,user_id);
        }
    }
    public void verifyEmail(){
        GlobalFunctions.showProgress(context, "Verifying Email...");
        ServicesMethodsManager serverManager = new ServicesMethodsManager();
        serverManager.verifyEmail(context, new ProfileDetailsModel(),new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                StatusModel statusModel=(StatusModel)arg0;
                Log.d(TAG, "onSuccess Response"+statusModel.toString());
                validateEmail(statusModel);
            }

            @Override
            public void OnFailureFromServer(String msg) {
                GlobalFunctions.hideProgress();
                Toast.makeText(activity,msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnError(String msg) {
                GlobalFunctions.hideProgress();
                Toast.makeText(activity,msg, Toast.LENGTH_SHORT).show();
            }
        },"Email verification");
    }
    public void verifyPhoneNumber(){
        GlobalFunctions.showProgress(context, "Validating OTP...");
        ServicesMethodsManager serverManager = new ServicesMethodsManager();
        OTPResendModel resendModel = new OTPResendModel(GlobalFunctions.getSharedPreferenceString(getApplication(), GlobalVariables.SHARED_PREFERENCE_PHONE));
        serverManager.resendOTP(context, resendModel, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                validateOTPResend((StatusModel) arg0);
            }

            @Override
            public void OnFailureFromServer(String msg) {
                GlobalFunctions.hideProgress();
                Toast.makeText(activity,msg,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnError(String msg) {
                GlobalFunctions.hideProgress();
                Toast.makeText(activity,msg, Toast.LENGTH_SHORT).show();
            }
        },"OTP verification");
    }
    private void validateOTPResend(StatusModel statusModel){
        if(statusModel.isStatus()){
            Toast.makeText(getApplicationContext(),"OTP Send Successfully", Toast.LENGTH_SHORT).show();
        }else{Toast.makeText(getApplicationContext(),"Already Authenticated", Toast.LENGTH_SHORT).show();}
    }

    private void validateEmail(StatusModel statusModel){
        if(statusModel.getMessage().equalsIgnoreCase("Verification link is sent to respective E-mail")){
            mail_verified=true;
        }else{
            Toast.makeText(getApplicationContext(),"Error on verifying Email", Toast.LENGTH_SHORT).show();}
    }
    public static void logoutAndMove(Context context){
        GlobalFunctions.removeSharedPreferenceKey(context,GlobalVariables.SHARED_PREFERENCE_TOKEN);
        if(activity!=null)
            activity.finish();
    }
    public void getShopDetails(Context context,String selle_user_id){
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.getShopDetails(context,selle_user_id,new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                Log.d(TAG, "onSuccess Response"+arg0.toString());
                shopModel = (ShopModel) arg0;
                Log.d(TAG,"####ShopModel#####"+shopModel.toString());
                Log.d(TAG,"####Profile model#####"+shopModel.getProfileDetailsModel().toString());
                Log.d(TAG,"####Item List#####"+shopModel.getItem_list());
             /*   proceedToShop();*/

            }

            @Override
            public void OnFailureFromServer(String msg) {
                Log.d(TAG, msg);
            }

            @Override
            public void OnError(String msg) {

                Log.d(TAG, msg);
            }
        }, "Shop Model");

    }
    public void proceedToShop() {
        if(shopModel!=null){
            Intent i = ShopActivity.newInstance(context,user_id);
            startActivity(i);
        }else
            getShopDetails(context,user_id);


    }
    public void proceedToReviews() {
        Intent intent= ReviewsRatings.newInstance(activity,null,null,profileDetailsModel,null);
        startActivity(intent);
    }

}
