package com.langoor.app.blueshak.garage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.divrt.co.R;
import com.langoor.app.blueshak.AppController;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.home.EndlessRecyclerOnScrollListener;
import com.langoor.app.blueshak.home.ItemListAdapter;
import com.langoor.app.blueshak.home.ItemListFragment;
import com.langoor.app.blueshak.services.ServerResponseInterface;
import com.langoor.app.blueshak.services.ServicesMethodsManager;
import com.langoor.app.blueshak.services.model.CreateProductModel;
import com.langoor.app.blueshak.services.model.CreateSalesModel;
import com.langoor.app.blueshak.services.model.ErrorModel;
import com.langoor.app.blueshak.services.model.HomeListModel;
import com.langoor.app.blueshak.services.model.MakeOfferModel;
import com.langoor.app.blueshak.services.model.MyItemListAvailableModel;
import com.langoor.app.blueshak.services.model.MyItemListModel;
import com.langoor.app.blueshak.services.model.ProductModel;
import com.langoor.app.blueshak.services.model.SalesListModel;
import com.langoor.app.blueshak.services.model.SalesModel;
import com.langoor.app.blueshak.services.model.StatusModel;
import com.langoor.app.blueshak.util.LocationListener;
import com.langoor.app.blueshak.util.LocationService;
import java.util.ArrayList;
import java.util.List;


public class MyItems extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "MyItems";
    private static Context context;
    private TextView no_sales;
    private static Activity activity;
    public static final String SALE_DETAILS_KEY = "SaleDetailsKey";
    public static final String MY_ITEMS_ITEMS_AVAILABLE = "MyItemsAvailableBundleKey";
    public static final String  FROM_KEY= "from_key";
    /*private SwipeRefreshLayout swipeRefreshLayout;*/
    private RecyclerView recyclerView;
    private List<ProductModel> product_list = new ArrayList<ProductModel>();
    private ArrayList<ProductModel> selected_list = new ArrayList<ProductModel>();
    private MyItemListAdapter adapter;
    private Toolbar toolbar;
    private Button done;
    private CreateSalesModel sm;
    int tag;
    private  boolean item_available=false;
    public static Intent newInstance(Context context, boolean item_available){
        Intent mIntent = new Intent(context, MyItems.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(MY_ITEMS_ITEMS_AVAILABLE, item_available);
        mIntent.putExtras(bundle);
        return mIntent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_items);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context= this;
        activity= this;
        if(getIntent().hasExtra(MY_ITEMS_ITEMS_AVAILABLE))
           item_available=getIntent().getBooleanExtra(MY_ITEMS_ITEMS_AVAILABLE,false);
        if(!item_available)
            callAddItem();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
       /* swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(context.getResources().getColor(R.color.brandColor),
                context.getResources().getColor(R.color.tab_selected),
                context.getResources().getColor(R.color.darkorange),
                context.getResources().getColor(R.color.brandColor));*/
        no_sales = (TextView) findViewById(R.id.no_sales);
        done=(Button)findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItems();
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new MyItemListAdapter(context, product_list,true);
        LinearLayoutManager linearLayoutManagerVertical =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManagerVertical);
       /* recyclerView.addItemDecoration(new SpacesItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.space)));*/
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
      /*  recyclerView.setOnScrollListener(new MyScrollListener(activity) {
            @Override
            public void onMoved(int distance) {
                toolbar.setTranslationY(-distance);
            }
        });*/
       /* recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        ProductModel obj=product_list.get(position);
                        view.setActivated(true);
                        if(!obj.is_selected()){
                            obj.setIs_selected(true);
                        }else{
                            obj.setIs_selected(false);
                        }


                    }
                })
        );*/
        /*recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(
                linearLayoutManagerVertical) {
            @Override
            public void onLoadMore(int current_page) {

            }
        });*/
        /*setThisPage();*/

    }
    @Override
    public void onRefresh() {
        setThisPage();
    }

    private void setThisPage(){
       /* if(GlobalFunctions.getItemsList(context,GlobalVariables.SELECTED_ITEMS_LIST)!=null){
            product_list.clear();
            product_list=GlobalFunctions.getItemsList(context,GlobalVariables.SELECTED_ITEMS_LIST);
            Toast.makeText(context,product_list.size()+"",Toast.LENGTH_LONG).show();
            Log.d(TAG,product_list.toString());
            synchronized (adapter){adapter.notifyDataSetChanged();}
        }else*/
            /*getMySales(context);*/
           /* getMySales(context);*/
         /*   getMyItemList(context);*/
        getMyItemList(context);
      /*  getMySales(context);*/



    }

    private static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private final int space;
        public SpacesItemDecoration(int space) {
            this.space = space;
        }
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = 2 * space;
            int pos = parent.getChildAdapterPosition(view);
            outRect.left = space;
            outRect.right = space;
            if (pos < 2)
                outRect.top = 2 * space;
        }
    }
    public abstract class MyScrollListener extends RecyclerView.OnScrollListener {
        private int toolbarOffset = 0;
        private int toolbarHeight;

        public MyScrollListener(Context context) {
            int[] actionBarAttr = new int[] { android.R.attr.actionBarSize };
            TypedArray a = context.obtainStyledAttributes(actionBarAttr);
            toolbarHeight = (int) a.getDimension(0, 0) + 10;
            a.recycle();
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            clipToolbarOffset();
            onMoved(toolbarOffset);

            if((toolbarOffset < toolbarHeight && dy>0) || (toolbarOffset > 0 && dy<0)) {
                toolbarOffset += dy;
            }
        }

        private void clipToolbarOffset() {
            if(toolbarOffset > toolbarHeight) {
                toolbarOffset = toolbarHeight;
            } else if(toolbarOffset < 0) {
                toolbarOffset = 0;
            }
        }

        public abstract void onMoved(int distance);
    }
    private void getMySales(final Context context){
        GlobalFunctions.showProgress(context,"Loading..");
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.getMySalesList(context, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                /*swipeRefreshLayout.setRefreshing(false);*/
                no_sales.setVisibility(View.GONE);
                Log.d(TAG, "onSuccess Response");
                done.setVisibility(View.VISIBLE);
                SalesListModel salesListModel = (SalesListModel)arg0;
                String str = salesListModel.toString();
                setValues(salesListModel);
                Log.d(TAG, str);
                //addItems();
            }

            @Override
            public void OnFailureFromServer(String msg) {
                GlobalFunctions.hideProgress();
               /* swipeRefreshLayout.setRefreshing(false);*/
                no_sales.setText("No Products Found");
                //  GlobalFunctions.hideProgress();
                Log.d(TAG, msg);
            }

            @Override
            public void OnError(String msg) {
                GlobalFunctions.hideProgress();
                /* swipeRefreshLayout.setRefreshing(false);*/
                no_sales.setText("No Products Found");
                Log.d(TAG, msg);
            }
        }, "List Sales");

    }
    private void setValues(SalesListModel model){
        String [] ids=new String[1000];
        if(model!=null){
           String data= GlobalFunctions.getSharedPreferenceString(context,GlobalVariables.SELECTED_ITEMS_LIST);
            if(!TextUtils.isEmpty(data)&&data.contains(",")){
                ids=data.split(",");
            }
            String str = "";
            SalesListModel salesListModel = model;
            List<SalesModel> productModels = salesListModel.getSalesList();
            List<ProductModel> temp_list = new ArrayList<ProductModel>();
            if(productModels!=null){
                if(productModels.size()>0){
                    product_list.clear();
                    temp_list.clear();
                    for (SalesModel salesModel:productModels){
                        temp_list.addAll(salesModel.getProducts());
                    }
                    for (ProductModel productModel:temp_list){
                        if(data!=null && ids!=null){
                            for (int i=0;i<ids.length;i++){
                              /*  Log.d(TAG+"##ids#########",ids[i]);*/
                                Log.d(TAG+"##data#########",data);
                                if(ids[i]!=null)
                                    if(Integer.parseInt(productModel.getId())==Integer.parseInt(ids[i])){
                                        productModel.setIs_selected(true);
                                    }
                            }
                        }
                        product_list.add(productModel);
                    }
                    synchronized (adapter){adapter.notifyDataSetChanged();}

                    if(!item_available && product_list.size()>0){
                        product_list.get(0).setIs_selected(true);
                        String selected_item =product_list.get(0).getId()+",";
                        GlobalFunctions.setSharedPreferenceString(context,GlobalVariables.SELECTED_ITEMS_LIST,selected_item);
                        setReturnResult(product_list);
                     }

                }
            }

        }

    }
    private void setValues(MyItemListModel model){

        String [] ids=new String[1000];
        if(model!=null){
            String data= GlobalFunctions.getSharedPreferenceString(context,GlobalVariables.SELECTED_ITEMS_LIST);
            if(!TextUtils.isEmpty(data)&&data.contains(",")){
                ids=data.split(",");
            }
            String str = "";
             MyItemListModel myItemListModel = model;
            List<ProductModel> productModels = myItemListModel.getItem_list();
            if(productModels!=null){
                if(productModels.size()>0){
                    product_list.clear();
                    for (ProductModel productModel:productModels){
                        if(data!=null && ids!=null){
                            for (int i=0;i<ids.length;i++){
                                if(ids[i]!=null)
                                    if(Integer.parseInt(productModel.getId())==Integer.parseInt(ids[i])){
                                        productModel.setIs_selected(true);
                                    }
                            }
                        }
                        product_list.add(productModel);
                    }
                    synchronized (adapter){adapter.notifyDataSetChanged();}

                    if(!item_available && product_list.size()>0){
                        product_list.get(0).setIs_selected(true);
                        String selected_item =product_list.get(0).getId()+",";
                        GlobalFunctions.setSharedPreferenceString(context,GlobalVariables.SELECTED_ITEMS_LIST,selected_item);
                        setReturnResult(product_list);
                    }

                }
            }

        }


        /*if(model!=null){
            String str = "";
            MyItemListModel myItemListModel = model;
            List<ProductModel> productModels = myItemListModel.getItem_list();
            if(productModels!=null){
                if(productModels.size()>0){
                    product_list.clear();
                    product_list.addAll(productModels);
                    productModels.clear();
                    synchronized (adapter){adapter.notifyDataSetChanged();}
                }
            }
        }
*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    @Override
    public void onResume() {
        super.onResume();
            setThisPage();
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.report_listing_menu, menu);
        MenuItem save=menu.findItem(R.id.save);
        if(save!=null)
            save.setTitle("New Item");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            saveList();
            Intent i=CreateSaleActivity.newInstance(activity,null,null,null,GlobalVariables.TYPE_HOME,GlobalVariables.TYPE_ITEM);
            startActivity(i);
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }
    public void saveList(){
        String data = "";
        List<ProductModel> stList = ((MyItemListAdapter) adapter).getProductList();
        List<ProductModel> selected_List =new ArrayList<ProductModel>();
        selected_List.clear();
        for (int i = 0; i < stList.size(); i++) {
            ProductModel productModel = stList.get(i);
            if (productModel.is_selected() == true) {
                /*data = data + "\n" + productModel.getName().toString();*/
                data=data+productModel.getId()+ ",";
                selected_List.add(productModel);
            }
        }

        GlobalFunctions.setSharedPreferenceString(context,GlobalVariables.SELECTED_ITEMS_LIST,data);
        Log.d(TAG,"####Selected ids#########"+data);
    }
    public void addItems(){
        String data = "";
        List<ProductModel> stList = ((MyItemListAdapter) adapter).getProductList();
        List<ProductModel> selected_List =new ArrayList<ProductModel>();
        selected_List.clear();
        for (int i = 0; i < stList.size(); i++) {
            ProductModel productModel = stList.get(i);
            if (productModel.is_selected() == true) {
                /*data = data + "\n" + productModel.getName().toString();*/
                data=data+productModel.getId()+ ",";
                selected_List.add(productModel);
            }
        }
     /*   GlobalFunctions.saveItemsList(context,GlobalVariables.SELECTED_ITEMS_LIST,stList);*/

        GlobalFunctions.setSharedPreferenceString(context,GlobalVariables.SELECTED_ITEMS_LIST,data);
        Log.d(TAG,"####Selected ids#########"+data);
        setReturnResult(selected_List);

    }

    private void setReturnResult(List<ProductModel> list_products){
        HomeListModel homeListModel=new HomeListModel();
        homeListModel.setItem_list(list_products);
        if(homeListModel!=null){
            Bundle bundle = new Bundle();
            bundle.putSerializable(CreateGarageSaleFragment.CREATE_GARRAGE_ACTIVITY_SELECT_ITEMS_KEY,homeListModel);
            Intent result = new Intent(activity,CreateSaleActivity.class);
            result.putExtras(bundle);
            setResult(this.RESULT_OK,result);
        }else{
            setResult(this.RESULT_CANCELED);
        }

        finish();
    }
    private void getMyItemList(final Context context){
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.getAvailableItemsList(context, new MyItemListModel(),new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                if(arg0 instanceof StatusModel){
                    StatusModel statusModel = (StatusModel) arg0;
                    no_sales.setVisibility(View.GONE);
                }else if(arg0 instanceof ErrorModel){
                    ErrorModel errorModel = (ErrorModel) arg0;
                    String msg = errorModel.getError()!=null ? errorModel.getError() : errorModel.getMessage();
                   /* Toast.makeText(context, msg, Toast.LENGTH_LONG).show();*/
                    no_sales.setVisibility(View.GONE);
                }else if(arg0 instanceof MyItemListModel){
                    MyItemListModel model = (MyItemListModel) arg0;
                    setValues(model);
                    done.setVisibility(View.VISIBLE);
                    no_sales.setVisibility(View.GONE);

                }
            }
            @Override
            public void OnFailureFromServer(String msg) {
               /* Toast.makeText(context, msg, Toast.LENGTH_LONG).show();*/
            }
            @Override
            public void OnError(String msg) {
                /*Toast.makeText(context, msg, Toast.LENGTH_LONG).show();*/
            }
        }, "GetMyItemList");
    }
    public void callAddItem(){
        Intent i=CreateSaleActivity.newInstance(activity,null,null,null,GlobalVariables.TYPE_HOME,GlobalVariables.TYPE_ITEM);
        startActivity(i);
    }
}
