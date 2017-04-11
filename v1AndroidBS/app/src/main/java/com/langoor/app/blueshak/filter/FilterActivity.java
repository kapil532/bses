package com.langoor.app.blueshak.filter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.divrt.co.R;
import com.edmodo.rangebar.RangeBar;
import com.google.android.gms.common.api.GoogleApiClient;
import com.langoor.app.blueshak.MainActivity;
import com.langoor.app.blueshak.PickLocation;
import com.langoor.app.blueshak.garage.CreateSaleActivity;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.services.model.CategoryListModel;
import com.langoor.app.blueshak.services.model.CreateProductModel;
import com.langoor.app.blueshak.services.model.FilterModel;
import com.langoor.app.blueshak.services.model.LocationModel;
import com.langoor.app.blueshak.services.model.PostcodeModel;
import com.langoor.app.blueshak.services.model.SalesListModel;
import com.langoor.app.blueshak.util.LocationListener;
import com.langoor.app.blueshak.util.LocationService;
import com.langoor.app.blueshak.view.MultiAutoCompletionView;
import com.tokenautocomplete.TokenCompleteTextView;

import java.util.ArrayList;

public class FilterActivity extends AppCompatActivity{
    String TAG = "FilterActivity";
    static Context context;
    static Activity activity;


    public static final String BUNDLE_KEY_FILTER_MODEL_SERIALIZABLE = "FilterKeyFilterModelSerializeable";
    public static final String BUNDLE_KEY_LOCATION_MODEL_SERIALIZABLE = "FilterKeyLocationModelSerializeable";
    public static final String SALES_LIST_GARAGGE_SALE_MODEL_TYPE="SalesListGarageSaleModelType";
    RangeBar priceRangeBar;
    SeekBar distanceRangeBar;
    AutoCompleteTextView places_actv;
    TextView priceMinRange_tv, priceMaxRange_tv, distanceMinRange_tv, distanceMaxRaange_tv, category_tv,location;
    RadioGroup shipping_group, available_group;
    /*RadioButton shippable_rb, pickupable_rb, available_rb, sold_rb;*/
    int minPriceValue=0, maxPriceValue=0, minDistanceValue=0, maxDistanceValue=0;
    private LocationModel locationModel=null;
    private static ArrayList<PostcodeModel> autoSuggesstionsList = new ArrayList<PostcodeModel>();
    private static ArrayList<PostcodeModel> selectedAutoSuggesstionsList = new ArrayList<PostcodeModel>();

    Button applyButton;
    static GlobalFunctions globalFunctions = new GlobalFunctions();
    static GlobalVariables globalVariables = new GlobalVariables();

    boolean[] is_checked;

   /* double lat=0, lng=0;*/
    LocationService locServices;
    private GoogleApiClient mGoogleApiClient;
    private Switch all_catrgory;

    FilterModel detail=new FilterModel();

    CategoryListModel categoryListModel= null;
    ArrayList<String> selectedCategoryString = new ArrayList<String>();
    ArrayList<String> selectedCategoryIDs = new ArrayList<String>();

    ArrayAdapter<PostcodeModel> autoCompletionAdapter = null;
    MultiAutoCompletionView zipcode_actv;
    LinearLayout location_content;
    private String type=GlobalVariables.TYPE_SHOP;

    public static Intent newInstance(Context context,LocationModel locationModel){
        Intent mIntent = new Intent(context,FilterActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_KEY_LOCATION_MODEL_SERIALIZABLE, locationModel);
        mIntent.putExtras(bundle);
        return mIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_filter);
        context=this;
        activity=this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("Filter");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.brandColor)));
        }
        priceRangeBar = (RangeBar) findViewById(R.id.price_range_bar);
        distanceRangeBar = (SeekBar) findViewById(R.id.distance_range_bar);
        priceMinRange_tv = (TextView) findViewById(R.id.price_start_tv);
        priceMaxRange_tv = (TextView) findViewById(R.id.price_end_tv);
        distanceMinRange_tv = (TextView) findViewById(R.id.distance_start_tv);
        distanceMaxRaange_tv = (TextView) findViewById(R.id.distance_end_tv);
        category_tv = (TextView) findViewById(R.id.pd_category);
        shipping_group = (RadioGroup) findViewById(R.id.shipping_rg);
        /*shippable_rb = (RadioButton) findViewById(R.id.shippable_rb);
        pickupable_rb = (RadioButton) findViewById(R.id.pickup_rb);*/
        location=(TextView)findViewById(R.id.location);
        applyButton = (Button) findViewById(R.id.applyButton);
        location_content=(LinearLayout)findViewById(R.id.location_content);
        location_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=PickLocation.newInstance(context,GlobalVariables.TYPE_FILTER_ACTIVITY);
                startActivityForResult(intent,globalVariables.REQUEST_CODE_FILTER_PICK_LOCATION);
            }
        });
        all_catrgory=(Switch)findViewById(R.id.all_catrgory);
        all_catrgory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if(all_catrgory.isChecked()){
                    category_tv.setText("All");
                    category_tv.setTextColor(context.getResources().getColor(R.color.brandColor));
                    category_tv.setEnabled(false);

                }else{
                    category_tv.setText("Select");
                    category_tv.setTextColor(context.getResources().getColor(R.color.brandColor));
                    category_tv.setEnabled(true);
                }
            }
        });
        if (getIntent().hasExtra(BUNDLE_KEY_LOCATION_MODEL_SERIALIZABLE)) {
            locationModel = (LocationModel) getIntent().getExtras().getSerializable(BUNDLE_KEY_LOCATION_MODEL_SERIALIZABLE);
        }
       /* if (getIntent().hasExtra(BUNDLE_KEY_LOCATION_MODEL_SERIALIZABLE)) {
            type = getIntent().getStringExtra(BUNDLE_KEY_LOCATION_MODEL_SERIALIZABLE);
        }*/

        String filter_string=GlobalFunctions.getSharedPreferenceString(context,GlobalVariables.FILTER_MODEL);
        if(filter_string!=null){detail.toObject(filter_string);setThisPage();}else{resetValues();}

        if(locationModel!=null){
            detail.setLatitude(locationModel.getLatitude());
            detail.setLongitude(locationModel.getLongitude());
            detail.setLocation(locationModel.getCity()+" "+locationModel.getState());
            detail.setFormatted_address(locationModel.getFormatted_address());
            location.setText(locationModel.getFormatted_address());
        }
        /*type = getArguments().getString(SALES_LIST_GARAGGE_SALE_MODEL_TYPE);*/
       /* setThisPage();*/
        priceRangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int minValue, int maxValue) {
                Log.e("value", minValue + "  " + maxValue);
                minPriceValue = minValue*GlobalVariables.PRICE_OFFSET_VALUE;
                maxPriceValue = maxValue*GlobalVariables.PRICE_OFFSET_VALUE;

                if(minPriceValue<0){
                    priceRangeBar.setThumbIndices(GlobalVariables.PRICE_MIN_VALUE / GlobalVariables.PRICE_OFFSET_VALUE, maxValue);
                }
                if(minPriceValue==GlobalVariables.PRICE_MIN_VALUE ||minPriceValue==0){
                    priceMinRange_tv.setText(GlobalVariables.CURRENCY_NOTATION+" "+GlobalVariables.PRICE_MIN_VALUE);
                }else{priceMinRange_tv.setText(GlobalVariables.CURRENCY_NOTATION+" "+minPriceValue);
                }
                if(maxPriceValue==0){
                    priceMaxRange_tv.setText(GlobalVariables.CURRENCY_NOTATION+" "+GlobalVariables.PRICE_MAX_VALUE);
                }else if(maxPriceValue==GlobalVariables.PRICE_MAX_VALUE){
                    priceMaxRange_tv.setText(GlobalVariables.CURRENCY_NOTATION+" "+GlobalVariables.PRICE_MAX_VALUE);
                }else{priceMaxRange_tv.setText(GlobalVariables.CURRENCY_NOTATION+" "+maxPriceValue);}
            }
        });

        distanceRangeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                minDistanceValue = progress;
                int MIN = GlobalVariables.DISTANCE_MIN_VALUE;
                if (progress < MIN) {
                    distanceMaxRaange_tv.setText(minDistanceValue+" Km");
                } else {
                    minDistanceValue = progress;
                }
                if(minDistanceValue==0){
                    distanceMaxRaange_tv.setText(1+" Km");
                }else{
                    distanceMaxRaange_tv.setText(minDistanceValue+" Kms");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });


        category_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(categoryListModel==null){
                    categoryListModel = GlobalFunctions.getCategories(activity);
                    is_checked = new boolean[categoryListModel.getCategoryNames().size()];
                }
                openCategoryDialog(activity, categoryListModel.getCategoryNames());
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFunction();
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.filter_menu, menu);


        return true;
    }
    public void resetValues(){
       /* location.setText("Select Location");*/
        Log.d(TAG,"Resetting the values");
        category_tv.setText("");
        setDistanceRangeBar(GlobalVariables.DISTANCE_MAX_VALUE);
        setPriceRangeBar(GlobalVariables.PRICE_MIN_VALUE+","+GlobalVariables.PRICE_MAX_VALUE);
        GlobalFunctions.removeSharedPreferenceKey(context,GlobalVariables.FILTER_MODEL);
       /* shippable_rb.isChecked();*/
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      /*  case android.R.id.home:*/
        if (item.getItemId() == android.R.id.home) {
            showExitAlert();
            return true;
        }else if (item.getItemId() == R.id.reset) {
            resetValues();
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void setThisPage(){
        if(detail!=null){
            location.setText(detail.getFormatted_address());
            setPriceRangeBar(detail.getPriceRange());
            setDistanceRangeBar(detail.getRange());
            if(categoryListModel!=null){is_checked = new boolean[categoryListModel.getCategoryNames().size()];}else {
                categoryListModel=GlobalFunctions.getCategories(context);}
            is_checked = new boolean[categoryListModel.getCategoryNames().size()];
            if(categoryListModel!=null){
                selectedCategoryString.clear();
                if(detail.getCategories()!=null){
                    String[] temp = detail.getCategories().split(",");
                    for(int i=0;i<temp.length;i++){
                        String name = categoryListModel.getNameFromID(temp[i]);
                        selectedCategoryString.add(name);
                        int index = categoryListModel.getIndexforCategoryID(temp[i]);
                        if(index>0)
                            if(is_checked.length>=index)
                                is_checked[index]=true;
                    }
                }
            }
            setCategory();
        }
    }
    private void setCategory()
    {
        if(selectedCategoryString.size()>0){
            String temp = "";
            for(int i=0;i<selectedCategoryString.size();i++){
                temp = i==0? selectedCategoryString.get(0): temp+", "+selectedCategoryString.get(i);
            }
            if(category_tv!=null){
                if(!TextUtils.isEmpty(temp)){
                    category_tv.setText(temp);
                    all_catrgory.setChecked(false);
                }else {
                    category_tv.setText("All");
                    category_tv.setTextColor(context.getResources().getColor(R.color.brandColor));
                    category_tv.setEnabled(true);
                }
            }
        }

    }
    private void setCategory_tv(String categoryNames){
        if(categoryNames!=null){
            category_tv.setText(categoryNames);
        }else{category_tv.setText("");}
    }

    private void setPriceRangeBar(String priceRange){
        int count_p = GlobalVariables.PRICE_MAX_VALUE/GlobalVariables.PRICE_OFFSET_VALUE;
        priceRangeBar.setTickCount(count_p+1);
        int min_p = 0 , max_p = count_p+1;
        if(priceRange!=null){
            String[] priceRangeAr = priceRange.split(",");
            if(priceRangeAr.length==2){
                minPriceValue = Integer.parseInt(priceRangeAr[0]);
                maxPriceValue = Integer.parseInt(priceRangeAr[1]);
            }
        }
        if(minPriceValue==0){min_p = GlobalVariables.PRICE_MIN_VALUE/GlobalVariables.PRICE_OFFSET_VALUE;}
        else{
            min_p = minPriceValue/GlobalVariables.PRICE_OFFSET_VALUE;}
        if(maxPriceValue==0){max_p = GlobalVariables.PRICE_MAX_VALUE/GlobalVariables.PRICE_OFFSET_VALUE;}
        else{max_p = maxPriceValue/GlobalVariables.PRICE_OFFSET_VALUE;}

        priceRangeBar.setThumbIndices(min_p,max_p);
        if(minPriceValue==GlobalVariables.PRICE_MIN_VALUE ||minPriceValue==0){priceMinRange_tv.setText(GlobalVariables.CURRENCY_NOTATION+" "+GlobalVariables.PRICE_MIN_VALUE);}else{priceMinRange_tv.setText(GlobalVariables.CURRENCY_NOTATION+" "+minPriceValue);}
        if(maxPriceValue==0){priceMaxRange_tv.setText(GlobalVariables.CURRENCY_NOTATION+" "+GlobalVariables.PRICE_MAX_VALUE);}else if(maxPriceValue==GlobalVariables.PRICE_MAX_VALUE){priceMaxRange_tv.setText(GlobalVariables.CURRENCY_NOTATION+" "+GlobalVariables.PRICE_MAX_VALUE);}else{priceMaxRange_tv.setText(GlobalVariables.CURRENCY_NOTATION+" "+maxPriceValue);}

    }

    private void openCategoryDialog(Activity act, ArrayList<String> list){
        // Intialize  readable sequence of char values
        final CharSequence[] dialogList=  list.toArray(new CharSequence[list.size()]);
        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(act);
        builderDialog.setTitle("Select Item Categories");
        int count = dialogList.length;
        //boolean[] is_checked = new boolean[count]; // set is_checked boolean false;

        // Creating multiple selection by using setMutliChoiceItem method
        builderDialog.setMultiChoiceItems(dialogList, is_checked,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton, boolean isChecked) {
                    }
                });

        builderDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override

                    public void onClick(DialogInterface dialog, int which) {

                        ListView list = ((AlertDialog) dialog).getListView();
                        // make selected item in the comma seprated string
                        selectedCategoryString.clear();
                        for (int i = 0; i < list.getCount(); i++) {
                            boolean checked = list.isItemChecked(i);

                            if (checked) {
                                selectedCategoryString.add(list.getItemAtPosition(i).toString());
                            }
                        }

                        if(selectedCategoryString.size()>0){
                            String temp = "";
                            for(int i=0;i<selectedCategoryString.size();i++){
                                temp = i==0? selectedCategoryString.get(0): temp+", "+selectedCategoryString.get(i);
                            }
                            setCategory_tv(temp);
                        }
                        dialog.dismiss();

                    }
                });

        builderDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builderDialog.create();
        alert.show();
    }

    private void setDistanceRangeBar(int val){
        distanceRangeBar.setProgress(val);
        if(val==0 || val==1){
            distanceMaxRaange_tv.setText(1+" Km");
        }else{
            distanceMaxRaange_tv.setText(val+" Kms");
        }
    }

    private void setPlacesAutoComplete(String lati, String lngi){
        if(lati!=null&&lngi!=null){
            try{
                places_actv.setText(GlobalFunctions.getLocationNames(activity, Double.parseDouble(lati), Double.parseDouble(lngi)));
            }catch(Exception ex){
                Log.d(TAG, "Double error catch");
            }
        }
    }

    private void onClickFunction(){
        int shippable_int = shipping_group.getCheckedRadioButtonId();
      /*  if(shippable_int == shippable_rb.getId()){
            detail.setShipable(true);
            detail.setPickup(false);
        }else if(shippable_int == pickupable_rb.getId()){
            detail.setPickup(true);
            detail.setShipable(false);
        }
*/
        String categories = category_tv.getText().toString();
        detail.setCategories(categories==null?"":categoryListModel.getIDStringfromNameString(categories));

        if(minDistanceValue==0)
            minDistanceValue=GlobalVariables.DISTANCE_MAX_VALUE;
        if(maxPriceValue==0)
            maxPriceValue=GlobalVariables.PRICE_MAX_VALUE;

        detail.setRange(minDistanceValue);
        detail.setPriceRange(minPriceValue+","+maxPriceValue);
        detail.setType(type);
        if(category_tv.getText().toString().equalsIgnoreCase("Select")){
            Toast.makeText(context,"Please select product categories",Toast.LENGTH_LONG).show();
            return;
        }else {
            detail.setCategories(categoryListModel.getIDStringfromNameString(category_tv.getText().toString()));
            GlobalFunctions.setSharedPreferenceString(context,GlobalVariables.FILTER_MODEL,detail.toString());
            Log.d(TAG, "filterModel :"+detail.toString());
            Intent intent=MainActivity.newInstance(context,null,detail);
            startActivity(intent);
            closeThisActivity();
        }


    }
    @Override
    public void onStart() {
        super.onStart();
        categoryListModel = GlobalFunctions.getCategories(activity);
        if(categoryListModel!=null)
            is_checked = new boolean[categoryListModel.getCategoryNames().size()];

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG,"on activity result");
        if(resultCode == activity.RESULT_OK){
            Log.i(TAG,"result ok ");
            if(requestCode == globalVariables.REQUEST_CODE_FILTER_PICK_LOCATION){
                Log.i(TAG,"request code "+globalVariables.REQUEST_CODE_FILTER_PICK_LOCATION);
                LocationModel location_model = (LocationModel) data.getExtras().getSerializable(BUNDLE_KEY_FILTER_MODEL_SERIALIZABLE);
                Log.i(TAG,"name pm"+location_model.getFormatted_address());
                detail.setLatitude(location_model.getLatitude());
                detail.setLongitude(location_model.getLongitude());
                detail.setLocation(location_model.getCity()+" "+location_model.getState());
                detail.setFormatted_address(location_model.getFormatted_address());
                location.setText(location_model.getFormatted_address());
            }
        }
    }
    public static void closeThisActivity(){
        if(activity!=null){activity.finish();}
    }
    public void showExitAlert() {
        final com.langoor.app.blueshak.view.AlertDialog alertDialog = new com.langoor.app.blueshak.view.AlertDialog(activity);
        alertDialog.setTitle("Are you sure?");
        alertDialog.setMessage("Filter changes will not reflect");
        alertDialog.setPositiveButton("Yes", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             closeThisActivity();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("No", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        showExitAlert();
    }

}
