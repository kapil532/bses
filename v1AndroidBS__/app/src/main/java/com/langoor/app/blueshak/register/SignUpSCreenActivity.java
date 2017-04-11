package com.langoor.app.blueshak.register;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.langoor.app.blueshak.AppController;
import com.langoor.app.blueshak.MainActivity;
import com.langoor.app.blueshak.Messaging.util.CommonUtil;
import com.langoor.app.blueshak.garage.CreateSaleActivity;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.login.LoginActivity;
import com.langoor.app.blueshak.services.ServerResponseInterface;
import com.langoor.app.blueshak.services.ServicesMethodsManager;
import com.langoor.app.blueshak.services.model.ErrorModel;
import com.langoor.app.blueshak.services.model.LocationModel;
import com.langoor.app.blueshak.services.model.CreateProductModel;
import com.langoor.app.blueshak.services.model.CreateSalesModel;
import com.langoor.app.blueshak.services.model.FacebookRegisterModel;
import com.langoor.app.blueshak.services.model.RegisterModel;
import com.langoor.app.blueshak.services.model.UserModel;
import com.langoor.app.blueshak.util.LocationListener;
import com.langoor.app.blueshak.util.LocationService;

import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static android.Manifest.permission.READ_CONTACTS;


public class SignUpSCreenActivity extends AppCompatActivity implements LocationListener, GoogleAccountsFragment.OnNewsItemSelectedListener
{

	private ImageView facebookLoginBtn;

	private ProgressDialog progressDialog;

	private CallbackManager callbackManager;
	private LoginButton loginButton;


	static final String  FACEBOOK_NAME = "name";
	static final String  FACEBOOK_EMAIL = "number";
	static final String  FACEBOOK_FLAG = "number";
	 int  flag=0; ;

	FacebookRegisterModel facebookregisterModel = new FacebookRegisterModel();



	private static final String TAG = "SignUpSCreenActivity";
	Context context;
	public static final String CREATE_GARRAGE_ACTIVITY_GARAGE_BUNDLE_KEY = "CreateGarrageActivityBundleKey";
	public static final String CREATE_GARRAGE_ACTIVITY_ITME_BUNDLE_KEY = "CreateItemActivityTypeBundleKey";
	private EditText etUserName;
	private EditText etEmail;
	private EditText etMobileNUmber,isd_code;
	private EditText location;
	private EditText etCOnfPass;
	private TextView loginPageLink;
	private TextView signUpText;
	private Button btnSignUp;
	private static Activity activity;
	private CheckBox terms_conditions;
	private TextView termsOfAgreement;
	public static FragmentManager mainActivityFM;
	/**
	 * Id to identity READ_CONTACTS permission request.
	 */
	private static final int REQUEST_READ_CONTACTS = 99;


	private Intent intent;
	LocationService locServices;
	RegisterModel registerModel = new RegisterModel();
	private ImageView face_book_login;
	ProgressDialog ringProgressDialog ;

	String name=null,email=null;
	String formatted_address;
	ImageView close_button;
	CreateSalesModel createSalesModel=null;
	CreateProductModel createProductModel=null;

	public static Intent newInstance(Context context,FacebookRegisterModel facebookRegisterModel,int flag,
									 CreateSalesModel createSalesModel, CreateProductModel createProductModel){
		Intent mIntent = new Intent(context, SignUpSCreenActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(FACEBOOK_NAME,facebookRegisterModel);
		bundle.putInt(FACEBOOK_FLAG,flag);
		bundle.putSerializable(CREATE_GARRAGE_ACTIVITY_GARAGE_BUNDLE_KEY,createSalesModel);
		bundle.putSerializable(CREATE_GARRAGE_ACTIVITY_ITME_BUNDLE_KEY,createProductModel);
		mIntent.putExtras(bundle);
		return mIntent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
			setContentView(R.layout.signup_screen);
        activity = this;
		locServices = new LocationService(activity);
		locServices.setListener(this);
        if(!locServices.canGetLocation()){
			locServices.showSettingsAlert();
		}
		activity = this;
		context=this;


		mainActivityFM = getSupportFragmentManager();
		System.out.println("######coming inside the sign up screen#######");

		etUserName =(EditText)findViewById(R.id.etUserName);
		etEmail =(EditText)findViewById(R.id.etEmail);
		terms_conditions=(CheckBox)findViewById(R.id.checkBox);
		/*mayRequestContacts();*/
		etMobileNUmber =(EditText)findViewById(R.id.etMobileNUmber);
		location =(EditText)findViewById(R.id.location);
		etCOnfPass =(EditText)findViewById(R.id.etCOnfPass);
		btnSignUp =(Button)findViewById(R.id.btnSignUp);
		isd_code=(EditText)findViewById(R.id.isd_code);


		loginPageLink=(TextView)findViewById(R.id.login_page_link);
		termsOfAgreement=(TextView)findViewById(R.id.termsOfAgreement);
		facebookLoginBtn = (ImageView)findViewById(R.id.face_book_login);
		facebookLoginBtn.setVisibility(View.GONE);
		getAddressFromLatLng(activity);
		close_button=(ImageView)findViewById(R.id.close_button);
		close_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
					finish();
			}
		});
		btnSignUp.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if(flag== GlobalVariables.TYPE_SIGN_UP){
					if(etUserName.getText().length()==0) {
						etUserName.setError("Please fill the Name");
						return;
					}else if(TextUtils.isDigitsOnly(etUserName.getText().toString())){
						etUserName.setError("Please enter valid Name");
						return;
					}else if(etEmail.getText().length() == 0) {
						etEmail.setError("Please fill the email");
						return;
					}else  if (!isValidEmail(etEmail.getText().toString())) {
						etEmail.setError("Please Check your Email id!!");
						return;
					}else if(isd_code.getText().length() == 0) {
						etEmail.setError("Please fill the ISD code");
						return;
					}else if(!isd_code.getText().toString().startsWith("+")){
						isd_code.setError("Please enter a valid ISD code");
					}else if(isd_code.getText().toString().startsWith("0")){
						isd_code.setError("Please enter a valid ISD code");
					}else if(etMobileNUmber.getText().length() == 0) {
						etMobileNUmber.setError("Please enter a valid 9 digit Mobile Number");
						return;
					}else if(!isValidInteger(etMobileNUmber.getText().toString())) {
						etMobileNUmber.setError("Please enter a valid 9 digit Mobile Number");
						return;
					}else if(!isValidMobile(etMobileNUmber.getText().toString())) {
						etMobileNUmber.setError("Please enter a valid 9 digit Mobile Number");
						return;
					}else {
						if(GlobalFunctions.isNetworkAvailable(context)){
							registerfbData(activity,etMobileNUmber.getText().toString(),etUserName.getText().toString(),etEmail.getText().toString(),isd_code.getText().toString());
						}else{
							Snackbar.make(etEmail, "Please check your internet connection", Snackbar.LENGTH_LONG)
									.setAction("Retry", new View.OnClickListener() {
										@Override
										@TargetApi(Build.VERSION_CODES.M)
										public void onClick(View v) {
											sendThaData();
										}
									}).show();

						}

					}

				}else
					sendThaData();
			}
		});
		String terms="I agree to the Terms and Conditions.";
		SpannableString ss = new SpannableString("I agree to the Terms and Conditions.");
		ss.setSpan(new MyClickableSpan(),0,terms.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		termsOfAgreement.setText(ss);
		termsOfAgreement.setMovementMethod(LinkMovementMethod.getInstance());

		SpannableString ss1 = new SpannableString("Already have a account? Sign In");

		ss1.setSpan(new MyClickableSpan(),24,31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		loginPageLink.setText(ss1);
		loginPageLink.setMovementMethod(LinkMovementMethod.getInstance());
		if(getIntent().hasExtra(FACEBOOK_NAME)){
			facebookregisterModel = (FacebookRegisterModel) getIntent().getExtras().getSerializable(FACEBOOK_NAME);
			System.out.println("################facebookregisterModel######"+facebookregisterModel.toString());
			flag=  getIntent().getExtras().getInt(FACEBOOK_FLAG);
			if(flag== GlobalVariables.TYPE_SIGN_UP){
				etCOnfPass.setVisibility(View.GONE);
				System.out.println("###################facebookregisterModel.getName()########"+facebookregisterModel.getName());
				System.out.println("###################facebookregisterModel.getEmail()########"+facebookregisterModel.getEmail());
				etUserName.setText(facebookregisterModel.getName());
				etEmail.setText(facebookregisterModel.getEmail());
				facebookLoginBtn.setVisibility(View.GONE);
			}

		}
		if(getIntent().hasExtra(CREATE_GARRAGE_ACTIVITY_GARAGE_BUNDLE_KEY)){
			createSalesModel = (CreateSalesModel) getIntent().getExtras().getSerializable(CREATE_GARRAGE_ACTIVITY_GARAGE_BUNDLE_KEY);
		}
		if(getIntent().hasExtra(CREATE_GARRAGE_ACTIVITY_GARAGE_BUNDLE_KEY)){
			createProductModel = (CreateProductModel) getIntent().getExtras().getSerializable(CREATE_GARRAGE_ACTIVITY_ITME_BUNDLE_KEY);

		}
		if(checkIfContactsAccountsPermission())
			showGmailAccountsAvailable();
		else
			checkContactsAccountsPermission();


	}
	class MyClickableSpan extends ClickableSpan { //clickable span
		public void onClick(View textView) {
			if(textView.getId()== R.id.login_page_link){
			/*	Intent creategarrage = LoginActivity.newInstance(activity,createSalesModel,createProductModel);
				startActivity(creategarrage);*/
				closeThisActivity();
			}else {
				Intent i=TermsConditions.newInstance(context,GlobalVariables.TYPE_SIGN_UP);
				startActivity(i);
			}
		}
		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setColor(getResources().getColor(R.color.black));//set text color
			ds.setUnderlineText(true);
			//ds.setStyle(Typeface.BOLD);
			//ds.setTypeface(Typeface.DEFAULT_BOLD);
		}
	}


	@Override
	public void onBackPressed() {
		this.startActivity(new Intent(this, LoginActivity.class));
		super.onBackPressed();
		finish();
	}


	private boolean mayRequestContacts() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}
		if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
				checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
			return true;
		}
		if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
			Snackbar.make(etEmail, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
					.setAction(android.R.string.ok, new View.OnClickListener() {
						@Override
						@TargetApi(Build.VERSION_CODES.M)
						public void onClick(View v) {
							requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
						}
					});
		} else {
			requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
		}
		return false;
	}



	void sendThaData(){

		if(etUserName.getText().length()==0) {
			etUserName.setError("Please fill the Name");
			return;
		}else if(TextUtils.isDigitsOnly(etUserName.getText().toString())){
			etUserName.setError("Please enter valid Name");
			return;
		}else if(etEmail.getText().length() == 0) {
			etEmail.setError("Please fill the email");
			return;
		}else  if (!isValidEmail(etEmail.getText().toString())) {
			etEmail.setError("Please Check your Email id!!");
			return;
		}else if(etCOnfPass.getText().length() ==0) {
			etCOnfPass.setError("Please fill the password");
			return;
		}else if(etCOnfPass.getText().length()<5) {
			etCOnfPass.setError("The password must be at least 5 characters");
			return;
		}else if(isd_code.getText().length() == 0) {
			isd_code.setError("Please fill the ISD code");
			return;
		}else if(!isd_code.getText().toString().startsWith("+")){
			isd_code.setError("Please enter a valid ISD code");
		}else if(isd_code.getText().toString().startsWith("0")){
			isd_code.setError("Please enter a valid ISD code");
		}else if(etMobileNUmber.getText().length() == 0) {
			etMobileNUmber.setError("Please enter a valid 9 digit Mobile Number");
			return;
		}else if(!isValidInteger(etMobileNUmber.getText().toString())) {
			etMobileNUmber.setError("Please enter a valid 9 digit Mobile Number");
			return;
		}else if(!isValidMobile(etMobileNUmber.getText().toString())) {
			etMobileNUmber.setError("Please enter a valid 9 digit Mobile Number");
			return;
		}else if(!terms_conditions.isChecked())
		{
			terms_conditions.setError("Please Agree for Terms and Conditions");
			return;
		}/*else if(TextUtils.isEmpty(getAddress())){
			Toast.makeText(context,"Please enable your GPS and try again ",Toast.LENGTH_LONG).show();
			if(!locServices.canGetLocation()){
				locServices.showSettingsAlert();
			}

			return;
		}*/else{
			if(GlobalFunctions.isNetworkAvailable(this)){
				registerData( this, etEmail.getText().toString(), etCOnfPass.getText().toString(), etUserName.getText().toString(), etMobileNUmber.getText().toString(),isd_code.getText().toString());
			}else{
				Snackbar.make(etEmail, "Please check your internet connection", Snackbar.LENGTH_LONG)
						.setAction("Retry", new View.OnClickListener() {
							@Override
							@TargetApi(Build.VERSION_CODES.M)
							public void onClick(View v) {
								sendThaData();
							}
						}).show();

			}
		}


	}
	public static Boolean isValidInteger(String value) {
		try {
			Integer val = Integer.valueOf(value);
			return val != null;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public final static boolean isValidEmail(CharSequence target) {
		if (target == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
		}
	}
	private boolean isValidMobile(String phone){
		boolean check=false;
		if(phone.length()!=9) {
			check = false;
			etMobileNUmber.setError("Please enter a valid 9 digit Mobile Number");
		}/*else if(!phone.startsWith("+")){
			etMobileNUmber.setError("Please enter a valid 9 digit Mobile Number");
		}*/else if(CommonUtil.getPhone(phone).startsWith("0")){
			etMobileNUmber.setError("Please enter a valid 9 digit Mobile Number");
		}else {
			check = true;
			etMobileNUmber.setText(phone);
		}
		return check;
	}


	private void registerData(Context context, String email, String password, String name, String phone,String isd){

		GlobalFunctions.showProgress(context, "Registering User");
		registerModel.setEmail(email);
        registerModel.setPassword(password);
        registerModel.setName(name);
        registerModel.setPhone(phone);
		registerModel.setIsd(isd);

		if(TextUtils.isEmpty(formatted_address)) {
			formatted_address ="45, Bannerghatta Main Rd, Sundar Ram Shetty Nagar, Bilekahalli, Bengaluru, Karnataka 560076, India";
		}
		if(!TextUtils.isEmpty(locServices.getPostalCode()))
			registerModel.setPostalCode(formatted_address);
		else
			registerModel.setPostalCode("560076");


		registerModel.setCity(formatted_address);
		registerModel.setAddress(formatted_address);
		registerModel.setState(formatted_address);
		registerModel.setLatitude(Double.toString(locServices.getLatitude()));
		registerModel.setLongitude(Double.toString(locServices.getLongitude()));
		ServicesMethodsManager serverManager = new ServicesMethodsManager();
		serverManager.registerUser(this, registerModel, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
				GlobalFunctions.hideProgress();
                UserModel user = (UserModel) arg0;
                validateLogin(user);

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

		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_EMAIL, user.getEmail());
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_FULLNAME, user.getName());
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_PHONE, user.getPhone());
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_ADDRESS, user.getAddress());
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_TOKEN, user.getToken());
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_AVATAR, user.getImage());
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_BS_ID, user.getId());
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_USERID, user.getId());
		GlobalFunctions.setProfile(this, user);

	/*	Intent edit = CreateSaleActivity.newInstance(activity,createSalesModel,createProductModel,GlobalVariables.TYPE_AC_SIGN_UP);
		startActivity(edit);
		closeThisActivity()*/;

		if(createSalesModel!=null || createProductModel!=null){
			Intent edit = CreateSaleActivity.newInstance(activity,createSalesModel,createProductModel,null,GlobalVariables.TYPE_AC_SIGN_UP,GlobalVariables.TYPE_GARAGE);
			startActivity(edit);
			closeThisActivity();
		}else {
			startActivity(new Intent(this,MainActivity.class));
			closeThisActivity();
		}
	}

    public void closeThisActivity(){
        if(activity!=null){activity.finish();}
    }



	@Override
	public void onLocationChanged(Location location) {
		if(location!=null){
			/*registerModel.setCity(locServices.getCityName());
			registerModel.setState(locServices.getStateName());
			registerModel.setPostalCode(locServices.getPostalCode());*/
			locServices.removeListener();
		}else{
			Toast.makeText(activity, "Fetching Location", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		AppController.getInstance().trackScreenView(SignUpSCreenActivity.TAG);

		/*showGmailAccountsAvailable();*/
		/*if(flag!= GlobalVariables.TYPE_SIGN_UP){
			try
			{
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.add(new GoogleAccountsFragment(), GoogleAccountsFragment.TAG);
				ft.commit();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}*/


		if(GlobalFunctions.isNetworkAvailable(context)) {
			//formatted_address = getAddress();
			callbackManager = CallbackManager.Factory.create();
			loginButton = (LoginButton) findViewById(R.id.login_button);
			loginButton.setReadPermissions("public_profile", "email", "user_friends");
			facebookLoginBtn = (ImageView) findViewById(R.id.face_book_login);
			facebookLoginBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					progressDialog = new ProgressDialog(SignUpSCreenActivity.this);
					progressDialog.setMessage("Loading...");
					progressDialog.show();
					disconnectFromFacebook();
					loginButton.performClick();

					loginButton.setPressed(true);

					loginButton.invalidate();

					loginButton.registerCallback(callbackManager, mCallBack);

					loginButton.setPressed(false);

					loginButton.invalidate();

				}
			});
		}else{
			Snackbar.make(etUserName, "Please check your internet connection", Snackbar.LENGTH_LONG)
					.setAction("Retry", new View.OnClickListener() {
						@Override
						@TargetApi(Build.VERSION_CODES.M)
						public void onClick(View v) {

						}
					}).show();
		}
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
								final String email = object.getString("email").toString();
								final String name = object.getString("name").toString();
								final	String gender = object.getString("gender").toString();
								Log.e("Face book response: ", response + "");
								System.out.println("#######response#########"+response +"id"+id+"email"+email
										+"name"+name
										+"gender"+gender
								);
								LayoutInflater factory = LayoutInflater.from(activity);
								final View textEntryView = factory.inflate(R.layout.report_a_bug_fragment, null);
								final EditText content = (EditText) textEntryView.findViewById(R.id.report_a_bug_fragment_content_ev);
								final String phone=content.getText().toString();
								new android.app.AlertDialog.Builder(SignUpSCreenActivity.this)
										.setTitle("Please enter your phone number")
										.setView(textEntryView)
										.setPositiveButton("Send",new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int whichButton) {
												//registerfbData(activity,email,name,phone,id);
											}
										})

										.show();


								Toast.makeText(SignUpSCreenActivity.this,"welcome "+object.getString("name").toString(),Toast.LENGTH_LONG).show();

							}catch (Exception e){
								e.printStackTrace();
							}


							Intent intent=new Intent(SignUpSCreenActivity.this,MainActivity.class);
							startActivity(intent);
							finish();

						}

					});

			Bundle parameters = new Bundle();
			parameters.putString("fields", "id,name,email,gender, birthday");
			request.setParameters(parameters);
			request.executeAsync();
		}

		@Override
		public void onCancel() {
			progressDialog.dismiss();
		}

		@Override
		public void onError(FacebookException e) {
			progressDialog.dismiss();
		}
	};
	public  void fetchFaceBookProfilePic(){
		// fetching facebook's profile picture
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

	private void registerfbData(final Context context, String phone, String name, String email, String isd){
		GlobalFunctions.showProgress(context, "Registering User...");
		facebookregisterModel.setPhone(phone);
		facebookregisterModel.setEmail(email);
		facebookregisterModel.setName(name);
		facebookregisterModel.setLatitude(Double.toString(locServices.getLatitude()));
		facebookregisterModel.setLongitude(Double.toString(locServices.getLongitude()));
		facebookregisterModel.setAddress(getAddress());
		facebookregisterModel.setCity(getAddress());
		facebookregisterModel.setState(getAddress());
		facebookregisterModel.setPostalCode(getAddress());

		ServicesMethodsManager serverManager = new ServicesMethodsManager();
		serverManager.registerFacebookUser(this, facebookregisterModel, new ServerResponseInterface() {
			@Override
			public void OnSuccessFromServer(Object arg0) {
				Log.d(TAG, "Object String:"+arg0.toString());
				if(arg0 instanceof UserModel){
					UserModel user = (UserModel) arg0;
					validateFbLogin(user);
					GlobalFunctions.hideProgress();
				}else if(arg0 instanceof ErrorModel){
					GlobalFunctions.hideProgress();
					ErrorModel errorModel=(ErrorModel)arg0;
					Toast.makeText(context,errorModel.getError(),Toast.LENGTH_SHORT).show();
				}

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
	private void validateFbLogin(UserModel user){
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_EMAIL, user.getEmail());
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_FULLNAME, user.getName());
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_PHONE, user.getPhone());
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_ADDRESS, user.getAddress());
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_TOKEN, user.getToken());
		GlobalFunctions.setSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_AVATAR, user.getImage());
		GlobalFunctions.setProfile(this, user);

	/*	Intent edit = CreateSaleActivity.newInstance(activity,createSalesModel,createProductModel,GlobalVariables.TYPE_AC_SIGN_UP);
		startActivity(edit);
		closeThisActivity();*/
		if(createSalesModel!=null || createProductModel!=null){
			Intent edit = CreateSaleActivity.newInstance(activity,createSalesModel,createProductModel,null,GlobalVariables.TYPE_AC_SIGN_UP,GlobalVariables.TYPE_GARAGE);
			startActivity(edit);
			closeThisActivity();
		}else {
			startActivity(new Intent(this,MainActivity.class));
			closeThisActivity();
		}


	}


	public String address;
	public void setAddress(String adress){
		this.address=adress;
	}
	public String getAddress(){
		return this.address;
	}
	private void getAddressFromLatLng(Context context){
		//GlobalFunctions.showProgress(context, "Fetching address...");
		ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
		servicesMethodsManager.getAddress(context, locServices.getLatitude(),locServices.getLongitude(), new ServerResponseInterface() {
			@Override
			public void OnSuccessFromServer(Object arg0) {
				//GlobalFunctions.hideProgress();
				Log.d(TAG, "onSuccess Response");
				LocationModel locationModel =(LocationModel)arg0;
				setAddress(locationModel);
				Log.d(TAG, "#########locationModel.getFormatted_address###########"+ locationModel.getFormatted_address());

			}

			@Override
			public void OnFailureFromServer(String msg) {
				//GlobalFunctions.hideProgress();
				Log.d(TAG, msg);
			}

			@Override
			public void OnError(String msg) {
				GlobalFunctions.hideProgress();
				Log.d(TAG, msg);
			}
		}, "Delete Sale");
	}
	public void setAddress(LocationModel locationModel){
		setAddress(locationModel.getFormatted_address());
		location.setText(locationModel.getFormatted_address());
	}
	//Override the method here
	@Override
	public void onNewsItemPicked(Item position){
		etUserName.setText(position.getKey());
		etEmail.setText(position.getValue());
		etMobileNUmber.setText(position.getPhoneNumber());

	}
	@Override
	public void onProviderEnabled(String provider) {

	}
	public void showGmailAccountsAvailable(){
		Log.d(TAG,"flag!= showGmailAccountsAvailable######################");
		if(flag!= GlobalVariables.TYPE_SIGN_UP){
			Log.d(TAG,"showGmailAccountsAvailable######################");
			try
			{	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.add(new GoogleAccountsFragment(), GoogleAccountsFragment.TAG);
				ft.commit();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
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
					showGmailAccountsAvailable();
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
		Log.d(TAG,"disconnectFromFacebook");
		if (AccessToken.getCurrentAccessToken() == null) {
			Log.d(TAG,"getCurrentAccessToken not null");
			return; // already logged out
		}

		new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
				.Callback() {
			@Override
			public void onCompleted(GraphResponse graphResponse) {
				Log.d(TAG,"logOut called");
				LoginManager.getInstance().logOut();

			}
		}).executeAsync();
	}
	/*@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
										   @NonNull int[] grantResults) {
		if (requestCode == REQUEST_READ_CONTACTS) {
			if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				etEmail.setText(AppController.getEmail(this));
				etMobileNUmber.setText(AppController.getMobileNumber(this));
			}
		}
	}*/
}
