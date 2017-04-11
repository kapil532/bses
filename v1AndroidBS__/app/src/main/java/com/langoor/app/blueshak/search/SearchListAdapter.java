package com.langoor.app.blueshak.search;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.divrt.co.R;
import com.langoor.app.blueshak.ImageCashing.ImageLoader;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.item.ProductDetail;
import com.langoor.app.blueshak.login.LoginActivity;
import com.langoor.app.blueshak.services.ServerResponseInterface;
import com.langoor.app.blueshak.services.ServicesMethodsManager;
import com.langoor.app.blueshak.services.model.ErrorModel;
import com.langoor.app.blueshak.services.model.ProductModel;
import com.langoor.app.blueshak.services.model.StatusModel;
import com.langoor.app.blueshak.util.LocationService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.util.List;


public class SearchListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        ViewPagerEx.OnPageChangeListener{

    private Context context;
    private List<ProductModel> albumList;
    private   boolean is_clicked=false;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    public String item_address;
    private ImageLoader imageLoader;
    LocationService locServices;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        protected TextView item_price,item_location,item_name;
        private ImageView new_flag_image;
        protected ImageView image_iv,favarite;
        LinearLayout linearLayout;
        public MyViewHolder(View view) {
            super(view);
            item_price = (TextView) view.findViewById(R.id.item_price);
            image_iv= (ImageView) view.findViewById(R.id.product_image);
            new_flag_image= (ImageView) view.findViewById(R.id.is_new_item);
            favarite= (ImageView) view.findViewById(R.id.item_favirate);
            item_location= (TextView) view.findViewById(R.id.item_location);
            item_name= (TextView) view.findViewById(R.id.item_name);
        }
    }

    public SearchListAdapter(Context mContext, List<ProductModel> albumList) {
        this.context = mContext;
        this.albumList = albumList;
        this.item_address=GlobalFunctions.getSharedPreferenceString(mContext,GlobalVariables.CURRENT_LOCATION);
        imageLoader=new ImageLoader(mContext);


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.search_item_list_row_item_new, parent, false);
            return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder view_holder, int position) {


           final  MyViewHolder holder=(MyViewHolder)view_holder;
            final ProductModel obj = albumList.get(position);

            int price=0;
            if(!TextUtils.isEmpty(obj.getSalePrice()))
                price=(int)Float.parseFloat(obj.getSalePrice());

            holder.item_price.setText("$ "+price);
             holder.item_name.setText(obj.getName());
           /* holder.item_price.setGravity(Gravity.CENTER_HORIZONTAL);*/

            if(!TextUtils.isEmpty(obj.getCity()))
                holder.item_location.setText(obj.getCity());
            else
                holder.item_location.setVisibility(View.GONE);


            if(obj.is_bookmark())
                holder.favarite.setImageResource(R.drawable.like_full);

            if(obj.is_new())
                holder.new_flag_image.setVisibility(View.VISIBLE);
            else
                holder.new_flag_image.setVisibility(View.GONE);

            holder.image_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = ProductDetail.newInstance(context, obj,null, GlobalVariables.TYPE_MY_SALE);
                    context.startActivity(intent);
                }
            });
            holder.favarite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(GlobalFunctions.is_loggedIn(context)){
                        if(obj.is_bookmark()){
                            holder.favarite.setImageResource(R.drawable.like_border);
                            deleteBookmark(context,obj.getId());
                            obj.setIs_bookmark(false);
                        }else{
                            holder.favarite.setImageResource(R.drawable.like_full);
                            addBookmark(context,obj.getId());
                            obj.setIs_bookmark(true);
                        }
                    }else
                        showSettingsAlert();


                }
            });

           /* if(!TextUtils.isEmpty(obj.getItem_display_Image())){
                imageLoader.DisplayImage(obj.getItem_display_Image(),holder.image_iv);
            }*/
            String image=obj.getItem_display_Image();
            if(!TextUtils.isEmpty(obj.getItem_display_Image())){
                Picasso.with(context).load(obj.getItem_display_Image()).placeholder(R.drawable.placeholder_background).error(R.drawable.placeholder_background).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            int targetWidth = bitmap.getWidth();
                            int height = bitmap.getHeight();
                            double aspectRatio = (double) bitmap.getHeight() / (double) bitmap.getWidth();
                            int targetHeight = (int) (targetWidth * aspectRatio);
                            Bitmap result = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false);
                            holder.image_iv.setImageBitmap(result);
                            if (result != bitmap) {
                                // Same bitmap is returned if sizes are the same
                                bitmap.recycle();

                            }
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            holder.image_iv.setImageDrawable(errorDrawable);
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            holder.image_iv.setImageDrawable(placeHolderDrawable);
                        }
                    });
                }


    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }
    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    private void deleteBookmark(final Context context, String productID){
        /*GlobalFunctions.showProgress(context, "Bookmarking Product...");*/
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.deleteBookmark(context, productID, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                /*GlobalFunctions.hideProgress();*/
                if(arg0 instanceof StatusModel){
                    StatusModel statusModel = (StatusModel) arg0;
                    if(statusModel.isStatus()){
                        Toast.makeText(context, "Bookmark Removed", Toast.LENGTH_SHORT).show();
                    }
                }else if(arg0 instanceof ErrorModel){
                    ErrorModel errorModel = (ErrorModel) arg0;
                    String msg = errorModel.getError()!=null ? errorModel.getError() : errorModel.getMessage();
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void OnFailureFromServer(String msg) {
                /*GlobalFunctions.hideProgress();*/
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void OnError(String msg) {
               /* GlobalFunctions.hideProgress();*/
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        }, "Add Bookmarks");

    }
    private void addBookmark(final Context context, String productID){
        /*GlobalFunctions.showProgress(context, "Bookmarking Product...");*/
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.addBookmark(context, productID, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                /*GlobalFunctions.hideProgress();*/
                if(arg0 instanceof StatusModel){
                    StatusModel statusModel = (StatusModel) arg0;
                    if(statusModel.isStatus()){

                        Toast.makeText(context, "Added to bookmarks", Toast.LENGTH_SHORT).show();
                    }
                }else if(arg0 instanceof ErrorModel){
                    ErrorModel errorModel = (ErrorModel) arg0;
                    String msg = errorModel.getError()!=null ? errorModel.getError() : errorModel.getMessage();
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void OnFailureFromServer(String msg) {
                /*GlobalFunctions.hideProgress();*/
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void OnError(String msg) {
               /* GlobalFunctions.hideProgress();*/
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        }, "Add Bookmarks");

    }
    public void showSettingsAlert(){
        final com.langoor.app.blueshak.view.AlertDialog alertDialog = new com.langoor.app.blueshak.view.AlertDialog(context);
        alertDialog.setTitle("Login/Register");
        alertDialog.setMessage("Please Login/Register to continue");
        alertDialog.setPositiveButton("Continue", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent creategarrage = LoginActivity.newInstance(context,null,null);
                context.startActivity(creategarrage);
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
