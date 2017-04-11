package com.langoor.app.blueshak.home;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.divrt.co.R;
import com.langoor.app.blueshak.AppController;
import com.langoor.app.blueshak.MainActivity;

import com.langoor.app.blueshak.PickLocation;
import com.langoor.app.blueshak.filter.FilterActivity;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.services.ServerResponseInterface;
import com.langoor.app.blueshak.services.ServicesMethodsManager;
import com.langoor.app.blueshak.services.model.FilterModel;
import com.langoor.app.blueshak.services.model.HomeListModel;
import com.langoor.app.blueshak.services.model.ItemListModel;
import com.langoor.app.blueshak.services.model.LocationModel;
import com.langoor.app.blueshak.services.model.ProductModel;
import com.langoor.app.blueshak.services.model.SalesListModel;
import com.langoor.app.blueshak.services.model.SalesModel;
import com.langoor.app.blueshak.util.LocationListener;
import com.langoor.app.blueshak.util.LocationService;

import java.util.ArrayList;
import java.util.List;


public class ItemListFragment extends Fragment implements LocationListener/*,onFilterChange*/ , SwipeRefreshLayout.OnRefreshListener{

    public static final String TAG = "ItemListFragment";
    public static final String SALES_LIST_GARAGGE_SALE_MODEL_SERIALIZE="SalesListGarageSaleModelSerialize";
    public static final String SALE_FILTER="filtermodel";
    public static final String LOCATION_MODEL="locationmodel";

    public static final String SALES_LIST_GARAGGE_SALE_MODEL_TYPE="SalesListGarageSaleModelType";

    private Context context;

    private TextView no_sales;

    private static  Activity activity;
    private LayoutInflater inflater = null;
    private SalesListModel salesListModel=new SalesListModel();
    private HomeListModel itemListModel=new HomeListModel();


    private ItemListAdapter adapter;
    private ArrayList<SalesModel> list = new ArrayList<SalesModel>();
    private ArrayList<ProductModel> product_list = new ArrayList<ProductModel>();

    private LocationService locServices;
    private boolean location_available;
    private RecyclerView recyclerView;
    private static FilterModel model=null;
    private  LocationModel locationModel= null;
    private TextView location,filter_tag;
    private ImageView location_arrow,filter;
    public String item_address;
    private String no_items_found;
    private  int last_page=0;
    private String Sydney_latitude="-33.8688196";
    private String Sydney_longitude="151.2092955";
    private  EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    int minPriceValue=0, maxPriceValue=GlobalVariables.PRICE_MAX_VALUE, minDistanceValue=0, maxDistanceValue=GlobalVariables.DISTANCE_MAX_VALUE;
    public static ItemListFragment newInstance(SalesListModel salesListModel, String type,FilterModel filterModel,LocationModel locationModel){
        ItemListFragment saleFragment = new ItemListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(SALES_LIST_GARAGGE_SALE_MODEL_SERIALIZE, salesListModel);
        bundle.putSerializable(SALE_FILTER, filterModel);
        bundle.putSerializable(LOCATION_MODEL,locationModel);
        bundle.putString(SALES_LIST_GARAGGE_SALE_MODEL_TYPE, type);
        saleFragment.setArguments(bundle);
        return saleFragment;
    }
    private Toolbar toolbar;
    /* @InjectView(R.id.swipe_container)*/
    private SwipeRefreshLayout swipeRefreshLayout;
    RelativeLayout header_content;
    private Handler handler=new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.sales_list_item_fragment, container, false);
        context= getActivity();
        activity= getActivity();
        this.inflater = inflater;
        locServices = new LocationService(activity);
        locServices.setListener(this);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
      /*  final SwipeRefreshLayout.OnRefreshListener swipeRefreshListner = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
              *//*  swipeRefreshLayout.setRefreshing(true);
               *//**//* setThisPage();*//*
            }
        };*/
        no_items_found=context.getResources().getString(R.string.no_items_found);
        swipeRefreshLayout.setColorSchemeColors(context.getResources().getColor(R.color.brandColor),
                context.getResources().getColor(R.color.tab_selected),
                context.getResources().getColor(R.color.darkorange),
                context.getResources().getColor(R.color.brandColor));
        salesListModel = (SalesListModel) getArguments().getSerializable(SALES_LIST_GARAGGE_SALE_MODEL_SERIALIZE);
        locationModel = (LocationModel) getArguments().getSerializable(LOCATION_MODEL);
        model = (FilterModel) getArguments().getSerializable(SALE_FILTER);
        salesListModel = (SalesListModel) getArguments().getSerializable(SALES_LIST_GARAGGE_SALE_MODEL_SERIALIZE);

        if(model==null){
            model=new FilterModel();
            if(locationModel!=null){
                Log.d(TAG,"######locationModel####"+locationModel.toString());
                Log.d(TAG,"######getState####"+locationModel.getState());
                Log.d(TAG,"######getLatitude####"+locationModel.getLatitude());
                Log.d(TAG,"######getLongitude####"+locationModel.getLongitude());
                if(locationModel.getCity()!=null&&locationModel.getState()!=null)
                    item_address=locationModel.getCity()+" "+locationModel.getState();
                else
                    item_address="Sydney"+", Australia";
                if(!locationModel.getLatitude().equalsIgnoreCase("0.0")&&!locationModel.getLongitude().equalsIgnoreCase("0.0")){
                    model.setLatitude(locationModel.getLatitude()+"");
                    model.setLongitude(locationModel.getLongitude()+"");
                }else{
                    model.setLatitude(GlobalVariables.Sydney_latitude);
                    model.setLongitude(GlobalVariables.Sydney_longitude);
                }
                model.setRange(maxDistanceValue);
                model.setAvailable(true);
                model.setPickup(true);
                model.setShipable(true);
                model.setType(GlobalVariables.TYPE_SHOP);
                model.setPriceRange(minPriceValue+","+maxPriceValue);
            }else{
                if(!locServices.canGetLocation()){
                    location_available=false;
                   /* locServices.showSettingsAlert();*/
                    model.setLatitude(Sydney_latitude);
                    model.setLongitude(Sydney_latitude);
                    model.setRange(maxDistanceValue);
                    model.setAvailable(true);
                    model.setPickup(true);
                    model.setShipable(true);
                    model.setType(GlobalVariables.TYPE_SHOP);
                    model.setPriceRange(minPriceValue+","+maxPriceValue);
         /*   getItemLists(context,model);*/
                }else{
                    location_available=true;
                    Location loc = locServices.getLocation();
                    if(loc!=null){
                        model.setLatitude(loc.getLatitude()+"");
                        model.setLongitude(loc.getLongitude()+"");
                        model.setRange(maxDistanceValue);
                        model.setAvailable(true);
                        model.setPickup(true);
                        model.setShipable(true);
                        model.setType(GlobalVariables.TYPE_SHOP);
                        model.setPriceRange(minPriceValue+","+maxPriceValue);
                /*getItemLists(context,model);*/
                    }
                }
            }
        }else{
            item_address=model.getLocation();
        }
        location = (TextView)view.findViewById(R.id.location);
        filter_tag = (TextView)view.findViewById(R.id.filter_tag);
        location_arrow=(ImageView)view.findViewById(R.id.location_arrow);
        filter=(ImageView)view.findViewById(R.id.filter);
        toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar);
        no_sales = (TextView) view.findViewById(R.id.no_sales);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        adapter = new ItemListAdapter(context, product_list);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new SpacesItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.space)));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
       /* recyclerView.setOnScrollListener(new MyScrollListener(activity) {
            @Override
            public void onMoved(int distance) {
                toolbar.setTranslationY(-distance);
            }
        });*/
        endlessRecyclerOnScrollListener=new EndlessRecyclerOnScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if(!(current_page>last_page)){
                    model.setPage(current_page);
                    getItemLists(context,model);
                }
            }
        };
        recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=PickLocation.newInstance(context,GlobalVariables.TYPE_HOME);
                context.startActivity(intent);
            }
        });
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*context.startActivity(new Intent(context, FilterActivity.class));*/
                Intent filter_intent=FilterActivity.newInstance(context,locationModel);
                startActivity(filter_intent);
            }
        });
        filter_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent filter_intent=FilterActivity.newInstance(context,locationModel);
                startActivity(filter_intent);
             /*   context.startActivity(new Intent(context, FilterActivity.class));*/
            }
        });

        location_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=PickLocation.newInstance(context,GlobalVariables.TYPE_HOME);
                context.startActivity(intent);
            }
        });
        if(!GlobalFunctions.isNetworkAvailable(context)){
            Toast.makeText(context,"Please check your internet connection",Toast.LENGTH_LONG).show();
        }else{
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setThisPage();
                }
            });

        }
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onResume() {
        endlessRecyclerOnScrollListener.reset(0, true);
        AppController.getInstance().trackScreenView(GarageSalesListFragment.TAG);
        MainActivity.setTitle(getString(R.string.item),0);
        super.onResume();
    }
    private void setThisPage(){
        if(TextUtils.isEmpty(item_address)&&model.getLongitude()!=null&&model.getLongitude()!=null)
            getAddressFromLatLng(model.getLatitude(),model.getLongitude());
        else
            setLocation(item_address);

        if(product_list!=null){
            product_list.clear();
        /*    synchronized (adapter){adapter.notifyDataSetChanged();*/
             /*   recyclerView.setAdapter(null);
            adapter=new ItemListAdapter(activity,product_list);
            recyclerView.setAdapter(adapter);*/
        }
        if(model!=null){
            model.setPage(1);
            getItemLists(context,model);
        }

    }
    private void setLocation(String item_address){
        Log.d(TAG,"#########item_address##############"+item_address);
        if(TextUtils.isEmpty(item_address))
            item_address ="Pick Location";
        String current_location="";
        if(!item_address.equalsIgnoreCase("Pick Location")){
            current_location="Listing near "+item_address;
            SpannableString ss = new SpannableString(current_location);
            ss.setSpan(new MyClickableSpan(),12,current_location.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            location.setText(ss);
            location.setMovementMethod(LinkMovementMethod.getInstance());
        }else{
            location.setText(item_address);
            location.setTextColor(context.getResources().getColor(R.color.tab_selected));
            location.setGravity(Gravity.CENTER);

        }
    }

    private void setItemListValues(HomeListModel model){
        if(model!=null){
            String str = "";
            itemListModel = model;
            last_page=itemListModel.getLast_page();
            List<ProductModel> productModels = itemListModel.getItem_list();
            if(productModels!=null){
                if(productModels.size()>0&&recyclerView!=null&&list!=null&&adapter!=null){
                    product_list.addAll(productModels);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (adapter){adapter.notifyDataSetChanged();}
                        }
                    });

                }
            }
        }

    }
    private void getItemLists(final Context context, FilterModel filterModel){
    /*    showProgress();*/
        Log.d(TAG, "GetItemLists filterModel :"+filterModel.toString());
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.getHomeList(context, filterModel, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                swipeRefreshLayout.setRefreshing(false);
                Log.d(TAG, "onSuccess Response");
                itemListModel = (HomeListModel) arg0;
                String str = itemListModel.toString();
                setItemListValues(itemListModel);
                no_sales.setVisibility(View.GONE);
            }

            @Override
            public void OnFailureFromServer(String msg) {
                swipeRefreshLayout.setRefreshing(false);
                no_sales.setVisibility(View.VISIBLE);
                no_sales.setText(no_items_found);
                Log.d(TAG, "OnFailureFromServer################"+msg);
              /*  Toast.makeText(context,no_items_found,Toast.LENGTH_LONG).show();*/

            }

            @Override
            public void OnError(String msg) {
                swipeRefreshLayout.setRefreshing(false);
                Log.d(TAG,"#######OnError######"+ msg);
                no_sales.setVisibility(View.VISIBLE);
                no_sales.setText(no_items_found);
                /*Toast.makeText(context,no_items_found,Toast.LENGTH_LONG).show();*/
            }
        }, "List Sales");

    }
    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);

     /*   setThisPage();*/
    }
    public void showProgress(){
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                    }
                                }
        );
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onLocationChanged(Location location) {
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
    class MyClickableSpan extends ClickableSpan { //clickable span
        public void onClick(View textView) {

        }
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(context.getResources().getColor(R.color.tab_selected));//set text color
          /*  ds.setUnderlineText(true);*/
            //ds.setStyle(Typeface.BOLD);
          /*  ds.setTypeface(Typeface.DEFAULT_BOLD);*/
        }
    }
    private void addMoreItems(final Context context, FilterModel filterModel){
        showProgress();
        Log.d(TAG, "FilterModel :"+filterModel.toString());
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.getHomeList(context,filterModel, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                swipeRefreshLayout.setRefreshing(false);
                no_sales.setVisibility(View.GONE);
                Log.d(TAG, "onSuccess Response");
                itemListModel = (HomeListModel)arg0;
                String str = itemListModel.toString();
                setItemListValues(itemListModel);
            }

            @Override
            public void OnFailureFromServer(String msg) {
                swipeRefreshLayout.setRefreshing(false);
                no_sales.setVisibility(View.VISIBLE);
                no_sales.setText(no_items_found);
                Log.d(TAG, "OnFailureFromServer################"+msg);
                /*Toast.makeText(context,no_items_found,Toast.LENGTH_LONG).show();*/

            }

            @Override
            public void OnError(String msg) {
                swipeRefreshLayout.setRefreshing(false);
                Log.d(TAG,"#######OnError######"+ msg);
                no_sales.setVisibility(View.VISIBLE);
                no_sales.setText(no_items_found);
                /*Toast.makeText(context,no_items_found,Toast.LENGTH_LONG).show();*/
            }
        }, "List Sales");

    }

    private void getAddressFromLatLng(final String lat,final String lng){
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.getAddress(activity,Double.parseDouble(lat),Double.parseDouble(lng), new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                LocationModel locationModel_ =(LocationModel)arg0;
                locationModel=locationModel_;
                locationModel.setLatitude(lat);
                locationModel.setLongitude(lng);
                if(locationModel_ !=null)
                    setLocation(locationModel_.getCity()+" "+locationModel_.getState());
                    GlobalFunctions.setSharedPreferenceString(context,GlobalVariables.CURRENT_LOCATION,locationModel_.getCity());
            }
            @Override
            public void OnFailureFromServer(String msg) {
            }

            @Override
            public void OnError(String msg) {
            }
        }, "Get AddressFromLatLng");


    }

}

