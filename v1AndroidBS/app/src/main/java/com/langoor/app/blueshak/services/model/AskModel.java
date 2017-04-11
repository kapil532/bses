package com.langoor.app.blueshak.services.model;

import android.util.Log;

import com.langoor.app.blueshak.AppController;

import org.json.JSONObject;

import java.io.Serializable;

public class AskModel implements Serializable {
    private final String TAG = "AskModel";
    private final String
            ASH_PHONE = "ask_phone";

    private final String
            EMAIL = "email";

    int ask_phone=0;



    public int getAsk_phone() {
        return ask_phone;
    }

    public void setAsk_phone(int ask_phone) {
        this.ask_phone = ask_phone;
    }

    public boolean toObject(String jsonObjectString){
        try {
            JSONObject obj=new JSONObject(jsonObjectString);
            if(obj.has(ASH_PHONE)){
               ask_phone=obj.getInt(ASH_PHONE);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            AppController.getInstance().trackException(e);
        }
        return false;
    }

    @Override
    public String toString(){
        String returnString = null;
        try{


        }
        catch (Exception ex){
            AppController.getInstance().trackException(ex);
            Log.d(TAG," To String Exception : "+ex);}
        return returnString;
    }

}
