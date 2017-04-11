package com.langoor.app.blueshak.horizontalListItems;

import com.langoor.app.blueshak.services.model.ProductModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sivasabharish Chinnaswamy on 29-10-2015.
 */
public class HorizontalListArray implements Serializable {

    String listTitle = "";
    List<ProductModel> horizontalListArrayList = new ArrayList<ProductModel>();

    public HorizontalListArray(String listTitle, List<ProductModel> horizontalListArrayList){
        this.listTitle = listTitle;
        this.horizontalListArrayList = horizontalListArrayList;
    }

    public List<ProductModel> getHorizontalListArrayList() {
        return horizontalListArrayList;
    }

    public String getListTitle() {
        return listTitle;
    }

}
