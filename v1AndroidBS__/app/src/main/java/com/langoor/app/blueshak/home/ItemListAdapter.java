package com.langoor.app.blueshak.home;

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
import com.langoor.app.blueshak.ImageCashing.ImageLoader;
import com.langoor.app.blueshak.PickLocation;
import com.langoor.app.blueshak.filter.FilterActivity;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.item.ProductDetail;
import com.langoor.app.blueshak.login.LoginActivity;
import com.langoor.app.blueshak.profile.ProfileActivity;
import com.langoor.app.blueshak.services.ServerResponseInterface;
import com.langoor.app.blueshak.services.ServicesMethodsManager;
import com.langoor.app.blueshak.services.model.ErrorModel;
import com.langoor.app.blueshak.services.model.ProductModel;
import com.langoor.app.blueshak.services.model.StatusModel;
import com.langoor.app.blueshak.util.LocationService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.util.List;


public class ItemListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        ViewPagerEx.OnPageChangeListener{
    private ImageLoader imgLoader;
    private Context context;
    private List<ProductModel> albumList;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    public String item_address;
    LocationService locServices;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        protected TextView item_price;
        private ImageView new_flag_image;
        protected ImageView image_iv,favarite;
        LinearLayout linearLayout;
        public MyViewHolder(View view) {
            super(view);
            item_price = (TextView) view.findViewById(R.id.item_price);
            image_iv= (ImageView) view.findViewById(R.id.product_image);
            new_flag_image= (ImageView) view.findViewById(R.id.is_new_item);
            favarite= (ImageView) view.findViewById(R.id.item_favirate);
        }
    }

    class VHHeader extends RecyclerView.ViewHolder{
        TextView location,filter_tag;
        ImageView location_arrow,filter;
        public VHHeader(View itemView) {
            super(itemView);
            location = (TextView)itemView.findViewById(R.id.location);
            filter_tag = (TextView)itemView.findViewById(R.id.filter_tag);
            location_arrow=(ImageView)itemView.findViewById(R.id.location_arrow);
            filter=(ImageView)itemView.findViewById(R.id.filter);
        }
    }

    public ItemListAdapter(Context mContext, List<ProductModel> albumList) {
        this.context = mContext;
        this.albumList = albumList;
        this.item_address=GlobalFunctions.getSharedPreferenceString(mContext,GlobalVariables.CURRENT_LOCATION);
        imgLoader = new ImageLoader(mContext);


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       /* if(viewType == TYPE_HEADER){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_row_item_header, parent, false);
            v.setVisibility(View.GONE);
            return  new VHHeader(v);
        }else if(viewType == TYPE_ITEM){*/
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_row_item_new, parent, false);
            return new MyViewHolder(itemView);
       /* }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
*/    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder view_holder, int position) {

        if(view_holder instanceof VHHeader){
            VHHeader VHheader = (VHHeader)view_holder;
            StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setFullSpan(true);
            VHheader.itemView.setLayoutParams(layoutParams);
            if(TextUtils.isEmpty(item_address))
                item_address ="Pick Location";

            String current_location="";
            if(!item_address.equalsIgnoreCase("Pick Location")){
                current_location="Listing near "+item_address;
                SpannableString ss = new SpannableString(current_location);
                ss.setSpan(new MyClickableSpan(),12,current_location.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                VHheader.location.setText(ss);
                VHheader.location.setMovementMethod(LinkMovementMethod.getInstance());
            }else{
                VHheader.location.setText(item_address);VHheader.location.setTextColor(context.getResources().getColor(R.color.tab_selected));
                VHheader.location.setGravity(Gravity.CENTER);

            }
            VHheader.location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=PickLocation.newInstance(context,GlobalVariables.TYPE_HOME);
                    context.startActivity(intent);
                }
            });
            VHheader.filter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   context.startActivity(new Intent(context, FilterActivity.class));
                }
            });
            VHheader.filter_tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, FilterActivity.class));
                }
            });

            VHheader.location_arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=PickLocation.newInstance(context,GlobalVariables.TYPE_HOME);
                    context.startActivity(intent);
                }
            });

        }else if(view_holder instanceof MyViewHolder){
           final  MyViewHolder holder=(MyViewHolder)view_holder;
          /*  final ProductModel obj = albumList.get(position-1);*/
            final ProductModel obj = albumList.get(position);
           /* int price=0;
            if(!TextUtils.isEmpty(obj.getSalePrice()))
                price=(int)Float.parseFloat(obj.getSalePrice());*/

            holder.item_price.setText("$ "+obj.getSalePrice());

            if(obj.is_bookmark())
                holder.favarite.setImageResource(R.drawable.like_full);
            else
                holder.favarite.setImageResource(R.drawable.like_border);
            if(obj.is_new())
                holder.new_flag_image.setVisibility(View.VISIBLE);
            else
                holder.new_flag_image.setVisibility(View.GONE);

            holder.image_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 /*  getItemInfo(obj.getId());*/
                    Intent intent = ProductDetail.newInstance(context,obj,null,GlobalVariables.TYPE_MY_SALE);
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
            String item_image=obj.getItem_display_Image();
          /*  if(!TextUtils.isEmpty(item_image)){
                imgLoader.DisplayImage(item_image,holder.image_iv);
            }*/

            if(!TextUtils.isEmpty(item_image)){
                Picasso.with(context).load(item_image).placeholder(R.drawable.placeholder_background).error(R.drawable.placeholder_background).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            int targetWidth = bitmap.getWidth();
                            double aspectRatio = (double) bitmap.getHeight() / (double) bitmap.getWidth();
                            int targetHeight = (int) (targetWidth * aspectRatio);
                            Bitmap result = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false);
                            holder.image_iv.setImageBitmap(result);
                            /*if (result != bitmap) {
                                // Same bitmap is returned if sizes are the same
                                bitmap.recycle();

                            }*/
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

    //    need to override this method
    @Override
    public int getItemViewType(int position) {
        if(isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position)
    {
        return position == 0;
    }
    class MyClickableSpan extends ClickableSpan { //clickable span
        public void onClick(View textView) {

        }
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(context.getResources().getColor(R.color.tab_selected));//set text color
          /*  ds.setUnderlineText(true);*/
            //ds.setStyle(Typeface.BOLD);
          /*  ds.setTypeface(Typeface.DEFAULT_BOLD);*/
        }
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
    public void add(List<ProductModel> items) {
        int previousDataSize = this.albumList.size();
        this.albumList.addAll(items);
        notifyItemRangeInserted(previousDataSize, items.size());
    }
    private void getItemInfo(String product_id){
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.getItemInfo(context, product_id, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                ProductModel productModel = (ProductModel) arg0;
                Intent intent = ProductDetail.newInstance(context,productModel,null, GlobalVariables.TYPE_MY_SALE);
                context.startActivity(intent);
              }

            @Override
            public void OnFailureFromServer(String msg) {

            }

            @Override
            public void OnError(String msg) {

            }
        }, "GetItemInfo onSuccess Response");

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
