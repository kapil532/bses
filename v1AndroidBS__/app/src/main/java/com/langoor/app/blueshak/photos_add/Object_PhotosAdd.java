package com.langoor.app.blueshak.photos_add;

import com.divrt.co.R;
import com.langoor.app.blueshak.services.model.CreateImageModel;

import java.io.Serializable;
import java.util.ArrayList;


public class Object_PhotosAdd implements Serializable{

    int categoryID = 0;
    String title = "Add Images";
    int addIconResID = R.drawable.ic_camera;
    ArrayList<CreateImageModel> availablePhotos = new ArrayList<CreateImageModel>();

    public ArrayList<CreateImageModel> getAvailablePhotos() {
        return availablePhotos;
    }

    public void setAvailablePhotos(ArrayList<CreateImageModel> availablePhotos) {
        this.availablePhotos = availablePhotos;
    }

    public int getAddIconResID() {
        return addIconResID;
    }

    public void setAddIconResID(int addIconResID) {
        this.addIconResID = addIconResID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }
}
