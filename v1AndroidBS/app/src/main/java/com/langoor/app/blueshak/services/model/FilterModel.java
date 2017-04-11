package com.langoor.app.blueshak.services.model;

import android.util.Log;

import com.langoor.app.blueshak.AppController;
import com.langoor.app.blueshak.global.GlobalVariables;

import org.json.JSONObject;

import java.io.Serializable;

public class FilterModel implements Serializable {
    private final String TAG = "FilterModel";
    public static final String
            LATITUDE = "latitude",
            LONGITUDE = "longitude",
            RANGE = "range",
            PRICE_RANGE = "price_range",
            ZIPCODE = "zip_code",
            TYPE = "type",
            PAGE="page",
            IS_SHIPABLE = "is_shippable",
            IS_PICKUP = "is_pickupable",
            IS_AVAILABLE = "is_available",
            LOCATION="location",
            FORMATTED_ADDRESS="formatted_address",
            CATEGORIES = "categories";

    String latitude = GlobalVariables.LATITUDE;
    String longitude =  GlobalVariables.LONGITUDE;
    String priceRange = GlobalVariables.PRICE_MIN_VALUE+","+GlobalVariables.PRICE_MAX_VALUE;
    String zipcode = null;
    String type = null;

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

    public FilterModel(){}

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
            if(json.has(FORMATTED_ADDRESS))formatted_address = json.getString(FORMATTED_ADDRESS);
            if(json.has(PRICE_RANGE))priceRange = json.getString(PRICE_RANGE);
            if(json.has(CATEGORIES))categories = json.getString(CATEGORIES);
            int temp = 0;
            try{temp = json.getInt(IS_SHIPABLE);}catch(Exception e){temp =0;}if(temp>0){shipable=true;}else{shipable=false;}temp=0;
            try{temp = json.getInt(IS_PICKUP);}catch(Exception e){temp =0;}if(temp>0){pickup=true;}else{pickup=false;}temp=0;
            try{temp = json.getInt(IS_AVAILABLE);}catch(Exception e){temp =0;}if(temp>0){available=true;}else{available=false;}temp=0;
            return true;
        }catch(Exception ex){
            AppController.getInstance().trackException(ex);
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
            jsonMain.put(PRICE_RANGE, priceRange);
            jsonMain.put(ZIPCODE, zipcode);
            jsonMain.put(LATITUDE, latitude);
            jsonMain.put(LONGITUDE, longitude);
            jsonMain.put(CATEGORIES, categories);
            jsonMain.put(FORMATTED_ADDRESS, formatted_address);
            jsonMain.put(PAGE, page);

            String temp = "0";
            if(shipable){temp = "1";}else{temp="0";}jsonMain.put(IS_SHIPABLE, temp);temp = "0";
            if(pickup){temp = "1";}else{temp="0";}jsonMain.put(IS_PICKUP, temp);temp = "0";
            if(available){temp = "1";}else{temp="0";}jsonMain.put(IS_AVAILABLE, temp);temp = "0";
            returnString = jsonMain.toString();
        }
        catch (Exception ex){Log.d(TAG," To String Exception : "+ex);
            AppController.getInstance().trackException(ex);}
        return returnString;
    }
}
