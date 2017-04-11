package com.langoor.app.blueshak.garage;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.divrt.co.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.langoor.app.blueshak.PickLocation;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.photos_add.Object_PhotosAdd;
import com.langoor.app.blueshak.photos_add.PhotosAddFragmentMain;
import com.langoor.app.blueshak.services.ServerResponseInterface;
import com.langoor.app.blueshak.services.ServicesMethodsManager;
import com.langoor.app.blueshak.services.model.LocationModel;
import com.langoor.app.blueshak.services.model.CategoryListModel;
import com.langoor.app.blueshak.services.model.CategoryModel;
import com.langoor.app.blueshak.services.model.CreateImageModel;
import com.langoor.app.blueshak.services.model.CreateProductModel;
import com.langoor.app.blueshak.services.model.ErrorModel;
import com.langoor.app.blueshak.services.model.IDModel;
import com.langoor.app.blueshak.services.model.PostcodeListModel;
import com.langoor.app.blueshak.services.model.PostcodeModel;
import com.langoor.app.blueshak.services.model.StatusModel;
import com.langoor.app.blueshak.util.LocationListener;
import com.langoor.app.blueshak.util.LocationService;
import com.langoor.app.blueshak.view.MultiAutoCompletionView;
import com.tokenautocomplete.TokenCompleteTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NanduYoga on 21/05/16.
 */
public class ProductDetails extends AppCompatActivity implements TokenCompleteTextView.TokenListener{
    private String TAG = "ProductDetails";
    private String[] ary;
    public static final String CREATE_PRODUCT_DETAIL_BUNDLE_KEY = "CreateProductDetailBundleKey";
    public static final String CREATE_GARRAGE_LOCATION_BUNDLE_KEY = "CreateItemActivityLocationBundleKey";
    public static final String CREATE_SALE_KEY = "CreateSaleKey";
    private Toolbar toolbar;
    private EditText category;
    private EditText name,description,saleprice,address;
    private Switch shippable,nagotiable,is_new_old;
    private Button save;
    private Activity activity;
    private  boolean[] is_checked;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private CategoryListModel clm;
    private List<CategoryModel> list_cm = new ArrayList<CategoryModel>();
    private LocationService locServices;
    private  ArrayList<String> selectedCategoryString = new ArrayList<String>();
    private ArrayList<String> selectedCategoryIDs = new ArrayList<String>();
    private static Object_PhotosAdd objectUploadPhoto = new Object_PhotosAdd();
    private static GlobalFunctions globalFunctions = new GlobalFunctions();
    private static GlobalVariables globalVariables = new GlobalVariables();
    private static CreateProductModel productModel = null;
    private Window window;
    private TextView mAutocompleteTextView;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    String str_name,str_desc,str_sp,locstr,descriptionstr,catogarystr,postalCode;
    boolean isAvailable = true;
    private static ArrayList<PostcodeModel> autoSuggesstionsList = new ArrayList<PostcodeModel>();
    private static ArrayList<PostcodeModel> selectedAutoSuggesstionsList = new ArrayList<PostcodeModel>();
    private  ArrayAdapter<PostcodeModel> autoCompletionAdapter = null;
    private MultiAutoCompletionView zipcode_actv;
        String sale_id;
    private Context context;
    private ArrayList<CreateImageModel> sale_images;
    private Boolean type_edit_sale=false;
    private String loading_label="Adding Sale Item...";
    private LocationModel locationModel=new LocationModel();
    public static Intent newInstance(Context context, CreateProductModel sales,String sale_id){
        Intent mIntent = new Intent(context, ProductDetails.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(CREATE_PRODUCT_DETAIL_BUNDLE_KEY, sales);
        bundle.putString(CREATE_SALE_KEY, sale_id);
        mIntent.putExtras(bundle);
        return mIntent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity=this;
        context=this;
        setContentView(R.layout.activity_productdetails);
        activity = this;
        context=this;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("Add Items");
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
        name = (EditText) findViewById(R.id.pd_name);
        description = (EditText) findViewById(R.id.pd_description);
        saleprice = (EditText) findViewById(R.id.pd_saleprice);
        /*retailprice = (EditText) findViewById(R.id.pd_retailprice);*/
        category = (EditText) findViewById(R.id.pd_category);
        mAutocompleteTextView = (TextView)findViewById(R.id.pick_location);
        mAutocompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= PickLocation.newInstance(context,GlobalVariables.TYPE_ADD_TEMS);
                startActivityForResult(intent,globalVariables.REQUEST_CODE_FILTER_PICK_LOCATION);
            }
        });
        shippable = (Switch) findViewById(R.id.pd_shippable);
        nagotiable = (Switch) findViewById(R.id.pd_nagotiable);
        is_new_old= (Switch)findViewById(R.id.is_new_old);

        zipcode_actv = (MultiAutoCompletionView) findViewById(R.id.creategarage_zip_code);
        zipcode_actv.setTokenListener(this);
        zipcode_actv.setThreshold(1);
        save = (Button) findViewById(R.id.pd_save);
        setAutoSuggestions(GlobalFunctions.getPostalCodes(activity));
        if(getIntent().hasExtra(CREATE_SALE_KEY)){
            sale_id=getIntent().getExtras().getString(CREATE_SALE_KEY);
        }
        if(getIntent().hasExtra(CREATE_PRODUCT_DETAIL_BUNDLE_KEY)){
            productModel = (CreateProductModel) getIntent().getExtras().getSerializable(CREATE_PRODUCT_DETAIL_BUNDLE_KEY);
            if(productModel!=null){
                setValues();
            }else{productModel=new CreateProductModel();
            productModel.setSaleID(sale_id);}

        }else{productModel=new CreateProductModel();}
        save=(Button)findViewById(R.id.pd_save);
        /*save.setVisibility(View.VISIBLE);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductDetails.this,CreateSaleActivity.class));
            }
        });*/
        shippable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((Switch) v).isChecked()) {
                    zipcode_actv.setVisibility(View.VISIBLE);
                }else{
                    zipcode_actv.setVisibility(View.GONE);
                }
            }
        });
        zipcode_actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoSuggesstionsList.get(position);
            }
        });
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clm==null){
                    clm = GlobalFunctions.getCategories(activity);
                    is_checked = new boolean[clm.getCategoryNames().size()];
                }
                openCategoryDialog(activity, clm.getCategoryNames());
            }
        });
    }

    private void setValues(){
        if(productModel!=null){
            type_edit_sale=true;
            loading_label="Updating Sale Item";
            save.setText("Update Sale Item...");
            name.setText(productModel.getName());
            description.setText(productModel.getDescription());
            saleprice.setText(productModel.getSalePrice());
            shippable.setChecked(productModel.isShippable()?true:false);
            /*if(shippable.isChecked()){
                if(address.getVisibility()==View.GONE)
                    address.setVisibility(View.VISIBLE);
                address.setText(productModel.getAddress());

            }*/
            mAutocompleteTextView.setText(productModel.getAddress());
            selectedCategoryIDs.clear();
            selectedCategoryIDs.addAll(productModel.getCategories());
            if(clm!=null){
                selectedCategoryString.clear();
                selectedCategoryString.addAll(clm.getNamesforIDs(selectedCategoryIDs));
                setCategory();
            }else{
                clm=GlobalFunctions.getCategories(context);
                selectedCategoryString.clear();
                selectedCategoryString.addAll(clm.getNamesforIDs(selectedCategoryIDs));
                setCategory();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        /*getMenuInflater().inflate(R.menu.edit_sale, menu);*/

        return true;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

    }
    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    protected void onStart() {
        super.onStart();

        clm = GlobalFunctions.getCategories(this);
        is_checked = new boolean[clm.getCategoryNames().size()];
    }

    @Override
    protected void onResume() {
        super.onResume();

        uploadImageFragment();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nagotiable.isChecked())
                    productModel.setNegotiable(true);
                postalCode = null;
                for(int  i=0;i<selectedAutoSuggesstionsList.size();i++){
                    postalCode = postalCode==null?selectedAutoSuggesstionsList.get(i).getId():postalCode+","+selectedAutoSuggesstionsList.get(i).getId();
                }
                str_name = name.getText().toString();
                str_desc = description.getText().toString();
                str_sp = saleprice.getText().toString();
                selectedCategoryIDs = clm.getIdsforNames(selectedCategoryString);
               /* ArrayList<String> ids = new ArrayList<String>();
                ids.add("22");
                ids.add("23");*/
                /*selectedCategoryIDs =ids;*/
                if(productModel.getImages().size()==0){
                    Toast.makeText(activity,"Please select a photo",Toast.LENGTH_LONG).show();
                }else if(TextUtils.isEmpty(str_name)){
                    name.setError("Please enter the product name");
                }else if(TextUtils.isEmpty(str_desc)){
                    description.setError("Please enter the product description");
                }/*else if(str_desc.length()<50){
                    description.setError("Product description must be at-least 50 characters");
                }*/else if(TextUtils.isEmpty(str_sp)){
                    saleprice.setError("Please enter the product price in AUD");
                }else if(selectedCategoryIDs.isEmpty()){
                    category.setError("Please fill the product Categories");
                }else if(shippable.isChecked() && TextUtils.isEmpty(mAutocompleteTextView.getText().toString())){
                    zipcode_actv.setError("Please enter the product zip code");
                }else if(TextUtils.isEmpty(mAutocompleteTextView.getText().toString())){
                    mAutocompleteTextView.setError("Please fill the product zip code");
                }else{
                    sale_images = productModel.getImages();
                    productModel.setAddress(mAutocompleteTextView.getText().toString());
                    productModel.setName(str_name);
                    productModel.setDescription(str_desc);
                    productModel.setSalePrice(str_sp);
                    productModel.setCategories(selectedCategoryIDs);
                    productModel.setCategory_string(category.getText().toString());
                    productModel.setPostcodes(postalCode);
                    productModel.setSaleType(GlobalVariables.TYPE_GARAGE);
                    productModel.setSaleID(sale_id);

                    if(nagotiable.isChecked())
                        productModel.setNegotiable(true);
                    if(shippable.isChecked())
                        productModel.setShippable(true);
                    if(is_new_old.isChecked())
                        productModel.setIs_product_new(true);

                    if(type_edit_sale)
                        productModel.setRequest_type(GlobalVariables.TYPE_UPDATE_REQUEST);
                    else
                        productModel.setRequest_type(GlobalVariables.TYPE_CREATE_REQUEST);

                    productModel.setAddress(mAutocompleteTextView.getText().toString());
                    if(locationModel!=null){
                        if(TextUtils.isEmpty(productModel.getLatitude())){
                            productModel.setLatitude(locationModel.getLatitude()+"");
                            productModel.setLongitude(locationModel.getLongitude()+"");
                        }
                    }
                    if(nagotiable.isChecked())
                        productModel.setNegotiable(true);

                    if(shippable.isChecked())
                        productModel.setShippable(true);
                    //productModel.setProductCategory(str_cat);
                    createSaleItem(context,productModel);
//                    sale_images = productModel.getImages();

                      /*  setReturnResult(productModel);*/
                }
            }
        });

    }

    private void createSaleItem(final Context context, final CreateProductModel createProductModel){
        GlobalFunctions.showProgress(context,loading_label);
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.createSaleItem(context, createProductModel, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                if(arg0 instanceof IDModel){
                    IDModel idModel = (IDModel) arg0;
                    createProductModel.setProduct_id(idModel.getId());
                    Toast.makeText(context, "Add has been added Successfully", Toast.LENGTH_LONG).show();
                    setReturnResult(createProductModel);
                    Log.d(TAG, "createProductModel "+createProductModel.toString());
                }else if(arg0 instanceof ErrorModel){
                    ErrorModel errorModel = (ErrorModel) arg0;
                    String msg = errorModel.getError()!=null ? errorModel.getError() : errorModel.getMessage();
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }else if(arg0 instanceof StatusModel){
                    Toast.makeText(context, "Add has been Updated Successfully", Toast.LENGTH_LONG).show();
                    setReturnResult(createProductModel);

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
    private void uploadImageFragment(){
        /*objectUploadPhoto.setAvailablePhotos(productModel.getImages());
        if(type_edit_sale){
            Fragment aboutFragment = PhotosAddFragmentMain.newInstance(activity, objectUploadPhoto,GlobalVariables.TYPE_UPDATE_REQUEST);
            getSupportFragmentManager().beginTransaction().replace(R.id.container_upload_image, aboutFragment, "uploadImage").commit();
        }else {
            Fragment aboutFragment = PhotosAddFragmentMain.newInstance(activity, objectUploadPhoto,GlobalVariables.TYPE_CREATE_REQUEST);
            getSupportFragmentManager().beginTransaction().replace(R.id.container_upload_image, aboutFragment, "uploadImage").commit();
        }
*/

        objectUploadPhoto.setAvailablePhotos(productModel.getImages());
        Fragment aboutFragment = PhotosAddFragmentMain.newInstance(activity,objectUploadPhoto,GlobalVariables.TYPE_CREATE_REQUEST);
        getSupportFragmentManager().beginTransaction().replace(R.id.container_upload_image, aboutFragment, "uploadImage").commit();

    }

    private void openCategoryDialog(Activity act, ArrayList<String> list){
        // Intialize  readable sequence of char values
        final CharSequence[] dialogList=  list.toArray(new CharSequence[list.size()]);
        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(act);
        builderDialog.setTitle("Select Product Categories");
       /* View tile=activity.getLayoutInflater().inflate(R.layout.autocomplete_row_item,null);
        builderDialog.setCustomTitle(tile);
      */  int count = dialogList.length;
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

                            if(checked) {
                                selectedCategoryString.add(list.getItemAtPosition(i).toString());
                            }
                        }
                        if(selectedCategoryString.size()<3){
                            setCategory();
                            dialog.dismiss();
                        }else{
                            Toast.makeText(activity,"Please select only two categories",Toast.LENGTH_LONG).show();
                        }
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

    private void setCategory()
    {
        if(selectedCategoryString.size()>0){
            String temp = "";
            for(int i=0;i<selectedCategoryString.size();i++){
                temp = i==0? selectedCategoryString.get(0): temp+", "+selectedCategoryString.get(i);
            }
            if(category!=null){category.setText(temp);}
        }


    }

    public static void setImages(){
        if(objectUploadPhoto!=null && productModel!=null){
            productModel.setImages(objectUploadPhoto.getAvailablePhotos());
        }

    }

    private void setReturnResult(CreateProductModel productModel){
        if(productModel!=null){
            Bundle bundle = new Bundle();
            bundle.putSerializable(AddItems.PRODUCT_DETAILA_BUNDLE_KEY,productModel);
            Intent result = new Intent(activity,AddItems.class);
            result.putExtras(bundle);
            setResult(this.RESULT_OK,result);
        }else{
            setResult(this.RESULT_CANCELED);
        }

        finish();
    }

    @Override
    public void onBackPressed() {
        setReturnResult(null);
        super.onBackPressed();
    }

    @Override
    public void onTokenAdded(Object obj) {
        PostcodeModel model = (PostcodeModel) obj;
        selectedAutoSuggesstionsList.add(model);
    }

    @Override
    public void onTokenRemoved(Object obj) {
        PostcodeModel model = (PostcodeModel) obj;
        for(int i=0;i<selectedAutoSuggesstionsList.size();i++){
            if(selectedAutoSuggesstionsList.get(i).getId().equalsIgnoreCase(model.getId())){
                selectedAutoSuggesstionsList.remove(i);break;
            }
        }
    }
    private void setAutoSuggestions(PostcodeListModel listModels){
        if(activity!=null && autoSuggesstionsList!=null && listModels!=null && zipcode_actv!=null){
            zipcode_actv.setText("");
            listModels.setDisplayFlag(true);
            autoSuggesstionsList.clear();
            autoSuggesstionsList.addAll(listModels.getPostcodeModelList());
            //synchronized (autoCompletionAdapter){autoCompletionAdapter.notifyDataSetChanged();}
            autoCompletionAdapter =  new ArrayAdapter<PostcodeModel>(this, android.R.layout.simple_list_item_1, autoSuggesstionsList);//new AutoCompletionAdapter(activity, autoSuggesstionsList);
            zipcode_actv.setAdapter(autoCompletionAdapter);
            zipcode_actv.setThreshold(1);
            //zipcode_actv.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG,"on activity result");
        if(resultCode == activity.RESULT_OK){
            Log.i(TAG,"result ok ");
            Log.i(TAG,"request code "+globalVariables.REQUEST_CODE_FILTER_PICK_LOCATION);
            if(requestCode==globalVariables.REQUEST_CODE_FILTER_PICK_LOCATION){
                if(data.hasExtra(CREATE_GARRAGE_LOCATION_BUNDLE_KEY)){
                    LocationModel location_model = (LocationModel) data.getExtras().getSerializable(CREATE_GARRAGE_LOCATION_BUNDLE_KEY);
                    Log.i(TAG,"name pm"+location_model.getFormatted_address());
                    locationModel=location_model;
                    productModel.setLatitude(location_model.getLatitude());
                    productModel.setLongitude(location_model.getLongitude());
                    productModel.setAddress(location_model.getFormatted_address());
                    productModel.setSuburb(location_model.getSubhurb());
                    productModel.setCity(location_model.getCity());
                    mAutocompleteTextView.setText(location_model.getFormatted_address());
                }

            }
            }

    }
}
