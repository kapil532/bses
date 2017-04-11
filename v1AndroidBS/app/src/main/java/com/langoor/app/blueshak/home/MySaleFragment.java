package com.langoor.app.blueshak.home;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;
import com.divrt.co.R;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.home.list_fragments.SalesList;
import com.langoor.app.blueshak.services.ServerResponseInterface;
import com.langoor.app.blueshak.services.ServicesMethodsManager;
import com.langoor.app.blueshak.services.model.FilterModel;
import com.langoor.app.blueshak.services.model.LocationModel;
import com.langoor.app.blueshak.services.model.SalesListModel;
import com.langoor.app.blueshak.services.model.SalesListModelNew;
/*import com.pushwoosh.internal.utils.DeviceUtils;*/

/*import android.app.ActionBar;*/

public class MySaleFragment extends Fragment implements
        TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
    public static final String TAG = "MySaleFragment";
    public static final String MY_SALE_FRAGMENT_SALES_BUNDLE_TYPE_STRING = "MySaleFragmentBundleTypeString";
    private static final String MY_SALE_FRAGMENT_SALES_DETAIL_BUNDLE_SERIALIZE = "MySaleFragmentDetailsBundleSerialize";
    private static final String SALES_FRAGMENT_SALES_DETAIL_BUNDLE_FLAG = "MySaleFragmentSaleDetailsBundleFlag";
    private TabsPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private TabHost mTabHost;
    private String[] tabs = {"Garage Sale", "Multiple Item Sale"};
    private SalesListModelNew salesListModel = new SalesListModelNew();
    private String type = GlobalVariables.TYPE_GARAGE;

    TextView no_sales;
    public static MySaleFragment newInstance(String type){
        MySaleFragment mapFragmentSales = new MySaleFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MY_SALE_FRAGMENT_SALES_BUNDLE_TYPE_STRING, type);
        mapFragmentSales.setArguments(bundle);
        return mapFragmentSales;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_message);
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.home_page, container, false);

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mTabHost = (TabHost) view.findViewById(android.R.id.tabhost);

     /*   salesListModel = (SalesListModel) getArguments().getSerializable(MY_SALE_FRAGMENT_SALES_DETAIL_BUNDLE_SERIALIZE);
        type = getArguments().getString(MY_SALE_FRAGMENT_SALES_BUNDLE_TYPE_STRING);
*/
      /*  if(type==GlobalVariables.TYPE_MY_SALE+""){
            getLists(getContext());
        }*/


        // Tab Initialization
        initialiseTabHost();
      /*  setTabColor(mTabHost);*/
        mAdapter = new TabsPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);
        return view;
    }

    // Method to add a TabHost
    private void AddTab(Activity activity, TabHost tabHost, TabHost.TabSpec tabSpec) {
        tabSpec.setContent(new MyTabFactory(activity));
        tabHost.addTab(tabSpec);
    }

    // Manages the Tab changes, synchronizing it with Pages
    public void onTabChanged(String tag) {
        int pos = this.mTabHost.getCurrentTab();
        this.mViewPager.setCurrentItem(pos);
      /*  setTabColor(mTabHost);*/
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    // Manages the Page changes, synchronizing it with Tabs
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        int pos = this.mViewPager.getCurrentItem();
        this.mTabHost.setCurrentTab(pos);
    }

    @Override
    public void onPageSelected(int arg0) {

    }

    private static View createTabView(final Context context, final String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_tabs_bg, null);
        TextView tv = (TextView) view.findViewById(R.id.home_tabsText);
        tv.setText(text);
        return view;
    }

    // Tabs Creation
    private void initialiseTabHost() {
        mTabHost.setup();
        // TODO Put here your Tabs
        AddTab(getActivity(), this.mTabHost, this.mTabHost.newTabSpec(getString(R.string.buyer)).setIndicator(createTabView(getContext(),"Garage Sale")));
        AddTab(getActivity(), this.mTabHost, this.mTabHost.newTabSpec(getString(R.string.seller)).setIndicator(createTabView(getContext(), "Item")));
        mTabHost.setOnTabChangedListener(this);
    }
    public static void setTabColor(TabHost tabhost) {
        for(int i=0;i<tabhost.getTabWidget().getChildCount();i++) {
            tabhost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#CC6E00")); //unselected#FF9000
        }
        tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).setBackgroundColor(Color.parseColor("#FF9000")); // selected
    }
    public class TabsPagerAdapter extends FragmentPagerAdapter {
        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index) {
            switch (index) {
                case 0:
                   /* return new GarageSalesListFragment().newInstance(salesListModel, type);*/
                    return GarageSalesListFragment.newInstance(salesListModel, GlobalVariables.TYPE_GARAGE,new FilterModel(),new LocationModel());


                case 1:
                 // return new ItemListFragment();
                return ItemListFragment.newInstance(new SalesListModel(), GlobalVariables.TYPE_MULTIPLE_ITEMS,null,null);
            }
            return null;
        }

        @Override
        public int getCount() {
            // get item count - equal to number of tabs
            return 2;
        }
    }

    private void getLists(Context context){
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.getMySalesList(context, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                Log.d(TAG, "onSuccess Response");
                if(salesListModel==null)
                    salesListModel=new SalesListModelNew();

                salesListModel = (SalesListModelNew)arg0;
                String str = salesListModel.toString();
                Log.d(TAG, str);
                //addItems();
            }

            @Override
            public void OnFailureFromServer(String msg) {
                Log.d(TAG, msg);
            }

            @Override
            public void OnError(String msg) {
                Log.d(TAG, msg);
            }
        }, "CreateSaleActivity Sales");

    }



    public class MyTabFactory implements TabHost.TabContentFactory {

        private final Context mContext;

        public MyTabFactory(Context context) {
            mContext = context;
        }

        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
    }
  /*  public class MyItem implements ClusterItem {
        private SalesModel salesModel;

        public MyItem(SalesModel salesModel) {
            this.salesModel = salesModel;
        }

        public SalesModel getSalesModel() {
            return salesModel;
        }

        public void setSalesModel(SalesModel salesModel) {
            this.salesModel = salesModel;
        }

        @Override
        public LatLng getPosition() {
            return new LatLng(Double.parseDouble(salesModel.getLatitude()), Double.parseDouble(salesModel.getLongitude()));
        }
    }*/
}
