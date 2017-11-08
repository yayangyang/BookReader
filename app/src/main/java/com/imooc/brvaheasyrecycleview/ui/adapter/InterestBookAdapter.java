package com.imooc.brvaheasyrecycleview.ui.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.InterestBookList;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.transform.GlideRoundTransform;

import java.util.List;

/**
 * Created by Administrator on 2017/11/7.
 */

public class InterestBookAdapter extends BaseQuickAdapter<InterestBookList.InterestBook,BaseViewHolder> {
    public InterestBookAdapter(int layoutResId, @Nullable List<InterestBookList.InterestBook> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, InterestBookList.InterestBook item) {
        ImageView view =helper.getView(R.id.ivBookCover);
        Glide.with(mContext).load(Constant.IMG_BASE_URL + item.cover)
                .placeholder(R.drawable.avatar_default) .transform(new GlideRoundTransform
                (mContext)).into(view);
        helper.setText(R.id.tvTitle,item.title);
    }
}
