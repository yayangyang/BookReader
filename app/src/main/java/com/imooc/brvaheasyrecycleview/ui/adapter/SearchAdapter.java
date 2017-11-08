package com.imooc.brvaheasyrecycleview.ui.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.SearchDetail;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.transform.GlideCircleTransform;
import com.imooc.brvaheasyrecycleview.transform.GlideRoundTransform;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;

import java.util.List;

public class SearchAdapter extends BaseQuickAdapter<SearchDetail.SearchBooks,BaseViewHolder> {

    public SearchAdapter(@LayoutRes int layoutResId, @Nullable List<SearchDetail.SearchBooks> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchDetail.SearchBooks item) {
        LogUtils.e("SearchAdapter-convert");
        ImageView view =helper.getView(R.id.ivBookCover);
        Glide.with(mContext).load(Constant.IMG_BASE_URL + item.cover)
                .placeholder(R.drawable.avatar_default) .transform(new GlideRoundTransform
                (mContext)).into(view);
        helper.setText(R.id.tvBookListTitle, item.title);
        helper.setText(R.id.tvLatelyFollower, String.format(mContext.getString
                        (R.string.search_result_lately_follower), item.latelyFollower));
        helper.setText(R.id.tvRetentionRatio, (TextUtils.isEmpty(item.retentionRatio) ?
                        String.format(mContext.getString(R.string.search_result_retention_ratio), "0")
                        : String.format(mContext.getString(R.string.search_result_retention_ratio),
                        item.retentionRatio)));
        helper.setText(R.id.tvBookListAuthor, String.format(mContext.getString
                        (R.string.search_result_author), item.author));
    }
}
