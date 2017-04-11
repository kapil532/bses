package com.langoor.app.blueshak.item;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.divrt.co.R;
import com.langoor.app.blueshak.ImageCashing.ImageLoader;
import com.langoor.app.blueshak.Messaging.activity.ChatActivity;
import com.langoor.app.blueshak.Messaging.data.Message;
import com.langoor.app.blueshak.Messaging.data.User;
import com.langoor.app.blueshak.Messaging.manager.MessageManager;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.services.ServerResponseInterface;
import com.langoor.app.blueshak.services.ServicesMethodsManager;
import com.langoor.app.blueshak.services.model.ErrorModel;
import com.langoor.app.blueshak.services.model.MakeOfferModel;
import com.langoor.app.blueshak.services.model.ProductModel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Date;

public class MakeOffersActivty extends AppCompatActivity {
    private static final String TAG = "ImageViewActivty";
    private static Context context;
    private static Activity activity;
    private TextView offer_text;
    private ImageView product_image;
    private EditText offer_price;
    private MakeOfferModel makeOfferModel=new MakeOfferModel();
    private Button make_offer;
    private ImageLoader imageLoader;
    private static final String  PRODUCTDETAIL_BUNDLE_KEY_POSITION = "ProductModelWithDetails";
    private static final String  PRODUCTDETAIL_BUNDLE_KEY_FLAG = "ProductModelWithFlag";
    private ProductModel productModel=null;
    private String seller_phone_number=null;
    private String seller_name=null;
    private String seller_profile_image=null;
    private String item_image=null;
    private String seller_user_id=null,item_name=null,item_price=null;
    private User user=new User();
    public static Intent newInstance(Context context, ProductModel product){
        Intent mIntent = new Intent(context,MakeOffersActivty.class);
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
        setContentView(R.layout.activity_make_offers_activty);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context=this;
        activity=this;
        imageLoader=new ImageLoader(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        offer_price=(EditText)findViewById(R.id.offer_price);
        make_offer=(Button)findViewById(R.id.make_offer_);
        offer_text=(TextView)findViewById(R.id.offer_text);
        product_image=(ImageView)findViewById(R.id.product_image);
        productModel=(ProductModel)getIntent().getExtras().getSerializable(PRODUCTDETAIL_BUNDLE_KEY_POSITION);
        if(productModel!=null)
            setThisPage();

        make_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(offer_price.getText().toString()))
                    if(productModel !=null){
                        makeOfferModel.setProduct_id(productModel.getId());
                        makeOfferModel.setOffer(offer_price.getText().toString());
                        makeOffer(makeOfferModel);


                    }
                else
                    Toast.makeText(context,"Please Enter your offer",Toast.LENGTH_LONG).show();
            }
        });
        make_offer.setOnEditorActionListener(new DoneOnEditorActionListener());
    }

    public void setThisPage(){
        String image_url="";
        if(productModel.getImage().size()>0)
            image_url=productModel.getImage().get(0);

        item_name =productModel.getName();
        item_price=productModel.getSalePrice();
        seller_name=productModel.getSellerName();
        seller_phone_number=productModel.getSeller_phone();
        seller_profile_image=productModel.getSeller_image();
        seller_user_id=productModel.getSeller_id();
        user.setName(seller_name);
        user.setBs_id(seller_user_id);
        user.setNumber(seller_phone_number);
        user.setProfileImageUrl(seller_profile_image);
        user.setProduct_name(item_name);
        user.setPrice(item_price);
        user.setProduct_url(image_url);
        user.setProduct_id(productModel.getId());
        user.setActive_tab(GlobalVariables.TYPE_BUYER_TAB);
        item_image=image_url;
        offer_text.setText("Enter your offer for "+productModel.getName());
        if(!TextUtils.isEmpty(image_url)){
            Picasso.with(context).load(image_url).placeholder(R.drawable.placeholder_background).error(R.drawable.placeholder_background).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                   product_image.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    product_image.setImageDrawable(errorDrawable);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    product_image.setImageDrawable(placeHolderDrawable);
                }
            });
        }

        }
    private void makeOffer(final MakeOfferModel makeOfferModel){
        GlobalFunctions.showProgress(context, "Making offer");
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.makeOffer(context, makeOfferModel, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                Log.d(TAG, "onSuccess Response");
                if(arg0 instanceof MakeOfferModel){
                    MakeOfferModel makeOfferModel1 = (MakeOfferModel) arg0;
                    Log.d(TAG,"##########OnSuccessFromServer##############"+makeOfferModel1.getActive_tab()+makeOfferModel1.getContact_avatar()+makeOfferModel1.getConversation_id());
                    user.setConversation_id(makeOfferModel1.getConversation_id());
                    String offerText=makeOfferModel1.getContact_name()+" has made an offer $"+makeOfferModel.getOffer()+" for "+productModel.getName();
                    pushNotification(offerText);
                    Intent i= ChatActivity.newInstance(context,user);
                    startActivity(i);
                    finish();
                }else if(arg0 instanceof ErrorModel){
                    ErrorModel errorModel = (ErrorModel) arg0;
                    String msg = errorModel.getError()!=null ? errorModel.getError() : errorModel.getMessage();
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
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
        }, "Make  Offer");

    }
    class DoneOnEditorActionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if(!TextUtils.isEmpty(offer_price.getText().toString()))
                    if(productModel !=null){
                        makeOfferModel.setProduct_id(productModel.getId());
                        makeOfferModel.setOffer(offer_price.getText().toString());
                        makeOffer(makeOfferModel);
                    }
                    else
                        Toast.makeText(context,"Please Enter your offer",Toast.LENGTH_LONG).show();
                return true;
            }
            return false;
        }
    }
    public void pushNotification(String offer){
        Message messageToSend = new Message(
                offer,
                GlobalFunctions.getSingedUser(getApplicationContext()),
                new Date(), "", "",
                user,GlobalVariables.TYPE_TEXT,"");
        messageToSend.setType(GlobalVariables.TYPE_TEXT);
        MessageManager.getInstance(context).sendMessage(messageToSend,user.getBs_id());
    }
}
