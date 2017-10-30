package com.imooc.brvaheasyrecycleview.ui.adapter;


import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.BookListTags;
import com.imooc.brvaheasyrecycleview.R;

import java.util.List;

public class SubjectTagsAdapter extends BaseQuickAdapter<BookListTags.DataBean,BaseViewHolder> {


    public SubjectTagsAdapter(int layoutResId, @Nullable List<BookListTags.DataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookListTags.DataBean item) {
        RecyclerView rvTagsItem = helper.getView(R.id.rvTagsItem);
        rvTagsItem.setHasFixedSize(true);
        rvTagsItem.setLayoutManager(new GridLayoutManager(mContext, 4));
        TagsItemAdapter adapter = new TagsItemAdapter(R.layout.item_subject_tag_list, item.tags);
        adapter.setOnItemClickListener(getOnItemClickListener());
        rvTagsItem.setAdapter(adapter);

        helper.setText(R.id.tvTagGroupName, item.name);
    }

    class TagsItemAdapter extends BaseQuickAdapter<String,BaseViewHolder> {

        public TagsItemAdapter(int layoutResId, @Nullable List<String> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            helper.setText(R.id.tvTagName, item);
        }
    }

}
