package com.langoor.app.blueshak;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.langoor.app.blueshak.Messaging.activity.ChatActivity;
import com.langoor.app.blueshak.Messaging.activity.MessageActivity;
import com.langoor.app.blueshak.Messaging.manager.MyPreferenceManager;
import com.langoor.app.blueshak.ImageCashing.LruBitmapCache;
import com.google.android.gms.analytics.Tracker;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by Spurthi
 * Version 2.0 ,Brighter India Foundation , Bangalore
 * on 1/12/2016.
 */
public class AppController extends MultiDexApplication {

    public static final String TAG = AppController.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    public boolean mUserFirstTimeLoggedIn = false;
    private static String acname;
    private static String mobile_no;
    private static String email;
    private static AppController mInstance;
    private AppController instance;
    private PowerManager.WakeLock wakeLock;
    MyPreferenceManager pref;
    /**
     * The Analytics singleton. The field is set in onCreate method override when the application
     * class is initially created.
     */
    private static GoogleAnalytics analytics;

    /**
     * The default app tracker. The field is from onCreate callback when the application is
     * initially created.
     */
    private static Tracker tracker;

    /**
     * Access to the global Analytics singleton. If this method returns null you forgot to either
     * set android:name="&lt;this.class.name&gt;" attribute on your application element in
     * AndroidManifest.xml or you are not setting this.analytics field in onCreate method override.
     */
    public static GoogleAnalytics analytics() {
        return analytics;
    }

    /**
     * The default app tracker. If this method returns null you forgot to either set
     * android:name="&lt;this.class.name&gt;" attribute on your application element in
     * AndroidManifest.xml or you are not setting this.tracker field in onCreate method override.
     */
    public static Tracker tracker() {
        return tracker;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
        registerActivityLifecycleCallbacks(new BaseActivityLifeCycleListener());
        analytics = GoogleAnalytics.getInstance(this);

        // TODO: Replace the tracker-id with your app one from https://www.google.com/analytics/web/
        tracker = analytics.newTracker("UA-82158918-1");

        // Provide unhandled exceptions reports. Do that first after creating the tracker
        tracker.enableExceptionReporting(true);

        // Enable Remarketing, Demographics & Interests reports
        // https://developers.google.com/analytics/devguides/collection/android/display-features
        tracker.enableAdvertisingIdCollection(true);

        // Enable automatic activity tracking for your app
        tracker.enableAutoActivityTracking(true);

        FacebookSdk.sdkInitialize(getApplicationContext());
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),  // replace with your unique package name
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                System.out.println("###KeyHash###"+Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }

    public static String getEmail(Context ctx)
    {


        String emai="";
        AccountManager am = AccountManager.get(ctx);
        Account[] accounts = am.getAccounts();
        for (Account ac : accounts)
        {
            acname = ac.name;

            if (acname.startsWith("91")) {
                mobile_no = acname;
            }else if(acname.endsWith("@gmail.com")||acname.endsWith("@yahoo.com")||acname.endsWith("@hotmail.com")){
                email = acname;
                return email;
            }

            // Take your time to look at all available accounts
            //Log.i("Accounts : ", "Accounts : " + email + mobile_no);
        }
        return emai;
    }


    public static String getMobileNumber(Context ctx)
    {

        String s1  ="";
        String main_data[] = {"data1", "is_primary", "data3", "data2", "data1", "is_primary", "photo_uri", "mimetype"};
        Object object = ctx.getContentResolver().query(Uri.withAppendedPath(android.provider.ContactsContract.Profile.CONTENT_URI, "data"),
                main_data, "mimetype=?",
                new String[]{"vnd.android.cursor.item/phone_v2"},
                "is_primary DESC");
        if (object != null)
        {
            do {
                if (!((Cursor) (object)).moveToNext())
                    break;
                s1 = ((Cursor) (object)).getString(4);
                if(s1.length() >2)
                {
                    return s1;
                }

                //Log.d("DATAT ","DATA"+s1);


            }
            while (true);
            ((Cursor) (object)).close();
        }

        return s1;
    }



    public static synchronized AppController getInstance() {
        return mInstance;
    }
    public MyPreferenceManager getPrefManager() {
        if (pref == null) {
            pref = new MyPreferenceManager(this);
        }

        return pref;
    }
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        req.setShouldCache(false);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        req.setShouldCache(false);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    /**
     * Dummy implementation of {@link android.app.Application.ActivityLifecycleCallbacks} such
     * that subclasses don't need override each and every methods in the interface
     */
    public static class BaseActivityLifeCycleListener implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            if (activity instanceof MessageActivity) {
                isRecentChatsActivityActivityVisible = true;
            }else if(activity instanceof ChatActivity) {
                isChatActivityVisible = true;
            }

        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (activity instanceof MessageActivity) {
                isRecentChatsActivityActivityVisible = true;
            }else if(activity instanceof ChatActivity) {
                isChatActivityVisible = true;
            }

        }
        private static boolean isRecentChatsActivityActivityVisible=false;
        private static boolean isChatActivityVisible=false;


        public boolean isRecentChatsActivityActivityVisible() {
            return isRecentChatsActivityActivityVisible;
        }
        public boolean isChatActivityVisible() {
            return isChatActivityVisible;
        }

        @Override
        public void onActivityResumed(Activity activity) {
            if (activity instanceof MessageActivity) {
                Log.d(TAG,"MessageActivity################ onActivityResumed");
                isRecentChatsActivityActivityVisible = true;
            }else if(activity instanceof ChatActivity) {
                Log.d(TAG,"ChatActivity################ onActivityResumed");
                isChatActivityVisible = true;
            }


        }
        @Override
        public void onActivityPaused(Activity activity) {
            if (activity instanceof MessageActivity) {
                isRecentChatsActivityActivityVisible = false;
                Log.d(TAG,"MessageActivity################ onActivityPaused");
            }else if(activity instanceof ChatActivity) {
                Log.d(TAG,"ChatActivity################ onActivityPaused");
                isChatActivityVisible = false;
            }

        }

        @Override
        public void onActivityStopped(Activity activity) {
            if (activity instanceof MessageActivity) {
                isRecentChatsActivityActivityVisible = false;
            }else if(activity instanceof ChatActivity) {
                isChatActivityVisible = false;
            }

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            if (activity instanceof MessageActivity) {
                isRecentChatsActivityActivityVisible = false;
            }else if(activity instanceof ChatActivity) {
                isChatActivityVisible = false;
            }

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if (activity instanceof MessageActivity) {
                isRecentChatsActivityActivityVisible = false;
            }else if(activity instanceof ChatActivity) {
                isChatActivityVisible = false;
            }

        }
    }

    public boolean isNetworkAvailable(Context inContext)
    {
        ConnectivityManager ConnectMgr = (ConnectivityManager) inContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(ConnectMgr == null) return false;
        NetworkInfo NetInfo = ConnectMgr.getActiveNetworkInfo();
        if(NetInfo == null) return false;
        return NetInfo.isConnected();
    }
    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }

    /***
     * Tracking screen view
     *
     * @param screenName screen name to be displayed on GA dashboard
     */
    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    /***
     * Tracking exception
     *
     * @param e exception to be tracked
     */
    public void trackException(Exception e) {
        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();

            t.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(
                            new StandardExceptionParser(this, null)
                                    .getDescription(Thread.currentThread().getName(), e))
                    .setFatal(false)
                    .build()
            );
        }
    }

    /***
     * Tracking event
     *
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */
    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }
}
