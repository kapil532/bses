package com.langoor.app.blueshak.services.model;

import android.util.Log;

import com.langoor.app.blueshak.AppController;

import org.json.JSONObject;

public class CategoryModel {
    private final String TAG = "CategoryModel";
    private final String
            ID = "id",
            NAME = "category_name";

    String id=null, name=null;

    public CategoryModel(){}

    public CategoryModel(String id, String name){
        this.id = id;
        this.name = name;
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

    public boolean toObject(String jsonObject){
        try{
            JSONObject json = new JSONObject(jsonObject);
            id = json.getString(ID);
            name = json.getString(NAME);
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
            jsonMain.put(ID, id);
            jsonMain.put(NAME, name);
            returnString = jsonMain.toString();
        }
        catch (Exception ex){Log.d(TAG," To String Exception : "+ex);
            AppController.getInstance().trackException(ex);}
        return returnString;
    }
}
