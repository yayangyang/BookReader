package com.imooc.brvaheasyrecycleview.ui.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.util.Log;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.bean;
import com.imooc.brvaheasyrecycleview.R;

import java.util.List;

/**
 * Created by Administrator on 2017/10/12.
 */

public class TestAdapter extends BaseQuickAdapter<bean,BaseViewHolder> {

    public TestAdapter(@LayoutRes int layoutResId, @Nullable List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, bean item) {
        helper.setText(R.id.name,"123");
        if((mData.size()-1)==helper.getLayoutPosition()){
            Log.e("convert","convert");
        }
    }

    @Override
    public int getItemCount() {
//        Log.e("getItemCount","getItemCount");
        return super.getItemCount();
    }
}
