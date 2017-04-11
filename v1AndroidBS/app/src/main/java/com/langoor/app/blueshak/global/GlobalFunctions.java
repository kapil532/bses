package com.langoor.app.blueshak.global;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.divrt.co.R;
import com.google.gson.Gson;
import com.langoor.app.blueshak.AppController;
import com.langoor.app.blueshak.Messaging.data.User;
import com.langoor.app.blueshak.services.ServerResponseInterface;
import com.langoor.app.blueshak.services.ServicesMethodsManager;
import com.langoor.app.blueshak.services.model.CategoryListModel;
import com.langoor.app.blueshak.services.model.PostcodeListModel;
import com.langoor.app.blueshak.services.model.ProductModel;
import com.langoor.app.blueshak.services.model.UserModel;
import com.langoor.app.blueshak.util.LocationService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GlobalFunctions {

    private static final String TAG = "GlobalFunctions";
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static ProgressDialog progressDialog = null;
    private static int navigationSelectedPosition = -1;
    static GlobalVariables globalVariables = new GlobalVariables();
    /*-----------------------(START) Shared Preference ----------------------*/
    public static void setSharedPreferenceString(Context context, String key, String value){
        if(sharedPreferences==null){sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);}
        sharedPreferences=context.getSharedPreferences(GlobalVariables.SHARED_PREFERENCE_KEY, Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void removeSharedPreferenceKey(Context context, String key){
        if(sharedPreferences==null){sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);}
        sharedPreferences=context.getSharedPreferences(GlobalVariables.SHARED_PREFERENCE_KEY, Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }

    public  static String getSharedPreferenceString(Context context, String key){
        if(sharedPreferences==null){sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);}
        String returnString = null;
        sharedPreferences=context.getSharedPreferences(GlobalVariables.SHARED_PREFERENCE_KEY, Activity.MODE_PRIVATE);
        if(sharedPreferences.contains(key)){
            returnString = sharedPreferences.getString(key,"");
        }
        return returnString;
    }
    public static void setSharedPreferenceInt(Context context, String key, int value){
        if(sharedPreferences==null){sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);}
        sharedPreferences=context.getSharedPreferences(GlobalVariables.SHARED_PREFERENCE_KEY, Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }
    public static  int  getSharedPreferenceInt(Context context, String key){
        if(sharedPreferences==null){sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);}
        int returnString=0;
        sharedPreferences=context.getSharedPreferences(GlobalVariables.SHARED_PREFERENCE_KEY, Activity.MODE_PRIVATE);
        if(sharedPreferences.contains(key)){
            returnString = sharedPreferences.getInt(key,0);
        }
        return returnString;
    }
    public static void removeAllSharedPreferences(Context context){
        if(sharedPreferences==null){sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);}
        sharedPreferences=context.getSharedPreferences(GlobalVariables.SHARED_PREFERENCE_KEY, Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    /*-----------------------Shared Preference (END)----------------------*/

    /*-----------------------(START) Progress Dialog ----------------------*/

    public static void showProgress(Context context,@Nullable String message){
        if(context!=null){
            progressDialog = new ProgressDialog(context);
            if(message==null){message = context.getString(R.string.loading);}
            progressDialog.setMessage(message);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    public static boolean isShowingProgress(){
        boolean showProgress = false;
        if(progressDialog!=null){
            if(progressDialog.isShowing()){
                showProgress = true;
            }
        }
        return showProgress;
    }

    public static void hideProgress(){
        if(progressDialog!=null) {
            if (progressDialog.isShowing()){progressDialog.hide();}
        }
    }

    /*----------------------- Progress Dialog (END)----------------------*/
    /*-----------------------(START) Program Fetcher ----------------------*/

    public static String getPhoneNo(Context context){
        String mPhoneNumber = null;
        try {
            TelephonyManager tMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            mPhoneNumber = tMgr.getLine1Number();
        }catch (Exception Ex){
            AppController.getInstance().trackException(Ex);
            Log.d(TAG, "Phone no Exception : "+Ex);
        }
        return mPhoneNumber;
    }

    /*----------------------- Program Fetcher (END)----------------------*/

    /*-----------------------(START) Profile Picture----------------------*/

    public static void saveImageWithFixedPixels(String path) {
        File image = new File(path);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inInputShareable = true;
        options.inPurgeable = true;

        Bitmap photo = BitmapFactory.decodeFile(image.getPath(), options);
        Log.d(TAG,"Bitmap SIZE : Width = "+options.outWidth+" Height = "+options.outHeight);
        if(options.outWidth>=GlobalVariables.IMAGE_WIDTH || options.outHeight>=GlobalVariables.IMAGE_HEIGHT){
            photo = lessResolution(path,GlobalVariables.IMAGE_WIDTH , GlobalVariables.IMAGE_HEIGHT);//Bitmap.createScaledBitmap(photo, globalVariables.IMAGE_WIDTH , globalVariables.IMAGE_HEIGHT, false);
            Log.d(TAG,"Resized Bitmap SIZE : Width = "+options.outWidth+" Height = "+options.outHeight);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            if(image.exists()){image.delete();}
            try {
                image.createNewFile();
                FileOutputStream fo = new FileOutputStream(image);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (Exception e) {
                AppController.getInstance().trackException(e);
                e.printStackTrace();
            }


        }

    }

    public static Bitmap lessResolution (String filePath, int width, int height) {
        int reqHeight = height;
        int reqWidth = width;
        BitmapFactory.Options options = new BitmapFactory.Options();

        // First decode with inJustDecodeBounds=true to check dimensions
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filePath);
            String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            int orientation = orientString != null ? Integer.parseInt(orientString) :  ExifInterface.ORIENTATION_NORMAL;
            Log.d(TAG,"H&W : "+orientation);
            Log.d(TAG,"Resized Bitmap SIZE : SampleSize1 = "+options.inSampleSize);
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, orientation,reqWidth, reqHeight)+1;
            Log.d(TAG,"Resized Bitmap SIZE : SampleSize2 = "+options.inSampleSize);

        } catch (IOException e) {
            e.printStackTrace();
            AppController.getInstance().trackException(e);
        }


        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int orientation, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        Log.d(TAG,"H&W : "+height+"--"+width+"--"+options.outMimeType);
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90||orientation == ExifInterface.ORIENTATION_ROTATE_270){
            int temp = reqHeight;
            reqHeight = reqWidth;
            reqWidth = temp;
        }

        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            Log.d(TAG,"Bitmap SIZE : "+inSampleSize+"--"+heightRatio+"--"+widthRatio);
        }
        return inSampleSize;
    }

    public static String getBase64fromPath(File path){
        Bitmap bm = BitmapFactory.decodeFile(path.getPath());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if(bm!=null)
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] byteArrayImage = baos.toByteArray();
        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        return encodedImage;
    }

    public static Bitmap getBitmapFromBase64(String encodedImage){
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return bitmap;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            AppController.getInstance().trackException(e);
            return null;
        }
    }

    public static void saveProfilePicture(Context context, String folder, String fileName, Bitmap bmp){
        File file = getProfilePicturePath(context, folder, fileName);
        if(file.exists()){
            file.delete();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
            AppController.getInstance().trackException(e);

        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                AppController.getInstance().trackException(e);
            }
        }
    }

    public static Bitmap getProfilePicture(Context context, String folder, String fileName){
        File file = getProfilePicturePath(context, folder, fileName);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), options);
        return bitmap;
    }

    public static void deleteProfilePicture(Context context, String folder, String fileName){
        File file = getProfilePicturePath(context, folder, fileName);
        if(file.exists()){
            file.delete();
        }
    }

    public static File getProfilePicturePath(Context context, String folder, String fileName){
        File file = new File(context.getFilesDir(), folder);
        if(!file.exists()){file.mkdir();}
        file = new File(file,fileName);
        return file;
    }

    /*----------------------- Profile Picture (END)----------------------*/
    /*-----------------------(START) Header----------------------*/

    public static Map<String, String> getHeader(Context context){
        Map<String, String> headers = new HashMap<String, String>();
        String token = getSharedPreferenceString(context, GlobalVariables.SHARED_PREFERENCE_TOKEN);
        headers.put(GlobalVariables.HEADER_CONTENT_TYPE, GlobalVariables.HEADER_CONTENT_TYPE_VALUE);
        headers.put(GlobalVariables.HEADER_TOKEN, token);
        return headers;
    }

    /*-----------------------Header (END)----------------------*/
    /*-----------------------(START) Navigation----------------------*/

    public static int getNavigationSelectedPosition(){
        return navigationSelectedPosition;
    }

  /*  public static void setNavigationSelectedPosition(int position){
        navigationSelectedPosition = position;
        NavigationDrawerFragment.refreshNavigationMainList();
    }*/

    /*-----------------------Navigation (END)----------------------*/

    /*-----------------------(START) Action Bar----------------------*/

    public int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void setTranslucentStatusFlag(Window window,boolean on) {
        if (Build.VERSION.SDK_INT >= 19) {

            WindowManager.LayoutParams winParams = window.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            if (on) {
                winParams.flags |= bits;
            } else {
                winParams.flags &= ~bits;
            }
            window.setAttributes(winParams);
        }
    }

    public int getSoftButtonsBarHeight(Window window) {
        // getRealMetrics is only available with API 17 and +
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            window.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            window.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }

    /*-----------------------Action Bar (END)----------------------*/


    /*-----------------------(START) Action Bar----------------------*/
    private static List<Address> getGeocoder(Activity act, double lat, double lng){
        Geocoder gcd = new Geocoder(act, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
            AppController.getInstance().trackException(e);
        }
        return addresses;
    }
    public Location getLocation(Activity act){
        String res  = null;
        LocationService ls = new LocationService(act);
        Location location = ls.getLocation();
        return location;
    }
    public static  String getLocationNames(double lat, double lng) {

        HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?latlng="+lat+","+lng+"&sensor=true");
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
            AppController.getInstance().trackException(e);
        } catch (IOException e) {
            AppController.getInstance().trackException(e);
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
            System.out.println("######Adredddddddd##########"+jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            AppController.getInstance().trackException(e);
        }
        return jsonObject.toString();
    }

    public  class AsyncTaskRunner extends AsyncTask<Double, String, String> {

        private String resp;

        @Override
        protected String doInBackground(Double... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                // Do your long operations here and return the result
                Double lat = params[0];
                Double lng = params[0];
                Double time = params[0];
                resp=   getLocationNames(lat, lng);

            }catch (Exception e) {
                e.printStackTrace();
                AppController.getInstance().trackException(e);
            }
            return resp;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            System.out.println("######result##########"+result);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onProgressUpdate(Progress[])
         */
        @Override
        protected void onProgressUpdate(String... text) {
            //  finalResult.setText(text[0]);
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }
    public static String getLocationNames(Activity act, double lat, double lng){

        String res = null;
        List<Address> addresses = getGeocoder(act, lat, lng);
        if(addresses!=null)
            if (addresses.size() > 0)
                res = (addresses.get(0).getLocality())+", "+(addresses.get(0).getSubLocality());

        return res;
    }

    public static void closeKeyboard(Activity activity){
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public static void showKeyboard(Activity activity){
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }
    /*-----------------------Action Bar (END)----------------------*/


    /*-----------------------(START) Category----------------------*/
    public static CategoryListModel getCategories(Context context){
        String res  = getSharedPreferenceString(context, GlobalVariables.SHARED_PREFERENCE_CATEGORIES);
      /*  Log.d(TAG, "Categories : "+res);*/
        if(res!=null){
            CategoryListModel categoryListModel = new CategoryListModel();
            categoryListModel.toObject(res);
            return categoryListModel;
        }
        return null;
    }

    public static void saveCategories(final Context context){
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.getCategoriesList(context, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                Log.d(TAG, "onSuccess Response");
                CategoryListModel categoryListModel = (CategoryListModel)arg0;
                Log.d(TAG, categoryListModel.toString());
            }

            @Override
            public void OnFailureFromServer(String msg) {
                Log.d(TAG, msg);
            }

            @Override
            public void OnError(String msg) {
                Log.d(TAG, msg);
            }
        }, "Categories");

    }
    /*-----------------------Category (END)----------------------*/

    /*-----------------------(START) PostalCode----------------------*/
    public static PostcodeListModel getPostalCodes(Context context){
        String res  = getSharedPreferenceString(context, GlobalVariables.SHARED_PREFERENCE_POSTALCODES);
        if(res!=null){
            PostcodeListModel postcodeListModel = new PostcodeListModel();
            postcodeListModel.toObject(res);
            return postcodeListModel;
        }
        return null;
    }

    public static void savePostalCodes(final Context context){
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.getPostCodesList(context, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                Log.d(TAG, "onSuccess Response");
                PostcodeListModel postcodeListModel = (PostcodeListModel)arg0;
            }

            @Override
            public void OnFailureFromServer(String msg) {
                Log.d(TAG, msg);
            }

            @Override
            public void OnError(String msg) {
                Log.d(TAG, msg);
            }
        }, "Categories");

    }
    /*-----------------------PostalCode (END)----------------------*/

    /*-----------------------(START) SaveObjects----------------------*/
    public static void saveObjects(final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                savePostalCodes(context);
                saveCategories(context);
            }
        }).start();


    }

    /*-----------------------SaveObjects (END)----------------------*/

    public static String getDevice(){
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        int version = Build.VERSION.SDK_INT;
        String versionRelease = Build.VERSION.RELEASE;

        return versionRelease+"("+version+"),"+manufacturer+"("+model+")";
    }

    public static int[] convertTexttoDate(String dateString){
        int[] dateArray= {0,0,0};
        if(!dateString.isEmpty()&&dateString!=null){
            String[] splitString = dateString.split("-");
            if(splitString.length==3){
                dateArray[2] = Integer.parseInt(splitString[0]);
                dateArray[1] = Integer.parseInt(splitString[1]);
                dateArray[0] = Integer.parseInt(splitString[2]);
            }
        }
        return dateArray;
    }
    public static User getSingedUser(Context context) {
        if(sharedPreferences==null){sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);}
        String returnString = null;
        sharedPreferences=context.getSharedPreferences(GlobalVariables.SHARED_PREFERENCE_KEY, Activity.MODE_PRIVATE);
        if (sharedPreferences.getString(GlobalVariables.SHARED_PREFERENCE_EMAIL, null) != null) {
            String email,firstName,lastName,profileImageUrl,avatar,number,token,profile_updated_at,profile_created_at,last_login_at,timezone;
            String cc_id;
            firstName = sharedPreferences.getString(GlobalVariables.SHARED_PREFERENCE_FULLNAME, null);
            email = sharedPreferences.getString( GlobalVariables.SHARED_PREFERENCE_EMAIL, null);
            number=sharedPreferences.getString(GlobalVariables.SHARED_PREFERENCE_PHONE, null);
            token=sharedPreferences.getString( GlobalVariables.SHARED_PREFERENCE_TOKEN, null);
            cc_id=sharedPreferences.getString(GlobalVariables.SHARED_PREFERENCE_BS_ID,null);
            avatar=sharedPreferences.getString(GlobalVariables.SHARED_PREFERENCE_AVATAR,null);
            User user=new User(firstName,email,number,cc_id,avatar);
            return user;
        }
        return null;
    }



    public static UserModel getProfile(Context context){
        String res  = getSharedPreferenceString(context, GlobalVariables.SHARED_PREFERENCE_PROFILE);
        if(res!=null){
            UserModel model = new UserModel();
            model.toObject(res);
            return model;
        }
        return null;
    }
    public static File getImageFilePath(Context context, String fileName){
        File imageDirectory;

        String fileDirectory = null;
        imageDirectory = getExternalStorageDirectory(context);
        if(imageDirectory==null){
            fileDirectory = getInternalStorageDirectory().toString();
        }else{
            fileDirectory = getExternalStorageDirectory(context).toString();
        }
        fileDirectory += "/" + context.getResources().getString(R.string.app_name) + "/Images";

        imageDirectory = new File(fileDirectory);

        if(!imageDirectory.exists()){
            imageDirectory.mkdirs();
        }
        File outputFile = new File(imageDirectory, fileName);
        Log.d(TAG,"SDCARD : "+outputFile.toString());

        return outputFile;
    }
    public static void setProfile(Context context, UserModel profileDetails){
        if(profileDetails!=null){
            setSharedPreferenceString(context, GlobalVariables.SHARED_PREFERENCE_PROFILE, profileDetails.toString());
        }
    }
    public static File getInternalStorageDirectory(){
        File returnFile = null;
        if(!Environment.isExternalStorageEmulated()){
            File internalFolder = new File(Environment.getDataDirectory().toString());
            returnFile = internalFolder;
        }else{returnFile = new File(Environment.getExternalStorageDirectory().toString());}
        return returnFile;
    }
    public static File getExternalStorageDirectory(Context context){
        File returnFile = null;
        if(Environment.isExternalStorageEmulated()){
            File CardPathFolder = new File(Environment.getExternalStorageDirectory().toString());//(System.getenv("SECONDARY_STORAGE"));
            if(CardPathFolder.getFreeSpace()>0) {returnFile = CardPathFolder;}
        }else{returnFile = new File(Environment.getExternalStorageDirectory().toString());}
        return returnFile;
    }
    public String getRealPathFromURI(Context context, Uri contentUri)
    {
        try{
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        catch (Exception e){
            AppController.getInstance().trackException(e);
            return contentUri.getPath();
        }
    }
    public void launchUrl(Context context, String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }
    public static boolean isNetworkAvailable(Context inContext)
    {
        ConnectivityManager ConnectMgr = (ConnectivityManager) inContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(ConnectMgr == null) return false;
        NetworkInfo NetInfo = ConnectMgr.getActiveNetworkInfo();
        if(NetInfo == null) return false;
        return NetInfo.isConnected();
    }
    public static String getDeviceUUID(Context inContext){
        String android_id = Settings.Secure.getString(inContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }

    public static String getSentenceFormat(String text){
        String capatalize_text = text.substring(0, 1).toUpperCase() + text.substring(1);
        return capatalize_text;
    }
    public static boolean debuggable(Context context){
        boolean isDebugMode = false;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
            int flags = packageInfo.applicationInfo.flags;
            isDebugMode = (flags & ApplicationInfo.FLAG_DEBUGGABLE)!=0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return isDebugMode;
    }
    public static boolean is_loggedIn(Context context){
        boolean is_logged_in=false;
        String token = GlobalFunctions.getSharedPreferenceString(context, GlobalVariables.SHARED_PREFERENCE_TOKEN);
        if(token!=null){
            return true;
        }else{
            return false;
        }

    }
    public static boolean isCameraPermissionAccepted(Activity activity) {
        int permissionCheck_cam = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
            if (permissionCheck_cam != PackageManager.PERMISSION_GRANTED) {
                return false;
            }else{
                return true;
            }
    }
    public static boolean isInternalPermissionAccepted(Activity activity) {
        int permissionCheck_External_storage = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck_External_storage != PackageManager.PERMISSION_GRANTED) {
            return false;
        }else{
            return true;
        }
    }
    public static void showCameraCheckSelfPermission(Activity activity) {
        int permissionCheck_cam = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (permissionCheck_cam != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, globalVariables.PERMISSIONS_REQUEST_CAMERA);
                }
        }
    }
    public static void accessPermissions(Activity activity) {
        int permissionCheck_getAccounts = ContextCompat.checkSelfPermission(activity, Manifest.permission.GET_ACCOUNTS);
        int permissionCheck_vibrate = ContextCompat.checkSelfPermission(activity, Manifest.permission.VIBRATE);
        int permissionCheck_lockwake = ContextCompat.checkSelfPermission(activity, Manifest.permission.WAKE_LOCK);
        int permissionCheck_internet = ContextCompat.checkSelfPermission(activity, Manifest.permission.INTERNET);
        int permissionCheck_Access_internet = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_NETWORK_STATE);
        int permissionCheck_Access_wifi = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_WIFI_STATE);
        int permissionCheck_External_storage = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck_cam = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        if (permissionCheck_lockwake != PackageManager.PERMISSION_GRANTED || permissionCheck_Access_wifi != PackageManager.PERMISSION_GRANTED || permissionCheck_getAccounts != PackageManager.PERMISSION_GRANTED || permissionCheck_vibrate != PackageManager.PERMISSION_GRANTED || permissionCheck_internet != PackageManager.PERMISSION_GRANTED || permissionCheck_Access_internet != PackageManager.PERMISSION_GRANTED || permissionCheck_External_storage != PackageManager.PERMISSION_GRANTED || permissionCheck_cam != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WAKE_LOCK) && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_WIFI_STATE) && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.GET_ACCOUNTS) && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.VIBRATE) && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.INTERNET) && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_NETWORK_STATE) && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WAKE_LOCK, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.VIBRATE, Manifest.permission.GET_ACCOUNTS, Manifest.permission.CAMERA}, globalVariables.PERMISSIONS_REQUEST_CALENDER);
            }
        }

        if (permissionCheck_internet != PackageManager.PERMISSION_GRANTED || permissionCheck_Access_internet != PackageManager.PERMISSION_GRANTED || permissionCheck_External_storage != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.INTERNET) && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_NETWORK_STATE) && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, globalVariables.PERMISSIONS_REQUEST_PRIMARY);
            }
        }


        if (permissionCheck_cam != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, globalVariables.PERMISSIONS_REQUEST_CAMERA);
            }
        }

    }
    public static ArrayList<ProductModel> getItemsList(Context context,String key) {
        if(sharedPreferences==null){sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);}
        sharedPreferences=context.getSharedPreferences(GlobalVariables.SHARED_PREFERENCE_KEY, Activity.MODE_PRIVATE);
        // used for retrieving arraylist from json formatted string
        // List<User> favorites;
        List<ProductModel> favorites;
        if (sharedPreferences.contains(key)) {
            String jsonFavorites = sharedPreferences.getString(key, null);
            Gson gson = new Gson();
            //	BeanSampleList[] favoriteItems = gson.fromJson(jsonFavorites,BeanSampleList[].class);
            // User[] users=gson.fromJson(jsonFavorites,User[].class);
            ProductModel[] users=gson.fromJson(jsonFavorites,ProductModel[].class);
            favorites = Arrays.asList(users);
            favorites = new ArrayList(favorites);
        }else
            return null;

        return (ArrayList) favorites;
    }
    public static void saveItemsList(Context context,String key,List<ProductModel> data) {
        if(sharedPreferences==null){sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);}
        sharedPreferences=context.getSharedPreferences(GlobalVariables.SHARED_PREFERENCE_KEY, Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(data);
        editor.putString(key,jsonFavorites);
        editor.commit();
    }


}
