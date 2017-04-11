package com.langoor.app.blueshak.home;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.divrt.co.R;
import com.langoor.app.blueshak.MainActivity;
import com.langoor.app.blueshak.garage.AddItems;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.services.ServerResponseInterface;
import com.langoor.app.blueshak.services.ServicesMethodsManager;
import com.langoor.app.blueshak.services.model.LocationModel;
import com.langoor.app.blueshak.services.model.CreateSalesModel;
import com.langoor.app.blueshak.services.model.ProductModel;
import com.langoor.app.blueshak.services.model.SalesModel;
import com.langoor.app.blueshak.view.AlertDialog;
import com.langoor.app.blueshak.view_sales.MapFragmentSales;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SaleFragment extends Fragment {

    public static final String TAG = "SaleFragment";
    private static final String SALES_FRAGMENT_SALES_DETAIL_BUNDLE_SERIALIZE = "salesFragmentSaleDetailsBundleSerialize";
    private static final String SALES_FRAGMENT_SALES_DETAIL_BUNDLE_FLAG = "salesFragmentSaleDetailsBundleFlag";
    private ListView listView;
    private ProductsListAdapter adapter;
    private SalesModel salesModelData;
    private String Flag = null;
    private DatePickerDialog toDatePickerDialog;

    private TextView saleName_tv = null, saleDate_tv = null,hgl_address1=null,sale_name=null;
    private ImageView saleDelete_iv = null, saleCalender_iv = null, edit_iv = null, move_iv=null,ic_options=null;
    private ImageButton options;

    private Button delete_item;
    private List<ProductModel> list = new ArrayList<ProductModel>();

    private Activity activity;
    private Context context;
    TextView no_sales;
  String title;
    public static SaleFragment newInstance(SalesModel salesModel, String flag){
        SaleFragment saleFragment = new SaleFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(SALES_FRAGMENT_SALES_DETAIL_BUNDLE_SERIALIZE, salesModel);
        bundle.putString(SALES_FRAGMENT_SALES_DETAIL_BUNDLE_FLAG, flag);
        saleFragment.setArguments(bundle);
        return saleFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.sales_fragment, container, false);
        activity = getActivity();
        context = getActivity();

        salesModelData = (SalesModel) getArguments().getSerializable(SALES_FRAGMENT_SALES_DETAIL_BUNDLE_SERIALIZE);
        Flag = getArguments().getString(SALES_FRAGMENT_SALES_DETAIL_BUNDLE_FLAG);
        no_sales = (TextView) view.findViewById(R.id.no_sales);
        listView = (ListView) view.findViewById(R.id.sales_fragment_list_view);
        adapter = new ProductsListAdapter(inflater, list,salesModelData.getSaleType());
        listView.setAdapter(adapter);

        if(salesModelData!=null){
            if(salesModelData.getSaleType().equalsIgnoreCase(GlobalVariables.TYPE_GARAGE))
                title= GlobalFunctions.getSentenceFormat(salesModelData.getName())+"'s Garage Sale";
            else
                title= GlobalFunctions.getSentenceFormat(salesModelData.getName())+"'s Multiple Sale";
            // SetHeader and set the list;
            setHeader(inflater);
            //Set lists products
            setList(salesModelData.getProducts());
        }

      /*  listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){position--;}
                Intent intent = ProductDetail.newInstance(context, list.get(position),salesModelData, GlobalVariables.TYPE_MY_SALE);
                startActivity(intent);
            }
        });*/


        return view;
    }
    @Override
    public void onResume() {
        if(salesModelData.getSaleType().equalsIgnoreCase(GlobalVariables.TYPE_GARAGE))
            MainActivity.setTitle("Garage Sale",0);
        else
            MainActivity.setTitle("Multiple Sale",0);

        super.onResume();
    }
    private void setHeader(LayoutInflater inflater){
        if(listView!=null && inflater != null){
            View headerView = inflater.inflate(R.layout.product_list_row_item_header, null, false);

            listView.addHeaderView(headerView,null,false);
           // ic_options=(ImageView)headerView.findViewById(R.id.ic_options);
            saleDate_tv = (TextView) headerView.findViewById(R.id.hgl_date);
            hgl_address1=(TextView) headerView.findViewById(R.id.hgl_address1);
            sale_name=(TextView) headerView.findViewById(R.id.sale_name);
            sale_name.setText(title);
            getAddressFromLatLng(activity,Double.parseDouble(salesModelData.getLatitude()),Double.parseDouble(salesModelData.getLongitude()));

            options = (ImageButton) headerView.findViewById(R.id.ic_options);

            options.setVisibility(View.GONE);

            System.out.println("####getAddress()#########"+getAddress());

            if(salesModelData.getStart_date()!=null){saleDate_tv.setText(salesModelData.getStart_date());}else{saleDate_tv.setText("");}

            if(salesModelData.getLatitude()!=null && salesModelData.getLongitude()!=null){
                String location_name= GlobalFunctions.getLocationNames(activity,Double.parseDouble(salesModelData.getLatitude()),Double.parseDouble(salesModelData.getLongitude()));
                hgl_address1.setText(getAddress());
            }

            hgl_address1.setText(getAddress());
            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open calender Image
                    PopupMenu popup = new PopupMenu(getActivity(), v);
                    popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            Toast.makeText(getContext(), "Clicked popup menu item " + item.getTitle(),
                                    Toast.LENGTH_SHORT).show();
                            if(item.getTitle().toString().equalsIgnoreCase(getActivity().getResources().getString(R.string.Edit_Sale))){

                            }else if(item.getTitle().toString().equalsIgnoreCase(getActivity().getResources().getString(R.string.Edit_Sale))){
// Open calender Image
                                final AlertDialog dialog = new AlertDialog(activity);
                                dialog.setTitle("Edit");
                                dialog.setIcon(R.drawable.ic_alert);
                                dialog.setIsCancelable(true);
                                dialog.setMessage("Are you sure Edit this Sale?");
                                dialog.setPositiveButton("Yes", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        openEditIntent(activity);
                                        dialog.dismiss();
                                    }
                                });

                                dialog.setNegativeButton("Cancel", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();

                            }else if(item.getTitle().toString().equalsIgnoreCase(getActivity().getResources().getString(R.string.end_sale))){

                                final AlertDialog dialog = new AlertDialog(activity);
                                dialog.setTitle("Delete");
                                dialog.setIcon(R.drawable.ic_alert);
                                dialog.setIsCancelable(true);
                                dialog.setMessage("Are you sure End this Sale?");
                                dialog.setPositiveButton("Yes", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        endSale(activity,salesModelData.getId());
                                        dialog.dismiss();
                                    }
                                });

                                dialog.setNegativeButton("Cancel", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                            }else if(item.getTitle().toString().equalsIgnoreCase(getActivity().getResources().getString(R.string.reschedule_sale))){

                                final AlertDialog dialog = new AlertDialog(activity);
                                dialog.setTitle("Delete");
                                dialog.setIcon(R.drawable.ic_alert);
                                dialog.setIsCancelable(true);
                                dialog.setMessage("Are you sure Reschedule this Sale?");
                                dialog.setPositiveButton("Yes", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int[] dateInts = GlobalFunctions.convertTexttoDate(salesModelData.getStart_date());
                                        setDateTimeField(dateInts[2],dateInts[1],dateInts[0]);
                                        dialog.dismiss();
                                    }
                                });

                                dialog.setNegativeButton("Cancel", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                            }/*else if(item.getTitle().toString().equalsIgnoreCase(getActivity().getResources().getString(R.string.delete_item))){

                                final AlertDialog dialog = new AlertDialog(activity);
                                dialog.setTitle("Delete");
                                dialog.setIcon(R.drawable.ic_alert);
                                dialog.setIsCancelable(true);
                                dialog.setMessage("Are You sure Delete this item?");
                                dialog.setPositiveButton("Yes", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        deleteSale(activity,salesModelData.getId());
                                        dialog.dismiss();
                                    }
                                });

                                dialog.setNegativeButton("Cancel", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                            }*/else if(item.getTitle().toString().equalsIgnoreCase(getActivity().getResources().getString(R.string.delete_sale))){

                              final AlertDialog dialog = new AlertDialog(activity);
                                dialog.setTitle("Delete");
                                dialog.setIcon(R.drawable.ic_alert);
                                dialog.setIsCancelable(true);
                                dialog.setMessage("Are you sure Delete this Sale?");
                                dialog.setPositiveButton("Yes", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        deleteSale(activity,salesModelData.getId());
                                        dialog.dismiss();
                                    }
                                });

                                dialog.setNegativeButton("Cancel", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                            }else if(item.getTitle().toString().equalsIgnoreCase(getActivity().getResources().getString(R.string.move_to_multiple))){
                                final AlertDialog dialog = new AlertDialog(activity);
                                dialog.setTitle("Move");
                                dialog.setIcon(R.drawable.ic_alert);
                                dialog.setIsCancelable(true);
                                dialog.setMessage("Are you sure you want to move this Sale to Multi Sale?");
                                dialog.setPositiveButton("Yes", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        moveSale(context, salesModelData.getId());
                                    }
                                });

                                dialog.setNegativeButton("Cancel", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();

                            }
                            return true;
                        }
                    });

                    popup.show();
                }
            });

        }

    }

    private void setList(List<ProductModel> list){
        if(list!=null){
            if(list.size()>0){
                no_sales.setVisibility(View.GONE);
                this.list.clear();
                this.list.addAll(list);
                if(adapter!=null){synchronized (adapter){adapter.notifyDataSetChanged();}}
            }else{
                no_sales.setVisibility(View.VISIBLE);
                no_sales.setText("No Products Found");
            }
        }
    }

    private void setDateTimeField(int year, int month, int day) {

        Calendar newCalendar = Calendar.getInstance();
        day = day==0? newCalendar.get(Calendar.DAY_OF_MONTH) : day;
        month = month==0? newCalendar.get(Calendar.MONTH) : month+1;
        year = year==0? newCalendar.get(Calendar.YEAR) : year;

        toDatePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                String newDateString = GlobalVariables.dateFormatter.format(newDate.getTime());
                changeDate(context, salesModelData.getId(), newDateString, salesModelData.getStart_time(), salesModelData.getEnd_time());
            }

        },year, month, day);

        toDatePickerDialog.show();
    }


    private void openEditIntent(Context context){
        if(context!=null&&salesModelData!=null){
            CreateSalesModel createSalesModel = new CreateSalesModel();
            createSalesModel.toObject(salesModelData, GlobalFunctions.getCategories(context));
           // MainActivity.replaceFragment(MapFragmentSales.newInstance(GlobalVariables.TYPE_MY_SALE+""), MapFragmentSales.TAG);
            Intent intent = AddItems.newInstance(context, createSalesModel, GlobalVariables.TYPE_MY_SALE);
            startActivity(intent);
        }
    }

    private void deleteSale(Context context, String saleID){
        GlobalFunctions.showProgress(context, "Deleting Sale...");
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.deleteSale(context, saleID, new ServerResponseInterface() {
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
    private void endSale(Context context, String saleID){
        GlobalFunctions.showProgress(context, "Ending Sale...");
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.endSale(context, saleID, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                Log.d(TAG, "onSuccess Response");
                MainActivity.replaceFragment(MySaleFragment.newInstance(GlobalVariables.TYPE_MY_SALE+""), MapFragmentSales.TAG);
             //   MainActivity.replaceFragment(MapFragmentSales.newInstance(GlobalVariables.TYPE_MY_SALE+""), MapFragmentSales.TAG);
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

    private void moveSale(Context context, String saleID){
        GlobalFunctions.showProgress(context, "Moving Sale...");
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.moveSaletoMultipleItem(context, saleID, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                Log.d(TAG, "onSuccess Response");
                MainActivity.replaceFragment(MySaleFragment.newInstance(GlobalVariables.TYPE_MY_SALE+""), MapFragmentSales.TAG);

              //  MainActivity.replaceFragment(MapFragmentSales.newInstance(GlobalVariables.TYPE_MY_SALE+""), MapFragmentSales.TAG);
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
        }, "Move Sale");
    }


    private void changeDate(Context context, String saleID, String startDate, String startTime, String endTime){
        GlobalFunctions.showProgress(context, "Rescheduling Sale...");
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.rescheduleSale(context, saleID, startDate, startTime, endTime, new ServerResponseInterface() {
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
    public String address;
    public void setAddress(String adress){
        this.address=adress;
    }
    public String getAddress(){
        return this.address;
    }
    private void getAddressFromLatLng(Context context,Double lat,Double lng){
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.getAddress(context,lat,lng,new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                Log.d(TAG, "onSuccess Response");
                LocationModel locationModel =(LocationModel)arg0;
                setAddress(locationModel);
                hgl_address1.setText(locationModel.getFormatted_address());
                Log.d(TAG, "#########locationModel.getFormatted_address###########"+ locationModel.getFormatted_address());

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

    }


}
