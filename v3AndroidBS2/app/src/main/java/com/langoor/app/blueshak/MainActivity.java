package com.langoor.app.blueshak;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.langoor.app.blueshak.home.ItemListAdapter;
import com.langoor.blueshak.R;
import com.langoor.app.blueshak.Messaging.activity.InboxFragment;
import com.langoor.app.blueshak.Messaging.activity.PushActivity;
import com.langoor.app.blueshak.garage.*;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.home.GarageSalesListFragment;
import com.langoor.app.blueshak.home.ItemListFragment;
import com.langoor.app.blueshak.item.ProductDetail;
import com.langoor.app.blueshak.login.LoginActivity;
import com.langoor.app.blueshak.profile.ProfileFragment;
import com.langoor.app.blueshak.search.SearchActivity;
import com.langoor.app.blueshak.services.ServerResponseInterface;
import com.langoor.app.blueshak.services.ServicesMethodsManager;
import com.langoor.app.blueshak.services.model.FilterModel;
import com.langoor.app.blueshak.services.model.LocationModel;
import com.langoor.app.blueshak.services.model.SalesListModel;
import com.langoor.app.blueshak.services.model.SalesListModelNew;
import com.langoor.app.blueshak.util.LocationListener;
import com.langoor.app.blueshak.util.LocationService;
import com.langoor.app.blueshak.view_sales.MapFragmentSales;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

public class MainActivity extends PushActivity implements LocationListener{
    private static BottomBar mBottomBar;
    private static final String TAG = "MainActivity";
    static Context mainContext;
    public static FragmentManager mainActivityFM;
    static Window mainWindow;
    static String mTitle;
    static int mResourceID;
    static Menu menu;
    static TextView toolbar_title;
    LocationService locServices;
    public static Activity activity = null;
    private SalesListModelNew salesListModel = new SalesListModelNew();
    String current_location;
    private SearchView mSearchView;
    public static final String MAIN_ACTIVITY_LOCATION_MODEL_SERIALIZE="locationModelSerialize";
    public static final String MAIN_ACTIVITY_FILTER_MODEL_SERIALIZE="filterModelSerialize";
    private static LocationModel locationModel=null;
    public   static FilterModel filterModel=null;
    private ImageView go_to_search,grid;
    boolean is_map=true;
    boolean is_list=true;
    public  static  boolean is_reset=false;
    public  static  boolean is_active=false;

    public static Intent newInstance(Context context, LocationModel locationModel, FilterModel filterModel){
        Intent mIntent = new Intent(context,MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MAIN_ACTIVITY_LOCATION_MODEL_SERIALIZE, locationModel);
        bundle.putSerializable(MAIN_ACTIVITY_FILTER_MODEL_SERIALIZE, filterModel);
        mIntent.putExtras(bundle);
        return mIntent;
    }

    int activeTab=0;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try{
            activity = this;
            mainContext = this;
            mainActivityFM = getSupportFragmentManager();
            mainWindow = getWindow();
            ProductDetail.is_map=true;
            Intent i=getIntent();
            if(i!=null){
                if(i.hasExtra(MAIN_ACTIVITY_LOCATION_MODEL_SERIALIZE)){
                    locationModel=(LocationModel)getIntent().getExtras().getSerializable(MAIN_ACTIVITY_LOCATION_MODEL_SERIALIZE);
                }
                if(i.hasExtra(MAIN_ACTIVITY_FILTER_MODEL_SERIALIZE)){
                    filterModel=(FilterModel) getIntent().getExtras().getSerializable(MAIN_ACTIVITY_FILTER_MODEL_SERIALIZE);
                }
            }

            locServices = new LocationService(activity);
            if(!locServices.canGetLocation()){
                startActivity(new Intent(this,PickLocation.class));
                closeThisActivity();}
            else  {
                if(GlobalFunctions.getSharedPreferenceString(activity,GlobalVariables.CURRENT_LOCATION)==null)
                    getAddressFromLatLng();

            }
            locServices.setListener(this);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            View v = inflator.inflate(R.layout.actionbar_custom_view, null);
            go_to_search=(ImageView)v.findViewById(R.id.go_to_search);
            grid=(ImageView)v.findViewById(R.id.grid);
            grid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                 switch (activeTab)
                 {
                     case 0:
                         if(!is_list){

                             ItemListFragment.listOrGrid=true;
                             ItemListFragment itemListFragment= ItemListFragment.newInstance(new SalesListModel(), GlobalVariables.TYPE_MULTIPLE_ITEMS,filterModel,locationModel);
                             mainActivityFM.beginTransaction().replace(R.id.container, itemListFragment, "").commit();
                             is_list=true;
                             grid.setImageResource(R.drawable.ic_grid);
                         }else{
                             ItemListFragment.listOrGrid=false;
                             ItemListFragment itemListFragment= ItemListFragment.newInstance(new SalesListModel(), GlobalVariables.TYPE_MULTIPLE_ITEMS,filterModel,locationModel);
                             mainActivityFM.beginTransaction().replace(R.id.container, itemListFragment, "").commit();
                             is_list=false;
                             grid.setImageResource(R.drawable.ic_list);
                         }

                         break;
                     case 1:
                         if(!is_map){
                             Fragment fragment = new MapFragmentSales().newInstance(GlobalVariables.TYPE_GARAGE,null,null,false,filterModel);
                             mainActivityFM.beginTransaction().replace(R.id.container,fragment,"").commit();
                             is_map=true;
                             grid.setImageResource(R.drawable.ic_grid);
                         }else{
                             GarageSalesListFragment garageSalesListFragment= GarageSalesListFragment.newInstance(salesListModel, GlobalVariables.TYPE_GARAGE,filterModel,locationModel,false);
                             mainActivityFM.beginTransaction().replace(R.id.container, garageSalesListFragment, "").commit();
                             is_map=false;
                             grid.setImageResource(R.drawable.pin_white);
                         }

                         break;
                     case 2:
                         break;
                     case 3:
                         break;
                     case 4:
                         break;
                 }



                }
            });
            go_to_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(activity, SearchActivity.class));
                }
            });
            v.setLayoutParams(layoutParams);
            toolbar.addView(v);

            mBottomBar = BottomBar.attach(this, savedInstanceState);
            // Disable the left bar on tablets and behave exactly the same on mobile and tablets instead.
            mBottomBar.noTabletGoodness();
            // Show all titles even when there's more than three tabs.
            mBottomBar.useFixedMode();
            mBottomBar.noNavBarGoodness();
       /* mBottomBar.setBackgroundColor(getResources().getColor(R.color.white));*/
            mBottomBar.setItems(R.menu.bottombar_menu);
            mBottomBar.setBackgroundColor(mainContext.getResources().getColor(R.color.bottom_bar_background_color));
            mBottomBar.setActiveTabColor(mainContext.getResources().getColor(R.color.brandColor));
           /* View menuItemRefresh = mBottomBar.findViewById(R.id.list);
            menuItemRefresh.setBackgroundResource(R.drawable.ic_bt_add3x);*/
            // Use custom typeface that's located at the "/src/main/assets" directory. If using with
            // custom text appearance, set the text appearance first.
            mBottomBar.setTypeFace("Raleway-Medium.ttf");
            mBottomBar.setOnMenuTabClickListener(new OnMenuTabClickListener() {
                @Override
                public void onMenuTabSelected(@IdRes int menuItemId) {
                    switch (menuItemId) {
                        case R.id.home:
                            activeTab=0;
                            grid.setVisibility(View.VISIBLE);
                            go_to_search.setVisibility(View.VISIBLE);
                            if(!is_list){

                                ItemListFragment.listOrGrid=true;
                                ItemListFragment itemListFragment= ItemListFragment.newInstance(new SalesListModel(), GlobalVariables.TYPE_MULTIPLE_ITEMS,filterModel,locationModel);
                                mainActivityFM.beginTransaction().replace(R.id.container, itemListFragment, "").commit();
                                is_list=true;
                                grid.setImageResource(R.drawable.ic_grid);
                            }else{
                                ItemListFragment.listOrGrid=false;
                                ItemListFragment itemListFragment= ItemListFragment.newInstance(new SalesListModel(), GlobalVariables.TYPE_MULTIPLE_ITEMS,filterModel,locationModel);
                                mainActivityFM.beginTransaction().replace(R.id.container, itemListFragment, "").commit();
                                is_list=false;
                                grid.setImageResource(R.drawable.ic_list);
                            }
                            break;
                        case R.id.garage:
                            activeTab=1;
                            grid.setVisibility(View.VISIBLE);
                            go_to_search.setVisibility(View.VISIBLE);
                            if(is_map){
                                Fragment fragment = new MapFragmentSales().newInstance(GlobalVariables.TYPE_GARAGE,null,null,false,filterModel);
                                mainActivityFM.beginTransaction().replace(R.id.container,fragment,"").commit();
                                grid.setImageResource(R.drawable.ic_grid);
                            }else{
                                GarageSalesListFragment garageSalesListFragment= GarageSalesListFragment.newInstance(salesListModel, GlobalVariables.TYPE_GARAGE,filterModel,locationModel,false);
                                mainActivityFM.beginTransaction().replace(R.id.container, garageSalesListFragment, "").commit();
                                grid.setImageResource(R.drawable.pin_white);
                            }
                            break;
                        case R.id.list:
                            activeTab=2;
                            grid.setVisibility(View.GONE);
                            go_to_search.setVisibility(View.VISIBLE);
                            View menuItemView = findViewById(R.id.list);
                     /*   menuItemView.setBackground(R.drawable.bt_add);*/
                            if(!((Activity)mainContext).isFinishing()){
                                show_popUp(menuItemView);
                            }
                            break;
                        case R.id.message:
                            activeTab=3;
                            grid.setVisibility(View.GONE);
                            go_to_search.setVisibility(View.VISIBLE);
                            if(!GlobalFunctions.is_loggedIn(activity))
                                showSettingsAlert();
                            else{
                                InboxFragment inboxFragment=new InboxFragment();
                                mainActivityFM.beginTransaction().replace(R.id.container, inboxFragment, "").commit();
                            }
                            break;
                        case R.id.profile:
                            activeTab=4;
                            grid.setVisibility(View.GONE);
                            go_to_search.setVisibility(View.GONE);
                            if(GlobalFunctions.is_loggedIn(activity)){
                                ProfileFragment profileFragment=new ProfileFragment();
                                mainActivityFM.beginTransaction().replace(R.id.container, profileFragment, "").commit();
                            }else{
                                showSettingsAlert();
                            }
                            break;
                    }
                }
                @Override
                public void onMenuTabReSelected(@IdRes int menuItemId) {
                    switch (menuItemId) {
                        case R.id.home:
                            grid.setVisibility(View.GONE);
                      /*  ItemListFragment itemListFragment= ItemListFragment.newInstance(new SalesListModel(), GlobalVariables.TYPE_MULTIPLE_ITEMS,filterModel,locationModel);
                        mainActivityFM.beginTransaction().replace(R.id.container, itemListFragment, "").commit();
                */      break
                                ;
                        case R.id.garage:
                            grid.setVisibility(View.VISIBLE);
                        /*GarageSalesListFragment garageSalesListFragment= GarageSalesListFragment.newInstance(salesListModel, GlobalVariables.TYPE_GARAGE,filterModel,locationModel);
                        mainActivityFM.beginTransaction().replace(R.id.container, garageSalesListFragment, "").commit();
               */         break;
                        case R.id.list:
                            grid.setVisibility(View.GONE);
                            View menuItemView = findViewById(R.id.list);
                            if(!((Activity)mainContext).isFinishing()){
                                show_popUp(menuItemView);
                            }
                            break;
                        case R.id.message:
                            grid.setVisibility(View.GONE);
                            if(!GlobalFunctions.is_loggedIn(activity))
                                showSettingsAlert();
                            break;
                        case R.id.profile:
                            grid.setVisibility(View.GONE);
                            if(!GlobalFunctions.is_loggedIn(activity))
                                showSettingsAlert();
                            break;
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG,e.getMessage());
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        try{
            is_active=true;
            if(GlobalFunctions.isNetworkAvailable(this)){
                if(GlobalFunctions.getCategories(this)==null){
                    GlobalFunctions.saveCategories(activity);
                }
                if(GlobalFunctions.getCurrencies(this)==null){
                    GlobalFunctions.saveCurrencies(activity);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        is_active=true;
        Log.d(TAG, "OnResume");
        if(is_reset){
            moveToHome(activity);
        }
        super.onResume();
    }

    public static void replaceFragment(Fragment fragment, @NonNull String tag) {
        if (mainActivityFM != null) {
            mainActivityFM.beginTransaction().replace(R.id.container, fragment, tag).commit();
        }
    }

    public static void addFragment(Fragment fragment, @NonNull String tag) {
        if (mainActivityFM != null) {
            mainActivityFM.beginTransaction().add(R.id.container, fragment, tag).addToBackStack(null).commit();
        }
    }
    public static void setTitle(String title, int resourceID) {
        mTitle = title;
        mResourceID = resourceID;
      /*  restoreToolbar();*/
    }


    public void setAddress(String item_address){
        this.current_location=item_address;
        GlobalFunctions.setSharedPreferenceString(activity,GlobalVariables.CURRENT_LOCATION,item_address);

    }
    public String getAddress(){
        return this.current_location;
    }

    private void getAddressFromLatLng(){
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.getAddress(activity, locServices.getLatitude(),locServices.getLongitude(), new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                LocationModel locationModel =(LocationModel)arg0;
                if(locationModel !=null)
                    setAddress(locationModel.getFormatted_address());
             }

            @Override
            public void OnFailureFromServer(String msg) {
            }

            @Override
            public void OnError(String msg) {
            }
        }, "Delete Sale");


    }
    @Override
    public void onLocationChanged(Location location) {
        if(location!=null){
            if(GlobalFunctions.getSharedPreferenceString(activity,GlobalVariables.CURRENT_LOCATION)==null)
                getAddressFromLatLng();
        }

    }
    @Override
    public void onProviderEnabled(String provider) {

    }
     /*   @Override
        public void onBackPressed() {
            showExitAlert();
          *//*  super.onBackPressed();*//*
        }
    */

    public static void closeThisActivity(){
        if(activity!=null){activity.finish();}
    }
    public void showSettingsAlert(){
        Intent creategarrage = new Intent(activity,LoginActivity.class);
        startActivity(creategarrage);
    }
    public  void show_popUp(View v){
        // Open calender Image
        if(activity!=null){
            final PopupMenu popup = new PopupMenu(activity, v);
            popup.getMenuInflater().inflate(R.menu.listing_menu_option, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    if(item.getTitle().toString().equalsIgnoreCase(activity.getResources().getString(R.string.list_item))){
                        if(GlobalFunctions.is_loggedIn(activity)){
                            Intent i=CreateSaleActivity.newInstance(activity,null,null,null,GlobalVariables.TYPE_HOME,GlobalVariables.TYPE_ITEM);
                            startActivity(i);
                          /*  setDeFaultTabAsHome();*/
                        }else
                            showSettingsAlert();
                    }else if(item.getTitle().toString().equalsIgnoreCase(activity.getResources().getString(R.string.create_sale))){
                        if(GlobalFunctions.is_loggedIn(activity)){
                            Intent i=CreateSaleActivity.newInstance(activity,null,null,null,GlobalVariables.TYPE_HOME,GlobalVariables.TYPE_GARAGE);
                            startActivity(i);
                          /*  setDeFaultTabAsHome();*/
                        }else
                            showSettingsAlert();
                    }/*else {
                    popup.dismiss();
                }*/
                    return true;
                }
            });
            if(popup!=null)
                popup.show();
        }

    }
    public void setDeFaultTabAsHome(){
        if(mBottomBar!=null){
            if(mBottomBar.getCurrentTabPosition()!=0){
                Log.d(TAG,"getCurrentTabPosition########"+mBottomBar.getCurrentTabPosition());

                mBottomBar.selectTabAtPosition(0,false);
            }



        }
    }
    @Override
    public void onPause(){
        super.onPause();
        /*setDeFaultTabAsHome();*/
    }
    @Override
    public void onStop(){
        super.onStop();
        /*setDeFaultTabAsHome();*/
    }
    public static void moveToHome(Activity activity){
        if(!activity.isFinishing()) {
            is_reset=false;
            FragmentTransaction ft = mainActivityFM.beginTransaction();
            ItemListFragment itemListFragment= ItemListFragment.newInstance(new SalesListModel(), GlobalVariables.TYPE_MULTIPLE_ITEMS,filterModel,locationModel);
            mainActivityFM.beginTransaction().replace(R.id.container, itemListFragment, "").commit();
            ft.commitAllowingStateLoss();
            mBottomBar.setDefaultTabPosition(0);
        }

    }
}
