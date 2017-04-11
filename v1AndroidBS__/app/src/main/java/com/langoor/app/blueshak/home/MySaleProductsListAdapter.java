package com.langoor.app.blueshak.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
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
import com.langoor.app.blueshak.MainActivity;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.services.ServerResponseInterface;
import com.langoor.app.blueshak.services.ServicesMethodsManager;
import com.langoor.app.blueshak.services.model.LocationModel;
import com.langoor.app.blueshak.services.model.ProductModel;
import com.langoor.app.blueshak.util.LocationListener;
import com.langoor.app.blueshak.util.LocationService;
import com.langoor.app.blueshak.view.AlertDialog;
import com.langoor.app.blueshak.view_sales.MapFragmentSales;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MySaleProductsListAdapter extends ArrayAdapter<ProductModel> implements LocationListener {
    public static final String TAG = "MySaleProdtAdapter";
    private final List<ProductModel> list;
    private final LayoutInflater layoutInflater;
    private static int selected = -1;
    LocationService locServices;
    private final Activity context;
    String sale_id;



    public MySaleProductsListAdapter(LayoutInflater layoutInflater, List<ProductModel> list, Activity context, String sale_id) {
        super(layoutInflater.getContext(), R.layout.product_list_row_item, list);
        this.layoutInflater = layoutInflater;
        this.list = list;
        this.context=context;
        sale_id=sale_id;
        locServices = new LocationService(context);
       // locServices.setListener(co);
        if(!locServices.canGetLocation()){locServices.showSettingsAlert();}
    }

    static class ViewHolder {
        protected TextView name_tv, price_tv, availability_tv;
        protected ImageView image_iv, sale_iv, delivery_iv, pick_iv,deleteImageView,list_row_item_share_iv;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       final ProductModel obj = list.get(position);
        View view = null;
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.listviewcell_my_sale_item, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.name_tv = (TextView) view.findViewById(R.id.item_row_list_name_tv);
            viewHolder.price_tv = (TextView) view.findViewById(R.id.item_row_list_price_tv);
            viewHolder.availability_tv = (TextView) view.findViewById(R.id.item_row_list_availability_tv);
            viewHolder.deleteImageView = (ImageView) view.findViewById(R.id.list_row_item_delete_iv);
            viewHolder.image_iv = (ImageView) view.findViewById(R.id.item_row_list_image_iv);
            viewHolder.list_row_item_share_iv = (ImageView) view.findViewById(R.id.list_row_item_share_iv);

          /*  viewHolder.delivery_iv = (ImageView) view.findViewById(R.id.list_row_item_delivery_iv);
            viewHolder.pick_iv = (ImageView) view.findViewById(R.id.list_row_item_pick_iv);
            viewHolder.sale_iv = (ImageView) view.findViewById(R.id.list_row_item_sale_iv);*/
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        final ViewHolder holder = (ViewHolder) view.getTag();

        if(!TextUtils.isEmpty(obj.getName()))
            holder.name_tv.setText(GlobalFunctions.getSentenceFormat(obj.getName()));

       /* holder.name_tv.setText(obj.getName());*/
        holder.price_tv.setText(GlobalVariables.CURRENCY_NOTATION+" "+obj.getSalePrice());
        holder.availability_tv.setText(obj.isAvailable() ? "Available" : "Sold");
        final ProductModel product_model=obj;
        getAddressFromLatLng(context,Double.parseDouble(obj.getLatitude()),Double.parseDouble(obj.getLongitude()));
       /* holder.delivery_iv.setImageResource(obj.isShipable()? R.drawable.ic_shipping_on : R.drawable.ic_shipping_off);
        holder.sale_iv.setImageResource(obj.isActive()? R.drawable.ic_garage_on : R.drawable.ic_garage_off);
        holder.pick_iv.setImageResource(obj.isPickup()? R.drawable.ic_pickup_on : R.drawable.ic_pickup_off);
*/
        final String product_id=obj.getId();
        final String product_name="Product Name :"+obj.getName();
        final String product_description="Product Description :"+obj.getDescription();
        String image_url=null;
        if(obj.getImage()!=null)
            if(obj.getImage().size()>0)
                image_url="Product Image Url :"+obj.getImage().get(0);

        final String product_image_url=image_url;
        final String product_location="Sale Location :"+getAddress();
        final String product_Id_for_share="Product Id :"+obj.getId();
        final String product_duration="Sale Duration :"+obj.getStartDate()+" to"+obj.getEndDate();



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
        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog alertDialog = new AlertDialog(context);
                alertDialog.setCancelable(true);
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setTitle(context.getResources()
                        .getString(R.string.app_name));
                alertDialog.setMessage("You want to delete this item image?");
                alertDialog.setPositiveButton("yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteSaleItem(product_id);
                        alertDialog.dismiss();
                    }
                });

                alertDialog.setNegativeButton("cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        });
        holder.list_row_item_share_iv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final AlertDialog alertDialog = new AlertDialog(context);
                alertDialog.setCancelable(true);
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setTitle(context.getResources()
                        .getString(R.string.app_name));
                alertDialog.setMessage("You want to Share this item?");
                alertDialog.setPositiveButton("yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                     /*   shareItem(product_model);*/
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Product Details");
                        shareIntent.putExtra(Intent.EXTRA_TEXT,product_name+"\n"+product_Id_for_share+"\n"+
                                product_description+"\n"+product_duration+"\n"+product_image_url+"\n"+product_location);
                        context.startActivity(Intent.createChooser(shareIntent, "Share this product using"));
                        alertDialog.dismiss();
                    }
                });

                alertDialog.setNegativeButton("cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        });
        return view;
    }

    private void deleteSaleItem(String productId){
        GlobalFunctions.showProgress(context, "Deleting Sale item...");
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.deleteSaleItem(context,sale_id,productId,new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                Log.d(TAG, "onSuccess Response");
                MainActivity.replaceFragment(MySaleFragment.newInstance(GlobalVariables.TYPE_MY_SALE+""), MapFragmentSales.TAG);

                // MainActivity.replaceFragment(MapFragmentSales.newInstance(GlobalVariables.TYPE_MY_SALE+""), MapFragmentSales.TAG);
            }

            @Override
            public void OnFailureFromServer(String msg) {
                GlobalFunctions.hideProgress();
                Log.d(TAG, msg);
            }

            @Override
            public void OnError(String msg) {
                GlobalFunctions.hideProgress();
                Log.d(TAG, msg);
            }
        }, "Delete Sale");
    }
    private void shareItem(ProductModel productModel){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        if(productModel.getImage().size()>0)
            shareIntent.putExtra(Intent.EXTRA_TEXT,"Product Image Url :"+productModel.getImage().get(0));
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Product Name :"+productModel.getName());
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Product Description :"+productModel.getDescription());
        shareIntent.putExtra(Intent.EXTRA_TEXT,"Sale Duration :"+productModel.getStartDate()+" to"+productModel.getEndDate());
        shareIntent.putExtra(Intent.EXTRA_TEXT,"Sale Location :"+getAddress());

        context.startActivity(Intent.createChooser(shareIntent, "Share this product using"));
    }

    @Override
    public void onLocationChanged(Location loc) {
        if(loc!=null){
            locServices.removeListener();
        }else{
            Toast.makeText(context, "Fetching Location", Toast.LENGTH_SHORT).show();
        }

    }
    public String salller_address;
    public void setAddress(String adress){
        this.salller_address=adress;
    }
    public String getAddress(){
        return this.salller_address;
    }
    private void getAddressFromLatLng(Context context,Double lat,Double lng){

        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.getAddress(context, lat,lng, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {

                Log.d(TAG, "onSuccess Response");
                LocationModel locationModel =(LocationModel)arg0;
                setAddress(locationModel);

            }

            @Override
            public void OnFailureFromServer(String msg) {

                Log.d(TAG, msg);
            }

            @Override
            public void OnError(String msg) {

                Log.d(TAG, msg);
            }
        }, "Delete Sale");
    }
    public void setAddress(LocationModel locationModel){
        setAddress(locationModel.getFormatted_address());
        // location.setText(locationModel.getFormatted_address());
    }
    @Override
    public void onProviderEnabled(String provider) {

    }
}
