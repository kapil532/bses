package com.langoor.app.blueshak.garage;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.langoor.app.blueshak.global.GlobalVariables;
import com.langoor.app.blueshak.services.model.CreateProductModel;
import com.langoor.app.blueshak.services.model.CreateSalesModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private final SparseArray<WeakReference<Fragment>> instantiatedFragments = new SparseArray<>();
    private ArrayList<String> mTabHeader;
    Context context;
    CreateSalesModel createSalesModel=null;
    CreateProductModel createProductModel=null;
    int from;
    public PagerAdapter(FragmentManager fm, ArrayList<String> tabHeader, Context context, CreateSalesModel createSalesModel, CreateProductModel createProductModel,int from) {
        super(fm);
        this.mTabHeader = tabHeader;
        this.context=context;
        this.createSalesModel=createSalesModel;
        this.createProductModel=createProductModel;
        this.from=from;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                if(createProductModel==null && createSalesModel==null)
                    return  CreateItemSaleFragment.newInstance(context,createProductModel,null, GlobalVariables.TYPE_GARAGE,from);
                else  if(createProductModel!=null)
                    return  CreateItemSaleFragment.newInstance(context,createProductModel,null, GlobalVariables.TYPE_GARAGE,from);
                else
                    return  CreateGarageSaleFragment.newInstance(context,createSalesModel, GlobalVariables.TYPE_GARAGE,from);

            case 1:
                if(createProductModel==null && createSalesModel==null)
                    return  CreateGarageSaleFragment.newInstance(context,createSalesModel, GlobalVariables.TYPE_GARAGE,from);
                else  if(createSalesModel!=null)
                    return  CreateGarageSaleFragment.newInstance(context,createSalesModel, GlobalVariables.TYPE_GARAGE,from);
                else
                    return  CreateItemSaleFragment.newInstance(context,createProductModel,null, GlobalVariables.TYPE_GARAGE,from);


            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mTabHeader.size();
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final Fragment fragment = (Fragment) super.instantiateItem(container, position);
        instantiatedFragments.put(position, new WeakReference<>(fragment));
        return fragment;
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        instantiatedFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    @Nullable
    public Fragment getFragment(final int position) {
        final WeakReference<Fragment> wr = instantiatedFragments.get(position);
        if (wr != null) {
            return wr.get();
        } else {
            return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabHeader.get(position);
    }
}
