package com.langoor.app.blueshak.search;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.divrt.co.R;
import com.langoor.app.blueshak.PickLocation;
import com.langoor.app.blueshak.bookmarks.BoomarkItemListAdapter;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.home.EndlessRecyclerOnScrollListener;
import com.langoor.app.blueshak.home.ItemListAdapter;
import com.langoor.app.blueshak.item.ProductDetail;
import com.langoor.app.blueshak.services.ServerResponseInterface;
import com.langoor.app.blueshak.services.ServicesMethodsManager;
import com.langoor.app.blueshak.services.model.ErrorModel;
import com.langoor.app.blueshak.services.model.HomeListModel;
import com.langoor.app.blueshak.services.model.ProductModel;
import com.langoor.app.blueshak.services.model.SearchListModel;
import com.langoor.app.blueshak.services.model.SearchModel;
import com.langoor.app.blueshak.services.model.StatusModel;
import com.langoor.app.blueshak.util.LocationListener;
import com.langoor.app.blueshak.util.LocationService;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements LocationListener,SearchView.OnQueryTextListener , SwipeRefreshLayout.OnRefreshListener{
    public static final String TAG = "SearchActivity";
  /*  private SearchListAdapter adapter;*/
    private SearchListAdapter adapter;
    private HomeListModel searchListModel=new HomeListModel();
    private ArrayList<ProductModel> product_list = new ArrayList<ProductModel>();
    private LocationService locServices;
    private SearchView searchView;
    private MenuItem searchMenuItem;
    static  Context context;
    static Activity activity;
    private  EditText searchViewResult;
    private TextView no_search;
    private String lng;
    private String lat;
    private  Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private  String search_query;
    private String type=GlobalVariables.TYPE_SHOP;
    private  int last_page=1;
    int range=50000;
    private  boolean hide_keyBoard=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_search);
        context=this;
        activity=this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("Search");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.brandColor)));
        }
        locServices = new LocationService(this);
        locServices.setListener(this);
        no_search=(TextView)findViewById(R.id.no_search);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
     /*   final SwipeRefreshLayout.OnRefreshListener swipeRefreshListner = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
               *//* swipeRefreshLayout.setRefreshing(true);*//*
                setThisPage();
            }
        };*/
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */

        swipeRefreshLayout.setColorSchemeColors(context.getResources().getColor(R.color.brandColor),
                context.getResources().getColor(R.color.tab_selected),
                context.getResources().getColor(R.color.darkorange),
                context.getResources().getColor(R.color.brandColor));
       /* swipeRefreshLayout.setRefreshing(true);*/
      /*  searchViewResult=(EditText) findViewById(R.id.searchViewResult);*/

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new SearchListAdapter(context, product_list);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new SpacesItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.space)));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(
                gridLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                Log.d(TAG,"onLoadMore##############");
                if(!(current_page>last_page)){
                    Log.d(TAG,"current_page##############"+current_page);
                    /*model.setPage(current_page);*/
                    getDetails(context,range,lat,lng,searchViewResult.getText().toString().trim(),current_page);

                }/*else
                    Toast.makeText(context,"Loaded all items",Toast.LENGTH_LONG).show();*/
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setOnScrollListener(new MyScrollListener(activity) {
            @Override
            public void onMoved(int distance) {
                toolbar.setTranslationY(-distance);
            }
        });
        LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        View v = inflator.inflate(R.layout.actionbar_search, null);
        searchViewResult=(EditText)v.findViewById(R.id.searchViewResult);
        searchViewResult.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hide_keyBoard=true;
                    doSearch();
                    return true;
                }
                return false;
            }
        });
        searchViewResult.addTextChangedListener(new TextChangedListener<EditText>(searchViewResult) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                if(target.getText().toString().length()>2){
                    if(hide_keyBoard)
                        hide_keyBoard=false;
                    doSearch();
                }

                //Do stuff
            }
        });


        GlobalFunctions.showKeyboard(activity);
       /* searchViewResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!locServices.canGetLocation()){
                    startActivity(new Intent(activity,PickLocation.class));
                }else {
                    doSearch();
                }
            }
        });*/
        v.setLayoutParams(layoutParams);
        toolbar.addView(v);
        searchViewResult.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (searchViewResult.getRight() - searchViewResult.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        hide_keyBoard=true;
                        doSearch();
                        return true;
                    }
                }
                return false;
            }
        });

    }
    public void showProgress(){
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        swipeRefreshLayout.setRefreshing(true);
                                    }
                                }
        );
    }
    @Override
    public void onResume() {

        super.onResume();
    }
    public void doSearch() {
        final Location loc=locServices.getLocation();
        lat=Double.toString(loc.getLatitude());
        lng=Double.toString(loc.getLongitude());
        if(loc!=null){
            if(GlobalFunctions.isNetworkAvailable(context)){
                search_query=searchViewResult.getText().toString().trim();
                if(!TextUtils.isEmpty(search_query)){
                    getDetails(context,range,Double.toString(loc.getLatitude()),Double.toString(loc.getLongitude()),searchViewResult.getText().toString().trim(),1);
                }/*else
                    Toast.makeText(context,"Please enter product name",Toast.LENGTH_LONG).show();*/
            }else {
                Snackbar.make(searchViewResult, "Please check your internet connection", Snackbar.LENGTH_LONG)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            @TargetApi(Build.VERSION_CODES.M)
                            public void onClick(View v) {
                                getDetails(context,range,Double.toString(loc.getLatitude()),Double.toString(loc.getLongitude()),searchViewResult.getText().toString(),1);
                            }
                        }).show();
            }


        }
    }
    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        /*if(!GlobalFunctions.isNetworkAvailable(context)){
            Snackbar.make(no_search, "Please check your internet connection", Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {

                        }
                    }).show();
        }else{
            last_page=1;
            setThisPage();
        }*/



    }

    private void setValues(HomeListModel model){
        if(model!=null){
            searchListModel = model;
            last_page=model.getLast_page();
            List<ProductModel> productModels = searchListModel.getItem_list();
            if(productModels!=null){
                if(productModels.size()>0&&recyclerView!=null&&product_list!=null&&adapter!=null){
                    no_search.setVisibility(View.GONE);
                   /* product_list.clear();*/
                    product_list.addAll(productModels);
                    synchronized (adapter){adapter.notifyDataSetChanged();}
                }
            }
        }

    }

    public  void setThisPage(){ swipeRefreshLayout.setRefreshing(true);
        getDetails(context,range,lat,lng,search_query,1);
    }

    private void getDetails(final Context context, int range, String lat, String Lng, String searchText, final int page){
        SearchModel searchModel=new SearchModel();
        searchModel.setType(GlobalVariables.TYPE_SHOP);
        searchModel.setSearch(searchText);
        searchModel.setLatitude(lat);
        searchModel.setLongitude(Lng);
        searchModel.setPage(page);
        searchModel.setRange(GlobalVariables.DISTANCE_SEARCH_MAX_VALUE);
        searchModel.setPriceRange(GlobalVariables.PRICE_MIN_VALUE+","+GlobalVariables.PRICE_MAX_VALUE);
        /* searchModel.setPriceRange(GlobalVariables.DISTANCE_MAX_VALUE);*/
        showProgress();
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.getSearchDetails(context,searchModel,new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                swipeRefreshLayout.setRefreshing(false);
                if(arg0 instanceof StatusModel){
                    if(hide_keyBoard)
                        GlobalFunctions.closeKeyboard(activity);
                    no_search.setVisibility(View.VISIBLE);
                    no_search.setText("No items found for your search");
                    StatusModel statusModel = (StatusModel) arg0;
                    refreshView();
                    /*Toast.makeText(context, "No items found your search", Toast.LENGTH_SHORT).show();*/
                }else if(arg0 instanceof ErrorModel){
                    if(hide_keyBoard)
                        GlobalFunctions.closeKeyboard(activity);
                    no_search.setVisibility(View.VISIBLE);
                    no_search.setText("No items found your search");
                    ErrorModel errorModel = (ErrorModel) arg0;
                    refreshView();
                    String msg = errorModel.getError()!=null ? errorModel.getError() : errorModel.getMessage();
                 /*   Toast.makeText(context, "No items found for your search", Toast.LENGTH_SHORT).show();*/
                }else if(arg0 instanceof HomeListModel){
                    if(hide_keyBoard)
                        GlobalFunctions.closeKeyboard(activity);
                    if(page==1&&product_list!=null)
                        product_list.clear();
                    HomeListModel model = (HomeListModel) arg0;
                    setValues(model);
                }
            }

            @Override
            public void OnFailureFromServer(String msg) {
                if(hide_keyBoard)
                    GlobalFunctions.closeKeyboard(activity);
                swipeRefreshLayout.setRefreshing(false);
                no_search.setVisibility(View.VISIBLE);
                refreshView();
                /*Toast.makeText(context, "No items found for your search", Toast.LENGTH_SHORT).show();*/
            }

            @Override
            public void OnError(String msg) {
                swipeRefreshLayout.setRefreshing(false);
                if(hide_keyBoard)
                    GlobalFunctions.closeKeyboard(activity);
                refreshView();
               /* Toast.makeText(context, "No items found for your search", Toast.LENGTH_SHORT).show();*/
                no_search.setVisibility(View.VISIBLE);

            }
        }, "Get Search List");

    }
    @Override
    public void onLocationChanged(Location loc) {
        if(loc!=null){
            lat=loc.getLatitude()+"";
            lng=loc.getLongitude()+"";
            locServices.removeListener();
        }else{
            Toast.makeText(this, "Fetching Location", Toast.LENGTH_SHORT).show();
        }
        GlobalFunctions.closeKeyboard(this);
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    public void refreshView(){
        if(product_list!=null)
            product_list.clear();
        synchronized (adapter){adapter.notifyDataSetChanged();}
    }
    @Override
    public boolean onQueryTextChange(String newText) {
        if (!TextUtils.isEmpty(newText)) {
            if(newText.length()>2)
                getDetails(this,range,lat,lng,searchView.getQuery().toString(),1);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // TODO Auto-generated method stub
        return false;
    }

    public abstract class MyScrollListener extends RecyclerView.OnScrollListener {
        private int toolbarOffset = 0;
        private int toolbarHeight;

        public MyScrollListener(Context context) {
            int[] actionBarAttr = new int[] { android.R.attr.actionBarSize };
            TypedArray a = context.obtainStyledAttributes(actionBarAttr);
            toolbarHeight = (int) a.getDimension(0, 0) + 10;
            a.recycle();
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            clipToolbarOffset();
            onMoved(toolbarOffset);

            if((toolbarOffset < toolbarHeight && dy>0) || (toolbarOffset > 0 && dy<0)) {
                toolbarOffset += dy;
            }
        }

        private void clipToolbarOffset() {
            if(toolbarOffset > toolbarHeight) {
                toolbarOffset = toolbarHeight;
            } else if(toolbarOffset < 0) {
                toolbarOffset = 0;
            }
        }

        public abstract void onMoved(int distance);
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
    public abstract class TextChangedListener<T> implements TextWatcher {
        private T target;

        public TextChangedListener(T target) {
            this.target = target;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            this.onTextChanged(target, s);
        }

        public abstract void onTextChanged(T target, Editable s);
    }
}
