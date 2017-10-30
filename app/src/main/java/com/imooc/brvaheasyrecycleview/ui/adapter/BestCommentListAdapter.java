package com.imooc.brvaheasyrecycleview.ui.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.CommentList;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.transform.GlideCircleTransform;

import java.util.List;

public class BestCommentListAdapter extends BaseQuickAdapter<CommentList.CommentsBean,BaseViewHolder> {

    public BestCommentListAdapter(@LayoutRes int layoutResId, @Nullable List<CommentList.CommentsBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CommentList.CommentsBean item) {
        ImageView view =helper.getView(R.id.ivBookCover);
        Glide.with(mContext).load(Constant.IMG_BASE_URL + item.author.avatar)
                .placeholder(R.drawable.avatar_default) .transform(new GlideCircleTransform
                (mContext)).into(view);
        helper.setText(R.id.tvBookTitle, item.author.nickname);
        helper.setText(R.id.tvContent, item.content);
        helper.setText(R.id.tvBookType, String.format(mContext.getString(R.string.book_detail_user_lv), item.author.lv));
        helper.setText(R.id.tvFloor, String.format(mContext.getString(R.string.comment_floor), item.floor));
        helper.setText(R.id.tvLikeCount, String.format(mContext.getString(R.string.comment_like_count), item.likeCount));
    }
}
