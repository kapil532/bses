package com.langoor.app.blueshak.profile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.divrt.co.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.langoor.app.blueshak.ImageCashing.ImageLoader;
import com.langoor.app.blueshak.Messaging.util.CommonUtil;
import com.langoor.app.blueshak.PickLocation;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.helper.RoundedImageView;
import com.langoor.app.blueshak.login.LoginActivity;
import com.langoor.app.blueshak.services.ServerResponseInterface;
import com.langoor.app.blueshak.services.ServicesMethodsManager;
import com.langoor.app.blueshak.services.model.ProductModel;
import com.langoor.app.blueshak.services.model.ProfileDetailsModel;
import com.langoor.app.blueshak.services.model.Shop;
import com.langoor.app.blueshak.services.model.StatusModel;
import com.langoor.app.blueshak.services.model.UserDetailsModel;
import com.langoor.app.blueshak.util.LocationService;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProfileEditActivity extends AppCompatActivity {
    String TAG = "ProfileEditActivity";

    Button btpic, btnup;
    String picturePath;
    Uri selectedImage;
    Bitmap photo;
    String ba1;
LinearLayout location_content,profile_photo_content;
    private Uri fileUri;
    private RoundedImageView rounde_image;
    private EditText edit_fullname;
    private TextView edit_mobile,email_verified;
    private TextView edit_address,profile_photo,shop_link;
    private EditText edit_email,shop_name,about_my_shop;
    private EditText edit_std; private TextView location ;
    static GlobalFunctions globalFunctions = new GlobalFunctions();
    static GlobalVariables globalVariables = new GlobalVariables();
    private static final int REQUEST_CAMERA = 11;
    private static final int PICK_PHOTO_FOR_AVATAR = 12;
    private Dialog alert;
    public String getImage_bmp() {
        return image_bmp;
    }
    public void setImage_bmp(String image_bmp) {
        this.image_bmp = image_bmp;
    }
    private String image_bmp;
    static Context context;
    static Activity activity;
    static final String  PRODUCTDETAIL_BUNDLE_KEY_POSITION = "ProductModelWithDetails";
    static final String  PRODUCTDETAIL_BUNDLE_KEY_FLAG = "ProductModelWithFlag";
    private ImageLoader imageLoader;
    private Switch fb_link;
    ProfileDetailsModel profileDetailsModel=new ProfileDetailsModel();
    protected static final int REQUEST_CHECK_CAMERA = 112;
    protected static final int REQUEST_CHECK_GALLARY = 113;
    public static Intent newInstance(Context context, ProfileDetailsModel product){
        Intent mIntent = new Intent(context,ProfileEditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(PRODUCTDETAIL_BUNDLE_KEY_POSITION, product);
        mIntent.putExtras(bundle);
        return mIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("Edit Profile");
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.brandColor)));
        }
        context=this;
        activity=this;
        imageLoader=new ImageLoader(this);
        rounde_image =(RoundedImageView) findViewById(R.id.rounde_image);
        edit_fullname =(EditText)findViewById(R.id.edit_fullname);
        edit_email =(EditText) findViewById(R.id.edit_email);
        location=(TextView) findViewById(R.id.location);
        edit_mobile =(TextView)findViewById(R.id.edit_mobile);
        edit_mobile.setKeyListener(null);
        edit_mobile.setKeyListener(null);
        edit_address = (TextView) findViewById(R.id.location);
        edit_email.setKeyListener(null);
        edit_email.setKeyListener(null);
        shop_link=(TextView) findViewById(R.id.shop_link);
        shop_name=(EditText)findViewById(R.id.shop_name);
        about_my_shop=(EditText)findViewById(R.id.about_my_shop);
        email_verified=(TextView)findViewById(R.id.email_verified);
        fb_link=(Switch)findViewById(R.id.fb_link);
        fb_link.setClickable(false);

        location_content=(LinearLayout)findViewById(R.id.location_content);

        location_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, PickLocation.class));
            }
        });
        profile_photo_content=(LinearLayout)findViewById(R.id.profile_photo_content);
        profile_photo_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert();
            }
        });
        profileDetailsModel=(ProfileDetailsModel) getIntent().getExtras().getSerializable(PRODUCTDETAIL_BUNDLE_KEY_POSITION);
        if(profileDetailsModel!=null)
            setThisPage();


        rounde_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //sendThaData();
                showAlert();
            }
        });


    }
    @Override
    public void onResume() {
        super.onResume();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == R.id.save) {
            sendRequestToServer(context);
          return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public  void setThisPage(){
        edit_fullname.setText(profileDetailsModel.getName());
        edit_email.setText(profileDetailsModel.getEmail());
        edit_address.setText(profileDetailsModel.getAddress());
        edit_mobile.setText(profileDetailsModel.getPhone());
        String avatar=profileDetailsModel.getAvatar();
        if(!TextUtils.isEmpty(avatar)){
            /*imageLoader.DisplayImage(avatar,rounde_image);*/
            Picasso.with(activity)
                    .load(avatar)
                    .placeholder(R.drawable.placeholder_background)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .fit().centerCrop()
                    .into(rounde_image);
        }
        Shop shop=profileDetailsModel.getShop();
        if(shop!=null){
            shop_name.setText(shop.getName());
            shop_link.setText(shop.getShop_link());
            about_my_shop.setText(shop.getDescription());
        }
        if(profileDetailsModel.is_email_verify())
            email_verified.setText("Verified");
        else
            email_verified.setText("Not Verified");
        if(!TextUtils.isEmpty(profileDetailsModel.getFb_id()))
            fb_link.setChecked(true);
    }

    private void sendRequestToServer(final Context context) {
        final String fullname = edit_fullname.getText().toString();
        final String email = edit_email.getText().toString();
        final String mobile = edit_mobile.getText().toString();
        final String address = edit_address.getText().toString();
        final String shp_name = shop_name.getText().toString();
        final String shop_description = about_my_shop.getText().toString();
        final String shp_link = shop_link.getText().toString();



        final String bmp=getImage_bmp();

        GlobalFunctions.showProgress(activity, "Updating your profile");
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        ProfileDetailsModel profileDetailsModel = new ProfileDetailsModel();
        profileDetailsModel.setName(fullname);
        profileDetailsModel.setEmail(email);
        profileDetailsModel.setPhone(mobile);
        profileDetailsModel.setAddress(address);
        profileDetailsModel.setImage(bmp);
        profileDetailsModel.setShop_description(shop_description);
        profileDetailsModel.setShop_name(shp_name);

    /*    Shop shop=new Shop();
        shop.setName(shp_name);
        shop.setDescription(shop_description);
        shop.setShop_link(shp_link);
        profileDetailsModel.setShop(shop);*/

        servicesMethodsManager.updateProfile(context, profileDetailsModel, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                ProfileDetailsModel model=(ProfileDetailsModel)arg0;
                GlobalFunctions.setSharedPreferenceString(context, GlobalVariables.SHARED_PREFERENCE_FULLNAME, model.getName());
                GlobalFunctions.setSharedPreferenceString(context, GlobalVariables.SHARED_PREFERENCE_PHONE, model.getPhone());
                GlobalFunctions.setSharedPreferenceString(context, GlobalVariables.SHARED_PREFERENCE_ADDRESS, model.getAddress());
                GlobalFunctions.setSharedPreferenceString(context, GlobalVariables.SHARED_PREFERENCE_AVATAR, model.getAvatar());

                Toast.makeText(context,"Your profile has been updated Successfully",Toast.LENGTH_LONG).show();
                finish();
              /*  if(statusModel.isStatus()){
                    Toast.makeText(context,"Your profile has been updated Successfully",Toast.LENGTH_LONG).show();
                    finish();
                }*/

             /*   OnUpdated();*/

            }
            @Override
            public void OnFailureFromServer(String msg) {
                GlobalFunctions.hideProgress();
                Toast.makeText(activity,"Can't update.....",Toast.LENGTH_LONG).show();
            }

            @Override
            public void OnError(String msg) {
                GlobalFunctions.hideProgress();
                Toast.makeText(activity,"Some thing went wrong...",Toast.LENGTH_LONG).show();
            }
        },"Profile update");

    }
    void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final CharSequence[] charSequences= new CharSequence[]{"Click a picture from camera", "Choose from gallery"};
        builder.setItems(R.array.select_dialog_items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                      /* User clicked so do some stuff */
                String[] items = getResources().getStringArray(R.array.select_dialog_items);
                if(which==0){
                    if(checkIfAlreadyhavePermission())
                        cameraIntent();
                    else
                        checkCameraPermission();
                }else{
                    if(checkIfReadExternalStorageAlreadyhavePermission())
                        pickImage();
                    else
                        checkReadExternalStoragePermission();

                }
            }
        });
        alert = builder.create();
        alert.show();

    }
    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(data.getData());
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);
                String base64_image= CommonUtil.encodeToBase64(bmp, Bitmap.CompressFormat.JPEG,100);
                setImage_bmp(base64_image);

              /*  if(bmp!=null)
                    bmp.recycle();
*/
                if(!TextUtils.isEmpty(base64_image)){

                    rounde_image.setImageBitmap(bmp);
                }else{
                    Toast.makeText(context,"Your profile pic is empty can't update the your profile",Toast.LENGTH_LONG).show();
                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else if(requestCode == REQUEST_CAMERA){

            onCaptureImageResult(data);
        }
    }
    private void cameraIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }
    private void onCaptureImageResult(Intent data) {
        if(data!=null){
            if(data.hasExtra("data")){
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                String base64_image= CommonUtil.encodeToBase64(thumbnail, Bitmap.CompressFormat.JPEG,100);
                rounde_image.setImageBitmap(thumbnail);
      /*  if(thumbnail!=null)
           thumbnail.recycle();
      */
                setImage_bmp(base64_image);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void setValues(UserDetailsModel model){

        GlobalFunctions.setSharedPreferenceString(context, GlobalVariables.SHARED_PREFERENCE_FULLNAME, model.getName());
        GlobalFunctions.setSharedPreferenceString(context, GlobalVariables.SHARED_PREFERENCE_PHONE, model.getPhone());
        GlobalFunctions.setSharedPreferenceString(context, GlobalVariables.SHARED_PREFERENCE_ADDRESS, model.getAddress());
        GlobalFunctions.setSharedPreferenceString(context, GlobalVariables.SHARED_PREFERENCE_AVATAR, model.getAvatar());
        Toast.makeText(context,"Your profile has been updated Successfully",Toast.LENGTH_LONG).show();

        Intent i= new Intent(this,ProfileActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        //    mainActivityFM.beginTransaction().replace(R.id.profile_container,fragment).commit();

    }


    public void OnUpdated() {
        GlobalFunctions.showProgress(context, "Updating profile data...");
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.getUserDetails(context, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();

                if(arg0 instanceof UserDetailsModel){
                    UserDetailsModel model = (UserDetailsModel) arg0;
                    setValues(model);
                }

            }

            @Override
            public void OnFailureFromServer(String msg) {
                GlobalFunctions.hideProgress();
                Toast.makeText(context,"Can't update.....",Toast.LENGTH_LONG).show();
            }

            @Override
            public void OnError(String msg) {
                GlobalFunctions.hideProgress();
                Toast.makeText(context,"Some thing went wrong...",Toast.LENGTH_LONG).show();
            }
        },"Profile update");

    }
    private static  void logoutAndMove(Context context){
        GlobalFunctions.removeAllSharedPreferences(context);
        activity.finish();
        Intent j = new Intent(activity,LoginActivity.class);
        context.startActivity(j);
    }
    public void checkCameraPermission() {
        Log.d(TAG,"checkCameraPermission######################");
        int permissionCheck_location_coarse = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if(permissionCheck_location_coarse != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, REQUEST_CHECK_CAMERA);
        }

    }
    private boolean checkIfAlreadyhavePermission() {
        int coarse_location = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (coarse_location == PackageManager.PERMISSION_GRANTED && result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(TAG,"onRequestPermissionsResult##############");
        switch (requestCode) {
            case REQUEST_CHECK_CAMERA:{
                Log.d(TAG,"onRequestPermissionsResult##############REQUEST_CAMERA");
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent();
                } else {
                    checkCameraPermission();
                }
                return;
            }

            case REQUEST_CHECK_GALLARY:{
                Log.d(TAG,"onRequestPermissionsResult##############REQUEST_CHECK_GALLARY");
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImage();
                } else {
                    checkReadExternalStoragePermission();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    public void checkReadExternalStoragePermission() {
        Log.d(TAG,"checkReadExternalStoragePermission######################");
        int permissionCheck_location_coarse = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionCheck_write_coarse = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permissionCheck_location_coarse != PackageManager.PERMISSION_GRANTED &&
                permissionCheck_write_coarse != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CHECK_GALLARY);
        }

    }
    private boolean checkIfReadExternalStorageAlreadyhavePermission() {
        Log.d(TAG,"checkIfReadExternalStorageAlreadyhavePermission######################");
        int coarse_location = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (coarse_location == PackageManager.PERMISSION_GRANTED && result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

}
