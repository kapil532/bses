package com.langoor.app.blueshak.view_sales;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.divrt.co.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.langoor.app.blueshak.AppController;
import com.langoor.app.blueshak.MainActivity;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.home.SaleFragment;
import com.langoor.app.blueshak.home.list_fragments.SalesList;
import com.langoor.app.blueshak.services.ServerResponseInterface;
import com.langoor.app.blueshak.services.ServicesMethodsManager;
import com.langoor.app.blueshak.services.model.FilterModel;
import com.langoor.app.blueshak.services.model.ProductModel;
import com.langoor.app.blueshak.services.model.SalesListModel;
import com.langoor.app.blueshak.services.model.SalesModel;
import com.langoor.app.blueshak.services.model.Shop;
import com.langoor.app.blueshak.util.LocationListener;
import com.langoor.app.blueshak.util.LocationService;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class MapFragmentSales extends Fragment implements OnMapReadyCallback,LocationListener {
    public static final String TAG = "MapFragmentSales";
    public static final String MAP_FRAGMENT_SALES_BUNDLE_TYPE_STRING = "MapFragmentSalesBundleTypeString";
    public static final String MAP_FRAGMENT_SALES_PRODUCT_ID_STRING = "MapFragmentProductIdTypeString";
    public static final String MAP_FRAGMENT_SALES_FROM_ACTIVITY_ID_STRING = "MapFragmentFromActivitypeString";
    public static final String SHOP = "shop";

    // Declare a variable for the cluster manager.
    private ClusterManager<MyItem> mClusterManager;
    Activity activity;
    private GoogleMap map;
    private FloatingActionButton listFAB;
    private SupportMapFragment fragment;
    private SalesListModel salesListModel = new SalesListModel();
    private String type = GlobalVariables.TYPE_GARAGE;
    TextView sale_header_name;
    LocationService locServices;
    private String product_id=null;
    private ProductModel productModel=null;
    private Shop shop=null;
    private Double lat;
    private  Double lng;
    private  String shop_name="";
    private  String icon_name="";
    private static View view;
    private  boolean from_activity=false;

    public static MapFragmentSales newInstance(String type,ProductModel product_id,Shop shop,boolean from_activity ){
        MapFragmentSales mapFragmentSales = new MapFragmentSales();
        Bundle bundle = new Bundle();
        bundle.putString(MAP_FRAGMENT_SALES_BUNDLE_TYPE_STRING, type);
        bundle.putSerializable(MAP_FRAGMENT_SALES_PRODUCT_ID_STRING, product_id);
        bundle.putBoolean(MAP_FRAGMENT_SALES_FROM_ACTIVITY_ID_STRING, from_activity);
        bundle.putSerializable(SHOP, shop);
        mapFragmentSales.setArguments(bundle);
        return mapFragmentSales;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        activity = getActivity();
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.map_fragment, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
            AppController.getInstance().trackException(e);
        }
        //View view = inflater.inflate(R.layout.map_fragment, container, false);
        locServices = new LocationService(activity);
        locServices.setListener(this);
        final FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        listFAB = (FloatingActionButton) view.findViewById(R.id.map_fragment_list_fab);
        sale_header_name= (TextView) view.findViewById(R.id.sale_header_name);

        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, fragment).commit();
            fragment.getMapAsync(this);
        }else{
            fragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
            fragment.getMapAsync(this);
        }
        from_activity = getArguments().getBoolean(MAP_FRAGMENT_SALES_FROM_ACTIVITY_ID_STRING);
        type = getArguments().getString(MAP_FRAGMENT_SALES_BUNDLE_TYPE_STRING);
        if(type.equalsIgnoreCase(GlobalVariables.TYPE_SHOP)){
            shop=(Shop)getArguments().getSerializable(SHOP);
            productModel=(ProductModel) getArguments().getSerializable(MAP_FRAGMENT_SALES_PRODUCT_ID_STRING);
            if(shop!=null){
                shop_name=shop.getName();
                icon_name=shop_name;
                lat=Double.parseDouble(shop.getLatitude());
                lng=Double.parseDouble(shop.getLongitude());
            }
            if(productModel!=null){
                icon_name=productModel.getName();
                shop_name=productModel.getName();
                lat=Double.parseDouble(productModel.getLatitude());
                lng=Double.parseDouble(productModel.getLongitude());
            }
        }else{
            if(type== GlobalVariables.TYPE_MY_SALE+""){
                    getLists(getContext());
            }else{
                Location loc = locServices.getLocation();
                    //getLists(getContext(), "-33.839838", "151.212033", 10, type);
                    if(loc!=null){
                        FilterModel model = new FilterModel();
                        model.setLatitude(loc.getLatitude()+"");
                        model.setLongitude(loc.getLongitude()+"");
                        model.setRange(10);
                        model.setAvailable(true);
                        model.setPickup(true);
                        model.setShipable(true);
                        model.setType(GlobalVariables.TYPE_SHOP);
                        model.setPriceRange("0,10000");
                        getLists(getContext(),model);
                        locServices.removeListener();
                    }
                }
        }

        listFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(salesListModel.getSalesList().size()>0){
                    MainActivity.replaceFragment(SalesList.newInstance(salesListModel, type,null,false), SalesList.TAG);}
            }
        });
        sale_header_name.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;
                final Location loc=locServices.getLocation();
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (sale_header_name.getRight() - sale_header_name.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if(salesListModel.getSalesList().size()>0){
                            MainActivity.replaceFragment(SalesList.newInstance(salesListModel, type,null,false), SalesList.TAG);}
                        return true;
                    }
                }
                return false;
            }
        });
        return view;
    }
    @Override
    public void onResume() {
        MainActivity.setTitle(getString(R.string.garageSale),0);
        super.onResume();
    }

    private void getLists(Context context, FilterModel filterModel){
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.getListDetails(context, filterModel, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                Log.d(TAG, "########onSuccess Response###########");
                salesListModel = (SalesListModel)arg0;
                String str = salesListModel.toString();
                Log.d(TAG, str);
                addItems();
            }

            @Override
            public void OnFailureFromServer(String msg) {
                Log.d(TAG, msg);
            }

            @Override
            public void OnError(String msg) {
                Log.d(TAG, msg);
            }
        }, "List Sales");

    }

    private void getLists(Context context){
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.getMySalesList(context, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                Log.d(TAG, "onSuccess Response");
                salesListModel = (SalesListModel)arg0;
                String str = salesListModel.toString();
                Log.d(TAG, str);
                addItems();
            }

            @Override
            public void OnFailureFromServer(String msg) {
                Log.d(TAG, msg);
            }

            @Override
            public void OnError(String msg) {
                Log.d(TAG, msg);
            }
        }, "List Sales");

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        /*googleMap.setMyLocationEnabled(true);*/
        googleMap.setMyLocationEnabled(true);
        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        map = googleMap;

        if(type.equalsIgnoreCase(GlobalVariables.TYPE_SHOP))
            addMarker();
        else
            setUpClusterer();
    }
    private void addMarker() {
        MarkerOptions options = new MarkerOptions();
        /*MarkerOptions marker = new MarkerOptions().position(currentLatLng).title("resturan")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.profile_pic));
        */

        // following four lines requires 'Google Maps Android API Utility Library'
        // https://developers.google.com/maps/documentation/android/utility/
        // I have used this to display the time as title for location markers
        // you can safely comment the following four lines but for this info
        IconGenerator iconFactory = new IconGenerator(activity);
        iconFactory.setStyle(IconGenerator.STYLE_PURPLE);
        iconFactory.setColor(getResources().getColor(R.color.tab_selected));
        options.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(icon_name)));
        options.anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
        LatLng currentLatLng = new LatLng(lat,lng);
        options.position(currentLatLng);
        Marker mapMarker = map.addMarker(options);
        mapMarker.setTitle(shop_name);
        Log.d(TAG, "Marker added.............................");
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,1));
        Log.d(TAG, "Zoom done.............................");

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(!from_activity){
                    ProductModel productModel=new ProductModel();
                    productModel.setName(shop_name);
                    productModel.setLongitude(Double.toString(lng));
                    productModel.setLatitude(Double.toString(lat));
                    Intent i= MapActivity.newInstance(activity,GlobalVariables.TYPE_SHOP,productModel);
                    startActivity(i);
                }
                return false;
            }
        });
    }
    @Override
    public void onLocationChanged(Location location) {
       /* if(!type.equalsIgnoreCase(GlobalVariables.TYPE_MY_SALE+"")){
            Location loc = locServices.getLocation();
            if(loc!=null){
                FilterModel model = new FilterModel();
                model.setLatitude(loc.getLatitude()+"");
                model.setLongitude(loc.getLongitude()+"");
                model.setRange(10);
                model.setAvailable(true);
                model.setPickup(true);
                model.setShipable(true);
                model.setType(type);
                model.setPriceRange("0,10000");
                getLists(getContext(),model);
                locServices.removeListener();
                setUpClusterer();
            }
        }*/
    }

    public class MyItem implements ClusterItem {
        private SalesModel salesModel;

        public MyItem(SalesModel salesModel) {
            this.salesModel = salesModel;
        }

        public SalesModel getSalesModel() {
            return salesModel;
        }

        public void setSalesModel(SalesModel salesModel) {
            this.salesModel = salesModel;
        }

        @Override
        public LatLng getPosition() {
            return new LatLng(Double.parseDouble(salesModel.getLatitude()), Double.parseDouble(salesModel.getLongitude()));
        }
    }

    private void setUpClusterer() {
        if(locServices.getLocation()!=null)map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locServices.getLatitude(), locServices.getLongitude()), 10));
        mClusterManager = new ClusterManager<MyItem>(getActivity(), map);
        /*mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new MarkerInfoWindowAdapter());
        mClusterManager.getClusterMarkerCollection().setOnInfoWindowAdapter(new ClusterInfoWindow());*/
        mClusterManager.setRenderer(new OwnIconRendered(getActivity().getApplicationContext(), map, mClusterManager));
        map.setOnCameraChangeListener(mClusterManager);
        map.setOnMarkerClickListener(mClusterManager);
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                mClusterManager.onCameraChange(map.getCameraPosition());
            }
        });

        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
            @Override
            public boolean onClusterItemClick(MyItem myItem) {
              /*  MainActivity.addFragment(SaleFragment.newInstance(myItem.getSalesModel(),type), "salesFragment");*/

                ProductModel productModel=new ProductModel();
                productModel.setName(shop_name);
                productModel.setLongitude(Double.toString(lat));
                productModel.setLatitude(Double.toString(lng));
                Intent i= MapActivity.newInstance(activity,GlobalVariables.TYPE_MULTIPLE_ITEMS,productModel);
                startActivity(i);
                return true;
            }
        });
    }

    private void addItems() {
        List<SalesModel> list = salesListModel.getSalesList();
        for (int i = 0; i < list.size(); i++) {
            SalesModel item = list.get(i);
            Double lat = Double.parseDouble(item.getLatitude());
            Double lng = Double.parseDouble(item.getLongitude());
            MyItem offsetItem = new MyItem(item);
            if(product_id!=null){
                for (ProductModel productModel:item.getProducts()) {
                    if(product_id.equals(productModel.getId())){
                        item.setName(productModel.getName());
                        item.setLongitude(productModel.getLongitude());
                        item.setLatitude(productModel.getLatitude());
                        item.setId(productModel.getId());
                        MyItem offsetItem1 = new MyItem(item);
                        if(mClusterManager!=null)mClusterManager.addItem(offsetItem1);
                        break;
                    }
                }
            }else{
                if(mClusterManager!=null)mClusterManager.addItem(offsetItem);
            }

        }
        if(mClusterManager!=null)mClusterManager.cluster();
    }
    class OwnIconRendered extends DefaultClusterRenderer<MyItem> {

        public OwnIconRendered(Context context, GoogleMap map,
                               ClusterManager<MyItem> clusterManager) {
            super(context, map, clusterManager);
        }

       /* @Override
        protected boolean shouldRenderAsCluster(Cluster<MyItem> cluster) {
            //start clustering if at least 2 items overlap
            int maxSize = 4;
            if(map.getCameraPosition().zoom<=0){maxSize=20;}
            return cluster.getSize() > maxSize;
        }*/

        @Override
        protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) {
            if(getActivity()!=null){
                IconGenerator icnGenerator = new IconGenerator(getActivity());
                icnGenerator.setColor(getResources().getColor(R.color.brandColor));
                icnGenerator.setTextAppearance(R.style.iconGenText);
                Bitmap iconBitmap = icnGenerator.makeIcon(item.getSalesModel().getName());
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(iconBitmap));
            /*    markerOptions.title("My Shop");*/

                //markerOptions.snippet(item.getSnippet());
                //markerOptions.title(item.getTitle());
            }

            super.onBeforeClusterItemRendered(item, markerOptions);
        }

    }
    @Override
    public void onProviderEnabled(String provider) {

    }

   /* public Bitmap createDrawableFromView(Context context) {
        View marker = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
                inflate(R.layout.map_popup, your_parent_layout,false);
        TextView tv_location_name = (TextView) marker.findViewById(R.id.tv_location_name);
        TextView tv_event_status = (TextView) marker.findViewById(R.id.tv_event_status);
        TextView tv_event_name = (TextView) marker.findViewById(R.id.tv_event_name);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);
        return bitmap;
    }*/
}
