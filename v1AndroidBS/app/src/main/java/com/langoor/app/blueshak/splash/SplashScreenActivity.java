package com.langoor.app.blueshak.splash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.divrt.co.R;
import com.langoor.app.blueshak.MainActivity;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;

/*import com.langoor.app.blueshak.login.LoginActivity;
import com.langoor.app.blueshak.util.LocationService;*/
/*
import io.fabric.sdk.android.Fabric;*/

public class SplashScreenActivity extends Activity {

	private final int SPLASH_SHOW_TIME = 1000;
	static Activity activity;
	static Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity=this;
		context=this;
		/*if(GlobalFunctions.getSharedPreferenceString(activity, GlobalVariables.SHARED_PREFERENCE_PERMISSION)!=null){
			if(GlobalFunctions.getSharedPreferenceString(activity,
					GlobalVariables.SHARED_PREFERENCE_PERMISSION).equalsIgnoreCase("true")){
				Intent i = new Intent(SplashScreenActivity.this,NextSplashScreen.class);
				startActivity(i);
				*//*finish();*//*
			}
		}*/
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

	/*	Fabric.with(this, new Crashlytics());*/
		setContentView(R.layout.activity_splash_screen);
	/*	LocationService locServices = new LocationService(this);
		if(!locServices.canGetLocation()){locServices.showSettingsAlert();}
	*/	new BackgroundSplashTask().execute();

	}

	/**
	 * Async Task: can be used to load DB, images during which the splash screen
	 * is shown to user
	 */
	private class BackgroundSplashTask extends AsyncTask<Void, Void , Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				Thread.sleep(SPLASH_SHOW_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			int is_first_time=GlobalFunctions.getSharedPreferenceInt(activity, GlobalVariables.SHARED_PREFERENCE_FIRST_TIME);
			if(is_first_time==0) {
				Intent i = new Intent(SplashScreenActivity.this,NextSplashScreen.class);
				startActivity(i);
				closeThisActivity();
			}else{
				Intent i = new Intent(SplashScreenActivity.this,MainActivity.class);
				startActivity(i);
				closeThisActivity();
			}
		}
	}

	public static void closeThisActivity(){
		if(activity!=null){activity.finish();}
	}

}