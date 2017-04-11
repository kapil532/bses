package com.langoor.app.blueshak.services.model;

import android.util.Log;

import com.langoor.app.blueshak.AppController;

import org.json.JSONObject;

public class OTPCheckerModel {

    private final String TAG = "LoginModel";
    private final String
            PHONE = "phone",
            OTP = "otp";

    String phone=null, otp=null;

    public OTPCheckerModel(){}

    public OTPCheckerModel(String phone, String otp){
        this.phone = phone;
        this.otp = otp;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOTP() {
        return otp;
    }

    public void setOTP(String otp) {
        this.otp = otp;
    }

    public boolean toObject(String jsonObject){
        try{
            JSONObject json = new JSONObject(jsonObject);
            phone = json.getString(PHONE);
            otp = json.getString(OTP);
            return true;
        }catch(Exception ex){
            Log.d(TAG, "Json Exception : " + ex);
            AppController.getInstance().trackException(ex);}
        return false;
    }

    @Override
    public String toString(){
        String returnString = null;
        try{
            JSONObject jsonMain = new JSONObject();
            jsonMain.put(PHONE, phone);
            jsonMain.put(OTP, otp);
            returnString = jsonMain.toString();
        }
        catch (Exception ex){Log.d(TAG," To String Exception : "+ex);
            AppController.getInstance().trackException(ex);
        }
        return returnString;
    }
}

