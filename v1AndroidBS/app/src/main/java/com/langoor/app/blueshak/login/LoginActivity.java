package com.langoor.app.blueshak.login;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.divrt.co.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.langoor.app.blueshak.AppController;
import com.langoor.app.blueshak.MainActivity;
import com.langoor.app.blueshak.Messaging.data.PostData;
import com.langoor.app.blueshak.forgot_password.ForgotPasswordActivity;
import com.langoor.app.blueshak.garage.CreateGarageSaleFragment;
import com.langoor.app.blueshak.garage.CreateItemSaleFragment;
import com.langoor.app.blueshak.garage.CreateSaleActivity;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.register.SignUpSCreenActivity;
import com.langoor.app.blueshak.services.ServerConstants;
import com.langoor.app.blueshak.services.ServerResponseInterface;
import com.langoor.app.blueshak.services.ServicesMethodsManager;
import com.langoor.app.blueshak.services.model.CreateProductModel;
import com.langoor.app.blueshak.services.model.CreateSalesModel;
import com.langoor.app.blueshak.services.model.FacebookRegisterModel;
import com.langoor.app.blueshak.services.model.LoginModel;
import com.langoor.app.blueshak.services.model.UserModel;
import com.langoor.app.blueshak.splash.NextSplashScreen;
import com.nostra13.universalimageloader.utils.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
	private EditText mUsername;
	private Button mLoginBtn;
	private ImageView facebookLoginBtn;
	private EditText etCOnfPass;
	private ProgressDialog mProgressDialog;
	private TextView mForgotpass;
	private TextView mSignup;
	private TextView btnLogin;
	private ProgressDialog progressDialog;
	static Activity activity;
	private CallbackManager callbackManager;
	private LoginButton loginButton;
	Boolean debug=false;
	public static final String FROM_KEY= "from_key";
	public static final String CREATE_GARRAGE_ACTIVITY_GARAGE_BUNDLE_KEY = "CreateGarrageActivityBundleKey";
	public static final String CREATE_GARRAGE_ACTIVITY_ITME_BUNDLE_KEY = "CreateItemActivityTypeBundleKey";
	private static final int REQUEST_READ_CONTACTS = 88;
	private static final String TAG = "SigninActivity";
	FacebookRegisterModel facebookregisterModel = new FacebookRegisterModel();
	CreateSalesModel createSalesModel=null;
	CreateProductModel createProductModel=null;
	static  Context context;
	int tag;
	ImageView close_button;

	public static Intent newInstance(Context context,CreateSalesModel createSalesModel,CreateProductModel createProductModel){
		Intent mIntent = new Intent(context, LoginActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(CREATE_GARRAGE_ACTIVITY_GARAGE_BUNDLE_KEY,createSalesModel);
		bundle.putSerializable(CREATE_GARRAGE_ACTIVITY_ITME_BUNDLE_KEY,createProductModel);
		mIntent.putExtras(bundle);
		return mIntent;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		int googlePlayServiceStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if(googlePlayServiceStatus != ConnectionResult.SUCCESS)
		{
			if(googlePlayServiceStatus == ConnectionResult.SERVICE_MISSING ||
					googlePlayServiceStatus == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED ||
					googlePlayServiceStatus == ConnectionResult.SERVICE_DISABLED )
				GooglePlayServicesUtil.getErrorDialog(googlePlayServiceStatus, this, 0).show();

			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_login_screen);
		activity=this;
		context=this;
		disconnectFromFacebook();
		if(getIntent().hasExtra(FROM_KEY))
			tag=getIntent().getIntExtra(FROM_KEY,0);

		if(getIntent().hasExtra(CREATE_GARRAGE_ACTIVITY_GARAGE_BUNDLE_KEY)){
			createSalesModel = (CreateSalesModel) getIntent().getExtras().getSerializable(CREATE_GARRAGE_ACTIVITY_GARAGE_BUNDLE_KEY);
		}
		if(getIntent().hasExtra(CREATE_GARRAGE_ACTIVITY_GARAGE_BUNDLE_KEY)){
			createProductModel = (CreateProductModel) getIntent().getExtras().getSerializable(CREATE_GARRAGE_ACTIVITY_ITME_BUNDLE_KEY);

		}
		mUsername = (EditText)findViewById(R.id.username);
		mLoginBtn = (Button)findViewById(R.id.login);
		facebookLoginBtn = (ImageView)findViewById(R.id.face_book_login);
		etCOnfPass=(EditText)findViewById(R.id.password);
		mForgotpass=(TextView)findViewById(R.id.forgot_password);
		context=this;
		if(!checkIfContactsAccountsPermission())
			checkContactsAccountsPermission();
		mForgotpass.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
				startActivity(i);

			}
		});

		mSignup=(TextView)findViewById(R.id.SignUp);
		mSignup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=SignUpSCreenActivity.newInstance(activity,facebookregisterModel, GlobalVariables.TYPE_AC_SIGN_UP,
						createSalesModel,createProductModel);
				startActivity(intent);


			}
		});

		close_button=(ImageView)findViewById(R.id.close_button);
		close_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*Intent edit = CreateSaleActivity.newInstance(activity,createSalesModel,createProductModel,null,GlobalVariables.TYPE_MY_GARAGE_SALE,GlobalVariables.TYPE_GARAGE);
				startActivity(edit);*/
				closeThisActivity();
			}
		});
		activity = this;
		mLoginBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View viewClicked)
	{
		switch (viewClicked.getId())
		{
			case R.id.login:
				sendRequestToServer();
				break;
		}
	}

	private void sendRequestToServer()
	{

		if(mUsername.getText().length() == 0)
		{
			mUsername.setError("Please fill the email");
			return;
		}else  if(!isValidEmail(mUsername.getText().toString())){

			mUsername.setError("Please Check your Email id!!");
			return;
		}else if(etCOnfPass.getText().length() ==0){
			etCOnfPass.setError("Please fill the password");
			return;
		}else {
			if(GlobalFunctions.isNetworkAvailable(this)){
				loginUser(this, mUsername.getText().toString(), etCOnfPass.getText().toString());
			}else{
				Snackbar.make(mUsername, "Please check your internet connection", Snackbar.LENGTH_LONG)
						.setAction("Retry", new View.OnClickListener() {
							@Override
							@TargetApi(Build.VERSION_CODES.M)
							public void onClick(View v) {
								sendRequestToServer();
							}
						}).show();
			}
		}
	}
	private void nextScreen()
	{
		String token = GlobalFunctions.getSharedPreferenceString(context, GlobalVariables.SHARED_PREFERENCE_TOKEN);
		if(token!=null)
		{
			Intent i = new Intent(LoginActivity.this,MainActivity.class);
			startActivity(i);
			closeThisActivity();
		}else {
			Intent j = new Intent(LoginActivity.this,LoginActivity.class);
			startActivity(j);
			closeThisActivity();
		}

	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		closeThisActivity();
	}
	private void loginUser(Context context, String email, String password){
		GlobalFunctions.showProgress(context, "Logging in...");
		LoginModel loginModel = new LoginModel();
		loginModel.setEmail(email);
		loginModel.setPassword(password);
		ServicesMethodsManager serverManager = new ServicesMethodsManager();
		serverManager.login(activity, loginModel, new ServerResponseInterface() {
			@Override
			public void OnSuccessFromServer(Object arg0) {
				GlobalFunctions.hideProgress();
				UserModel user = (UserModel) arg0;
				validateLogin(user);
			}

			@Override
			public void OnFailureFromServer(String msg) {
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
				GlobalFunctions.hideProgress();
			}

			@Override
			public void OnError(String msg) {
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
				GlobalFunctions.hideProgress();
			}
		}, "login Data");
	}

	private void ask_phone_number(Context context,String email){
		GlobalFunctions.showProgress(context, "Please wait..");
		ServicesMethodsManager serverManager = new ServicesMethodsManager();
		serverManager.ask_for_phone_number(this, email, new ServerResponseInterface() {
			@Override
			public void OnSuccessFromServer(Object arg0) {
				Log.d(TAG, "Object String:"+arg0.toString());
				UserModel user = (UserModel) arg0;
				validateFacebookLogin(user);
				GlobalFunctions.hideProgress();
			}

			@Override
			public void OnFailureFromServer(String msg) {
				GlobalFunctions.hideProgress();
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void OnError(String msg) {
				GlobalFunctions.hideProgress();
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			}
		}, "Register User");
	}
	public void validateFacebookLogin(UserModel userModel){

		if(userModel.getAsk_phone()==1){
			System.out.println("##getAsk_phone###facebookregisterModel######"+facebookregisterModel.toString());
			FacebookRegisterModel facebookRegisterModel11=new FacebookRegisterModel();
			facebookRegisterModel11.setEmail(getEmail());
			facebookRegisterModel11.setName(getName());
			facebookRegisterModel11.setFb_id(getId());
			System.out.println("##getAsk_phone###facebookRegisterModel11######"+facebookRegisterModel11.toString());
			Intent intent=SignUpSCreenActivity.newInstance(activity,facebookRegisterModel11, GlobalVariables.TYPE_SIGN_UP,
					createSalesModel,createProductModel);
			startActivity(intent);
			closeThisActivity();
		}else{
			Log.d(TAG,"#####userModel Token#####"+userModel.getToken());
			if(!TextUtils.isEmpty(userModel.getToken())&&userModel.getToken()!=null){
				Log.d(TAG,"#####userModel Token#####"+userModel.getToken());
				validateLogin(userModel);
			}

		}

	}
	private void registerFacebookUser(Context context,FacebookRegisterModel facebookRegisterModel){
		GlobalFunctions.showProgress(context, "registerFacebookUser");
		ServicesMethodsManager serverManager = new ServicesMethodsManager();
		serverManager.registerFacebookUser(this, facebookRegisterModel, new ServerResponseInterface() {
			@Override
			public void OnSuccessFromServer(Object arg0) {
				Log.d(TAG, "Object String:"+arg0.toString());
				FacebookRegisterModel user = (FacebookRegisterModel) arg0;
				validatefbLogin(user);
				GlobalFunctions.hideProgress();
			}

			@Override
			public void OnFailureFromServer(String msg) {
				GlobalFunctions.hideProgress();
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void OnError(String msg) {
				GlobalFunctions.hideProgress();
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			}
		}, "Register User");
	}
	public final static boolean isValidEmail(CharSequence target) {
		if (target == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
		}
	}

	private void validatefbLogin(FacebookRegisterModel user){
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_EMAIL, user.getEmail());
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_FULLNAME, user.getName());
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_PHONE, user.getPhone());
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_ADDRESS, user.getAddress());
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_TOKEN, user.getToken());
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_BS_ID, user.getId());
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_USERID, user.getId());
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_AVATAR, user.getToken());
		//GlobalFunctions.setProfile(this, user);
		Intent edit = CreateSaleActivity.newInstance(activity,createSalesModel,createProductModel,null,GlobalVariables.TYPE_AC_SIGN_UP,GlobalVariables.TYPE_GARAGE);
		startActivity(edit);
		closeThisActivity();
	}

	public void closeThisActivity(){
		if(activity!=null){activity.finish();}
	}



	@Override
	protected void onResume() {
		super.onResume();
		AppController.getInstance().trackScreenView(LoginActivity.TAG);

		callbackManager= CallbackManager.Factory.create();

		loginButton= (LoginButton)findViewById(R.id.login_button);

		loginButton.setReadPermissions("public_profile", "email","user_friends");

		facebookLoginBtn= (ImageView) findViewById(R.id.face_book_login);
		facebookLoginBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				progressDialog = new ProgressDialog(LoginActivity.this);
				progressDialog.setMessage("Loading...");
				/*progressDialog.show();*/

				loginButton.performClick();

				loginButton.setPressed(true);

				loginButton.invalidate();

				loginButton.registerCallback(callbackManager, mCallBack);

				loginButton.setPressed(false);

				loginButton.invalidate();

				/*boolean sessionExpired;
				if(AccessToken.getCurrentAccessToken()!=null){
					Log.d(TAG,"getCurrentAccessTokenis not null");
					sessionExpired = AccessToken.getCurrentAccessToken().isExpired();
				}else{
					sessionExpired = true;
				}
				if(sessionExpired){
					Log.d(TAG,"getCurrentAccessTokenis sessionExpired");
					LoginManager.getInstance().logOut();
				}*/




			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}

	private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
		@Override
		public void onSuccess(LoginResult loginResult) {
			progressDialog.dismiss();
			final String email=null;
			Log.e("loginResult##########: ", loginResult + "");
			System.out.println("#######loginResult#########"+loginResult + "");

			GraphRequest request = GraphRequest.newMeRequest(
					loginResult.getAccessToken(),
					new GraphRequest.GraphJSONObjectCallback() {
						@Override
						public void onCompleted(
								JSONObject object,
								GraphResponse response) {
							Log.e("response: ", response + "");
							System.out.println("#######response#########"+response + "");
							try {

								final String id= object.getString("id").toString();
								String email = object.getString("email").toString();
								final String name = object.getString("name").toString();
								final	String gender = object.getString("gender").toString();
								facebookregisterModel.setEmail(email);
								facebookregisterModel.setName(name);
								facebookregisterModel.setFb_id(id);
								setId(id);
								setEmail(email);
								setName(name);
								ask_phone_number(activity,facebookregisterModel.getEmail());
								//facebookregisterModel.setAddress(getA);
								System.out.println("#####facebookregisterModel######"+facebookregisterModel.toString());
									Log.e("Face book response: ", response + "");
								System.out.println("#######response#########"+response +"id"+id+"email"+email
										+"name"+name
										+"gender"+gender
										);


							}catch (Exception e){
								e.printStackTrace();
							}




						}

					});

			Bundle parameters = new Bundle();
			parameters.putString("fields", "id,name,email,gender, birthday");
			request.setParameters(parameters);
			request.executeAsync();


		}

		@Override
		public void onCancel() {

			System.out.println("#######onCancel#########");
			progressDialog.dismiss();
		}

		@Override
		public void onError(FacebookException e) {
			System.out.println("#######onError#########");
		/*	Log.e("######onError####: " , e.e.printStackTrace()());*/
			e.printStackTrace();
			progressDialog.dismiss();
		}
	};
	public void fetchFaceBookProfilePic(){
		new AsyncTask<Void,Void,Void>(){
			@Override
			protected Void doInBackground(Void... params) {
				URL imageURL = null;
				try {
					imageURL = new URL("https://graph.facebook.com/" + "1435767690067161" + "/picture?type=large");
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				try {
					Bitmap bitmap  = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());

				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				super.onPostExecute(aVoid);
				//  profileImage.setImageBitmap(bitmap);
			}
		}.execute();

	}
	private final String
			ID = "bs_id",
			IMAGE = "avatar",
			CITY = "city",
			POSTAL_CODE = "postal_code",
			STATE = "state",
			EMAIL = "email",
			TOKEN = "token",
			NAME = "name",
			PHONE = "phone",
			IS_ACTIVE="is_active",
			IS_BANNED="is_banned",
			ADDRESS = "address";

	private void registerfbData(Context context, String email, String name, String phone, String address,String fb_id){
		GlobalFunctions.showProgress(context, "Registering User");
		facebookregisterModel.setEmail(email);
		facebookregisterModel.setName(name);
		facebookregisterModel.setPhone(phone);
		facebookregisterModel.setAddress(address);
		facebookregisterModel.setFb_id(fb_id);
		ServicesMethodsManager serverManager = new ServicesMethodsManager();
		serverManager.registerFacebookUser(this, facebookregisterModel, new ServerResponseInterface() {
			@Override
			public void OnSuccessFromServer(Object arg0) {
				Log.d(TAG, "Object String:"+arg0.toString());
				UserModel user = (UserModel) arg0;
				validateLogin(user);
				GlobalFunctions.hideProgress();
			}

			@Override
			public void OnFailureFromServer(String msg) {
				GlobalFunctions.hideProgress();
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void OnError(String msg) {
				GlobalFunctions.hideProgress();
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			}
		}, "Register User");
	}

	private void validateLogin(UserModel user){
		Log.d(TAG,"validateLogin"+user.toString());
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_EMAIL, user.getEmail());
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_FULLNAME, user.getName());
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_PHONE, user.getPhone());
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_ADDRESS, user.getAddress());
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_TOKEN, user.getToken());
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_AVATAR, user.getImage());
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_BS_ID, user.getId());
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_USERID, user.getId());
		GlobalFunctions.setProfile(this, user);
		if(createSalesModel!=null || createProductModel!=null){
			Intent edit = CreateSaleActivity.newInstance(activity,createSalesModel,createProductModel,null,GlobalVariables.TYPE_AC_SIGN_UP,GlobalVariables.TYPE_GARAGE);
			startActivity(edit);
			closeThisActivity();
		}else {
			startActivity(new Intent(this,MainActivity.class));
			closeThisActivity();
		}


		//closeThisActivity();


		/*if(tag==GlobalVariables.TYPE_START_A_GARAGE_SALE){
			Intent i = new Intent(LoginActivity.this,CreateSaleActivity.class);
			startActivity(i);
		}else{
			Intent i = new Intent(LoginActivity.this,CreateSaleActivity.class);
			startActivity(i);
		}
*/

	}

	private void registerData()
		{
			System.out.println("######coming inside the register data########");
			final JSONObject table = new JSONObject();
			if (GlobalFunctions.isNetworkAvailable(this)) {
				try {
					table.put("email",facebookregisterModel.getEmail());
					table.put("name", facebookregisterModel.getName());
					table.put("fb_id",facebookregisterModel.getFb_id());
				} catch (JSONException e) {
				}
				PostData.callString(this, table, ServerConstants.URL_UserFb_ask_phoneRegistration, jsonPost);
			} else {
				Snackbar.make(mUsername, "Please check your internet connection", Snackbar.LENGTH_LONG)
						.setAction("Retry", new View.OnClickListener() {
							@Override
							@TargetApi(Build.VERSION_CODES.M)
							public void onClick(View v) {
								sendRequestToServer();
							}
						}).show();
			}

		}

		PostData.PostCommentJsonStringResponseListener jsonPost = new PostData.PostCommentJsonStringResponseListener() {
			@Override
			public void requestStarted() {
				// TODO Auto-generated method stub

			}
			@Override
			public void requestEndedWithError(String message) {
				System.out.println("######requestEndedWithError#######"+message.toString());


				Log.d(TAG, "Response: " + message);
				String messageError = "";
				if (message.length() > 0) {
					try {
						JSONObject json = new JSONObject(message);
						messageError = json.getString("error");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (messageError.equalsIgnoreCase("invalid_credentials")) {
					messageError = "Invalid credentials";
				} else {
					messageError = "Please Try Again Later";
				}

				Log.d(TAG, "messageError: " + messageError);
				Snackbar.make(mUsername, messageError, Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
			@Override
			public void requestCompleted(JSONObject json) {
				Log.d(TAG, "Message Response: " + json.toString());
				System.out.println("######requestCompleted#######"+json.toString());

				String email=null, token = null, name = null, address = "", phone = "", id, city, postalCode, state, image=null;
				int is_active=1,is_banned=0;

				int i=0;
				try{
					if(json.has("ask_phone")){
						i=json.getInt("ask_phone");
						if(i==1){
							Intent intent=SignUpSCreenActivity.newInstance(activity,facebookregisterModel, GlobalVariables.TYPE_SIGN_UP
							,createSalesModel,createProductModel);
							startActivity(intent);
						}else {
							try{

								id = json.getString(ID);
								city = json.getString(CITY);
								postalCode = json.getString(POSTAL_CODE);
								state = json.getString(STATE);
								if(json.has(IMAGE))image = json.getString(IMAGE);
								if(json.has(IS_ACTIVE))is_active=json.getInt(IS_ACTIVE);
								if(json.has(IS_BANNED))is_banned=json.getInt(IS_BANNED);
								token = json.getString(TOKEN);
								name = json.getString(NAME);
								email = json.getString(EMAIL);
								phone = json.getString(PHONE);
								address = json.getString(ADDRESS);

								UserModel userModel=new UserModel();
								userModel.setId(id);
								userModel.setCity(city);
								userModel.setPostalCode(postalCode);
								userModel.setState(state);
								userModel.setImage(image);
								userModel.setIs_active(is_active);
								userModel.setToken(token);
								userModel.setName(name);
								userModel.setEmail(email);
								userModel.setEmail(phone);
								userModel.setEmail(address);
								validateLogin(userModel);
							}
							catch(Exception ex){
								Log.d(TAG, "Json Exception : " + ex);}
						}
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		};
	PostData.PostCommentJsonStringResponseListener jsonPost1 = new PostData.PostCommentJsonStringResponseListener() {
		@Override
		public void requestStarted() {
			// TODO Auto-generated method stub

		}
		@Override
		public void requestEndedWithError(String message) {

			Log.d(TAG, "Response: " + message);
			String messageError = "";
			if (message.length() > 0) {
				try {
					JSONObject json = new JSONObject(message);
					messageError = json.getString("error");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (messageError.equalsIgnoreCase("invalid_credentials")) {
				messageError = "Invalid credentials";
			} else {
				messageError = "Please Try Again Later";
			}

			Log.d(TAG, "messageError: " + messageError);
			Snackbar.make(mUsername, messageError, Snackbar.LENGTH_LONG)
					.setAction("Action", null).show();
		}
		@Override
		public void requestCompleted(JSONObject message) {
			Log.d(TAG, "messageError: " + message.toString());

		}
	};
	String
			id = null,
			email=null,
			name = null;
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public void checkContactsAccountsPermission() {
		Log.d(TAG,"checkContactsAccountsPermission######################");
		int permissionCheck_contacts_coarse = ContextCompat.checkSelfPermission(activity, Manifest.permission.GET_ACCOUNTS);
		int permissionCheck_accounts_coarse = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS);

		if(permissionCheck_contacts_coarse != PackageManager.PERMISSION_GRANTED &&
				permissionCheck_accounts_coarse != PackageManager.PERMISSION_GRANTED ) {
			ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.GET_ACCOUNTS,Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS);
		}

	}
	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		Log.d(TAG,"onRequestPermissionsResult##############");
		switch (requestCode) {
			case REQUEST_READ_CONTACTS:{
				Log.d(TAG,"onRequestPermissionsResult##############REQUEST_CAMERA");
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Log.d(TAG,"checkContactsAccountsPermission######################");
					/*etEmail.setText(AppController.getEmail(this));
					etMobileNUmber.setText(AppController.getMobileNumber(this));*/
					/*showGmailAccountsAvailable();*/
				} else {
					checkContactsAccountsPermission();
				}
				return;
			}
			// other 'case' lines to check for other
			// permissions this app might request
		}
	}
	private boolean checkIfContactsAccountsPermission() {
		Log.d(TAG,"checkIfReadExternalStorageAlreadyhavePermission######################");
		int coarse_location = ContextCompat.checkSelfPermission(activity, Manifest.permission.GET_ACCOUNTS);
		int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS);
		if (coarse_location == PackageManager.PERMISSION_GRANTED && result == PackageManager.PERMISSION_GRANTED) {
			return true;
		} else {
			return false;
		}
	}
	public void disconnectFromFacebook() {
		Log.d(TAG,"########disconnectFromFacebook");
		LoginManager.getInstance().logOut();

	/*	if (AccessToken.getCurrentAccessToken() == null) {
			Log.d(TAG,"##########getCurrentAccessToken  null");
			return; // already logged out
		}

		new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
				.Callback() {
			@Override
			public void onCompleted(GraphResponse graphResponse) {
				Log.d(TAG,"###########logOut called");
				LoginManager.getInstance().logOut();

			}
		}).executeAsync();*/
	}
}
