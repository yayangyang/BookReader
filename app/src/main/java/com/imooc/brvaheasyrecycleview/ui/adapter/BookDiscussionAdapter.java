package com.imooc.brvaheasyrecycleview.ui.adapter;

import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.DiscussionList;
import com.imooc.brvaheasyrecycleview.Bean.Disscussion;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.manager.SettingManager;
import com.imooc.brvaheasyrecycleview.transform.GlideCircleTransform;
import com.imooc.brvaheasyrecycleview.utils.FormatUtils;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;
import com.imooc.brvaheasyrecycleview.utils.ScreenUtils;

import java.util.List;

import static android.R.attr.data;

/**
 * Created by Administrator on 2017/10/19.
 */

public class BookDiscussionAdapter extends BaseQuickAdapter<DiscussionList.PostsBean,BaseViewHolder> {

    public BookDiscussionAdapter(@LayoutRes int layoutResId, @Nullable List<DiscussionList.PostsBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DiscussionList.PostsBean item) {
        LogUtils.e("convert");
        if (!SettingManager.getInstance().isNoneCover()) {
            ImageView view =helper.getView(R.id.ivBookCover);
            Glide.with(mContext).load(Constant.IMG_BASE_URL + item.author.avatar)
                    .placeholder(R.drawable.avatar_default) .transform(new GlideCircleTransform
                    (mContext)).into(view);
        } else {
            helper.setImageResource(R.id.ivBookCover, R.drawable.avatar_default);
        }

        helper.setText(R.id.tvBookTitle, item.author.nickname)
                .setText(R.id.tvBookType, String.format(mContext.getString(R.string.book_detail_user_lv), item.author.lv))
                .setText(R.id.tvTitle, item.title)
                .setText(R.id.tvHelpfulYes, item.commentCount + "")
                .setText(R.id.tvLikeCount, item.likeCount + "");

        try {
            TextView textView = helper.getView(R.id.tvHelpfulYes);
            if (item.type.equals("vote")) {
                Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_notif_vote);
                drawable.setBounds(0, 0, ScreenUtils.dpToPxInt(15), ScreenUtils.dpToPxInt(15));
                textView.setCompoundDrawables(drawable, null, null, null);
            } else {
                Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_notif_post);
                drawable.setBounds(0, 0, ScreenUtils.dpToPxInt(15), ScreenUtils.dpToPxInt(15));
                textView.setCompoundDrawables(drawable, null, null, null);
            }

            if (TextUtils.equals(item.state, "hot")) {
                helper.setVisible(R.id.tvHot, true);
                helper.setVisible(R.id.tvTime, false);
                helper.setVisible(R.id.tvDistillate, false);
            } else if (TextUtils.equals(item.state, "distillate")) {
                helper.setVisible(R.id.tvDistillate, true);
                helper.setVisible(R.id.tvHot, false);
                helper.setVisible(R.id.tvTime, false);
            } else {
                helper.setVisible(R.id.tvTime, true);
                helper.setVisible(R.id.tvHot, false);
                helper.setVisible(R.id.tvDistillate, false);
                helper.setText(R.id.tvTime, FormatUtils.getDescriptionTimeFromDateString(item.created));
            }
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }
    }
}
