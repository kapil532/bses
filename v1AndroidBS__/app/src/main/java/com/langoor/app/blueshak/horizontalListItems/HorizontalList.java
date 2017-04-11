package com.langoor.app.blueshak.horizontalListItems;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;

/**
 * Created by Sivasabharish Chinnaswamy on 29-09-2015.
 */
public class HorizontalList implements Serializable{
    String itemId,name,offer;
    BigDecimal oldPrice,price;
    URL image_url;

    public HorizontalList(String itemId, URL image_url, String itemName, BigDecimal oldPrice, BigDecimal price, String offer){
        this.itemId = itemId;
        this.name=itemName;
        this.oldPrice=oldPrice;
        this.price=price;
        this.offer=offer;
        this.image_url=image_url;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public BigDecimal getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(BigDecimal oldPrice) {
        this.oldPrice = oldPrice;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public URL getImage_url() {
        return image_url;
    }

    public void setImage_url(URL image_download_url) {
        this.image_url = image_download_url;
    }


}
