package com.langoor.app.blueshak.view_sales;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.divrt.co.R;
import com.langoor.app.blueshak.MainActivity;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.home.ItemListFragment;
import com.langoor.app.blueshak.services.model.ProductModel;
import com.langoor.app.blueshak.services.model.SalesModel;

public class MapActivity extends AppCompatActivity {
    private static final String TAG = "MapActivity";
    static Context context;
    static Activity activity;
    public static FragmentManager mainActivityFM;
    public static final String MAP_FRAGMENT_SALES_BUNDLE_TYPE_STRING = "MapFragmentSalesBundleTypeString";
    public static final String MAP_FRAGMENT_SALES_PRODUCT_ID_STRING = "MapFragmentProductIdTypeString";
    String product_id=null;
    private ProductModel productModel=new ProductModel();
  /*  public static Intent newInstance(String type, String product_id){
        Intent mapFragmentSales = new MapFragmentSales();
        Bundle bundle = new Bundle();
        bundle.putString(MAP_FRAGMENT_SALES_BUNDLE_TYPE_STRING, type);
        bundle.putString(MAP_FRAGMENT_SALES_PRODUCT_ID_STRING, product_id);
        mapFragmentSales.setArguments(bundle);
        return mapFragmentSales;
    }*/
    public static Intent newInstance(Context context, String type, ProductModel product_id){
        Intent mIntent = new Intent(context, MapActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MAP_FRAGMENT_SALES_BUNDLE_TYPE_STRING, type);
        bundle.putSerializable(MAP_FRAGMENT_SALES_PRODUCT_ID_STRING, product_id);
        mIntent.putExtras(bundle);
        return mIntent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        activity=this;
        context=this;
        mainActivityFM = getSupportFragmentManager();
        if(getIntent()!=null)
            if(getIntent().getExtras()!=null)
                productModel =(ProductModel)  getIntent().getExtras().getSerializable(MAP_FRAGMENT_SALES_PRODUCT_ID_STRING);
        if(productModel!=null)
            product_id=productModel.getId();
        MapFragmentSales itemListFragment= MapFragmentSales.newInstance(null,productModel,null,true);
        Fragment fragment = new MapFragmentSales().newInstance(GlobalVariables.TYPE_SHOP,productModel,null,true);
       /* fragmentManager.beginTransaction().replace(R.id.map,fragment).commit();*/
        mainActivityFM.beginTransaction().replace(R.id.container,fragment, "").commit();

    }

}
