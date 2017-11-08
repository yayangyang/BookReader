package com.imooc.brvaheasyrecycleview.ui.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.InterestBookList;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.transform.GlideCircleTransform;
import com.imooc.brvaheasyrecycleview.transform.GlideRoundTransform;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/11/7.
 */

public class SearchInterestAdapter extends BaseQuickAdapter<InterestBookList.InterestBook,BaseViewHolder> {
    public SearchInterestAdapter(int layoutResId, @Nullable List<InterestBookList.InterestBook> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, InterestBookList.InterestBook item) {
        LogUtils.e("SearchAdapter-convert");
        ImageView view =helper.getView(R.id.ivBookCover);
        Glide.with(mContext).load(Constant.IMG_BASE_URL + item.cover)
                .placeholder(R.drawable.cover_default) .transform(new GlideRoundTransform
                (mContext)).into(view);
        helper.setText(R.id.tvBookListTitle, item.title);
        helper.setText(R.id.tvAuthor, String.format(mContext.getString
                (R.string.search_result_author), item.author));
        helper.setText(R.id.tvShortIntro,item.shortIntro);
        String fans=String.format(mContext.getString
                (R.string.search_result_lately_follower), Integer.valueOf(item.latelyFollower))+
                (TextUtils.isEmpty(item.retentionRatio) ?
                        String.format(mContext.getString(R.string.search_result_retention_ratio), "0")
                        : String.format(mContext.getString(R.string.search_result_retention_ratio),
                        item.retentionRatio));
        helper.setText(R.id.tvFans, fans);
    }
}
