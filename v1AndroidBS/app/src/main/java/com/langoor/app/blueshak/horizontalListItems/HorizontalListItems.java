package com.langoor.app.blueshak.horizontalListItems;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.divrt.co.R;
import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.home.EndlessRecyclerOnScrollListener;
import com.langoor.app.blueshak.home.ItemListAdapter;
import com.langoor.app.blueshak.item.ProductDetail;
import com.langoor.app.blueshak.services.model.ProductModel;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sivasabharish Chinnaswamy on 29-10-2015.
 */
public class HorizontalListItems extends Fragment {
    static final String TAG = "HorizontalListItems";
    static Context context;
    //static HorizontalListView horizontalListView;
    TwoWayView horizontalListView;
    static TextView titleTextView;
    static String title;
    /*static HorizontalListAdapter adapter;*/
    static HorizontalListAdapterNew adapter;
    private RecyclerView recyclerView;

    static List<ProductModel> listItems = new ArrayList<ProductModel>();

    private static final String HORIZONTAL_ITEM_LISTITEMS_BUNDLE_KEY = "SerializeableListItemKey";

    public static HorizontalListItems newInstance(HorizontalListArray horizontalListArray){
        HorizontalListItems fragment = new HorizontalListItems();
        Bundle bundle = new Bundle();
        bundle.putSerializable(HORIZONTAL_ITEM_LISTITEMS_BUNDLE_KEY, horizontalListArray);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.horizontal_list_items,null,false);
        context = getActivity();
       /* horizontalListView = (TwoWayView) view.findViewById(R.id.horizontalListItems);*/
        recyclerView = (RecyclerView) view.findViewById(R.id.horizontalListItems);
     LinearLayoutManager linearLayoutManagerVertical =
                new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManagerVertical);
   /*  *//*   StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);*//*
        recyclerView.setLayoutManager(gridLayoutManager);*/
       /* recyclerView.addItemDecoration(new SpacesItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.space)));*/
    /*    recyclerView.addItemDecoration(new SpacesItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.space)));*/
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
       /* recyclerView.setOnScrollListener(new MyScrollListener(activity) {
            @Override
            public void onMoved(int distance) {
                toolbar.setTranslationY(-distance);
            }
        });*/
      /*  recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(
                gridLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if(!(current_page>last_page)){
                    model.setPage(current_page);
                    getItemLists(context,model);
                }*//*else
                    Toast.makeText(context,"Loaded all items",Toast.LENGTH_LONG).show();*//*
            }
        });*/
        titleTextView = (TextView) view.findViewById(R.id.horizontalListTitle);

        if(!getActivity().isFinishing()){
            if(getArguments()!=null){

                HorizontalListArray horizontalListArray = (HorizontalListArray) getArguments().getSerializable(HORIZONTAL_ITEM_LISTITEMS_BUNDLE_KEY);
                title = horizontalListArray.getListTitle();
                listItems = horizontalListArray.getHorizontalListArrayList();
                titleTextView.setText(title);
                adapter = new HorizontalListAdapterNew(context,listItems);
              /*  horizontalListView.setAdapter(adapter);*/
                recyclerView.setAdapter(adapter);

            }
           /* horizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(position<=listItems.size()){
                        ProductDetail.closeThisActivity();
                        Intent intent = ProductDetail.newInstance(context, listItems.get(position),null, GlobalVariables.TYPE_SIMILAR_PRODUCT);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                       *//*  if(ProductDetail.isActive()){
                            ProductDetail productDetail = new ProductDetail();
                            productDetail.setProductModel(listItems.get(position));
                        }else{
                             Intent intent = ProductDetail.newInstance(context, listItems.get(position),null, GlobalVariables.TYPE_SIMILAR_PRODUCT);
                             startActivity(intent);
                         }*//*
                    }
                }
            });*/
        }

        return view;
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
}
