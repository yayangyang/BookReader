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
import com.imooc.brvaheasyrecycleview.Bean.BookReviewList;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.BaseRVFragment;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.manager.SettingManager;
import com.imooc.brvaheasyrecycleview.transform.GlideCircleTransform;
import com.imooc.brvaheasyrecycleview.utils.FormatUtils;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;
import com.imooc.brvaheasyrecycleview.utils.ScreenUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/10/20.
 */

public class BookReviewAdapter extends BaseQuickAdapter<BookReviewList.ReviewsBean,BaseViewHolder> {

    public BookReviewAdapter(@LayoutRes int layoutResId, @Nullable List<BookReviewList.ReviewsBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookReviewList.ReviewsBean item) {
        LogUtils.e("convert");
        if (!SettingManager.getInstance().isNoneCover()) {
            ImageView view =helper.getView(R.id.ivBookCover);
            Glide.with(mContext).load(Constant.IMG_BASE_URL + item.book.cover)
                    .placeholder(R.drawable.avatar_default) .transform(new GlideCircleTransform
                    (mContext)).into(view);
        } else {
            helper.setImageResource(R.id.ivBookCover, R.drawable.cover_default);
        }

        helper.setText(R.id.tvBookTitle, item.book.title)
                .setText(R.id.tvBookType, String.format(mContext.getString(R.string.book_review_book_type), Constant.bookType.get(item.book.type)))
                .setText(R.id.tvTitle, item.title)
                .setText(R.id.tvHelpfulYes, String.format(mContext.getString(R.string.book_review_helpful_yes), item.helpful.yes));

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

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        LogUtils.e("onBindViewHolder");
        super.onBindViewHolder(holder, position);
    }
}
