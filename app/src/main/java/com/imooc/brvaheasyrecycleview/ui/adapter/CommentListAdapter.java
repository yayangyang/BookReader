package com.imooc.brvaheasyrecycleview.ui.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.CommentList;
import com.imooc.brvaheasyrecycleview.Bean.user.Login;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.manager.SettingManager;
import com.imooc.brvaheasyrecycleview.transform.GlideCircleTransform;
import com.imooc.brvaheasyrecycleview.utils.FormatUtils;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/10/23.
 */

public class CommentListAdapter extends BaseQuickAdapter<CommentList.CommentsBean,BaseViewHolder> {

    public CommentListAdapter(@LayoutRes int layoutResId, @Nullable List<CommentList.CommentsBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CommentList.CommentsBean item) {
        if (!SettingManager.getInstance().isNoneCover()) {
            ImageView view =helper.getView(R.id.ivBookCover);
            Glide.with(mContext).load(Constant.IMG_BASE_URL + item.author.avatar)
                    .placeholder(R.drawable.avatar_default) .transform(new GlideCircleTransform
                    (mContext)).into(view);
        } else {
            helper.setImageResource(R.id.ivBookCover, R.drawable.avatar_default);
        }

        helper.setText(R.id.tvBookTitle, item.author.nickname)
                .setText(R.id.tvContent, item.content)
                .setText(R.id.tvBookType, String.format(mContext.getString(R.string.book_detail_user_lv), item.author.lv))
                .setText(R.id.tvFloor, String.format(mContext.getString(R.string.comment_floor), item.floor))
                .setText(R.id.tvTime, FormatUtils.getDescriptionTimeFromDateString(item.created));
        LogUtils.e("nickname:"+item.author.nickname);
        LogUtils.e("item.created:"+item.created);

        if (item.replyTo == null) {
            helper.setVisible(R.id.tvReplyNickName, false);
            helper.setVisible(R.id.tvReplyFloor, false);
        } else {
            helper.setText(R.id.tvReplyNickName, String.format(mContext.getString(R.string.comment_reply_nickname), item.replyTo.author.nickname))
                    .setText(R.id.tvReplyFloor, String.format(mContext.getString(R.string.comment_reply_floor), item.replyTo.floor));
            helper.setVisible(R.id.tvReplyNickName, true);
            helper.setVisible(R.id.tvReplyFloor, true);
        }
    }
}
