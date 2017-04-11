package com.langoor.app.blueshak.garage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.divrt.co.R;
import com.langoor.app.blueshak.MainActivity;
import com.langoor.app.blueshak.garage.AdapterListener;
import com.langoor.app.blueshak.garage.CreateProductsListAdapter;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;
/*import com.langoor.app.blueshak.login.LoginActivity;
import com.langoor.app.blueshak.product.ProductDetail;*/
import com.langoor.app.blueshak.register.TermsConditions;
import com.langoor.app.blueshak.services.ServerResponseInterface;
import com.langoor.app.blueshak.services.ServicesMethodsManager;
import com.langoor.app.blueshak.services.model.CreateProductModel;
import com.langoor.app.blueshak.services.model.CreateSalesModel;
import com.langoor.app.blueshak.services.model.ErrorModel;
import com.langoor.app.blueshak.services.model.IDModel;
import com.langoor.app.blueshak.services.model.StatusModel;
import com.langoor.app.blueshak.view.AlertDialog;
import com.langoor.app.blueshak.view_sales.MapFragmentSales;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NanduYoga on 28/05/16.
 */
public class AddItems extends AppCompatActivity implements AdapterListener {
    String TAG = "AddItems";
    public static final String SALE_DETAILS_KEY = "SaleDetailsKey";
    public static final String  FROM_KEY= "from_key";
    public static final String PRODUCT_DETAILA_BUNDLE_KEY = "ProductDetailsBundleKey";

    private Toolbar toolbar;
    ListView lv;
    CreateProductsListAdapter adapter;

    TextView date,address1,additem;
    TextView time;
    Button startSale;

    CreateSalesModel sm;
    List<CreateProductModel> list_pm = new ArrayList<CreateProductModel>();
    CreateProductModel pm;
    int tag;
    static Activity act;
    Window window;
    static GlobalFunctions globalFunctions = new GlobalFunctions();
    static GlobalVariables globalVariables = new GlobalVariables();
    private CheckBox terms_conditions;
    private TextView termsOfAgreement;
    static Activity activity;
    static Context context;

    public static Intent newInstance(Context context, CreateSalesModel sales,int from){
        Intent mIntent = new Intent(context, AddItems.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(SALE_DETAILS_KEY, sales);
        bundle.putInt(FROM_KEY, from);
        mIntent.putExtras(bundle);
        return mIntent;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_additems);
        activity=this;
        context=this;

        LayoutInflater inflater = getLayoutInflater();
        act = AddItems.this;
    /*    toolbar = (Toolbar) findViewById(R.id.additem_toolbar);
        toolbar.setTitle(getResources().getString(R.string.additem));
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(getResources().getColor(R.color.brandColor));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        startSale = (Button) findViewById(R.id.additem_startsale);
        lv = (ListView) findViewById(R.id.additem_listview);
        adapter=new CreateProductsListAdapter(inflater, list_pm, this);
        lv.setAdapter(adapter);

        View header = inflater.inflate(R.layout.header_garrage_list, lv, false);
        //time = (TextView) header.findViewById(R.id.hgl_clock);
        date = (TextView) header.findViewById(R.id.hgl_date);
        address1 = (TextView) header.findViewById(R.id.hgl_address1);
        terms_conditions=(CheckBox)findViewById(R.id.checkBox);
        termsOfAgreement=(TextView)findViewById(R.id.termsOfAgreement);
        //lv.addHeaderView(header);
        //lv.setHeaderDividersEnabled(true);

        String terms="I agree to the Terms and Conditions.";
        SpannableString ss = new SpannableString("I agree to the Terms and Conditions.");
        ss.setSpan(new MyClickableSpan(),0,terms.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        termsOfAgreement.setText(ss);
        termsOfAgreement.setMovementMethod(LinkMovementMethod.getInstance());

        SpannableString ss1 = new SpannableString("Already have a account? Sign In");

        ss1.setSpan(new MyClickableSpan(),24,31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        View footer = inflater.inflate(R.layout.footer_garrage_list,lv,false);
        additem = (TextView) footer.findViewById(R.id.fgl_additem);
        lv.addFooterView(footer);
        lv.setFooterDividersEnabled(true);

        if(getIntent().hasExtra(AddItems.SALE_DETAILS_KEY))
            sm = (CreateSalesModel) getIntent().getExtras().getSerializable(SALE_DETAILS_KEY);

        if(getIntent().hasExtra(AddItems.FROM_KEY))
            tag=getIntent().getIntExtra(AddItems.FROM_KEY,0);

        String title;
        if(tag==GlobalVariables.TYPE_MY_SALE){
            title="Update Sale";
            startSale.setText(title);
            additem.setVisibility(View.GONE);
        }else {
            title="Publish";

        }


    /*    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.brandColor)));
        }
        window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //enable translucent statusbar via flags
            globalFunctions.setTranslucentStatusFlag(window, true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //we don't need the translucent flag this is handled by the theme
            globalFunctions.setTranslucentStatusFlag(window, true);
            //set the statusbarcolor transparent to remove the black shadow
            this.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }*/

        if(sm!=null)
            if(sm.getProducts().size()==0)
                callProductDetails(null);


        startSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tag==GlobalVariables.TYPE_MY_SALE){
                    update_sale(act);
                }else {
                    if(!terms_conditions.isChecked()){
                        terms_conditions.setError("Please Agree for Terms and Conditions");
                        return;
                    }else {
                        startSale(act);
                    }

                }
            }
        });

        Log.i(TAG,"name is "+sm.getName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            closeThisActivity();
        }
      if (id == R.id.profile_edit) {
          if(tag==GlobalVariables.TYPE_MY_SALE){
                Intent edit = CreateSaleActivity.newInstance(act,sm,null,null,GlobalVariables.TYPE_MY_SALE,GlobalVariables.TYPE_GARAGE);
                startActivity(edit);
                closeThisActivity();
            }else{
                Intent edit = CreateSaleActivity.newInstance(act,sm,null,null,GlobalVariables.TYPE_ADD_TEMS,GlobalVariables.TYPE_GARAGE);
                startActivity(edit);
                closeThisActivity();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(sm !=null){
            list_pm.clear();
            list_pm.addAll(sm.getProducts());
            if(adapter!=null){synchronized (adapter){adapter.notifyDataSetChanged();}}
            date.setText(sm.getStart_date());
            if(sm!=null)
                address1.setText(sm.getAddress());
            //   address1.setText(globalFunctions.getLocationNames(act,Double.parseDouble(sm.getLatitude()),Double.parseDouble(sm.getLongitude())));
            //address1.setText(g);
        }
        additem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(sm.getProducts().size()>=10))
                    callProductDetails(null);
                else
                    Toast.makeText(act,"Only 10 products can be added in a single sale",Toast.LENGTH_LONG).show();
            }
        });
    }
    private void update_sale(Context context){
        if(sm.getProducts().size()>0){
            updateSale(context,sm);
        }else{
            Toast.makeText(context, "At-least One product should be added to start a sale",Toast.LENGTH_LONG).show();}
    }
    private void startSale(Context context){
        if(sm.getProducts().size()>0){
            sm.setRequest_type(GlobalVariables.TYPE_PUBLISH_REQUEST);
            createSale(context, sm);
        }else{
            Toast.makeText(context, "At-least One product should be added to start a sale",Toast.LENGTH_LONG).show();}
    }
    public void callProductDetails(CreateProductModel product){
        Intent addItem = ProductDetails.newInstance(act, product,sm.getSaleID());
        startActivityForResult(addItem,globalVariables.REQUEST_CODE_ADD_PRODUCT);
    }
    public void callUpdateProductDetails(CreateProductModel product){
        Intent addItem = ProductDetails.newInstance(act, product,sm.getSaleID());
        startActivityForResult(addItem,globalVariables.REQUEST_CODE_UPDATE_PRODUCT);
    }
    private void addProductToList(CreateProductModel productModel){
        Log.d(TAG, "product "+productModel.toString());
        if(productModel!=null){
            List<CreateProductModel> tempList = new ArrayList<CreateProductModel>();
            tempList.addAll(sm.getProducts());
            if(tempList.size()>productModel.getId()){
                if(productModel.getId()<0){
                    productModel.setId(sm.getProducts().size());
                    tempList.add(productModel);
                }else{
                    tempList.remove(productModel.getId());
                    tempList.add(productModel.getId(), productModel);
                }
            }
            sm.setProducts(tempList);
        }
    }
    private void createSaleItem(final Context context, final CreateProductModel createProductModel){
        GlobalFunctions.showProgress(context, "Adding...");
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.createSaleItem(context, createProductModel, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                if(arg0 instanceof IDModel){
                    IDModel idModel = (IDModel) arg0;
                    createProductModel.setProduct_id(idModel.getId());
                    Toast.makeText(context, "Add has been added to draft Successfully", Toast.LENGTH_LONG).show();
                  /*  setReturnResult(createProductModel);*/
                }else if(arg0 instanceof ErrorModel){
                    ErrorModel errorModel = (ErrorModel) arg0;
                    String msg = errorModel.getError()!=null ? errorModel.getError() : errorModel.getMessage();
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void OnFailureFromServer(String msg) {
                GlobalFunctions.hideProgress();
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void OnError(String msg) {
                GlobalFunctions.hideProgress();
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        }, "Create Sale");

    }
    private void updateProductToList(CreateProductModel productModel){
        Log.d(TAG, "product "+productModel.toString());
        if(productModel!=null){
            List<CreateProductModel> tempList = new ArrayList<CreateProductModel>();
            tempList.addAll(sm.getProducts());
            for(CreateProductModel createProductModel:sm.getProducts()){
                if(createProductModel.getId()==productModel.getId()){
                    int index = tempList.indexOf(createProductModel);
                    tempList.remove(index);
                    tempList.add(index,productModel);
                    break;
                }
            }
            sm.setProducts(tempList);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG,"on activity result");
        if(resultCode == act.RESULT_OK){
            Log.i(TAG,"result ok ");
            if(requestCode == globalVariables.REQUEST_CODE_ADD_PRODUCT){
                Log.i(TAG,"request code "+globalVariables.REQUEST_CODE_ADD_PRODUCT);
                CreateProductModel pm = (CreateProductModel) data.getExtras().getSerializable(PRODUCT_DETAILA_BUNDLE_KEY);

                Log.i(TAG,"name pm"+pm.getName());
                addProductToList(pm);
            }else if(requestCode == globalVariables.REQUEST_CODE_UPDATE_PRODUCT){
                Log.i(TAG,"request code "+globalVariables.REQUEST_CODE_ADD_PRODUCT);
                CreateProductModel pm = (CreateProductModel) data.getExtras().getSerializable(PRODUCT_DETAILA_BUNDLE_KEY);

                Log.i(TAG,"name pm"+pm.getName());
                updateProductToList(pm);

            }
        }
    }

    public void deleteProductItem(final int position){
        final AlertDialog dialog = new AlertDialog(act);
        dialog.setTitle("Delete");
        dialog.setIcon(R.drawable.ic_alert);
        dialog.setIsCancelable(true);
        dialog.setMessage("Are You sure remove this product?");
        dialog.setPositiveButton("Yes", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateProductModel createProductModel=list_pm.get(position);
                deleteSaleItem(createProductModel,position);
              /*  list_pm.remove(position);
                sm.setProducts(list_pm);
                if(adapter!=null){synchronized (adapter){adapter.notifyDataSetChanged();}}
       */         dialog.dismiss();
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
    private void deleteSaleItem(CreateProductModel createProductModel,final int position){
        GlobalFunctions.showProgress(context, "Deleting Sale item...");
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.deleteSaleItem(context,createProductModel.getSaleID(),createProductModel.getProduct_id(),new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                Log.d(TAG, "onSuccess Response");
                Toast.makeText(context,"Sale item has been Deleted",Toast.LENGTH_LONG).show();
                list_pm.remove(position);
                sm.setProducts(list_pm);
                if(adapter!=null){synchronized (adapter){adapter.notifyDataSetChanged();}}
                if(sm!=null)
                    if(sm.getProducts().size()==0)
                        callProductDetails(null);

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
        }, "Delete Sale");
    }
    public static void closeThisActivity(){
        if(activity!=null){activity.finish();}
    }
    @Override
    public void onDelete(int position) {
        deleteProductItem(position);
    }

    @Override
    public void onEdit(int position) {
        if(tag==GlobalVariables.TYPE_MY_SALE)
            callUpdateProductDetails(list_pm.get(position));
        else
            callProductDetails(list_pm.get(position));
    }

    private void createSale(final Context context, CreateSalesModel createSalesModel){
        GlobalFunctions.showProgress(context, "Publishing Sale...");
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.createSale(context, createSalesModel, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                if(arg0 instanceof IDModel){
                    Toast.makeText(context, "Sale has been published Successfully", Toast.LENGTH_LONG).show();
                    closeThisActivity();
                    startActivity(new Intent(AddItems.this,MainActivity.class));
                }else if(arg0 instanceof ErrorModel){
                    ErrorModel errorModel = (ErrorModel) arg0;
                    String msg = errorModel.getError()!=null ? errorModel.getError() : errorModel.getMessage();
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }else if(arg0 instanceof StatusModel){
                    Toast.makeText(context, "Sale has been published Successfully", Toast.LENGTH_LONG).show();
                    closeThisActivity();
                    startActivity(new Intent(AddItems.this,MainActivity.class));
                }
            }

            @Override
            public void OnFailureFromServer(String msg) {
                GlobalFunctions.hideProgress();
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void OnError(String msg) {
                GlobalFunctions.hideProgress();
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        }, "Create Sale");

    }
    private void updateSale(final Context context, CreateSalesModel createSalesModel){
        GlobalFunctions.showProgress(context, "Starting Sale...");
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.updateSale(context, createSalesModel, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                if(arg0 instanceof StatusModel){
                    StatusModel statusModel = (StatusModel) arg0;
                    Toast.makeText(context, "Sale has been updated Successfully", Toast.LENGTH_LONG).show();
                    closeThisActivity();
                }else if(arg0 instanceof ErrorModel){
                    ErrorModel errorModel = (ErrorModel) arg0;
                    String msg = errorModel.getError()!=null ? errorModel.getError() : errorModel.getMessage();
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void OnFailureFromServer(String msg) {
                GlobalFunctions.hideProgress();
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void OnError(String msg) {
                GlobalFunctions.hideProgress();
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        }, "Create Sale");

    }
    @Override
    public void OnView(int position) {

    }
    class MyClickableSpan extends ClickableSpan { //clickable span
        public void onClick(View textView) {
            startActivity(new Intent(AddItems.this,TermsConditions.class));
        }
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(getResources().getColor(R.color.black));//set text color
            ds.setUnderlineText(true);
            //ds.setStyle(Typeface.BOLD);
            //ds.setTypeface(Typeface.DEFAULT_BOLD);
        }
    }
}
