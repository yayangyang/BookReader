package com.imooc.brvaheasyrecycleview.ui.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.CategoryList;
import com.imooc.brvaheasyrecycleview.R;

import java.util.List;

public class TopCategoryListAdapter extends BaseQuickAdapter<CategoryList.MaleBean,BaseViewHolder> {

    private String gender;

    public TopCategoryListAdapter(int layoutResId, @Nullable List<CategoryList.MaleBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CategoryList.MaleBean item) {
        helper.setText(R.id.tvName, item.name)
                .setText(R.id.tvBookCount, String.format(mContext.getString(R.string
                        .category_book_count), item.bookCount));
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
