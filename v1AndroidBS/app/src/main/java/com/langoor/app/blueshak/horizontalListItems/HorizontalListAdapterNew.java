package com.langoor.app.blueshak.horizontalListItems;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.divrt.co.R;
import com.langoor.app.blueshak.PickLocation;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.item.ProductDetail;
import com.langoor.app.blueshak.services.ServerResponseInterface;
import com.langoor.app.blueshak.services.ServicesMethodsManager;
import com.langoor.app.blueshak.services.model.ErrorModel;
import com.langoor.app.blueshak.services.model.ProductModel;
import com.langoor.app.blueshak.services.model.StatusModel;
import com.langoor.app.blueshak.util.LocationService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

/**
 * Created by Manu on 17-07-2015.
 */
public class HorizontalListAdapterNew extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private List<ProductModel> albumList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        protected TextView name, price;
        protected ImageView imageView;
        public MyViewHolder(View view) {
            super(view);
            price = (TextView) view.findViewById(R.id.horizontal_list_item_text_price);
            imageView = (ImageView) view.findViewById(R.id.horizontal_list_item_image);
        }
    }

    public HorizontalListAdapterNew(Context mContext, List<ProductModel> albumList) {
        this.context = mContext;
        this.albumList = albumList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.horizontal_list_items_row, parent, false);
            return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder view_holder, int position) {
            final  MyViewHolder holder=(MyViewHolder)view_holder;
            final ProductModel obj = albumList.get(position);
            int price=0;
            if(!TextUtils.isEmpty(obj.getSalePrice()))
                price=(int)Float.parseFloat(obj.getSalePrice());

            holder.price.setText("$ "+price);

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = ProductDetail.newInstance(context, obj,null, GlobalVariables.TYPE_MY_SALE);
                    context.startActivity(intent);
                }
            });
        List<String> imageURI = null;

        imageURI = obj.getImage();

       /* if(imageURI!=null){
            if(imageURI.size()>0){
                Toast.makeText()
                imageLoader.DisplayImage(imageURI.get(0),holder.imageView);
            }
        }*/
        String image_url="";
        if(imageURI!=null){
            if(imageURI.size()>0){
                image_url=imageURI.get(0);
            }else{
                image_url=obj.getItem_display_Image();
            }
        }
        if(!TextUtils.isEmpty(image_url)){
            Picasso.with(context).load(image_url).placeholder(R.drawable.placeholder_background).error(R.drawable.placeholder_background).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    holder.imageView.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    holder.imageView.setImageDrawable(errorDrawable);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    holder.imageView.setImageDrawable(placeHolderDrawable);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }


}
