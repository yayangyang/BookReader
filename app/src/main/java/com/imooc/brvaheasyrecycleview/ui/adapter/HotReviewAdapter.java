package com.imooc.brvaheasyrecycleview.ui.adapter;


import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.HotReview;
import com.imooc.brvaheasyrecycleview.Bean.expand.Lv0;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.transform.GlideCircleTransform;
import com.imooc.brvaheasyrecycleview.view.XLHRatingBar;

import java.util.List;

public class HotReviewAdapter extends BaseQuickAdapter<HotReview.Reviews,BaseViewHolder> {

    public HotReviewAdapter(@LayoutRes int layoutResId, @Nullable List<HotReview.Reviews> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HotReview.Reviews item) {
        ImageView view =helper.getView(R.id.ivBookCover);
        Glide.with(mContext).load(Constant.IMG_BASE_URL + item.author.avatar)
                .placeholder(R.drawable.avatar_default) .transform(new GlideCircleTransform
                (mContext)).into(view);
        helper.setText(R.id.tvBookTitle, item.author.nickname);
        helper.setText(R.id.tvBookType, String.format(mContext.getString(R.string
                        .book_detail_user_lv), item.author.lv));
        helper.setText(R.id.tvTitle, item.title);
        helper.setText(R.id.tvContent, String.valueOf(item.content));
        helper.setText(R.id.tvHelpfulYes, String.valueOf(item.helpful.yes));
        XLHRatingBar ratingBar = helper.getView(R.id.rating);
        ratingBar.setCountSelected(item.rating);
    }
}