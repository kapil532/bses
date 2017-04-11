package com.langoor.app.blueshak.forgot_password;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.divrt.co.R;
import com.langoor.app.blueshak.AppController;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.login.LoginActivity;
import com.langoor.app.blueshak.services.ServerResponseInterface;
import com.langoor.app.blueshak.services.ServicesMethodsManager;
import com.langoor.app.blueshak.services.model.ForgotPasswordModel;
import com.langoor.app.blueshak.services.model.StatusModel;

public class ForgotPasswordActivity extends Activity
{

	private TextView signup_text;
	private EditText etEmail;
	private Button btnSignIn;

    private static Activity activity;
    ImageView close_button;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forgot_password_screen);
        activity = this;
        etEmail =(EditText)findViewById(R.id.etEmail);
        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendRequestToServer(activity,etEmail.getText().toString());
            }
        });
        close_button=(ImageView)findViewById(R.id.close_button);
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               closeThisActivity();
            }
        });
	}

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
	private void sendRequestToServer(Context context, String email) {
        if(etEmail.getText().length() == 0) {
            etEmail.setError("Please fill the email");
            return;
        }else  if (!isValidEmail(etEmail.getText().toString())) {
            etEmail.setError("Please Check your Email id!!");
            return;
        }
        GlobalFunctions.showProgress(activity, "Generating Password...");
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        ForgotPasswordModel forgotPasswordModel = new ForgotPasswordModel(email);
        servicesMethodsManager.forgotPassword(context, forgotPasswordModel, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                validateResult((StatusModel) arg0);
            }

            @Override
            public void OnFailureFromServer(String msg) {
                GlobalFunctions.hideProgress();
                Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnError(String msg) {
                GlobalFunctions.hideProgress();
                Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_SHORT).show();
            }
        }, "Forgot Password");
	}
    @Override
    public void onResume() {
        super.onResume();

        // Tracking the screen view
        AppController.getInstance().trackScreenView("ForgotPasswordActivity");
    }
    private void validateResult(StatusModel statusModel){
        if(statusModel.isStatus()){
            Toast.makeText(activity,"Successfully submitted",Toast.LENGTH_LONG).show();
            Intent intt  = new Intent(ForgotPasswordActivity.this,LoginActivity.class);
            startActivity(intt);
            closeThisActivity();
        }else{Toast.makeText(getApplicationContext(),"Error on Generating Password, try again latter", Toast.LENGTH_SHORT).show();}
    }

    public static void closeThisActivity(){
        if(activity!=null){activity.finish();}
    }
	/*PostCommentJsonStringResponseListener jsonPost = new PostCommentJsonStringResponseListener() {

		@Override
		public void requestStarted() {
			// TODO Auto-generated method stub

		}

		@Override
		public void requestEndedWithError(String message) {
			// TODO Auto-generated method stub
			Log.d("RESPONSE","RESPONS"+message);
			String messageError ="";
			if(message.length() >0)
			{
				try {
					JSONObject json = new JSONObject(message);
					messageError =    json.getString("error");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			Toast.makeText(ForgotPasswordActivity.this, ""+messageError, Toast.LENGTH_LONG).show();
		}

		@Override
		public void requestCompleted(JSONObject message) 
		{
			String ssu="";
			if(message.length()>1)
			{
				try {
					ssu = message.getString("status");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if(ssu.equalsIgnoreCase("success"))
				{

					Intent j = new Intent(ForgotPasswordActivity.this,LoginActivity.class);
					startActivity(j);
					Toast.makeText(ForgotPasswordActivity.this, "Check Your mail", Toast.LENGTH_LONG).show();
					
					finish();
				}
			}

		}
	};


	PostCommentJsonResponseListener mpost = new PostCommentJsonResponseListener() {



		private ProgressDialog ringProgressDialog;

		@Override
		public void requestStarted()
		{
			ringProgressDialog	= ProgressDialog.show(ForgotPasswordActivity.this, "", "Please wait ...", true);
			ringProgressDialog.setCancelable(true);
			ringProgressDialog.show();
		}

		@Override
		public void requestEndedWithError(VolleyError error) {
			// TODO Auto-generated method stub
			ringProgressDialog.dismiss();

			Log.d("THIS IS RESPONSE","RESPONSE--->"+error.getMessage());


		}

		@Override
		public void requestCompleted(JSONObject message) 
		{
			ringProgressDialog.dismiss();
			Log.d("THIS IS RESPONSE","RESPONSE---MES-->"+message.toString());
		}
	};*/

}
