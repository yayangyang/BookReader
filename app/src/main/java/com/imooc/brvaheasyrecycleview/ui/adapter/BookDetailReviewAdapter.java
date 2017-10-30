package com.imooc.brvaheasyrecycleview.ui.adapter;


import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.HotReview;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.manager.SettingManager;
import com.imooc.brvaheasyrecycleview.transform.GlideCircleTransform;
import com.imooc.brvaheasyrecycleview.utils.FormatUtils;
import com.imooc.brvaheasyrecycleview.view.XLHRatingBar;

import java.util.List;

public class BookDetailReviewAdapter extends BaseQuickAdapter<HotReview.Reviews,BaseViewHolder> {

    public BookDetailReviewAdapter(@LayoutRes int layoutResId, @Nullable List<HotReview.Reviews> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HotReview.Reviews item) {
        if (!SettingManager.getInstance().isNoneCover()) {
            ImageView view =helper.getView(R.id.ivBookCover);
            Glide.with(mContext).load(Constant.IMG_BASE_URL + item.author.avatar)
                    .placeholder(R.drawable.avatar_default) .transform(new GlideCircleTransform
                    (mContext)).into(view);
        } else {
            helper.setImageResource(R.id.ivBookCover, R.drawable.avatar_default);
        }

        helper.setText(R.id.tvBookTitle, item.author.nickname)
                .setText(R.id.tvBookType, String.format(mContext.getString(R.string
                        .book_detail_user_lv), item.author.lv))
                .setText(R.id.tvTime, FormatUtils.getDescriptionTimeFromDateString(item.created))
                .setText(R.id.tvTitle, item.title)
                .setText(R.id.tvContent, String.valueOf(item.content))
                .setText(R.id.tvHelpfulYes, String.valueOf(item.helpful.yes));
        helper.setVisible(R.id.tvTime, true);
        XLHRatingBar ratingBar = helper.getView(R.id.rating);
        ratingBar.setCountSelected(item.rating);
    }
}
