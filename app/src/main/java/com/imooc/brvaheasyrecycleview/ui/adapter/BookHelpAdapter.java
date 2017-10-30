package com.imooc.brvaheasyrecycleview.ui.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.BookHelpList;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.manager.SettingManager;
import com.imooc.brvaheasyrecycleview.transform.GlideCircleTransform;
import com.imooc.brvaheasyrecycleview.utils.FormatUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/10/20.
 */

public class BookHelpAdapter extends BaseQuickAdapter<BookHelpList.HelpsBean,BaseViewHolder> {

    public BookHelpAdapter(@LayoutRes int layoutResId, @Nullable List<BookHelpList.HelpsBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookHelpList.HelpsBean item) {
        if (!SettingManager.getInstance().isNoneCover()) {
            ImageView view =helper.getView(R.id.ivBookCover);
            Glide.with(mContext).load(Constant.IMG_BASE_URL + item.author.avatar)
                    .placeholder(R.drawable.avatar_default) .transform(new GlideCircleTransform
                    (mContext)).into(view);
        } else {
            helper.setImageResource(R.id.ivBookCover, R.drawable.avatar_default);
        }

        helper.setText(R.id.tvBookType, String.format(mContext.getString(R.string
                .book_detail_user_lv), item.author.lv))
                .setText(R.id.tvTitle, item.title)
                .setText(R.id.tvHelpfulYes, item.commentCount + "");

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
    }
}
