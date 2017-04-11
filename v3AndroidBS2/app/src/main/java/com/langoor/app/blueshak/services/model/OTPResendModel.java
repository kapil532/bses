package com.langoor.app.blueshak.services.model;

import android.util.Log;

import com.langoor.app.blueshak.AppController;

import org.json.JSONObject;

public class OTPResendModel {

    private final String TAG = "LoginModel";
    private final String
            PHONE = "phone";

    String phone=null;

    public OTPResendModel(){}

    public OTPResendModel(String phone){
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean toObject(String jsonObject){
        try{
            JSONObject json = new JSONObject(jsonObject);
            phone = json.getString(PHONE);
            return true;
        }catch(Exception ex){

            Log.d(TAG, "Json Exception : " + ex);}
        return false;
    }

    @Override
    public String toString(){
        String returnString = null;
        try{
            JSONObject jsonMain = new JSONObject();
            jsonMain.put(PHONE, phone);
            returnString = jsonMain.toString();
        }
        catch (Exception ex){Log.d(TAG," To String Exception : "+ex);
           }
        return returnString;
    }
}

