package com.langoor.app.blueshak.garage;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.divrt.co.R;
import com.langoor.app.blueshak.Messaging.util.CommonUtil;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.services.model.CategoryListModel;
import com.langoor.app.blueshak.services.model.CreateProductModel;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class CreateProductsListAdapter extends ArrayAdapter<CreateProductModel> {

    private final List<CreateProductModel> list;
    private final LayoutInflater layoutInflater;
    private AdapterListener listener;
    private static int selected = -1;
    CategoryListModel clm;

    public CreateProductsListAdapter(LayoutInflater layoutInflater, List<CreateProductModel> list, AdapterListener listener) {
        super(layoutInflater.getContext(), R.layout.create_product_list_row_item, list);
        this.layoutInflater = layoutInflater;
        this.list = list;
        this.listener = listener;
    }

    static class ViewHolder {
        protected TextView name_tv, price_tv, availability_tv;
        protected ImageView image_iv,delete_iv, edit_iv;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CreateProductModel obj = list.get(position);
        View view = null;
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.create_product_list_row_item, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.name_tv = (TextView) view.findViewById(R.id.item_row_list_name_tv);
            viewHolder.price_tv = (TextView) view.findViewById(R.id.item_row_list_price_tv);
            viewHolder.availability_tv = (TextView) view.findViewById(R.id.item_row_list_availability_tv);

            viewHolder.image_iv = (ImageView) view.findViewById(R.id.item_row_list_image_iv);
            viewHolder.delete_iv = (ImageView) view.findViewById(R.id.list_row_item_delete_iv);

            viewHolder.edit_iv = (ImageView) view.findViewById(R.id.list_row_item_edit_iv);

            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.name_tv.setText(obj.getName());
        holder.price_tv.setText("Price "+ GlobalVariables.CURRENCY_NOTATION+" "+obj.getSalePrice());
       // holder.availability_tv.setText(obj.isAvailable() ? "Available" : "Out of Stock");

        holder.availability_tv.setText(obj.getCategory_string());
        holder.delete_iv.setTag(position);
        holder.edit_iv.setTag(position);


        if(obj.getImages()!=null){
            if(obj.getImages().size()>0){

                File f = new File(obj.getImages().get(0).getImage());
                /*Picasso.with(getContext()).
                        cancelRequest(holder.image_iv);*/
               /* Picasso.with(getContext())
                        .load(f)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .fit().centerCrop()
                        .into(holder.image_iv);*/
                if(obj.getImages().get(0)!=null)
                    if(!TextUtils.isEmpty(obj.getImages().get(0).getImage())){
                        holder.image_iv.setImageBitmap(GlobalFunctions.getBitmapFromBase64(obj.getImages().get(0).getImage()));
                     /*   holder.image_iv.resize(100,100).centerCrop();*/
                    }
            }

        }


        holder.delete_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                listener.onDelete(position);

            }
        });

        holder.edit_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                listener.onEdit(position);

            }
        });


        return view;
    }

  /*  public boolean IsBase64Encoded(String str)
    {
        try{
            // If no exception is caught, then it is possibly a base64 encoded string
            byte[] data = Convert.FromBase64String(str);
            // The part that checks if the string was properly padded to the
            // correct length was borrowed from d@anish's solution
            return (str.Replace(" ","").Length % 4 == 0);
        }
        catch
        {
            // If exception is caught, then it is not a base64 encoded string
            return false;
        }
    }*/

}
