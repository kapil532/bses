package com.langoor.app.blueshak.register;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import com.divrt.co.R;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.services.ServerConstants;
import com.langoor.app.blueshak.services.model.ProductModel;
import com.langoor.app.blueshak.support.WebViewFragment;

public class TermsConditions extends AppCompatActivity {
    public static FragmentManager mainActivityFM;
    static final String  PRODUCTDETAIL_BUNDLE_KEY_POSITION = "ProductModelWithDetails";
    public static Intent newInstance(Context context, int product){
        Intent mIntent = new Intent(context,TermsConditions.class);
        Bundle bundle = new Bundle();
        bundle.putInt(PRODUCTDETAIL_BUNDLE_KEY_POSITION, product);
        mIntent.putExtras(bundle);
        return mIntent;
    }
    private String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.content_terms_conditions);
        mainActivityFM=getSupportFragmentManager();
        if(getIntent()!=null){
            if(getIntent().getExtras().getInt(PRODUCTDETAIL_BUNDLE_KEY_POSITION)== GlobalVariables.TYPE_SIGN_UP){
                title="Terms & Conditions";
                TermsConditions.replaceFragment(WebViewFragment.newInstance(this, ServerConstants.URL_Blueshak_Terms_Conditions_Url),WebViewFragment.TAG);
            }else{
                title="Help Center";
                TermsConditions.replaceFragment(WebViewFragment.newInstance(this, ServerConstants.URL_Blueshak_Support_Url),WebViewFragment.TAG);
            }
         }
      /*  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.brandColor)));
        }
*/
    }
    public static void replaceFragment(Fragment fragment, @NonNull String tag){
        if(mainActivityFM!=null){mainActivityFM.beginTransaction().replace(R.id.terms_conditions_frame,fragment, tag).commit();}
    }
}
