package com.langoor.app.blueshak.horizontalListItems;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.divrt.co.R;
import com.langoor.app.blueshak.ImageCashing.ImageLoader;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.services.model.ProductModel;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

/**
 * Created by Sivasabharish Chinnaswamy on 17-07-2015.
 */
public class HorizontalListAdapter extends ArrayAdapter<ProductModel> {

    private static final String TAG = "HorizontalListAdapter";
    private final List<ProductModel> list;
    private final LayoutInflater layoutInflater;
    private ImageLoader imageLoader;

    static GlobalVariables globalVariables = new GlobalVariables();

    public HorizontalListAdapter(LayoutInflater layoutInflater, List<ProductModel> list) {
        super(layoutInflater.getContext(), R.layout.horizontal_list_items_row, list);
        this.layoutInflater = layoutInflater;
        this.list = list;
        imageLoader=new ImageLoader(getContext());
    }

    static class ViewHolder {
        protected TextView name, price;
        protected ImageView imageView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.horizontal_list_items_row, null);
            final ViewHolder viewHolder = new ViewHolder();
          /*  viewHolder.name = (TextView) view.findViewById(R.id.horizontal_list_item_text_name);*/
            viewHolder.price = (TextView) view.findViewById(R.id.horizontal_list_item_text_price);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.horizontal_list_item_image);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        final ViewHolder holder = (ViewHolder) view.getTag();

      /*  holder.name.setText(null);*/
        holder.price.setText(null);

      /*  holder.name.setText(list.get(position).getName());*/

      /*  int price=0;
        if(!TextUtils.isEmpty(list.get(position).getSalePrice()))
            price=(int)Float.parseFloat(list.get(position).getSalePrice());*/

        holder.price.setText(globalVariables.CURRENCY_NOTATION+" "+list.get(position).getSalePrice());

        List<String> imageURI = null;

        imageURI = list.get(position).getImage();
        Log.d(TAG, "ImageURL : " + imageURI);
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
                image_url=list.get(position).getItem_display_Image();
            }
        }
        if(!TextUtils.isEmpty(image_url)){
            Picasso.with(getContext()).load(image_url).placeholder(R.drawable.placeholder_background).error(R.drawable.placeholder_background).into(new Target() {
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

        return view;
    }
}
