package com.langoor.app.blueshak.report;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.Toast;

import com.divrt.co.R;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.services.ServerResponseInterface;
import com.langoor.app.blueshak.services.ServicesMethodsManager;
import com.langoor.app.blueshak.services.model.ErrorModel;
import com.langoor.app.blueshak.services.model.ProductModel;
import com.langoor.app.blueshak.services.model.SalesModel;
import com.langoor.app.blueshak.services.model.StatusModel;

public class ReportListing extends AppCompatActivity {
    private static final String TAG = "ReportListing";
    static Context context;
    static Activity activity;
    static final String  PRODUCTDETAIL_BUNDLE_KEY_POSITION = "ProductModelWithDetails";
    static final String  PRODUCTDETAIL_BUNDLE_KEY_FLAG = "ProductModelWithFlag";
    static final String  SELL_MODEL = "sellmodel";
    public static ProductModel productModel = new ProductModel();
    RadioButton radioButton,radioButton1,radioButton2,radioButton3,radioButton4,radioButton5,radioButton6
            ,   radioButton7,radioButton8,radioButton9;


    public static Intent newInstance(Context context, ProductModel product){
        Intent mIntent = new Intent(context, ReportListing.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(PRODUCTDETAIL_BUNDLE_KEY_POSITION, product);
   /*     bundle.putSerializable(SELL_MODEL, salesModel);
        bundle.putInt(PRODUCTDETAIL_BUNDLE_KEY_FLAG, flag);*/
        mIntent.putExtras(bundle);
        return mIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_report_listing);
        context=this;
        activity=this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        radioButton=(RadioButton)findViewById(R.id.radioButton);
        radioButton1=(RadioButton)findViewById(R.id.radioButton1);
        radioButton2=(RadioButton)findViewById(R.id.radioButton2);
        radioButton3=(RadioButton)findViewById(R.id.radioButton3);
        radioButton4=(RadioButton)findViewById(R.id.radioButton4);
        radioButton5=(RadioButton)findViewById(R.id.radioButton5);
        radioButton6=(RadioButton)findViewById(R.id.radioButton6);
        radioButton7=(RadioButton)findViewById(R.id.radioButton7);
        radioButton8=(RadioButton)findViewById(R.id.radioButton8);
        radioButton9=(RadioButton)findViewById(R.id.radioButton9);
        if(getIntent().hasExtra(PRODUCTDETAIL_BUNDLE_KEY_POSITION))
            productModel = (ProductModel) getIntent().getExtras().getSerializable(PRODUCTDETAIL_BUNDLE_KEY_POSITION);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.report_listing_menu, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.save) {
            getReportText();
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void getReportText() {
         if(radioButton.isChecked()){
            reportListing(radioButton.getText().toString());
        }else if(radioButton1.isChecked()){
             reportListing(radioButton1.getText().toString());
        }else if(radioButton2.isChecked()){
             reportListing(radioButton2.getText().toString());
        }else if(radioButton3.isChecked()){
             reportListing(radioButton3.getText().toString());
        }else if(radioButton4.isChecked()){
             reportListing(radioButton4.getText().toString());
        }else if(radioButton5.isChecked()){
             reportListing(radioButton5.getText().toString());
        }else if(radioButton6.isChecked()){
             reportListing(radioButton6.getText().toString());
        }else if(radioButton7.isChecked()){
             reportListing(radioButton7.getText().toString());
        }else if(radioButton8.isChecked()){
             reportListing(radioButton8.getText().toString());
        }else if(radioButton9.isChecked()){
             reportListing(radioButton9.getText().toString());
        }else {
            Toast.makeText(context,"Please select reasen for reporting",Toast.LENGTH_LONG).show();
        }

    }
    private void reportListing(String content){
        GlobalFunctions.showProgress(context, "Reporting this Listing...");
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.markInappropirate(context, productModel.getId(), content, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                Log.d(TAG, "onSuccess Response");
                if(arg0 instanceof StatusModel){
                    StatusModel statusModel = (StatusModel) arg0;
                    if(statusModel.isStatus()){
                        Toast.makeText(context, "We have received your report,thanks", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }else if(arg0 instanceof ErrorModel){
                    GlobalFunctions.hideProgress();
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
}
