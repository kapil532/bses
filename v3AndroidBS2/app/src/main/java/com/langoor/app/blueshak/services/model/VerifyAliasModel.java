package com.langoor.app.blueshak.services.model;

import android.util.Log;

import org.json.JSONObject;
import java.io.Serializable;

public class VerifyAliasModel implements Serializable{
    private final String TAG = "VerifyAliasModel";
    private final String
            IS_EXISTING = "is_existing";
    private final String
            MESSAGE = "message";

    boolean alias_available=false;

    public boolean isAlias_available() {
        return alias_available;
    }

    public void setAlias_available(boolean alias_available) {
        this.alias_available = alias_available;
    }

    public VerifyAliasModel(){}




    public boolean toObject(String jsonObject){
        try{
            JSONObject json = new JSONObject(jsonObject);
            int temp = 0;
            try{temp = json.getInt(IS_EXISTING);}catch(Exception e){temp =0;}if(temp>0){alias_available=true;}else{alias_available=false;}temp=0;
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
            if(IS_EXISTING!=null)jsonMain.put(IS_EXISTING, alias_available);
            returnString = jsonMain.toString();
        }
        catch (Exception ex){Log.d(TAG," To String Exception : "+ex);
           }
        return returnString;
    }
}
