package com.langoor.app.blueshak.home;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.divrt.co.R;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.services.model.ProductModel;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductsListAdapter extends ArrayAdapter<ProductModel> {

    private final List<ProductModel> list;
    private final LayoutInflater layoutInflater;
    private static int selected = -1;
   String saletype=null;
    public ProductsListAdapter(LayoutInflater layoutInflater, List<ProductModel> list, String saletype) {
        super(layoutInflater.getContext(), R.layout.product_list_row_item, list);
        this.layoutInflater = layoutInflater;
        this.list = list;
        this.saletype=saletype;
    }

    static class ViewHolder {
        protected TextView name_tv, price_tv, availability_tv,shippable_sale_name,garage_sale_name,pick_up_name;;
        protected ImageView image_iv, sale_iv, delivery_iv, pick_iv;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProductModel obj = list.get(position);
        View view = null;
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.product_list_row_item, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.name_tv = (TextView) view.findViewById(R.id.item_row_list_name_tv);
            viewHolder.price_tv = (TextView) view.findViewById(R.id.item_row_list_price_tv);
            viewHolder.availability_tv = (TextView) view.findViewById(R.id.item_row_list_availability_tv);
            viewHolder.shippable_sale_name= (TextView) view.findViewById(R.id.shippable_sale_name);
            viewHolder.garage_sale_name= (TextView) view.findViewById(R.id.garage_sale_name);
            viewHolder.pick_up_name= (TextView) view.findViewById(R.id.pick_up_name);

            viewHolder.image_iv = (ImageView) view.findViewById(R.id.item_row_list_image_iv);

            viewHolder.delivery_iv = (ImageView) view.findViewById(R.id.list_row_item_delivery_iv);
            viewHolder.pick_iv = (ImageView) view.findViewById(R.id.list_row_item_pick_iv);
            viewHolder.sale_iv = (ImageView) view.findViewById(R.id.list_row_item_sale_iv);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        final ViewHolder holder = (ViewHolder) view.getTag();
        if(!TextUtils.isEmpty(obj.getName()))
            holder.name_tv.setText(GlobalFunctions.getSentenceFormat(obj.getName()));

      /*  holder.name_tv.setText(obj.getName());*/
        holder.price_tv.setText(GlobalVariables.CURRENCY_NOTATION+" "+obj.getSalePrice());
        holder.availability_tv.setText(obj.isAvailable() ? "Available" : "Sold");
        int grey2=getContext().getResources().getColor(R.color.grey_2);
        int black=getContext().getResources().getColor(R.color.black);

        holder.delivery_iv.setImageResource(obj.isShipable()? R.drawable.shippingon : R.drawable.shippingoff);
        if(saletype!=null){
            if(saletype.equalsIgnoreCase(GlobalVariables.TYPE_GARAGE)){
              /*  holder.sale_iv.setImageResource(obj.isActive()? R.drawable.garageon : R.drawable.garageoff);
              */
                holder.sale_iv.setImageResource(R.drawable.garageon);
                holder.garage_sale_name.setText("Garage Sale");
                holder.garage_sale_name.setTextColor(black);
           /*     holder.garage_sale_name.setTextColor(obj.isActive()?black:grey2);*/
            }else{
                /*holder.sale_iv.setImageResource(obj.isActive()? R.drawable.multion : R.drawable.multioff);*/
                holder.sale_iv.setImageResource(R.drawable.multion);
                holder.garage_sale_name.setText("Multiple Sale");
                holder.garage_sale_name.setTextColor(black);
             /*   holder.garage_sale_name.setTextColor(obj.isActive()?black:grey2);*/
            }

        }
        holder.pick_iv.setImageResource(obj.isPickup()? R.drawable.ic_pickup_on : R.drawable.ic_pickup_off);



        holder.shippable_sale_name.setTextColor(obj.isShipable()? black:grey2);
   /*     holder.garage_sale_name.setTextColor(obj.isActive()?black:grey2);*/
        holder.pick_up_name.setTextColor(obj.isPickup()?black:grey2);

     /*   for(int i=0;1<obj.getImage().size();i++){

        }*/
        if(obj.getImage()!=null){
            if(obj.getImage().size()>0) {
                Picasso.with(getContext()).
                        cancelRequest(holder.image_iv);
                Picasso.with(getContext())
                        .load(obj.getImage().get(0))
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .fit().centerCrop()
                        .into(holder.image_iv);
            }
        }
        return view;
    }
}
