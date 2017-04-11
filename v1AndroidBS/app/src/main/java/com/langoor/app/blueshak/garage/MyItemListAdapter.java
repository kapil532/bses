package com.langoor.app.blueshak.garage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.divrt.co.R;
import com.langoor.app.blueshak.ImageCashing.ImageLoader;
import com.langoor.app.blueshak.PickLocation;
import com.langoor.app.blueshak.filter.FilterActivity;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.item.ProductDetail;
import com.langoor.app.blueshak.services.model.CategoryListModel;
import com.langoor.app.blueshak.services.model.CreateProductModel;
import com.langoor.app.blueshak.services.model.ProductModel;
import com.langoor.app.blueshak.util.LocationService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyItemListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<ProductModel> albumList;
    private List<ProductModel> selected_list=new ArrayList<ProductModel>();
    public String item_address;
    private ImageLoader imageLoader;
    private boolean item_editable =false;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        protected TextView item_price,item_name;
        protected ImageView item_image;
        public CheckBox chkSelected;
        LinearLayout item_view;
        public MyViewHolder(View view) {
            super(view);
            item_price = (TextView) view.findViewById(R.id.price_tv);
            item_name = (TextView) view.findViewById(R.id.name_tv);
           /* done = (ImageView) view.findViewById(R.id.done);*/
            item_image = (ImageView) view.findViewById(R.id.image_iv);
            item_view=(LinearLayout)view.findViewById(R.id.item_view);
            chkSelected = (CheckBox) view.findViewById(R.id.chkSelected);
        }
    }


    public MyItemListAdapter(Context mContext, List<ProductModel> albumList,boolean item_editable) {
        this.context = mContext;
        this.albumList = albumList;
        this.item_address = GlobalFunctions.getSharedPreferenceString(mContext, GlobalVariables.CURRENT_LOCATION);
        imageLoader=new ImageLoader(mContext);
        this.item_editable=item_editable;


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.add_my_item_row, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder view_holder, int position) {
        final int pos = position;
      if (view_holder instanceof MyViewHolder) {
            final MyViewHolder holder = (MyViewHolder) view_holder;
             ProductModel obj = albumList.get(position);

          holder.item_price.setText("$ " + obj.getSalePrice());
          holder.item_name.setText(obj.getName());
          holder.chkSelected.setChecked(obj.is_selected());
          if(obj.is_selected()){
              holder.chkSelected.setChecked(obj.is_selected());
              holder.item_price.setTextColor(context.getResources().getColor(R.color.brandColor));
              holder.item_name.setTextColor(context.getResources().getColor(R.color.brandColor));
          }else{
              holder.item_price.setTextColor(context.getResources().getColor(R.color.black));
              holder.item_name.setTextColor(context.getResources().getColor(R.color.black));
          }
          if(!item_editable)
              holder.chkSelected.setEnabled(false);

          holder.chkSelected.setTag(obj);

          holder.chkSelected.setOnClickListener(new View.OnClickListener() {
              public void onClick(View v) {
                  CheckBox cb = (CheckBox) v;
                  ProductModel obj = (ProductModel) cb.getTag();
                  obj.setIs_selected(cb.isChecked());
                  albumList.get(pos).setIs_selected(cb.isChecked());
                  if(cb.isChecked()){
                      holder.item_price.setTextColor(context.getResources().getColor(R.color.brandColor));
                      holder.item_name.setTextColor(context.getResources().getColor(R.color.brandColor));
                  }else{
                      holder.item_price.setTextColor(context.getResources().getColor(R.color.black));
                      holder.item_name.setTextColor(context.getResources().getColor(R.color.black));
                  }
              }
          });

          List<String> imageURI = null;
          imageURI = obj.getImage();
          String item_image=obj.getItem_display_Image();
          if(!TextUtils.isEmpty(item_image)){
              /*imageLoader.DisplayImage(item_image,holder.item_image);*/
              Picasso.with(context).load(item_image).placeholder(R.drawable.placeholder_background).error(R.drawable.placeholder_background).into(new Target() {
                  @Override
                  public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                      holder.item_image.setImageBitmap(bitmap);
                  }

                  @Override
                  public void onBitmapFailed(Drawable errorDrawable) {
                      holder.item_image.setImageDrawable(errorDrawable);
                  }

                  @Override
                  public void onPrepareLoad(Drawable placeHolderDrawable) {
                      holder.item_image.setImageDrawable(placeHolderDrawable);
                  }
              });
          }
         /* if(imageURI!=null){
              if(imageURI.size()>0){
                  imageLoader.DisplayImage(imageURI.get(0),holder.item_image);
              }
          }*/


     /*     if(imageURI!=null){
              if(imageURI.size()>0){
                  Picasso.with(context).load(imageURI.get(0)).placeholder(R.drawable.placeholder_background).error(R.drawable.placeholder_background).into(new Target() {
                      @Override
                      public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                          holder.item_image.setImageBitmap(bitmap);
                      }

                      @Override
                      public void onBitmapFailed(Drawable errorDrawable) {
                          holder.item_image.setImageDrawable(errorDrawable);
                      }

                      @Override
                      public void onPrepareLoad(Drawable placeHolderDrawable) {
                          holder.item_image.setImageDrawable(placeHolderDrawable);
                      }
                  });
              }
          }*/

        }
    }
    @Override
    public int getItemCount() {
        return albumList.size();
    }
    // method to access in activity after updating selection
    public List<ProductModel> getProductList() {
        return albumList;
    }


}
