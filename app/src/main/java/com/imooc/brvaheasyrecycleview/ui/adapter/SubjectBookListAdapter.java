package com.imooc.brvaheasyrecycleview.ui.adapter;


import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.BookLists;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.manager.SettingManager;
import com.imooc.brvaheasyrecycleview.transform.GlideRoundTransform;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;

import java.util.List;

public class SubjectBookListAdapter extends BaseQuickAdapter<BookLists.BookListsBean,BaseViewHolder> {

    public SubjectBookListAdapter(int layoutResId, @Nullable List<BookLists.BookListsBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookLists.BookListsBean item) {
        if (!SettingManager.getInstance().isNoneCover()) {
            ImageView view =helper.getView(R.id.ivSubCateCover);
            Glide.with(mContext).load(Constant.IMG_BASE_URL + item.cover)
                    .placeholder(R.drawable.avatar_default) .transform(new GlideRoundTransform
                    (mContext)).into(view);
        } else {
            helper.setImageResource(R.id.ivSubCateCover, R.drawable.cover_default);
        }

        helper.setText(R.id.tvSubCateTitle, item.title)
                .setText(R.id.tvSubCateAuthor, item.author)
                .setText(R.id.tvSubCateShort, item.desc)
                .setText(R.id.tvSubCateMsg, String.format(mContext.getResources().getString
                        (R.string.subject_book_msg), item.bookCount, item.collectorCount));
    }
}
