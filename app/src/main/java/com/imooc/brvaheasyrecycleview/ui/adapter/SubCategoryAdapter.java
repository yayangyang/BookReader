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
import com.imooc.brvaheasyrecycleview.Bean.BooksByCats;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.manager.SettingManager;
import com.imooc.brvaheasyrecycleview.transform.GlideCircleTransform;
import com.imooc.brvaheasyrecycleview.transform.GlideRoundTransform;

import java.util.List;


public class SubCategoryAdapter extends BaseQuickAdapter<BooksByCats.BooksBean,BaseViewHolder> {


    public SubCategoryAdapter(@LayoutRes int layoutResId, @Nullable List<BooksByCats.BooksBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BooksByCats.BooksBean item) {
        if (!SettingManager.getInstance().isNoneCover()) {
            ImageView view =helper.getView(R.id.ivSubCateCover);
            Glide.with(mContext).load(Constant.IMG_BASE_URL + item.cover)
                    .placeholder(R.drawable.avatar_default) .transform(new GlideRoundTransform
                    (mContext)).into(view);
        } else {
            helper.setImageResource(R.id.ivSubCateCover, R.drawable.cover_default);
        }

        helper.setText(R.id.tvSubCateTitle, item.title)
                .setText(R.id.tvSubCateAuthor, (item.author == null ? "未知" : item.author) + " | " + (item.majorCate == null ? "未知" : item.majorCate))
                .setText(R.id.tvSubCateShort, item.shortIntro)
                .setText(R.id.tvSubCateMsg, String.format(mContext.getResources().getString(R.string.category_book_msg),
                        item.latelyFollower,
                        TextUtils.isEmpty(item.retentionRatio) ? "0" : item.retentionRatio));
    }
}
