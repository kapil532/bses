package com.langoor.app.blueshak.garage;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
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
import com.langoor.app.blueshak.AppController;
import com.langoor.app.blueshak.MainActivity;
import com.langoor.app.blueshak.PickLocation;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.item.ProductDetail;
import com.langoor.app.blueshak.login.LoginActivity;
import com.langoor.app.blueshak.photos_add.Object_PhotosAdd;
import com.langoor.app.blueshak.photos_add.PhotosAddFragmentMain;
import com.langoor.app.blueshak.services.ServerResponseInterface;
import com.langoor.app.blueshak.services.ServicesMethodsManager;
import com.langoor.app.blueshak.services.model.CategoryListModel;
import com.langoor.app.blueshak.services.model.CategoryModel;
import com.langoor.app.blueshak.services.model.CreateProductModel;
import com.langoor.app.blueshak.services.model.ErrorModel;
import com.langoor.app.blueshak.services.model.IDModel;
import com.langoor.app.blueshak.services.model.LocationModel;
import com.langoor.app.blueshak.services.model.PostcodeListModel;
import com.langoor.app.blueshak.services.model.PostcodeModel;
import com.langoor.app.blueshak.services.model.ProductModel;
import com.langoor.app.blueshak.services.model.SalesModel;
import com.langoor.app.blueshak.services.model.StatusModel;
import com.langoor.app.blueshak.util.LocationListener;
import com.langoor.app.blueshak.util.LocationService;
import com.langoor.app.blueshak.view.MultiAutoCompletionView;
import com.tokenautocomplete.TokenCompleteTextView;
import java.util.ArrayList;
import java.util.List;

public class CreateItemSaleFragment extends Fragment  implements TokenCompleteTextView.TokenListener{

    String TAG = "CreateItemSaleFragment";
    String[] ary;
    public static final String CREATE_ITEM_BUNDLE_KEY = "CreateItemSaleFragmentBundleKey";
    public static final String CREATE_ITEM_TYPE_BUNDLE_KEY = "CreateItemSaleFragmentTypeBundleKey";
    public static final String CREATE_ITEM_LOCATION_BUNDLE_KEY = "CreateItemActivityLocationBundleKey";
    public static final String  FROM_KEY= "from_key";
    private Toolbar toolbar;
    private EditText category;
    private EditText name,description,saleprice,address;
    private Switch shippable,nagotiable,is_new_old;
    private Button save;
    private boolean[] is_checked;
    static Activity activity;
    private static final int GOOGLE_API_CLIENT_ID = 1;
    private CategoryListModel clm;
    private List<CategoryModel> list_cm = new ArrayList<CategoryModel>();
    private ArrayList<String> selectedCategoryString = new ArrayList<String>();
    private ArrayList<String> selectedCategoryIDs = new ArrayList<String>();
    private static Object_PhotosAdd objectUploadPhoto = new Object_PhotosAdd();
    private static GlobalFunctions globalFunctions = new GlobalFunctions();
    private static GlobalVariables globalVariables = new GlobalVariables();
    private static CreateProductModel productModel = null;
    private TextView mAutocompleteTextView;
    private String str_name,str_desc,str_sp,locstr,descriptionstr,catogarystr,postalCode;
    boolean isAvailable = true;
    private static ArrayList<PostcodeModel> autoSuggesstionsList = new ArrayList<PostcodeModel>();
    private static ArrayList<PostcodeModel> selectedAutoSuggesstionsList = new ArrayList<PostcodeModel>();
    private ArrayAdapter<PostcodeModel> autoCompletionAdapter = null;
    private MultiAutoCompletionView zipcode_actv;
    private int from_key;
    private Context context;
    private LocationModel locationModel=new LocationModel();
    private View view;
    private Boolean type_edit_item=false;
    private String loading_label="Publishing Item...";
    public static CreateItemSaleFragment newInstance(Context context, CreateProductModel sales, LocationModel locationModel,String type, int from){
        CreateItemSaleFragment createItemSaleFragment = new CreateItemSaleFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(CREATE_ITEM_BUNDLE_KEY, sales);
        bundle.putSerializable(CREATE_ITEM_TYPE_BUNDLE_KEY, type);
        bundle.putSerializable(CREATE_ITEM_LOCATION_BUNDLE_KEY, locationModel);
        bundle.putInt(FROM_KEY, from);
        createItemSaleFragment.setArguments(bundle);
        return createItemSaleFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        context= getActivity();
        activity=getActivity();
        view = inflater.inflate(R.layout.item_productdetails_new, container, false);
        name = (EditText) view.findViewById(R.id.pd_name);
        description = (EditText)view.findViewById(R.id.pd_description);
        saleprice = (EditText) view.findViewById(R.id.pd_saleprice);
        category = (EditText) view.findViewById(R.id.pd_category);
        category.setOnFocusChangeListener(new View.OnFocusChangeListener() {
           @Override
           public void onFocusChange(View v, boolean hasFocus){
               if(hasFocus){
                   if(clm==null){
                       clm = GlobalFunctions.getCategories(activity);
                       if(clm!=null){
                           is_checked = new boolean[clm.getCategoryNames().size()];
                           openCategoryDialog(activity, clm.getCategoryNames());
                       }
                   }else{
                       openCategoryDialog(activity, clm.getCategoryNames());
                   }
               }
           }
        });
        mAutocompleteTextView = (TextView) view.findViewById(R.id
                .pick_location);
        mAutocompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= PickLocation.newInstance(context,GlobalVariables.TYPE_ADD_TEMS);
                startActivityForResult(intent,globalVariables.REQUEST_CODE_FILTER_PICK_LOCATION);
            }
        });
        save = (Button)view.findViewById(R.id.pd_publish);
        mAutocompleteTextView.setOnEditorActionListener(new DoneOnEditorActionListener());
        shippable = (Switch)view.findViewById(R.id.pd_shippable);
        nagotiable = (Switch)view.findViewById(R.id.pd_nagotiable);
        is_new_old= (Switch)view.findViewById(R.id.is_new_old);
        zipcode_actv = (MultiAutoCompletionView)view.findViewById(R.id.creategarage_zip_code);
        if(shippable.isChecked())
            zipcode_actv.setVisibility(View.VISIBLE);
        zipcode_actv.setTokenListener(this);
        zipcode_actv.setThreshold(1);
        zipcode_actv.allowCollapse(true);
        zipcode_actv.allowDuplicates(true);
        zipcode_actv.setSoundEffectsEnabled(true);
        productModel = (CreateProductModel) getArguments().getSerializable(CREATE_ITEM_BUNDLE_KEY);
        locationModel=(LocationModel)getArguments().getSerializable(CREATE_ITEM_LOCATION_BUNDLE_KEY);
        if(productModel!=null){
                setValues();
        }else{productModel=new CreateProductModel();}
        from_key = getArguments().getInt(FROM_KEY);

        shippable.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((Switch) v).isChecked()) {
                    zipcode_actv.setVisibility(View.VISIBLE);
                }else{
                    zipcode_actv.setVisibility(View.GONE);
                    mAutocompleteTextView.requestFocus();
                    /*pick_location.requestFocus();*/

                }
            }
        });
        zipcode_actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              /*  Toast.makeText(context,"Zipcode",Toast.LENGTH_LONG).show();*/
                 autoSuggesstionsList.get(position);
            }
        });
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clm==null){
                    clm = GlobalFunctions.getCategories(activity);
                    if(clm!=null){
                        is_checked = new boolean[clm.getCategoryNames().size()];
                        openCategoryDialog(activity, clm.getCategoryNames());
                    }
                }else{
                    openCategoryDialog(activity, clm.getCategoryNames());
                }

            }
        });


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.list_menu, menu);
        if (menu.findItem(R.id.action_publiesh) != null){
            if(type_edit_item)
                menu.findItem(R.id.action_publiesh).setTitle("Update");
            else
                menu.findItem(R.id.action_publiesh).setTitle("Publish");
        }

        // You can look up you menu item here and store it in a global variable by
        // 'mMenuItem = menu.findItem(R.id.my_menu_item);'
    }
    @Override
    public void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clm = GlobalFunctions.getCategories(activity);
                        if(clm!=null)
                            is_checked = new boolean[clm.getCategoryNames().size()];
                        setAutoSuggestions(GlobalFunctions.getPostalCodes(activity));

                    }
                });
            }
        }).start();


    }
    @Override
    public void onDestroy() {
        super.onDestroy();

    }
    public void setLocation(LocationModel locationModel){
        this.locationModel=locationModel;
        Toast.makeText(context,"setLocation",Toast.LENGTH_LONG).show();
        if(mAutocompleteTextView!=null)
            mAutocompleteTextView.setText(locationModel.getFormatted_address());
    }

    @Override
    public void onPause() {
        super.onPause();

    }
    public void Publish() {
        onClickProcessing();
    }
    private void onClickProcessing(){

        if(nagotiable.isChecked())
            productModel.setNegotiable(true);

        if(is_new_old.isChecked())
            productModel.setIs_product_new(true);

        postalCode = null;
        for(int  i=0;i<selectedAutoSuggesstionsList.size();i++){
            postalCode = postalCode==null?selectedAutoSuggesstionsList.get(i).getId():postalCode+","+selectedAutoSuggesstionsList.get(i).getId();
        }
        str_name = name.getText().toString();
        str_desc = description.getText().toString();
        str_sp = saleprice.getText().toString();
     /*   ArrayList<String> ids = new ArrayList<String>();
        ids.add("22");
        ids.add("23");
        selectedCategoryIDs =ids;*/
        if(clm!=null)
            selectedCategoryIDs = clm.getIdsforNames(selectedCategoryString);
        if(productModel.getImages().size()==0){
            Toast.makeText(activity,"Please select a photo",Toast.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(str_name)){
           /* name.setError("Please enter the product name");*/
            Toast.makeText(activity,"Please enter the product name",Toast.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(str_sp)){
           /* saleprice.setError("Please enter the product price in AUD");*/
            Toast.makeText(activity,"Please enter the product price in AUD",Toast.LENGTH_LONG).show();
        }else if(str_sp.length()>8){
           /* saleprice.setError("Please enter the valid product price in AUD");*/
            Toast.makeText(activity,"Please enter the valid product price in AUD",Toast.LENGTH_LONG).show();
        }else if(selectedCategoryIDs.isEmpty()){
          /*  category.setError("Please fill the product Category");*/
            Toast.makeText(activity,"Please fill the product Category",Toast.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(str_desc)){
           /* description.setError("Please enter the product description");*/
            Toast.makeText(activity,"Please enter the product description",Toast.LENGTH_LONG).show();
        }else if(shippable.isChecked() && TextUtils.isEmpty(mAutocompleteTextView.getText().toString())){
            /*zipcode_actv.setError("Please enter the product zip code");*/
            Toast.makeText(activity,"Please enter the product zip code",Toast.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(mAutocompleteTextView.getText().toString())){
            /*mAutocompleteTextView.setError("Please fill the product location");*/
            Toast.makeText(activity,"Please fill the product location",Toast.LENGTH_LONG).show();
        }else{
            productModel.setAddress(mAutocompleteTextView.getText().toString());
            productModel.setName(str_name);
            productModel.setDescription(str_desc);
            productModel.setSalePrice(str_sp);
            productModel.setCategories(selectedCategoryIDs);
            productModel.setPostcodes(postalCode);
            productModel.setSaleType(GlobalVariables.TYPE_ITEM);
            /*productModel.setSaleID(sale_id);*/
            if(type_edit_item)
                productModel.setRequest_type(GlobalVariables.TYPE_UPDATE_REQUEST);
            else
                productModel.setRequest_type(GlobalVariables.TYPE_CREATE_REQUEST);


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
            else
                productModel.setIs_pickup(true);
            if(is_new_old.isChecked())
                productModel.setIs_product_new(true);
            String token = GlobalFunctions.getSharedPreferenceString(context, GlobalVariables.SHARED_PREFERENCE_TOKEN);
            if(token!=null){
                createSaleItem(context,productModel);
            }else{
                showSettingsAlert();
            }

        }
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
                    showSuccesAlert(getResources().getString(R.string.item_listed),getResources().getString(R.string.item_listed_message));
                /*     Toast.makeText(context, "Item has been listed Successfully", Toast.LENGTH_LONG).show();
               */    /* closeThisActivity();
                    startActivity(new Intent(activity,MainActivity.class));*/
                  }else if(arg0 instanceof ErrorModel){
                    ErrorModel errorModel = (ErrorModel) arg0;
                    String msg = errorModel.getError()!=null ? errorModel.getError() : errorModel.getMessage();
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }else if (arg0 instanceof StatusModel){
                    GlobalFunctions.hideProgress();
                    StatusModel statusModel=(StatusModel)arg0;
                    if(statusModel.isStatus()){
                        showSuccesAlert(getResources().getString(R.string.item_updated),getResources().getString(R.string.item_updated_message));
                        ProductModel productModel=new ProductModel();
                        productModel.setId(createProductModel.getProduct_id());
                        closeThisActivity();
                        Intent intent = ProductDetail.newInstance(context,productModel,null,GlobalVariables.TYPE_MY_SALE);
                        context.startActivity(intent);
                    }
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
    public void onResume() {
        super.onResume();

        uploadImageFragment();

        AppController.getInstance().trackScreenView(CreateGarageSaleFragment.TAG);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickProcessing();
            }
        });

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    if(productModel!=null){
                        if(productModel.getImages().size()!=0||
                                !TextUtils.isEmpty(name.getText().toString())||
                                !TextUtils.isEmpty(description.getText().toString())||
                                !TextUtils.isEmpty(saleprice.getText().toString())||
                                !selectedCategoryIDs.isEmpty()||
                                !TextUtils.isEmpty(mAutocompleteTextView.getText().toString())
                                ){
                            showExitAlert();
                        }else
                            activity.finish();
                    }else
                        activity.finish();


                    return true;

                }

                return false;
            }
        });

    }

    private void uploadImageFragment(){
        objectUploadPhoto.setAvailablePhotos(productModel.getImages());
        Fragment aboutFragment = PhotosAddFragmentMain.newInstance(activity,objectUploadPhoto,GlobalVariables.TYPE_CREATE_REQUEST);
        getChildFragmentManager().beginTransaction().replace(R.id.container_upload_image, aboutFragment, "uploadImage").commit();
    }

    private void setValues(){
        if(productModel!=null){
          /*  additem.setText("Update Sale");*/
            loading_label="Updating Item...";
            save.setText("Update");
            type_edit_item=true;
            name.setText(productModel.getName());
            description.setText(productModel.getDescription());
            saleprice.setText(productModel.getSalePrice());
            shippable.setChecked(productModel.isShippable()?true:false);
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
           /* createSaleItem(context,productModel);*/
        }
    }
    private void setCategory()
    {
        if(selectedCategoryString.size()>0){
            String temp = "";
            for(int i=0;i<selectedCategoryString.size();i++){
                temp = i==0? selectedCategoryString.get(0): temp+", "+selectedCategoryString.get(i);
            }
            if(category!=null){category.setText(temp);
                category.setFocusable(false);
             /*   if(shippable.isChecked())
                    zipcode_actv.requestFocus();*/
            }
        }

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

    private void openCategoryDialog(Activity act, ArrayList<String> list){
        // Intialize  readable sequence of char values
        final CharSequence[] dialogList=  list.toArray(new CharSequence[list.size()]);
        final AlertDialog.Builder builderDialog =  new AlertDialog.Builder(activity);
                /*new ContextThemeWrapper(context, android.R.style.Theme_Material_Dialog_Alert));*/
        builderDialog.setTitle("Select Item Category");
        /*builderDialog.setCustomTitle(R.layout.progress_empty_view);*/
        int count = dialogList.length;
        //boolean[] is_checked = new boolean[count]; // set is_checked boolean false;

        // Creating multiple selection by using setMutliChoiceItem method
        builderDialog.setMultiChoiceItems(dialogList, is_checked,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton, boolean isChecked) {
                    }
                });
        builderDialog
                .setSingleChoiceItems(dialogList, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int item) {
                        selectedCategoryString.clear();
                        selectedCategoryString.add(dialogList[item].toString());
                        setCategory();
                        description.requestFocus();
                        dialogInterface.dismiss();
                      /*  Toast.makeText(context, dialogList[item], Toast.LENGTH_SHORT).show();*/
                    }
                });

      /*  builderDialog.setPositiveButton("OK",
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
                        if(!(selectedCategoryString.size()>1)){
                            setCategory();

                            description.requestFocus();
                            *//*if(shippable.isChecked())
                                zipcode_actv.requestFocus();
                            else
                                mAutocompleteTextView.requestFocus();
*//*
                            dialog.dismiss();

                        }else{
                            Toast.makeText(activity,"Please select only one category",Toast.LENGTH_LONG).show();
                        }
                    }
                });

        builderDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });*/
        AlertDialog alert = builderDialog.create();
        alert.show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

       if(id ==android.R.id.home){
             if(productModel!=null){
               if(productModel.getImages().size()!=0||
                       !TextUtils.isEmpty(name.getText().toString())||
                       !TextUtils.isEmpty(description.getText().toString())||
                       !TextUtils.isEmpty(saleprice.getText().toString())||
                       !selectedCategoryIDs.isEmpty()||
                       !TextUtils.isEmpty(mAutocompleteTextView.getText().toString())
                       ){
                   showExitAlert();
               }else
                   activity.finish();
           }else
               activity.finish();

            return true;
        }else if(id==R.id.action_publiesh){
            onClickProcessing();
       }

        return super.onOptionsItemSelected(item);
    }

    private void setAutoSuggestions(PostcodeListModel listModels){
        if(activity!=null && autoSuggesstionsList!=null && listModels!=null && zipcode_actv!=null){
            zipcode_actv.setText("");
            listModels.setDisplayFlag(true);
            autoSuggesstionsList.clear();
            autoSuggesstionsList.addAll(listModels.getPostcodeModelList());
            //synchronized (autoCompletionAdapter){autoCompletionAdapter.notifyDataSetChanged();}
            autoCompletionAdapter =  new ArrayAdapter<PostcodeModel>(activity, android.R.layout.simple_list_item_1, autoSuggesstionsList);//new AutoCompletionAdapter(activity, autoSuggesstionsList);
            zipcode_actv.setAdapter(autoCompletionAdapter);
            zipcode_actv.setThreshold(1);
            //zipcode_actv.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        }
    }
    public void showExitAlert(){
        final com.langoor.app.blueshak.view.AlertDialog alertDialog = new com.langoor.app.blueshak.view.AlertDialog(activity);
        alertDialog.setTitle("Are you sure?");
        alertDialog.setMessage("Are you sure to stop listing this item? The data you've created will be lost.");
        alertDialog.setPositiveButton("Yes", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(from_key==GlobalVariables.TYPE_AC_SIGN_UP)
                    startActivity(new Intent(getActivity(),MainActivity.class));
                else
                    getActivity().finish();
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
    public void showSuccesAlert(String title,String message){
        final com.langoor.app.blueshak.view.AlertDialog alertDialog = new com.langoor.app.blueshak.view.AlertDialog(activity);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Add more items", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity,CreateSaleActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                alertDialog.dismiss();
                closeThisActivity();

            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Continue shopping", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Intent intent=new Intent(activity,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);*/
                alertDialog.dismiss();
                closeThisActivity();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    public void showSettingsAlert(){
        final com.langoor.app.blueshak.view.AlertDialog alertDialog = new com.langoor.app.blueshak.view.AlertDialog(context);
        alertDialog.setMessage("Please Login/Register to continue");
        alertDialog.setPositiveButton("Continue", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent creategarrage = LoginActivity.newInstance(activity,null,productModel);
                startActivity(creategarrage);
             /*   mGoogleApiClient.stopAutoManage(getActivity());*/
                /*mGoogleApiClient.disconnect();*/
                closeThisActivity();
            }
        });

       /* // on pressing cancel button
        alertDialog.setNegativeButton("Not now", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });*/

        // Showing Alert Message
        alertDialog.show();
    }
    public static void closeThisActivity(){
        if(activity!=null){activity.finish();}
    }


    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG,"on activity result");
        if(resultCode == activity.RESULT_OK){
            Log.i(TAG,"result ok ");
            Log.i(TAG,"request code "+globalVariables.REQUEST_CODE_FILTER_PICK_LOCATION);
            LocationModel location_model = (LocationModel) data.getExtras().getSerializable(CREATE_ITEM_LOCATION_BUNDLE_KEY);
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
     class DoneOnEditorActionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onClickProcessing();
                return true;
            }
            return false;
        }
    }
}

