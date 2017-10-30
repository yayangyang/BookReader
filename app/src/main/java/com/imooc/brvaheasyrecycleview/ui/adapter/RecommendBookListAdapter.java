package com.imooc.brvaheasyrecycleview.ui.adapter;


import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.RecommendBookList;
import com.imooc.brvaheasyrecycleview.Bean.expand.Lv0;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.manager.SettingManager;
import com.imooc.brvaheasyrecycleview.transform.GlideCircleTransform;
import com.imooc.brvaheasyrecycleview.transform.GlideRoundTransform;
import com.imooc.brvaheasyrecycleview.utils.NoDoubleClickListener;

import java.util.List;

public class RecommendBookListAdapter extends BaseQuickAdapter<RecommendBookList.RecommendBook,BaseViewHolder> {


    public RecommendBookListAdapter(@LayoutRes int layoutResId, @Nullable List<RecommendBookList.RecommendBook> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RecommendBookList.RecommendBook item) {
        if (!SettingManager.getInstance().isNoneCover()) {
            ImageView view =helper.getView(R.id.ivBookListCover);
            Glide.with(mContext).load(Constant.IMG_BASE_URL + item.cover)
                    .placeholder(R.drawable.avatar_default) .transform(new GlideRoundTransform
                    (mContext)).into(view);
        }

        helper.setText(R.id.tvBookListTitle, item.title);
        helper.setText(R.id.tvBookAuthor, item.author);
        helper.setText(R.id.tvBookListTitle, item.title);
        helper.setText(R.id.tvBookListDesc, item.desc);
        helper.setText(R.id.tvBookCount, String.format(mContext.getString(R.string
                        .book_detail_recommend_book_list_book_count), item.bookCount))
                .setText(R.id.tvCollectorCount, String.format(mContext.getString(R.string
                        .book_detail_recommend_book_list_collector_count), item.collectorCount));
    }
}