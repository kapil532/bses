package com.langoor.app.blueshak.home.list_fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.divrt.co.R;
import com.langoor.app.blueshak.MainActivity;

import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.home.SaleFragment;
import com.langoor.app.blueshak.home.SalesListAdapter;
import com.langoor.app.blueshak.services.ServerResponseInterface;
import com.langoor.app.blueshak.services.ServicesMethodsManager;
import com.langoor.app.blueshak.services.model.FilterModel;
import com.langoor.app.blueshak.services.model.SalesListModel;
import com.langoor.app.blueshak.services.model.SalesModel;
import com.langoor.app.blueshak.util.LocationListener;
import com.langoor.app.blueshak.util.LocationService;
import com.langoor.app.blueshak.view_sales.MapFragmentSales;
import java.util.ArrayList;
import java.util.List;

public class SalesList extends Fragment implements LocationListener/*,onFilterChange*/ {

    public static final String TAG = "SalesList";

    public static final String SALES_LIST_GARAGGE_SALE_MODEL_SERIALIZE="SalesListGarageSaleModelSerialize";
    public static final String SALE_FILTER="filtermodel";
    public static final String SALES_LIST_GARAGGE_SALE_MODEL_TYPE="SalesListGarageSaleModelType";
    public static final String IS_FROM_FILTER="from_filter";
    private Button retry;
    private Context context;
    private Boolean from_filter=false;
    private TextView no_sales;
    private String no_sales_text;
    private Activity activity;
    private LayoutInflater inflater = null;
    private SalesListModel salesListModel=new SalesListModel();
    private ListView listview;
    private SalesListAdapter adapter;
    private TextView total_tv = null,sale_header_name;
    private String header_name;
    private ArrayList<SalesModel> list = new ArrayList<SalesModel>();
    private String type = GlobalVariables.TYPE_GARAGE;
    private LocationService locServices;
    private FloatingActionButton mapFAB, settingFAB;
    private FilterModel filterModel = new FilterModel();
    public static SalesList newInstance(SalesListModel salesListModel, String type, FilterModel filterModel, boolean is_from_filter){
        SalesList saleFragment = new SalesList();
        Bundle bundle = new Bundle();
        bundle.putSerializable(SALES_LIST_GARAGGE_SALE_MODEL_SERIALIZE, salesListModel);
        bundle.putSerializable(SALE_FILTER, filterModel);
        bundle.putString(SALES_LIST_GARAGGE_SALE_MODEL_TYPE, type);
        bundle.putBoolean(IS_FROM_FILTER, is_from_filter);
        saleFragment.setArguments(bundle);
        return saleFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.sales_list_fragment, container, false);
        context= getActivity();
        activity= getActivity();
        this.inflater = inflater;

       /* Filter filter=new Filter();
        filter.setListener(this);*/
        salesListModel = (SalesListModel) getArguments().getSerializable(SALES_LIST_GARAGGE_SALE_MODEL_SERIALIZE);
        type = getArguments().getString(SALES_LIST_GARAGGE_SALE_MODEL_TYPE);
        listview = (ListView) view.findViewById(R.id.sales_list_fragment_list_view);
        mapFAB = (FloatingActionButton) view.findViewById(R.id.sales_list_fragment_map_fab);
        settingFAB = (FloatingActionButton) view.findViewById(R.id.sales_list_fragment_filter_fab);
        no_sales = (TextView) view.findViewById(R.id.no_sales);
        sale_header_name= (TextView) view.findViewById(R.id.sale_header_name);
        retry= (Button) view.findViewById(R.id.retry);
        locServices = new LocationService(activity);
        // if(!locServices.canGetLocation()){locServices.showSettingsAlert();}
        locServices.setListener(this);
        adapter = new SalesListAdapter(inflater,list,activity);
        listview.setAdapter(adapter);

       /* filterModel = (FilterModel) getArguments().getSerializable(SALE_FILTER);
        from_filter= getArguments().getBoolean(IS_FROM_FILTER);
        if(from_filter){
            if (filterModel!=null && !TextUtils.isEmpty(filterModel.getLongitude())){
                if(filterModel!=null)
                    getLists(context,filterModel);
            }
        }else{*/
            if(type.equalsIgnoreCase(GlobalVariables.TYPE_GARAGE)&&salesListModel!=null) {
                setValues(salesListModel);
                mapFAB.setVisibility(View.VISIBLE);
            }else{
                mapFAB.setVisibility(View.GONE);
                Location loc = locServices.getLocation();
                //getLists(getContext(), "-33.839838", "151.212033", 10, type);
                if(loc!=null){
                    FilterModel model = new FilterModel();
                    model.setLatitude(loc.getLatitude()+"");
                    model.setLongitude(loc.getLongitude()+"");
                    model.setRange(10);
                    model.setAvailable(true);
                    model.setPickup(true);
                    model.setShipable(true);
                    model.setType(type);
                    model.setPriceRange("0,10000");
                    getLists(context,model);
                    locServices.removeListener();
                }
            }
      //  }
        mapFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.replaceFragment(MapFragmentSales.newInstance(type,null,null,false), MapFragmentSales.TAG);
            }
        });

        /*settingFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.addFragment(Filter.newInstance(context,type,filterModel), SaleFragment.TAG);

            }
        });
*/
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.addFragment(SaleFragment.newInstance(list.get(position),type), SaleFragment.TAG);
            }
        });




        return view;
    }

    @Override
    public void onResume() {
        if(type.equalsIgnoreCase(GlobalVariables.TYPE_GARAGE)){
            MainActivity.setTitle(getString(R.string.garageSale),0);
            no_sales_text="No Garage Sales found";
        }else{
            MainActivity.setTitle(getString(R.string.multipleSale),0);
            no_sales_text="No Multiples Sales found";
        }
        if(!GlobalFunctions.isNetworkAvailable(context)){
            sale_header_name.setText("No connection");
            Snackbar.make(no_sales, "Please check your internet connection", Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {

                        }
                    }).show();
        }
        super.onResume();
    }
    public void setPage(){
        if(from_filter){
            if (filterModel!=null && !TextUtils.isEmpty(filterModel.getLongitude())){
                if(filterModel!=null)
                    getLists(context,filterModel);
            }
        }else{
            if(type.equalsIgnoreCase(GlobalVariables.TYPE_GARAGE)&&salesListModel!=null) {
                setValues(salesListModel);
                mapFAB.setVisibility(View.VISIBLE);
            }else{
                mapFAB.setVisibility(View.GONE);
                Location loc = locServices.getLocation();
                //getLists(getContext(), "-33.839838", "151.212033", 10, type);
                if(loc!=null){
                    FilterModel model = new FilterModel();
                    model.setLatitude(loc.getLatitude()+"");
                    model.setLongitude(loc.getLongitude()+"");
                    model.setRange(10);
                    model.setAvailable(true);
                    model.setPickup(true);
                    model.setShipable(true);
                    model.setType(type);
                    model.setPriceRange("0,10000");
                    getLists(context,model);
                    locServices.removeListener();
                }
            }
        }
    }

    private void setValues(SalesListModel model){
        if(model!=null){
            String str = "";
            salesListModel = model;
            List<SalesModel> productModels = salesListModel.getSalesList();
            if(productModels!=null){
                if(productModels.size()>0&&listview!=null&&list!=null&&adapter!=null){
                    if(type.equalsIgnoreCase(GlobalVariables.TYPE_GARAGE)){
                        str = productModels.size()+" Garage Sales near you";
                    }else{
                        str = productModels.size() + " Multiple Sales near you";
                    }
                    sale_header_name.setText(str);
                    no_sales.setVisibility(View.GONE);
                    list.clear();
                    list.addAll(productModels);
                    synchronized (adapter){adapter.notifyDataSetChanged();}
                    // setHeader(inflater);
                } else{
                    no_sales.setVisibility(View.VISIBLE);
                    sale_header_name.setText("No Sales found near you");
                    no_sales.setText(no_sales_text);
                }
            }
        }

    }

  /*  private void setHeader(LayoutInflater inflater) {
        if (listview != null && inflater != null) {
            View headerView = inflater.inflate(R.layout.sales_list_row_item_header, null, false);
            listview.addHeaderView(headerView);
            total_tv = (TextView) headerView.findViewById(R.id.sales_list_row_item_header_total_tv);
            String str = "";
            if (list != null) {
                str = list.size() + " Sales";
            }
            total_tv.setText(str);
        }
    }*/

    private void deleteList(int position){
        if(position>=0&&listview!=null&&list!=null&&adapter!=null){
            if(position+1<=list.size()){
                list.remove(position);
                synchronized (adapter){adapter.notifyDataSetChanged();}
            }
        }
    }

    private void getLists(Context context, FilterModel filterModel){
        GlobalFunctions.showProgress(context, "Loading Garage Sales...");
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.getListDetails(context, filterModel, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                Log.d(TAG, "onSuccess Response");
                salesListModel = (SalesListModel)arg0;
                String str = salesListModel.toString();
                Log.d(TAG, str);
                setValues(salesListModel);
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
        }, "List Sales");

    }

    @Override
    public void onLocationChanged(Location location) {
        if(!type.equalsIgnoreCase(GlobalVariables.TYPE_GARAGE+"")){
            Location loc = locServices.getLocation();
            if(loc!=null){
                FilterModel model = new FilterModel();
                model.setLatitude(loc.getLatitude()+"");
                model.setLongitude(loc.getLongitude()+"");
                model.setRange(10);
                model.setAvailable(true);
                model.setPickup(true);
                model.setShipable(true);
                model.setType(type);
                model.setPriceRange("0,10000");
                getLists(getContext(),model);
                locServices.removeListener();
            }
        }else {
            locServices.removeListener();
        }
    }
/*    @Override
    public void onSccessFilterChange(FilterModel filterModel) {
        getLists(context,filterModel);
    }

    @Override
    public void onErrorFilterChange() {

    }*/
    public void openInternetSettings(){
      /*  Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.android.phone","com.android.phone.NetworkSetting");
        startActivity(intent);
      */  Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        context.startActivity(intent);
    }

   /* private void showErrorPage(){
        if(progressActivity!=null){
            progressActivity.showError(context.getResources().getDrawable(R.drawable.ic_error), "No Connection",
                    "We could not establish a connection with our servers. Try again when you are connected to the internet.",
                    "Try Again", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
        }
    }*/
   @Override
   public void onProviderEnabled(String provider) {

   }
}

