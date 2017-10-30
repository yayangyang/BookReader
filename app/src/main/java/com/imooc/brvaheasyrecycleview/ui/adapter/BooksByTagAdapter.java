package com.imooc.brvaheasyrecycleview.ui.adapter;


import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.BooksByTag;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.transform.GlideCircleTransform;
import com.imooc.brvaheasyrecycleview.transform.GlideRoundTransform;

import java.util.List;

public class BooksByTagAdapter extends BaseQuickAdapter<BooksByTag.TagBook,BaseViewHolder> {

    public BooksByTagAdapter(@LayoutRes int layoutResId, @Nullable List<BooksByTag.TagBook> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BooksByTag.TagBook item) {
        StringBuffer sbTags = new StringBuffer();
        for (String tag : item.tags) {
            if (!TextUtils.isEmpty(tag)) {
                sbTags.append(tag);
                sbTags.append(" | ");
            }
        }
        ImageView view =helper.getView(R.id.ivBookCover);
        Glide.with(mContext).load(Constant.IMG_BASE_URL + item.cover)
                .placeholder(R.drawable.avatar_default) .transform(new GlideRoundTransform
                (mContext)).into(view);
        helper.setText(R.id.tvBookListTitle, item.title);
        helper.setText(R.id.tvShortIntro, item.shortIntro);
        helper.setText(R.id.tvTags, (item.tags.size() == 0 ? "" : sbTags.substring(0, sbTags
                        .lastIndexOf(" | "))));
    }
}
