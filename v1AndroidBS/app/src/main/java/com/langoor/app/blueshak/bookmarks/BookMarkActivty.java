package com.langoor.app.blueshak.bookmarks;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.divrt.co.R;
import com.langoor.app.blueshak.global.GlobalFunctions;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.home.EndlessRecyclerOnScrollListener;
import com.langoor.app.blueshak.home.ItemListAdapter;
import com.langoor.app.blueshak.home.ItemListFragment;
import com.langoor.app.blueshak.services.ServerResponseInterface;
import com.langoor.app.blueshak.services.ServicesMethodsManager;
import com.langoor.app.blueshak.services.model.BookmarkListModel;
import com.langoor.app.blueshak.services.model.ErrorModel;
import com.langoor.app.blueshak.services.model.HomeListModel;
import com.langoor.app.blueshak.services.model.ProductModel;
import com.langoor.app.blueshak.services.model.SalesListModel;
import com.langoor.app.blueshak.services.model.StatusModel;
import com.langoor.app.blueshak.util.LocationService;
import java.util.ArrayList;
import java.util.List;

public class BookMarkActivty extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,OnBookMarksDeleted{
    String TAG = "BookMarkActivty";
    static Context context;
    private Handler handler=new Handler();
    static Activity activity;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String type = GlobalVariables.TYPE_GARAGE;
    private RecyclerView recyclerView;
    private  Toolbar toolbar;
    private  TextView no_bookmarks;
    private BoomarkItemListAdapter adapter;
    private HomeListModel bookmarkListModel=new HomeListModel();
    private ArrayList<ProductModel> product_list = new ArrayList<ProductModel>();
    private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    private  int last_page=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_book_mark_activty);
        context=this;
        activity=this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("My Wishlist");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.brandColor)));
        }
        no_bookmarks=(TextView)findViewById(R.id.no_bookmarks);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        final SwipeRefreshLayout.OnRefreshListener swipeRefreshListner = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
               /* swipeRefreshLayout.setRefreshing(true);*/
                setThisPage();
            }
        };
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        // directly call onRefresh() method
                                        swipeRefreshListner.onRefresh();
                                        swipeRefreshLayout.setRefreshing(true);
                                    }
                                }
        );
        swipeRefreshLayout.setColorSchemeColors(context.getResources().getColor(R.color.brandColor),
                context.getResources().getColor(R.color.tab_selected),
                context.getResources().getColor(R.color.darkorange),
                context.getResources().getColor(R.color.brandColor));
       /* swipeRefreshLayout.setRefreshing(true);*/


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new BoomarkItemListAdapter(context, product_list,this);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new SpacesItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.space)));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        recyclerView.setOnScrollListener(new MyScrollListener(activity) {
            @Override
            public void onMoved(int distance) {
                toolbar.setTranslationY(-distance);
            }
        });
        endlessRecyclerOnScrollListener=new EndlessRecyclerOnScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if(!(current_page>last_page)){
                 /*   getItemLists(context,model);*/
                }
            }
        };
        recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void setThisPage(){
        swipeRefreshLayout.setRefreshing(true);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getBookmarks(context,"1");
            }
        });



    }
    public void showProgress(){
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        swipeRefreshLayout.setRefreshing(true);
                                    }
                                }
        );
    }
    @Override
    public void onResume() {
        if(!GlobalFunctions.isNetworkAvailable(context)){
            Snackbar.make(no_bookmarks, "Please check your internet connection", Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {

                        }
                    }).show();
        }else{
            setThisPage();
        }
        super.onResume();
    }
    @Override
    public void onRefresh() {
        if(!GlobalFunctions.isNetworkAvailable(context)){
            Snackbar.make(no_bookmarks, "Please check your internet connection", Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {

                        }
                    }).show();
        }else{
            setThisPage();
        }

    }

    private void getBookmarks(final Context context,String page){
        showProgress();
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.getBookmarksList(context,page, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {

                swipeRefreshLayout.setRefreshing(false);
                if(arg0 instanceof StatusModel){
                    StatusModel statusModel = (StatusModel) arg0;
                    if(statusModel.getMessage().equalsIgnoreCase("not_bookmarks_found"))
                        no_bookmarks.setVisibility(View.VISIBLE);

                    Toast.makeText(context,"No products found", Toast.LENGTH_LONG).show();
                }else if(arg0 instanceof ErrorModel){

                    ErrorModel errorModel = (ErrorModel) arg0;
                    String msg = errorModel.getError()!=null ? errorModel.getError() : errorModel.getMessage();
                    no_bookmarks.setVisibility(View.VISIBLE);

                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }else if(arg0 instanceof HomeListModel){
                    no_bookmarks.setVisibility(View.GONE);
                    HomeListModel model = (HomeListModel) arg0;
                    setValues(model);
                }
            }

            @Override
            public void OnFailureFromServer(String msg) {
                swipeRefreshLayout.setRefreshing(false);

                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void OnError(String msg) {
                swipeRefreshLayout.setRefreshing(false);

                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        }, "Get Bookmarks");

    }
    private void setValues(HomeListModel model){
        if(model!=null){
            bookmarkListModel = model;
            last_page=bookmarkListModel.getLast_page();
            List<ProductModel> productModels = bookmarkListModel.getItem_list();
            if(productModels!=null){
                if(productModels.size()>0&&recyclerView!=null&&product_list!=null&&adapter!=null){
                    product_list.clear();
                    product_list.addAll(productModels);
                    refreshList();
                }else
                    no_bookmarks.setVisibility(View.VISIBLE);
            }
        }else
            no_bookmarks.setVisibility(View.VISIBLE);

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
        @Override
        public void onBookMarksDeleted(int position) {
            no_bookmarks.setVisibility(View.VISIBLE);
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
    public void refreshList(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (adapter){adapter.notifyDataSetChanged();}
            }
        });
    }
}
