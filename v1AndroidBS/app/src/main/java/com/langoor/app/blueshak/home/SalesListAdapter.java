package com.langoor.app.blueshak.home;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.divrt.co.R;
import com.langoor.app.blueshak.MainActivity;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.services.model.ProductModel;
import com.langoor.app.blueshak.services.model.SalesModel;
import com.langoor.app.blueshak.view.CustomSliderTextView;
import com.vlonjatg.progressactivity.ProgressActivity;

import java.util.ArrayList;
import java.util.List;

public class SalesListAdapter extends ArrayAdapter<SalesModel> implements /*BaseSliderView.OnSliderClickListener,*/ ViewPagerEx.OnPageChangeListener{

    private final List<SalesModel> list;
    private final LayoutInflater layoutInflater;
    Context context;


    public SalesListAdapter(LayoutInflater layoutInflater, List<SalesModel> list, Activity activity) {
        super(layoutInflater.getContext(), R.layout.sale_list_row_item, list);
        this.layoutInflater = layoutInflater;
        this.list = list;
        this.context=activity;
    }

    static class ViewHolder {
        protected TextView name_tv, items_tv, date_tv, distance_tv,view_counts;
    //    protected ImageView image_iv;
        protected ImageView image_iv;
        protected SliderLayout mProductSlider;
        protected ProgressActivity progressActivity;
        protected PagerIndicator mPagerIndicator;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       final SalesModel obj = list.get(position);
        View view = null;
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.sale_list_row_item, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.name_tv = (TextView) view.findViewById(R.id.sale_item_row_list_name_tv);
            viewHolder.items_tv = (TextView) view.findViewById(R.id.sale_item_row_list_items_no_tv);
            viewHolder.date_tv = (TextView) view.findViewById(R.id.sales_item_row_list_date_tv);
            viewHolder.distance_tv = (TextView) view.findViewById(R.id.sales_item_row_list_distance_tv);
            viewHolder.progressActivity = (ProgressActivity) view.findViewById(R.id.products_details_progressActivity);
            viewHolder.mProductSlider = (SliderLayout) view.findViewById(R.id.product_detail_slider);
            viewHolder.view_counts= (TextView) view.findViewById(R.id.item_view_counts);
          /*  viewHolder.mPagerIndicator = (PagerIndicator)view.findViewById(R.id.product_detail_custom_indicator);
*/
         /*   viewHolder.image_iv = (ImageView) view.findViewById(R.id.saleimage);*/

            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        final ViewHolder holder = (ViewHolder) view.getTag();
        if(!TextUtils.isEmpty(obj.getName()))
            holder.name_tv.setText(GlobalFunctions.getSentenceFormat(obj.getName()));

      /*  holder.name_tv.setText(obj.getName());*/
        String title="";
        if(obj.getProducts()!=null)
            if(obj.getProducts().size()==1)
                title=obj.getProducts().size()+" Item For Sale";
            else
                title=obj.getProducts().size()+" Items For Sale";

        holder.items_tv.setText(title);
        holder.date_tv.setText(obj.getStart_date());
       /* holder.distance_tv.setText(obj.getDistanceAway()+" "+getContext().getString(R.string.milesAway));*/
        holder.distance_tv.setText(obj.getDistanceAway()+" "+getContext().getString(R.string.milesAway));
        holder.view_counts.setText(obj.getViews());
        holder.distance_tv.setVisibility(obj.getDistanceAway()==null?View.GONE:View.VISIBLE);

        holder.mProductSlider.removeAllSliders();
        ArrayList<String> displayImageURL = new ArrayList<String>();
        displayImageURL.clear();

        for(ProductModel productModel:obj.getProducts()){
            if(productModel.getImage().size()>0)
                for (int i=0;i<productModel.getImage().size();i++){
                    displayImageURL.add(productModel.getImage().get(i));
                }
        }


        //displayImageURL.addAll(displayImageURL);

        for(int i=0;i<displayImageURL.size()&&displayImageURL.get(i)!=null;i++){
            CustomSliderTextView textSliderView = new CustomSliderTextView(context);
            // initialize a SliderLayout
            textSliderView
                    .description(1+"")
                    .image(displayImageURL.get(i).toString());
                 /*   .setScaleType(BaseSliderView.ScaleType.CenterInside);*/
                   /* .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView slider) {
                            String signed_user_user_id= GlobalFunctions.getSharedPreferenceString(context, GlobalVariables.SHARED_PREFERENCE_BS_ID);
                            String seller_user_id=obj.getUserID();
                            if(signed_user_user_id!=null && seller_user_id!=null){
                                if(Integer.parseInt(seller_user_id) == Integer.parseInt(signed_user_user_id)) {
                                    MainActivity.addFragment(MySaleProductsFragment.newInstance(obj,obj.getSaleType()), "MySaleProductsFragment");

                                }else{
                                    MainActivity.addFragment(SaleFragment.newInstance(obj,obj.getSaleType()), SaleFragment.TAG);
                                }
                            }


                        }
                    });*/
            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", 1 + "");

            holder.mProductSlider.addSlider(textSliderView);
        }

      /*  holder.mProductSlider.setPresetTransformer(SliderLayout.Transformer.ZoomOutSlide);*/
        holder.mProductSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
      /*  if(holder.mPagerIndicator!=null){holder.mProductSlider.setCustomIndicator(holder.mPagerIndicator);}
      */  holder.mProductSlider.setCustomAnimation(new DescriptionAnimation());
        holder.mProductSlider.setDuration(4000);
        holder.mProductSlider.addOnPageChangeListener(this);
        holder.mProductSlider.stopAutoCycle();

     /*   if(obj.getProducts()!=null){
            if(obj.getProducts().size()>0)
                if(obj.getProducts().get(0)!=null){
                    if(obj.getProducts().get(0).getImage()!=null){
                        Picasso.with(getContext()).
                                cancelRequest(holder.image_iv);
                        String Image_url=null;
                        if(obj.getProducts().size()>0)
                            if(obj.getProducts().get(0).getImage().size()>0)
                                Image_url=obj.getProducts().get(0).getImage().get(0);
                        System.out.println("####Image_url#######"+Image_url);
                        Picasso.with(getContext())
                                .load(Image_url)
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .fit().centerCrop()
                                .into(holder.image_iv);
                    }
                }
        }*/

        return view;
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

   /* @Override
    public void onSliderClick(BaseSliderView baseSliderView) {
      *//*  holder.mProductSlider.getCurrentPosition();*//*
       *//* ArrayList<String> imageURLs = productDetailLists.get(selectedPosition).getImageURLS();
        Log.d(TAG,"Product:"+imageURLs);
        ProductOpenZoom productOpenZoom = new ProductOpenZoom(context);
        productOpenZoom.setProductImageList(imageURLs);
        productOpenZoom.show();*//*

    }*/





}
