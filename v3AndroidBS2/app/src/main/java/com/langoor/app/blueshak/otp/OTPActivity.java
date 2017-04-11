package com.langoor.app.blueshak.otp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.langoor.blueshak.R;
import com.langoor.app.blueshak.MainActivity;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.login.LoginActivity;
import com.langoor.app.blueshak.root.RootActivity;
import com.langoor.app.blueshak.services.ServerResponseInterface;
import com.langoor.app.blueshak.services.ServicesMethodsManager;
import com.langoor.app.blueshak.services.model.OTPCheckerModel;
import com.langoor.app.blueshak.services.model.OTPResendModel;
import com.langoor.app.blueshak.services.model.StatusModel;


public class OTPActivity extends RootActivity {
    private TextView signup_text,otp_get_another,otp_try_again,cancel;
	private EditText etEmail;
	private Button btnSignIn;
	private LinearLayout resend_otp;
    private static Activity activity;
    private ImageView close_button;
    private ProgressBar progress_bar;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.otp_screen);
        activity = this;
        etEmail =(EditText)findViewById(R.id.etEmail);
		btnSignIn = (Button)findViewById(R.id.btnSignIn);
        otp_get_another=(TextView) findViewById(R.id.otp_get_another);
        otp_try_again=(TextView) findViewById(R.id.otp_try_again);
		String email = GlobalFunctions.getSharedPreferenceString(this, GlobalVariables.SHARED_PREFERENCE_EMAIL);
        close_button=(ImageView)findViewById(R.id.close_button);
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Update the action bar title with the TypefaceSpan instance
        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.action_bar_titlel, null);
        ((TextView)v.findViewById(R.id.title)).setText(/*this.getTitle()*/"OTP ");
        toolbar.addView(v);
        cancel=(TextView)v.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeThisActivity();
            }
        });

        progress_bar=(ProgressBar)findViewById(R.id.progress_bar);
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
        if(email!=null){etEmail.setText(email);}
        btnSignIn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
                String otpText = etEmail.getText().toString().trim();
                if(otpText.length()>0) {
                    sendRequestToServer(activity, otpText);}
                else {Toast.makeText(activity, "Please fill required field", Toast.LENGTH_LONG).show();}

			}
		});
        String otp_get_another1="Didn't receive one? Get another.";

        SpannableString ss = new SpannableString("Didn't receive one? Get another.");
        ss.setSpan(new MyClickableSpan(),20,otp_get_another1.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        otp_get_another.setText(ss);
        otp_get_another.setMovementMethod(LinkMovementMethod.getInstance());

        String otp_try_another="Entered the wrong number? Try again.";


        SpannableString ss1 = new SpannableString("Entered the wrong number? Try again.");

        ss1.setSpan(new MyClickableSpan(),26,otp_try_another.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        otp_try_again.setText(ss1);
        otp_try_again.setMovementMethod(LinkMovementMethod.getInstance());
	}

    class MyClickableSpan extends ClickableSpan { //clickable span
        public void onClick(View textView) {
            if(textView.getId()== R.id.otp_get_another){
             reSendRequestToServer();
            }else {
                if(!TextUtils.isEmpty(etEmail.getText().toString()))
                    sendRequestToServer(activity,etEmail.getText().toString());
            }
        }
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(activity.getResources().getColor(R.color.brandColor));//set text color
            ds.setUnderlineText(true);
            //ds.setTypeface(Typeface.DEFAULT_BOLD);
        }
    }
	private void sendRequestToServer(Context context, String otp){
        showProgressBar();
        ServicesMethodsManager serverManager = new ServicesMethodsManager();
        OTPCheckerModel checkerModel = new OTPCheckerModel(GlobalFunctions.getSharedPreferenceString(context, GlobalVariables.SHARED_PREFERENCE_PHONE),otp);
        serverManager.verifyOTP(context, checkerModel, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
               hideProgressBar();
                validateOTPSignin((StatusModel) arg0);
            }

            @Override
            public void OnFailureFromServer(String msg) {
                 hideProgressBar();
                Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnError(String msg) {
                 hideProgressBar();
                Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_SHORT).show();
            }
        },"OTP verification");
        Intent intt = new Intent(OTPActivity.this,LoginActivity.class);
        startActivity(intt);
        closeThisActivity();
	}


    private void validateOTPSignin(StatusModel statusModel){
        if(statusModel.isStatus()){
         proceedToMainActivity();
        }else{Toast.makeText(getApplicationContext(),"Error on processing OTP, try resending OTP", Toast.LENGTH_SHORT).show();}
    }

    private void validateOTPResend(StatusModel statusModel){
        if(statusModel.isStatus()){
            Toast.makeText(getApplicationContext(),"OTP Resent Successfully", Toast.LENGTH_SHORT).show();
           if(etEmail!=null){etEmail.setText("");}
        }else{Toast.makeText(getApplicationContext(),"Already Authenticated", Toast.LENGTH_SHORT).show();}
    }


	private void reSendRequestToServer() {
        showProgressBar();
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        OTPResendModel resendModel = new OTPResendModel(GlobalFunctions.getSharedPreferenceString(getApplication(), GlobalVariables.SHARED_PREFERENCE_PHONE));
        servicesMethodsManager.resendOTP(getApplicationContext(), resendModel, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                 hideProgressBar();
                validateOTPResend((StatusModel)arg0);
            }

            @Override
            public void OnFailureFromServer(String msg) {
                 hideProgressBar();
                Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnError(String msg) {
                 hideProgressBar();
                Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_SHORT).show();
            }
        },"Resend OTP");
	}

    public static void closeThisActivity(){
        if(activity!=null){activity.finish();}
    }
    public void showProgressBar(){
        if(progress_bar!=null)
            progress_bar.setVisibility(View.VISIBLE);
    }
    public void hideProgressBar(){
        if(progress_bar!=null)
            progress_bar.setVisibility(View.GONE);
    }

    public void proceedToMainActivity(){
        MainActivity.closeThisActivity();
        startActivity(new Intent(this,MainActivity.class));
        closeThisActivity();
    }


}
