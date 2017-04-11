package com.langoor.app.blueshak.item;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.divrt.co.R;
import com.langoor.app.blueshak.Messaging.activity.ChatActivity;
import com.langoor.app.blueshak.Messaging.activity.MessageActivity;
import com.langoor.app.blueshak.Messaging.data.User;
import com.langoor.app.blueshak.bookmarks.BoomarkItemListAdapter;
import com.langoor.app.blueshak.garage.CreateSaleActivity;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.helper.RoundedImageView;
import com.langoor.app.blueshak.home.EndlessRecyclerOnScrollListener;
import com.langoor.app.blueshak.home.ItemListFragment;
import com.langoor.app.blueshak.horizontalListItems.HorizontalListArray;
import com.langoor.app.blueshak.horizontalListItems.HorizontalListItems;
import com.langoor.app.blueshak.login.LoginActivity;
import com.langoor.app.blueshak.report.ReportListing;
import com.langoor.app.blueshak.reviews_rating.ReviewsRatings;
import com.langoor.app.blueshak.search.SearchListAdapter;
import com.langoor.app.blueshak.services.ServerResponseInterface;
import com.langoor.app.blueshak.services.ServicesMethodsManager;
import com.langoor.app.blueshak.services.model.ConversationIdModel;
import com.langoor.app.blueshak.services.model.CreateProductModel;
import com.langoor.app.blueshak.services.model.CreateSalesModel;
import com.langoor.app.blueshak.services.model.HomeListModel;
import com.langoor.app.blueshak.services.model.LocationModel;
import com.langoor.app.blueshak.services.model.BookmarkListModel;
import com.langoor.app.blueshak.services.model.CategoryListModel;
import com.langoor.app.blueshak.services.model.ErrorModel;
import com.langoor.app.blueshak.services.model.ProductModel;
import com.langoor.app.blueshak.services.model.ReviewsRatingsListModel;
import com.langoor.app.blueshak.services.model.SalesListModel;
import com.langoor.app.blueshak.services.model.SalesModel;
import com.langoor.app.blueshak.services.model.ShopModel;
import com.langoor.app.blueshak.services.model.SimilarProductsModel;
import com.langoor.app.blueshak.services.model.StatusModel;
import com.langoor.app.blueshak.shop.ShopActivity;
import com.langoor.app.blueshak.util.LocationListener;
import com.langoor.app.blueshak.util.LocationService;
import com.langoor.app.blueshak.view.AlertDialog;
import com.langoor.app.blueshak.view.CustomSliderTextView;
import com.langoor.app.blueshak.view_sales.MapActivity;
import com.langoor.app.blueshak.view_sales.MapFragmentSales;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.vlonjatg.progressactivity.ProgressActivity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ProductDetail extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener , LocationListener {

    private static final String TAG = "ProductDetail";
    private static final String  PRODUCTDETAIL_BUNDLE_KEY_POSITION = "ProductModelWithDetails";
    private static final String  PRODUCTDETAIL_BUNDLE_KEY_FLAG = "ProductModelWithFlag";
    private static final String  SELL_MODEL = "sellmodel";
    private Toolbar toolbar;
    private ActionBar actionBar;
    private ProgressActivity progressActivity;
    public static ProductModel productModel = new ProductModel();
    private String selle_user_id;
    private Menu menu;
    private View category_line;
    private CategoryListModel categoriesModels = null;
    private CardView user_commu_content;
    private TextView view_offers,image_counts,brand_new,view_reviews,reviews_rating_count,views_count,offers_count,location,pick_up_name,shippable_sale_name,report_listing,item_seller_name,product_name,garage_sale_name,SimilarListing,productName_tv, rating_count,product_retail_price, availability_tv, shipable_tv, negotabile_tv, inappropirate_tv, date_tv, category_tv, productDescriptionDetail_tv;
    private EditText inappropirate_content;
    private ImageView rating1,rating2,rating3,rating4,rating5,share,delivery_iv,sale_iv,pick_iv;
    private ImageButton bookmark;
    private Button inAppropirate_button;
    private Button callButton, messageButton,make_offer,offers;
    private LinearLayout offers_content,profile_content,tap_offer_extra_layout, inappropirate_layout,reviews_content,bottom_bar,category_content,item_status;
    /* String flag = GlobalVariables.TYPE_GARAGE;
 */ private int flag = GlobalVariables.TYPE_MY_SALE;
    private Handler handler=new Handler();
    private CardView tapOffer_card;
    private ImageView got_to_ratings;
    private static boolean active=false;
    private  boolean  self_user=false,is_sale=false;
    private SliderLayout mProductSlider;
    private PagerIndicator mPagerIndicator;
    private RoundedImageView seller_image;
    private Context context;
    public static Activity activity;
    public Window window;
    boolean iS_from_search_or_bookmark=false;
    private FragmentManager fragmentManager;
    private String seller_phone_number=null;
    private String seller_name=null;
    private String seller_profile_image=null;
    private String product_image=null;
    private String seller_user_id=null,item_name=null,item_price=null;
    private String sell_type=null;
    private boolean is_bookmarked=false;
    private LocationService locServices;
    private TextView price_tv;
    private List<ProductModel> relatedList = new ArrayList<ProductModel>();
    private HorizontalListArray relatedListArray ;
    private  static   SalesModel salesModel=new SalesModel();
    private GlobalVariables globalVariables = new GlobalVariables();
    private GlobalFunctions globalFunctions = new GlobalFunctions();
    private User user=new User();
    private FrameLayout frameLayout;
    private   ConversationIdModel conversationIdModel=new ConversationIdModel();
    private double latitude,longitude;
    private RecyclerView recyclerView;
    private RatingBar ratingBar1;
    private SearchListAdapter adapter;
    public static Intent newInstance(Context context, ProductModel product, SalesModel salesModel, int flag){
        Intent mIntent = new Intent(context, ProductDetail.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(PRODUCTDETAIL_BUNDLE_KEY_POSITION, product);
        bundle.putSerializable(SELL_MODEL, salesModel);
        bundle.putInt(PRODUCTDETAIL_BUNDLE_KEY_FLAG, flag);
        mIntent.putExtras(bundle);
        return mIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
   /*     setContentView(R.layout.activity_item_detail2);*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.product_detail);
        context = this;
        activity = this;
        window = getWindow();
        actionBar = getSupportActionBar();
        fragmentManager = getSupportFragmentManager();
        categoriesModels = GlobalFunctions.getCategories(activity);
        if (getIntent().hasExtra(PRODUCTDETAIL_BUNDLE_KEY_POSITION))
            productModel = (ProductModel) getIntent().getExtras().getSerializable(PRODUCTDETAIL_BUNDLE_KEY_POSITION);
        if (getIntent().hasExtra(SELL_MODEL))
            salesModel = (SalesModel) getIntent().getExtras().getSerializable(SELL_MODEL);
        if (getIntent().hasExtra(PRODUCTDETAIL_BUNDLE_KEY_FLAG))
            flag = getIntent().getExtras().getInt(PRODUCTDETAIL_BUNDLE_KEY_FLAG);

        frameLayout=(FrameLayout)findViewById(R.id.map);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //enable translucent statusbar via flags
            globalFunctions.setTranslucentStatusFlag(window, true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //we don't need the translucent flag this is handled by the theme
            globalFunctions.setTranslucentStatusFlag(window, true);
            //set the statusbarcolor transparent to remove the black shadow
            this.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        restoreToolbar();
        /*
        * Registering broadcast
        * Intent Filter : Adding filter for Broadcast response
        */
        /*IntentFilter filter = new IntentFilter();
        filter.addAction(globalVariables.BROADCAST_KEY_CART_DATA);
        filter.addAction(globalVariables.BROADCAST_KEY_WISHLIST_DATA);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);*/
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        ratingBar1=(RatingBar)findViewById(R.id.ratingBar1);
        adapter = new SearchListAdapter(context, relatedList);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new SpacesItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.space)));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        progressActivity = (ProgressActivity) findViewById(R.id.products_details_progressActivity);
        mProductSlider = (SliderLayout) findViewById(R.id.product_detail_slider);
      /*  mPagerIndicator = (PagerIndicator)findViewById(R.id.product_detail_custom_indicator);*/
        productName_tv = (TextView) findViewById(R.id.product_detail_name_tv);
        SimilarListing= (TextView) findViewById(R.id.SimilarListing);
        price_tv = (TextView) findViewById(R.id.product_detail_price_tv);
        availability_tv = (TextView) findViewById(R.id.product_detail_available_tv);
        shipable_tv = (TextView) findViewById(R.id.product_detail_shippable_tv);
        negotabile_tv = (TextView) findViewById(R.id.product_detail_negotiable_tv);
        inappropirate_tv = (TextView) findViewById(R.id.product_detail_inappropirate_tv);
        date_tv = (TextView) findViewById(R.id.product_detail_date_tv);
        category_tv = (TextView) findViewById(R.id.product_detail_category_tv);
        productDescriptionDetail_tv = (TextView) findViewById(R.id.product_detail_description_tv);
        inappropirate_content = (EditText) findViewById(R.id.product_detail_mark_et);
        inAppropirate_button = (Button) findViewById(R.id.product_detail_markButton);
        inappropirate_layout = (LinearLayout) findViewById(R.id.product_detail_inappropirate_layout);
        reviews_content= (LinearLayout) findViewById(R.id.reviews_content);
        item_status= (LinearLayout) findViewById(R.id.item_status);
        got_to_ratings=(ImageView)findViewById(R.id.got_to_review_ratings);
        user_commu_content=(CardView)findViewById(R.id.user_commu_content);
        bottom_bar=(LinearLayout)findViewById(R.id.bottom_bar);
        bookmark=(ImageButton)findViewById(R.id.bookmark_this);
        offers_content=(LinearLayout)findViewById(R.id.offers_content);
        offers_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_loggedIn()){
                    startActivity(new Intent(activity, MessageActivity.class));
                }else{
                    showSettingsAlert();
                }

            }
        });
        view_offers=(TextView)findViewById(R.id.view_offers);
        view_offers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_loggedIn()){
                    startActivity(new Intent(activity, MessageActivity.class));
                }else{
                    showSettingsAlert();
                }

            }
        });
        share=(ImageView)findViewById(R.id.share);
        rating_count= (TextView) findViewById(R.id.reviews_rating_count);
        product_retail_price= (TextView) findViewById(R.id.product_retail_price);
        rating1=(ImageView)findViewById(R.id.rating_image1);
        rating2=(ImageView)findViewById(R.id.rating_image2);
        rating3=(ImageView)findViewById(R.id.rating_image3);
        rating4=(ImageView)findViewById(R.id.rating_image4);
        rating5=(ImageView)findViewById(R.id.rating_image5);
        delivery_iv=(ImageView)findViewById(R.id.list_row_item_delivery_iv);
        sale_iv=(ImageView)findViewById(R.id.list_row_item_sale_iv);
        pick_iv=(ImageView)findViewById(R.id.list_row_item_pick_iv);
        product_name = (TextView) findViewById(R.id.product_name);
        pick_up_name = (TextView) findViewById(R.id.pick_up_name);
        shippable_sale_name = (TextView) findViewById(R.id.shippable_sale_name);
        garage_sale_name = (TextView) findViewById(R.id.garage_sale_name);
        report_listing= (TextView) findViewById(R.id.report_listing);
        /*callButton = (Button) findViewById(R.id.product_detail_call_button);*/
        messageButton = (Button) findViewById(R.id.product_detail_message_button);
        item_seller_name=(TextView)findViewById(R.id.item_seller_name);
        reviews_rating_count=(TextView)findViewById(R.id.reviews_rating_count);
        views_count=(TextView)findViewById(R.id.views_count);
        item_seller_name=(TextView)findViewById(R.id.item_seller_name);
        offers_count=(TextView)findViewById(R.id.offers_count);
        make_offer = (Button) findViewById(R.id.make_offer);
        offers = (Button) findViewById(R.id.offers);
        location=(TextView)findViewById(R.id.location);
        view_reviews=(TextView)findViewById(R.id.view_reviews);
        brand_new=(TextView)findViewById(R.id.brand_new);
        image_counts=(TextView)findViewById(R.id.image_counts);
        seller_image=(RoundedImageView)findViewById(R.id.seller_image);
        category_content=(LinearLayout)findViewById(R.id.category_content);
        category_line=(View)findViewById(R.id.category_line);
        profile_content=(LinearLayout)findViewById(R.id.profile_content);
        profile_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getShopDetails(context,seller_user_id);
            }
        });
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= MapActivity.newInstance(context,GlobalVariables.TYPE_MULTIPLE_ITEMS,productModel);
                startActivity(i);
            }
        });
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= MapActivity.newInstance(context,GlobalVariables.TYPE_MULTIPLE_ITEMS,productModel);
                startActivity(i);
            }
        });
        report_listing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_loggedIn()){
                    Intent report_listing= ReportListing.newInstance(context,productModel);
                    startActivity(report_listing);
                }else{
                    showSettingsAlert();
                }

            }
        });

        if(flag==GlobalVariables.TYPE_MY_BOOKMARKS || flag==GlobalVariables.TYPE_SEARCH || flag==GlobalVariables.TYPE_SIMILAR_PRODUCT)
            iS_from_search_or_bookmark=true;

        runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(productModel!=null){
                        getItemInfo(productModel,context);
              /*  setThisPage();*/
                    }else if (salesModel!=null){
                        getSaleInfo(salesModel,context);

                    }else{
                        showErrorPage();
                    }
                }
            });


        toolbar = (Toolbar) findViewById(R.id.product_detail_tool_bar);
        toolbar.setTitle(GlobalFunctions.getSentenceFormat("Listing Details"));
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(getResources().getColor(R.color.brandColor));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(is_loggedIn()){
                    if(!self_user){
                        if(seller_name!=null || seller_user_id!=null){
                          /*  final Intent intent = new Intent(ProductDetail.this, ChatActivity.class);
                            Gson gson=new Gson();
                            String data=gson.toJson(new User(seller_name,null,seller_phone_number,seller_user_id,seller_profile_image,product_image,item_name,item_price));
                            intent.putExtra("User", data);
                            startActivity(intent);*/
                            check_conversation_exists();

                        }else{
                            Toast.makeText(activity,"Sorry can't send message",Toast.LENGTH_LONG).show();
                        }
                     }else {
                        if(!is_sale){
                            CreateProductModel createProductModel = new CreateProductModel();
                            createProductModel.toObject(productModel,GlobalFunctions.getCategories(context));
                            closeThisActivity();
                            Intent i= CreateSaleActivity.newInstance(context,null,createProductModel,null,0,GlobalVariables.TYPE_ITEM);
                            startActivity(i);
                        }else{
                            CreateSalesModel createSalesModel = new CreateSalesModel();
                            createSalesModel.toObject(salesModel,GlobalFunctions.getCategories(context));
                            closeThisActivity();
                            Intent i= CreateSaleActivity.newInstance(context,createSalesModel,null,null,0,GlobalVariables.TYPE_GARAGE);
                            startActivity(i);

                        }
                    }
                }else
                    showSettingsAlert();



            }
        });
        make_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_loggedIn()){
                    if (!self_user) {
                        Intent intent = MakeOffersActivty.newInstance(context, productModel);
                        startActivity(intent);
                    } else {
                        if (!is_sale) {
                            if (productModel != null)
                                updateItemStatus(context, false, productModel.getId());
                        } else {
                            if (salesModel != null)
                                show_alert(salesModel.getId());
                                /*updateSaleStatus(context,false,salesModel.getId());*/
                        }
                    }
                }else
                    showSettingsAlert();
            }
        });

        offers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Under Development",Toast.LENGTH_LONG).show();
            }
        });
        view_reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= ReviewsRatings.newInstance(activity,productModel,null,null,salesModel);
                startActivity(intent);
            }
        });

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_loggedIn()){
                    if(productModel!=null){
                        int book_mark_count=GlobalFunctions.getSharedPreferenceInt(activity,GlobalVariables.BOOKMARKS_UNREAD_COUNT);
                        GlobalFunctions.setSharedPreferenceInt(activity,GlobalVariables.BOOKMARKS_UNREAD_COUNT,book_mark_count+1);
                        if(productModel.is_bookmark())
                           deleteBookmark(context,productModel.getId());
                        else
                            addBookmark(context, productModel.getId());
                    } else
                        Toast.makeText(context,"This product can't be bookmarked",Toast.LENGTH_LONG).show();
                }else{
                    showSettingsAlert();
                }


            }
        });

        locServices = new LocationService(activity);
        locServices.setListener(this);
        if(!locServices.canGetLocation()){locServices.showSettingsAlert();}else {
            latitude=locServices.getLatitude();longitude=locServices.getLongitude();
        }


        reviews_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= ReviewsRatings.newInstance(activity,productModel,null,null,salesModel);
                startActivity(intent);
            }
        });

    }

    private void validateMarkInappropirate(){
        if(inappropirate_content!=null){
            String inapp_content = inappropirate_content.getText().toString();
            if(inapp_content.isEmpty()){
                inappropirate_content.setError("Please fill the reason to Mark.");
            }else{
                sendInappropirateMark(inapp_content);
            }
        }
    }
    public void call(String Phone) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:"+Phone));
        startActivity(callIntent);
    }
    private void sendInappropirateMark(String content){
        GlobalFunctions.showProgress(context, "Marking Inappropriate Product...");
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.markInappropirate(context, productModel.getId(), content, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                Log.d(TAG, "onSuccess Response");
                if(arg0 instanceof StatusModel){
                    StatusModel statusModel = (StatusModel) arg0;
                    if(statusModel.isStatus()){
                        Toast.makeText(context, "Product has been successfully submitted as inappropriate", Toast.LENGTH_SHORT).show();
                        toggleInappropirateLayout();
                        inappropirate_tv.setVisibility(View.GONE);
                    }
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
        }, "Similar Products");
    }
    public boolean is_loggedIn(){
        boolean is_logged_in=false;
        String token = GlobalFunctions.getSharedPreferenceString(context, GlobalVariables.SHARED_PREFERENCE_TOKEN);
        if(token!=null){
            return true;
        }else{
            return false;
        }

    }
    public void showSettingsAlert(){
        final com.langoor.app.blueshak.view.AlertDialog alertDialog = new com.langoor.app.blueshak.view.AlertDialog(context);
        alertDialog.setMessage("Please Login/Register to continue");
        alertDialog.setTitle("Login/Register");
        alertDialog.setPositiveButton("Continue", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent creategarrage = LoginActivity.newInstance(activity,null,null);
                startActivity(creategarrage);
                closeThisActivity();
            }
        });

       /* // on pressing cancel button
        alertDialog.setNegativeButton("Not now", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });*/

        // Showing Alert Message
        alertDialog.show();
    }
    private void getSimilarProducts(){
        relatedList.clear();
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        if(categoriesModels==null)categoriesModels = GlobalFunctions.getCategories(activity);
        String categoryIdsString = "";
        if(categoriesModels!=null){
            categoryIdsString = categoriesModels.getIDStringfromNameString(productModel.getProductCategory());
        }
        if(categoryIdsString==null || !TextUtils.isEmpty(categoryIdsString))
            categoryIdsString="4,5";
        servicesMethodsManager.getSimilarProducts(context, productModel.getId(),categoryIdsString,Double.toString(latitude),Double.toString(longitude), new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                Log.d(TAG, "getSimilarProducts onSuccess Response");
                SimilarProductsModel similarProductsModel = (SimilarProductsModel)arg0;
            /*    setSimilarProducts(similarProductsModel.getProductsList(),"Similar products");*/
                setValues(similarProductsModel.getProductsList());
                Log.d(TAG, similarProductsModel.toString());
            }

            @Override
            public void OnFailureFromServer(String msg) {
                Log.d(TAG, msg);
            }

            @Override
            public void OnError(String msg) {
                Log.d(TAG, msg);
            }
        }, "Similar Products");

    }
    public void setSimilarProducts(List<ProductModel> products,String title){
        Log.d(TAG,"########coming inside : "+fragmentManager);
        if(fragmentManager!=null){
            relatedList.clear();
            relatedList.addAll(products);
            try{
                if(relatedList.size()>0){
                    relatedListArray = new HorizontalListArray(title,relatedList);
                    Log.d(TAG,"prodFM : "+fragmentManager);
                    Log.d(TAG, "prodFM bool Detaching: " + this.isFinishing());
                    if(fragmentManager!=null&&!this.isFinishing()){
                        Fragment fragment = new HorizontalListItems().newInstance(relatedListArray);
                        fragmentManager.beginTransaction().replace(R.id.similar_products_container,fragment).commit();
                    }
                }
            }catch (Exception ex){Log.d(TAG, "Exception on setting History Data : "+ex);}
        }
    }

    public void setMap(){
        if(fragmentManager!=null){
              if(fragmentManager!=null&&!this.isFinishing()){
                        Fragment fragment = new MapFragmentSales().newInstance(GlobalVariables.TYPE_SHOP,productModel,null,false);
                        fragmentManager.beginTransaction().replace(R.id.map,fragment).commit();
              }
        }
    }

    public void restoreToolbar(){
        Log.d(TAG, "Restore Tool Bar");
        if(actionBar!=null){
            Log.d(TAG, "Restore Action Bar not Null");
            actionBar.setTitle("");
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        }

    }

    public static boolean isActive(){
        return active;
    }

    private void toggleInappropirateLayout(){
        if(inappropirate_layout!=null){
            if(inappropirate_layout.getVisibility()==View.VISIBLE){
                inappropirate_layout.setVisibility(View.GONE);
            }else{
                inappropirate_layout.setVisibility(View.VISIBLE);
            }
        }
    }


    /*public void setProductModel(ProductModel product){
        productModel = product;
        setThisPage();
    }*/
    private void getItemInfo(ProductModel productModel,Context context){
        GlobalFunctions.showProgress(context,"Loading...");
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.getItemInfo(context, productModel.getId(), new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                Log.d(TAG, "GetItemInfo onSuccess Response");
                ProductModel productModel = (ProductModel) arg0;
                setThisPage(productModel);
                Log.d(TAG, productModel.toString());
            }

            @Override
            public void OnFailureFromServer(String msg) {
                Log.d(TAG, msg);
                GlobalFunctions.hideProgress();
            }

            @Override
            public void OnError(String msg) {
                Log.d(TAG, msg);
                GlobalFunctions.hideProgress();
            }
        }, "GetItemInfo onSuccess Response");

    }
    private void getSaleInfo(SalesModel salesModel,Context context){
        GlobalFunctions.showProgress(context,"Loading...");
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.getSaleInfo(context, salesModel.getId(),Double.toString(latitude),Double.toString(longitude), new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                Log.d(TAG, "GetItemInfo onSuccess Response");
                 SalesModel salesModel = (SalesModel) arg0;
                Log.d(TAG+"#Sale ####", salesModel.toString());
                setThisPage(salesModel);


                Log.d(TAG, salesModel.toString());
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
        }, "GetItemInfo onSuccess Response");

    }
    private void setThisPage(ProductModel product){
        productModel=product;

        if(productModel.getName()!=null)
            product_name.setText(GlobalFunctions.getSentenceFormat(productModel.getName()));

        item_name=productModel.getName();
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
        user.setProduct_id(productModel.getId());
        user.setProduct_url(product_image);
        user.setActive_tab(GlobalVariables.TYPE_BUYER_TAB);
        String signed_user_user_id=GlobalFunctions.getSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_USERID);
        if(signed_user_user_id!=null && seller_user_id!=null){
            if(Integer.parseInt(seller_user_id) == Integer.parseInt(signed_user_user_id)) {
                self_user=true;
                report_listing.setVisibility(View.GONE);
                bookmark.setVisibility(View.GONE);
                make_offer.setText("Mark Sold");
                messageButton.setText("Edit");
            }
        }



        if(!TextUtils.isEmpty(productModel.getName()))
            productName_tv.setText(GlobalFunctions.getSentenceFormat(productModel.getName()));

        if(!TextUtils.isEmpty(productModel.getSellerName()))
            item_seller_name.setText(GlobalFunctions.getSentenceFormat(productModel.getSellerName()));

        reviews_rating_count.setText(productModel.getReviews_count());
        views_count.setText(productModel.getViews());
        offers_count.setText(productModel.getOffers());
      /*  productName_tv.setText(productModel.getName());*/
        String avatar=productModel.getSeller_image();
        if(!TextUtils.isEmpty(avatar)){
            Picasso.with(activity)
                    .load(avatar)
                    .placeholder(R.drawable.placeholder_background)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .fit().centerCrop()
                    .into(seller_image);
        }
        /*int price=0;
        if(!TextUtils.isEmpty(productModel.getSalePrice()))
            price=(int)Float.parseFloat(productModel.getSalePrice());*/

        price_tv.setText(globalVariables.CURRENCY_NOTATION+" "+productModel.getSalePrice());
        if(productModel.is_product_new()){
            brand_new.setVisibility(View.VISIBLE);
        }
        if(productModel.is_bookmark()){
            bookmark.setImageResource(R.drawable.ic_favorite_black_24dp);
        }
        int grey2=getResources().getColor(R.color.grey_2);
        int black=getResources().getColor(R.color.black);
        delivery_iv.setImageResource(productModel.isShipable()? R.drawable.shippingon : R.drawable.shippingoff);
        pick_iv.setImageResource(productModel.isPickup()? R.drawable.pickupon : R.drawable.pickupoff);
        shippable_sale_name.setTextColor(productModel.isShipable()? black:grey2);
        pick_up_name.setTextColor(productModel.isPickup()?black:grey2);
        date_tv.setText(/*productModel.getLast_updated()*/productModel.getProductCreatedAt());
        String category=productModel.getProductCategory();
        if(!TextUtils.isEmpty(category) && !productModel.getProductCategory().equalsIgnoreCase("null"))
            category=productModel.getProductCategory();
        else
            category="Electronics and Computers";
        category_tv.setText(category);
        productDescriptionDetail_tv.setText(productModel.getDescription());
        setImageOnView(productModel.getImages());
        getSimilarProducts();
        if(productModel.getCummalative_rating()!=null&&!TextUtils.isEmpty(productModel.getCummalative_rating()))
            ratingBar1.setRating(Float.parseFloat(productModel.getCummalative_rating()));
        getAddressFromLatLng(Double.parseDouble(productModel.getLatitude()),Double.parseDouble(productModel.getLongitude()));
        showContent();
    }

    private void shareProduct(){
        final String product_="Family and freinds,please share this product to help me sell faster.";
        final String product_name="Product Name :"+productModel.getName();
        final String product_description="Product Description :"+productModel.getDescription();
        String image_url=null;
        if(productModel.getImage()!=null)
            if(productModel.getImage().size()>0)
                image_url="Product Image Url :"+productModel.getImage().get(0);

        final String product_image_url=image_url;
        final String product_location="Product Location :"+location.getText().toString();
        final String product_Id_for_share="Product Id :"+productModel.getId();
        final String product_duration="Sale Duration :"+productModel.getStartDate()+" to"+productModel.getEndDate();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Family and freinds,please share this "+product_name+
                " to help me sell faster."+"\n"+"Product Details");
        shareIntent.putExtra(Intent.EXTRA_TEXT,product_+"\n"+product_name+"\n"+product_Id_for_share+"\n"+
                product_description+"\n"/*+product_duration+"\n"*/+product_image_url+"\n"+product_location
        );
        startActivity(Intent.createChooser(shareIntent, "Share this product using"));

    }


    private void setImageOnView(ArrayList<String> images){

        mProductSlider.removeAllSliders();
        ArrayList<String> displayImageURL = new ArrayList<String>();
        displayImageURL.clear();
        displayImageURL.addAll(/*productModel.getImage()*/images);
        if(displayImageURL.size()>0){
            product_image=displayImageURL.get(0);
            user.setProduct_url(product_image);
        }
        if(productModel.getImage().size()>1){
            image_counts.setVisibility(View.VISIBLE);
            image_counts.setText("1"+"/"+productModel.getImage().size());
        }

        for(int i=0;i<displayImageURL.size()&&displayImageURL.get(i)!=null;i++){
            final String url=displayImageURL.get(i).toString();
              final  Uri uri=getUriFromUrl(url);
            CustomSliderTextView textSliderView = new CustomSliderTextView(context);
            // initialize a SliderLayout
            textSliderView
                    .description(1+"")
                    .image(displayImageURL.get(i).toString())
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                   /* .setOnSliderClickListener(this);*/
            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {
                    Intent intent=ImageViewActivty.newInstance(context,productModel);
                    startActivity(intent);
                }
            });
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
        mProductSlider.addOnPageChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.product_menu, menu);
        this.menu = menu;
        // setOptionsMenuVisiblity(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.share) {
            shareProduct();

            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }
    private Uri.Builder builder;
    public Uri getUriFromUrl(String thisUrl) {
        try {
            URL url = new URL(thisUrl);
            builder =  new Uri.Builder()
                    .scheme(url.getProtocol())
                    .authority(url.getAuthority())
                    .appendPath(url.getPath());
            return builder.build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return  null;
        }

    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        if(productModel!=null)
            image_counts.setText(i+1+"/"+productModel.getImage().size());
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onSliderClick(BaseSliderView baseSliderView) {
        mProductSlider.getCurrentPosition();
    }

    private void showLoading(){
        if(progressActivity!=null){
            progressActivity.showLoading();
        }
    }

    private void showContent(){
        if(progressActivity!=null){
            progressActivity.showContent();
        }
    }

    private void showErrorPage(){
        if(progressActivity!=null){
            progressActivity.showError(context.getResources().getDrawable(R.drawable.ic_error), "No Connection",
                    "We could not establish a connection with our servers. Try again when you are connected to the internet.",
                    "Try Again", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ProductDetail productDetail = new ProductDetail();
                            productDetail.setThisPage(productModel);
                        }
                    });
        }
    }

    private void showEmptyPage(){
        if(progressActivity!=null){
            progressActivity.showEmpty(context.getResources().getDrawable(R.drawable.ic_error), "Empty Shopping Cart",
                    "Please add things in the cart to continue.");
        }
    }


    private void addBookmark(final Context context, String productID){
        /*GlobalFunctions.showProgress(context, "Bookmarking Product...");*/
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.addBookmark(context, productID, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                /*GlobalFunctions.hideProgress();*/
                if(arg0 instanceof StatusModel){
                    StatusModel statusModel = (StatusModel) arg0;
                    if(statusModel.isStatus()){
                        bookmark.setImageResource(R.drawable.ic_favorite_black_24dp);
                        productModel.setIs_bookmark(true);
                        Toast.makeText(context, "Added to bookmarks", Toast.LENGTH_SHORT).show();
                    }
                }else if(arg0 instanceof ErrorModel){
                    ErrorModel errorModel = (ErrorModel) arg0;
                    String msg = errorModel.getError()!=null ? errorModel.getError() : errorModel.getMessage();
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void OnFailureFromServer(String msg) {
                /*GlobalFunctions.hideProgress();*/
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void OnError(String msg) {
               /* GlobalFunctions.hideProgress();*/
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        }, "Add Bookmarks");

    }

    @Override
    protected void onResume() {
        if(salesModel!=null){
            productModel=new ProductModel();
            productModel.setLongitude(salesModel.getLongitude());
            productModel.setLatitude(salesModel.getLatitude());
            productModel.setName(salesModel.getName());
            productModel.setId(salesModel.getId());
            productModel.setImage(salesModel.getImages());
        }
        setMap();
        super.onResume();
    }

    @Override
    protected void onStart() {
        active = true;
        super.onStart();
    }

    @Override
    protected void onStop() {
        active = false;
        mProductSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        finish();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }

    public static void closeThisActivity(){
        Log.d(TAG, "Close this Activity" + activity);
        if(activity!=null){
            activity.finish();
        }
    }
    @Override
    public void onProviderEnabled(String provider) {

    }
    @Override
    public void onLocationChanged(Location location) {

    }
    private void getAddressFromLatLng(Double lat,Double lng){
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.getAddress(activity, lat,lng, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                LocationModel locationModel =(LocationModel)arg0;
               if(locationModel !=null)
                   location.setText(locationModel.getFormatted_address());
            }

            @Override
            public void OnFailureFromServer(String msg) {
            }

            @Override
            public void OnError(String msg) {
            }
        }, "Delete Sale");


    }
    private void setThisPage(SalesModel salesModel){
        is_sale=true;
        this.salesModel=salesModel;
        product_name.setText(GlobalFunctions.getSentenceFormat(salesModel.getName()));

        if(salesModel.getProducts().size()>0)
            SimilarListing.setText("Items for sale");
        else
            SimilarListing.setText("No items for sale");

        seller_user_id=salesModel.getUserID();
        seller_name=salesModel.getSellerName();
        seller_phone_number=salesModel.getSellerNumber();
        seller_profile_image=salesModel.getSeller_image();
        user.setName(seller_name);
        user.setBs_id(seller_user_id);
        user.setNumber(seller_phone_number);
        user.setProfileImageUrl(seller_profile_image);
        user.setProduct_name(salesModel.getName());
        user.setPrice(item_price);
        user.setProduct_id(productModel.getId());
        user.setProduct_url(product_image);
        user.setActive_tab(GlobalVariables.TYPE_BUYER_TAB);
        if(salesModel.is_own_sale()) {
                self_user=true;
                report_listing.setVisibility(View.GONE);
                bookmark.setVisibility(View.GONE);
                make_offer.setText("Delete");
                messageButton.setText("Edit");
        }

        if(flag==GlobalVariables.TYPE_MY_BOOKMARKS)
            bookmark.setVisibility(View.GONE);
        if(!TextUtils.isEmpty(salesModel.getName()))
            productName_tv.setText(GlobalFunctions.getSentenceFormat(salesModel.getName()));
        if(!TextUtils.isEmpty(salesModel.getSellerName()))
            item_seller_name.setText(GlobalFunctions.getSentenceFormat(salesModel.getSellerName()));
        price_tv.setVisibility(View.GONE);
        category_content.setVisibility(View.GONE);
        category_line.setVisibility(View.GONE);
        String avatar=salesModel.getSeller_image();
        if(!TextUtils.isEmpty(avatar)){
            Picasso.with(activity)
                    .load(avatar)
                    .placeholder(R.drawable.profile_pic)
                   /* .memoryPolicy(MemoryPolicy.valueOf(avatar))*/
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    /*.networkPolicy(NetworkPolicy.shouldReadFromDiskCache())*/
                    .fit().centerCrop()
                    .into(seller_image);
        }
        date_tv.setText(salesModel.getStart_date());
        setImageOnView(salesModel.getImages());
        bookmark.setVisibility(View.GONE);
        if(!salesModel.is_own_sale())
            make_offer.setVisibility(View.GONE);
        item_status.setVisibility(View.GONE);
        productDescriptionDetail_tv.setText(salesModel.getDescription());
        setValues(salesModel.getProducts());
        String rating=salesModel.getAvg_seller_rating();
        if(salesModel.getAvg_seller_rating()!=null&&!TextUtils.isEmpty(salesModel.getAvg_seller_rating())&&!rating.equalsIgnoreCase("null"))
            ratingBar1.setRating(Float.parseFloat(salesModel.getAvg_seller_rating()));

        getAddressFromLatLng(Double.parseDouble(salesModel.getLatitude()),Double.parseDouble(salesModel.getLongitude()));
        showContent();
    }
    public void updateItemStatus(final Context context,boolean is_Available,String product_id){
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.updateItemStatus(context,product_id,is_Available,new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                Log.d(TAG, "onSuccess Response");
                if(arg0 instanceof StatusModel){
                    StatusModel statusModel = (StatusModel) arg0;
                    if(statusModel.isStatus()){
                        Toast.makeText(context, "Item has been marked as sold", Toast.LENGTH_LONG).show();
                        closeThisActivity();
                    }
                }else if(arg0 instanceof ErrorModel){
                    ErrorModel errorModel = (ErrorModel) arg0;
                    String msg = errorModel.getError()!=null ? errorModel.getError() : errorModel.getMessage();
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void OnFailureFromServer(String msg) {
                Log.d(TAG, msg);
            }

            @Override
            public void OnError(String msg) {
                Log.d(TAG, msg);
            }
        }, "Review Ratings");
    }
    public void updateSaleStatus(final Context context,boolean is_Available,String sale_id){
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.updateSaleStatus(context,sale_id,is_Available,new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                Log.d(TAG, "onSuccess Response");
                if(arg0 instanceof StatusModel){
                    StatusModel statusModel = (StatusModel) arg0;
                    if(statusModel.isStatus()){
                        Toast.makeText(context, "Sale has been marked as sold", Toast.LENGTH_LONG).show();
                        closeThisActivity();
                    }
                }else if(arg0 instanceof ErrorModel){
                    ErrorModel errorModel = (ErrorModel) arg0;
                    String msg = errorModel.getError()!=null ? errorModel.getError() : errorModel.getMessage();
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void OnFailureFromServer(String msg) {
                Log.d(TAG, msg);
            }

            @Override
            public void OnError(String msg) {
                Log.d(TAG, msg);
            }
        }, "Review Ratings");
    }
    private void deleteSale(final Context context, String saleID){
        GlobalFunctions.showProgress(context, "Deleting Sale...");
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.deleteSale(context, saleID, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                Log.d(TAG, "onSuccess Response");
                Toast.makeText(context,"Sale has been deleted Successfully",Toast.LENGTH_LONG).show();
                closeThisActivity();       }

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
        }, "Delete Sale");
    }
    public  void show_alert(final String sale_id){
        final AlertDialog dialog = new AlertDialog(activity);
        dialog.setTitle("Delete Sale");
        dialog.setIcon(R.drawable.ic_warning_black_24dp);
        dialog.setIsCancelable(true);
        dialog.setMessage("Are you sure Delete this Sale?");
        dialog.setPositiveButton("Yes", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSale(activity,sale_id);
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void getShopDetails(Context context,String selle_user_id){
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.getShopDetails(context,selle_user_id,new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                Log.d(TAG, "onSuccess Response"+arg0.toString());
                ShopModel ShopModel = (ShopModel) arg0;
                Log.d(TAG,"####ShopModel#####"+ShopModel.toString());
                Log.d(TAG,"####Profile model#####"+ShopModel.getProfileDetailsModel().toString());
                Log.d(TAG,"####Item List#####"+ShopModel.getItem_list());
                proceedToShop(ShopModel);

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
    public  void proceedToShop(ShopModel shopModel) {
        Intent i = ShopActivity.newInstance(context, seller_user_id);
        startActivity(i);

    }
    public void refreshPage(Context context){
        if(isActive()){
            if(is_sale)
                getSaleInfo(salesModel,context);
            else
                getItemInfo(productModel,context);
        }else {
            if(is_sale){
                Intent intent = ProductDetail.newInstance(context,null,salesModel,GlobalVariables.TYPE_MY_SALE);
                context.startActivity(intent);
            }else{
                Intent intent = ProductDetail.newInstance(context,productModel,null,GlobalVariables.TYPE_MY_SALE);
                context.startActivity(intent);
            }



        }


    }
    private void check_conversation_exists(){
        if(is_sale)
            conversationIdModel.setIs_garage(true);
        conversationIdModel.setProduct_id(productModel.getId());
        conversationIdModel.setSeller_id(seller_user_id);
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.check_conversation_exists(context, conversationIdModel,new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                Log.d(TAG, "onSuccess Response");
                if(arg0 instanceof StatusModel){
                    StatusModel statusModel = (StatusModel) arg0;
                    if(statusModel.isStatus()){
                        Toast.makeText(context, statusModel.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }else if(arg0 instanceof ErrorModel){
                    ErrorModel errorModel = (ErrorModel) arg0;
                    String msg = errorModel.getError()!=null ? errorModel.getError() : errorModel.getMessage();
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }else if(arg0 instanceof ConversationIdModel){
                    conversationIdModel=(ConversationIdModel)arg0;
                    Log.d(TAG,"conversationIdModel iddd##############"+conversationIdModel.getConversation_id());
                    user.setConversation_id(conversationIdModel.getConversation_id());
                    user.setProduct_id(conversationIdModel.getProduct_id());
                    Intent i= ChatActivity.newInstance(context,user);
                    startActivity(i);
                   /* GlobalFunctions.hideProgress();
                    reviewsRatingsModel = (ConversationIdModel)arg0;
                   *//* setReviewsRatings(reviewsRatingsModel);*//*
                    Log.d(TAG, reviewsRatingsModel.toString());    */   }
            }

            @Override
            public void OnFailureFromServer(String msg) {
                Log.d(TAG, msg);
            }

            @Override
            public void OnError(String msg) {
                Log.d(TAG, msg);
            }
        }, "Review Ratings");

    }
    private static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private final int space;
        public SpacesItemDecoration(int space) {
            this.space = space;
        }
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = 2 * space;
            int pos = parent.getChildAdapterPosition(view);
            outRect.left = space;
            outRect.right = space;
            if (pos < 2)
                outRect.top = 2 * space;
        }
    }
    private void setValues(List<ProductModel> products){
            relatedList.clear();
            relatedList.addAll(products);
            if(relatedList!=null){
                if(relatedList.size()>0&&recyclerView!=null&&relatedList!=null&&adapter!=null){
                   refreshList();
                }
            }
    }
    private void deleteBookmark(final Context context, final String productID){
        /*GlobalFunctions.showProgress(context, "Bookmarking Product...");*/
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.deleteBookmark(context, productID, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                /*GlobalFunctions.hideProgress();*/
                if(arg0 instanceof StatusModel){
                    StatusModel statusModel = (StatusModel) arg0;
                    if(statusModel.isStatus()){
                        productModel.setIs_bookmark(false);
                        bookmark.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        Toast.makeText(context, "Bookmark Removed", Toast.LENGTH_SHORT).show();
                    }
                }else if(arg0 instanceof ErrorModel){
                    ErrorModel errorModel = (ErrorModel) arg0;
                    String msg = errorModel.getError()!=null ? errorModel.getError() : errorModel.getMessage();
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            }


            @Override
            public void OnFailureFromServer(String msg) {
                /*GlobalFunctions.hideProgress();*/
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void OnError(String msg) {
               /* GlobalFunctions.hideProgress();*/
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        }, "Add Bookmarks");

    }
    public void refreshList(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (adapter){adapter.notifyDataSetChanged();}
            }
        });
    }
}
