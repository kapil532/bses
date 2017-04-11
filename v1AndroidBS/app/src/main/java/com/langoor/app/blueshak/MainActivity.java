package com.langoor.app.blueshak;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.divrt.co.R;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.langoor.app.blueshak.Messaging.activity.MessageActivity;
import com.langoor.app.blueshak.Messaging.activity.PushActivity;
import com.langoor.app.blueshak.bookmarks.BookMarkActivty;
import com.langoor.app.blueshak.garage.*;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.home.GarageSalesListFragment;
import com.langoor.app.blueshak.home.ItemListFragment;
import com.langoor.app.blueshak.home.list_fragments.SalesList;
import com.langoor.app.blueshak.login.LoginActivity;
import com.langoor.app.blueshak.profile.ProfileActivity;
import com.langoor.app.blueshak.search.SearchActivity;
import com.langoor.app.blueshak.services.ServerResponseInterface;
import com.langoor.app.blueshak.services.ServicesMethodsManager;
import com.langoor.app.blueshak.services.model.FilterModel;
import com.langoor.app.blueshak.services.model.LocationModel;
import com.langoor.app.blueshak.services.model.SalesListModel;
import com.langoor.app.blueshak.services.model.SalesListModelNew;
import com.langoor.app.blueshak.util.LocationListener;
import com.langoor.app.blueshak.util.LocationService;
import com.langoor.app.blueshak.view.AlertDialog;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;
import java.util.List;

public class MainActivity extends /*AppCompatActivity*/PushActivity implements LocationListener{
    private BottomBar mBottomBar;
    private static final String TAG = "MainActivity";
    static Context mainContext;
    public static FragmentManager mainActivityFM;
    static Window mainWindow;
    private HomePagerAdapter adapter;
    private TabLayout tabLayout;
    static Toolbar toolbar;
    static ActionBar actionBar;
    static String mTitle;
    static int mResourceID;
    static Menu menu;
    static TextView toolbar_title;
    LocationService locServices;
    public static Activity activity = null;
    private SalesListModelNew salesListModel = new SalesListModelNew();
    static GlobalFunctions globalFunctions = new GlobalFunctions();
    static GlobalVariables globalVariables = new GlobalVariables();
    String current_location;
    private SearchView mSearchView;
    public static final String MAIN_ACTIVITY_LOCATION_MODEL_SERIALIZE="locationModelSerialize";
    public static final String MAIN_ACTIVITY_FILTER_MODEL_SERIALIZE="filterModelSerialize";
    private  LocationModel locationModel=null;
    private  FilterModel filterModel=null;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    public static Intent newInstance(Context context, LocationModel locationModel, FilterModel filterModel){
        Intent mIntent = new Intent(context,MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MAIN_ACTIVITY_LOCATION_MODEL_SERIALIZE, locationModel);
        bundle.putSerializable(MAIN_ACTIVITY_FILTER_MODEL_SERIALIZE, filterModel);
        mIntent.putExtras(bundle);
        return mIntent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        activity = this;
        mainContext = this;
        mainActivityFM = getSupportFragmentManager();
        mainWindow = getWindow();

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
           startActivity(new Intent(this,PickLocation.class));}
        else  {
            if(GlobalFunctions.getSharedPreferenceString(activity,GlobalVariables.CURRENT_LOCATION)==null)
                getAddressFromLatLng();

        }
        locServices.setListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_logo);
        View logoView =toolbar.getChildAt(1);
        logoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDeFaultTabAsHome();
            }
        });
        LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        View v = inflator.inflate(R.layout.actionbar_search_view, null);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
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

        mBottomBar.setOnMenuTabClickListener(new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                switch (menuItemId) {
                    case R.id.home:
                        ItemListFragment itemListFragment= ItemListFragment.newInstance(new SalesListModel(), GlobalVariables.TYPE_MULTIPLE_ITEMS,filterModel,locationModel);
                        mainActivityFM.beginTransaction().replace(R.id.container, itemListFragment, "").commit();

                        break;
                    case R.id.garage:
                        GarageSalesListFragment garageSalesListFragment= GarageSalesListFragment.newInstance(salesListModel, GlobalVariables.TYPE_GARAGE,filterModel,locationModel);
                        mainActivityFM.beginTransaction().replace(R.id.container, garageSalesListFragment, "").commit();

                        /*if(GlobalFunctions.is_loggedIn(activity)){
                            Intent i=CreateSaleActivity.newInstance(activity,null,null,null,GlobalVariables.TYPE_HOME,GlobalVariables.TYPE_GARAGE);
                            startActivity(i);
                        }else
                            showSettingsAlert();*/
                        break;
                    case R.id.list:
                        View menuItemView = findViewById(R.id.list);
                        show_popUp(menuItemView);
                        break;
                    case R.id.message:
                        if(!GlobalFunctions.is_loggedIn(activity))
                            showSettingsAlert();
                        else{
                          startActivity(new Intent(MainActivity.this,MessageActivity.class));
                           /* setDeFaultTabAsHome();*/
                        }
                        break;
                    case R.id.profile:
                        if(GlobalFunctions.is_loggedIn(activity)){
                            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                           /* setDeFaultTabAsHome();*/
                        }else
                            showSettingsAlert();
                        break;
                }
            }
            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                switch (menuItemId) {
                    case R.id.home:
                      /*  ItemListFragment itemListFragment= ItemListFragment.newInstance(new SalesListModel(), GlobalVariables.TYPE_MULTIPLE_ITEMS,filterModel,locationModel);
                        mainActivityFM.beginTransaction().replace(R.id.container, itemListFragment, "").commit();
                */      break;
                    case R.id.garage:
                        /*GarageSalesListFragment garageSalesListFragment= GarageSalesListFragment.newInstance(salesListModel, GlobalVariables.TYPE_GARAGE,filterModel,locationModel);
                        mainActivityFM.beginTransaction().replace(R.id.container, garageSalesListFragment, "").commit();
               */         break;
                    case R.id.list:
                        View menuItemView = findViewById(R.id.list);
                        show_popUp(menuItemView);

                        break;
                    case R.id.message:
                     if(!GlobalFunctions.is_loggedIn(activity))
                            showSettingsAlert();
                        else{
                           /* startActivity(new Intent(MainActivity.this,MessageActivity.class));*/
                     }
                   break;
                    case R.id.profile:
                      if(GlobalFunctions.is_loggedIn(activity)){
                            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                      }else
                            showSettingsAlert();
                    break;
                }
            }
        });
        mBottomBar.setActiveTabColor(getResources().getColor(R.color.tab_selected));
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(GlobalFunctions.isNetworkAvailable(this)){
            if(GlobalFunctions.getCategories(this)==null){
                GlobalFunctions.saveCategories(activity);
            }
            if(GlobalFunctions.getPostalCodes(this)==null){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        GlobalFunctions.savePostalCodes(activity);
                    }
                }).start();
            }
          /*  if(!locServices.canGetLocation()){
                startActivity(new Intent(this,PickLocation.class));}*/
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "OnResume");
       /* if(mBottomBar!=null)
            mBottomBar.setDefaultTabPosition(0);
*/
        if(!locServices.canGetLocation()){
            startActivity(new Intent(this,PickLocation.class));}

        setDeFaultTabAsHome();

        AppController.getInstance().trackScreenView(MainActivity.TAG);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        /*  MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setIconifiedByDefault(true);
        setupSearchView(searchItem);*/
        return true;
    }

    private void setupSearchView(MenuItem searchItem) {

        if (isAlwaysExpanded()) {
            mSearchView.setIconifiedByDefault(true);
        } else {
            searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
                    | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();

            // Try to use the "applications" global search provider
            SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
            for (SearchableInfo inf : searchables) {
                if (inf.getSuggestAuthority() != null
                        && inf.getSuggestAuthority().startsWith("applications")) {
                    info = inf;
                }
            }
            mSearchView.setSearchableInfo(info);
        }

       /* mSearchView.setOnQueryTextListener(this);*/
    }
    protected boolean isAlwaysExpanded() {
        return false;
    }
/*    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Necessary to restore the BottomBar's state, otherwise we would
        // lose the current tab on orientation change.
        setDeFaultTabAsHome();
        mBottomBar.onSaveInstanceState(outState);

    }*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_bookmarks) {
            if(GlobalFunctions.is_loggedIn(activity))
                startActivity(new Intent(MainActivity.this, BookMarkActivty.class));
            else
                showSettingsAlert();
            return true;
        }else if(id==android.R.id.home) {
            Toast.makeText(this,"Home", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
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


    public static void accessPermissions(Activity activity) {
        int permissionCheck_getAccounts = ContextCompat.checkSelfPermission(activity, Manifest.permission.GET_ACCOUNTS);
        int permissionCheck_vibrate = ContextCompat.checkSelfPermission(activity, Manifest.permission.VIBRATE);
        int permissionCheck_lockwake = ContextCompat.checkSelfPermission(activity, Manifest.permission.WAKE_LOCK);
        int permissionCheck_internet = ContextCompat.checkSelfPermission(activity, Manifest.permission.INTERNET);
        int permissionCheck_Access_internet = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_NETWORK_STATE);
        int permissionCheck_Access_wifi = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_WIFI_STATE);
        int permissionCheck_External_storage = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck_Internal_storage = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        int permissionCheck_cam = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        if (permissionCheck_lockwake != PackageManager.PERMISSION_GRANTED || permissionCheck_Access_wifi != PackageManager.PERMISSION_GRANTED || permissionCheck_getAccounts != PackageManager.PERMISSION_GRANTED || permissionCheck_vibrate != PackageManager.PERMISSION_GRANTED || permissionCheck_internet != PackageManager.PERMISSION_GRANTED || permissionCheck_Access_internet != PackageManager.PERMISSION_GRANTED || permissionCheck_External_storage != PackageManager.PERMISSION_GRANTED || permissionCheck_cam != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WAKE_LOCK) && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_WIFI_STATE) && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.GET_ACCOUNTS) && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.VIBRATE) && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.INTERNET) && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_NETWORK_STATE) && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WAKE_LOCK, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.VIBRATE, Manifest.permission.GET_ACCOUNTS, Manifest.permission.CAMERA}, globalVariables.PERMISSIONS_REQUEST_CALENDER);
            }
        }

        if (permissionCheck_internet != PackageManager.PERMISSION_GRANTED || permissionCheck_Access_internet != PackageManager.PERMISSION_GRANTED || permissionCheck_External_storage != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.INTERNET) && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_NETWORK_STATE) && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, globalVariables.PERMISSIONS_REQUEST_PRIMARY);
            }
        }


        if(permissionCheck_cam != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, globalVariables.PERMISSIONS_REQUEST_CAMERA);
            }
        }
        if(permissionCheck_Internal_storage != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, globalVariables.PERMISSIONS_REQUEST_CAMERA);
            }
        }
        GlobalFunctions.setSharedPreferenceString(activity, GlobalVariables.SHARED_PREFERENCE_PERMISSION, "true");

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
    public void showExitAlert(){
        final com.langoor.app.blueshak.view.AlertDialog alertDialog = new com.langoor.app.blueshak.view.AlertDialog(activity);
        alertDialog.setMessage("You are about to exit from the APP.");
        alertDialog.setPositiveButton("EXIT", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              closeThisActivity();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("NO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    public static void closeThisActivity(){
        if(activity!=null){activity.finish();}
    }
    public void showSettingsAlert(){
       /* setDeFaultTabAsHome();*/
        final com.langoor.app.blueshak.view.AlertDialog alertDialog = new com.langoor.app.blueshak.view.AlertDialog(activity);
        alertDialog.setTitle("Login/Register");
        alertDialog.setMessage("Please Login/Register to continue");
        alertDialog.setPositiveButton("Continue", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent creategarrage = LoginActivity.newInstance(activity,null,null);
                startActivity(creategarrage);
                /*closeThisActivity();*/
                alertDialog.dismiss();

            }
        });
        alertDialog.show();
    }
    public void settingsrequest()
    {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(null, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                      Toast.makeText(mainContext,"onActivityResult",Toast.LENGTH_LONG).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        settingsrequest();//keep asking if imp or do whatever
                        break;
                }
                break;
        }
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
}
