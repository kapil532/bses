package com.langoor.app.blueshak.services.model;

import android.util.Log;
import com.langoor.app.blueshak.AppController;
import com.langoor.app.blueshak.global.GlobalVariables;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchModel implements Serializable {
    private final String TAG = "SearchModel";
    public static final String
            LATITUDE = "latitude",
            LONGITUDE = "longitude",
            RANGE = "range",
            PRICE_RANGE = "price_range",
            ZIPCODE = "zip_code",
            TYPE = "type",
            SEARCH="search",
            PAGE="page",
            CATEGORIES = "categories";

    String
            latitude = null;
    String longitude =null;
    String priceRange = "0";
    String zipcode = null;
    String type = null;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    String search= null;

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    String formatted_address=null;
    String categories = null;
    String location=null;

    int page=1;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    int range = 5;

    boolean
            shipable        = false,
            pickup          = false,
            available       = false;

    public SearchModel(){}

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public boolean isShipable() {
        return shipable;
    }

    public void setShipable(boolean shipable) {
        this.shipable = shipable;
    }

    public boolean isPickup() {
        return pickup;
    }

    public void setPickup(boolean pickup) {
        this.pickup = pickup;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean toObject(String jsonObject){
        try{
            JSONObject json = new JSONObject(jsonObject);
            if(json.has(LATITUDE))latitude = json.getString(LATITUDE);
            if(json.has(LONGITUDE))longitude = json.getString(LONGITUDE);
            if(json.has(ZIPCODE))zipcode = json.getString(ZIPCODE);
            if(json.has(TYPE))type = json.getString(TYPE);
            if(json.has(RANGE))range = json.getInt(RANGE);
            if(json.has(PRICE_RANGE))priceRange = json.getString(PRICE_RANGE);
            if(json.has(CATEGORIES))categories = json.getString(CATEGORIES);
            int temp = 0;
          return true;
        }catch(Exception ex){

            Log.d(TAG, "Json Exception : " + ex);}
        return false;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString(){
        String returnString = null;
        try{
            JSONObject jsonMain = new JSONObject();
            jsonMain.put(TYPE, type);
            jsonMain.put(RANGE, range);
            jsonMain.put(SEARCH, search);
            jsonMain.put(PRICE_RANGE, priceRange);
            jsonMain.put(ZIPCODE, zipcode);
            jsonMain.put(LATITUDE, latitude);
            jsonMain.put(LONGITUDE, longitude);
            jsonMain.put(CATEGORIES, categories);
            jsonMain.put(PAGE, page);
            returnString = jsonMain.toString();
        }catch (Exception ex){Log.d(TAG," To String Exception : "+ex);
           }
        return returnString;
    }
}
