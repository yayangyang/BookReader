package com.imooc.brvaheasyrecycleview.ui.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.BookListDetail;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.manager.SettingManager;
import com.imooc.brvaheasyrecycleview.transform.GlideCircleTransform;
import com.imooc.brvaheasyrecycleview.transform.GlideRoundTransform;

import java.util.List;

public class SubjectBookListDetailBooksAdapter extends BaseQuickAdapter<BookListDetail.BookListBean.BooksBean,BaseViewHolder> {

    public SubjectBookListDetailBooksAdapter(@LayoutRes int layoutResId, @Nullable List<BookListDetail.BookListBean.BooksBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookListDetail.BookListBean.BooksBean item) {
        if (!SettingManager.getInstance().isNoneCover()) {
            ImageView view =helper.getView(R.id.ivBookCover);
            Glide.with(mContext).load(Constant.IMG_BASE_URL + item.getBook().getCover())
                    .placeholder(R.drawable.avatar_default) .transform(new GlideRoundTransform
                    (mContext)).into(view);
        } else {
            helper.setImageResource(R.id.ivBookCover, R.drawable.cover_default);
        }

        helper.setText(R.id.tvBookListTitle, item.getBook().getTitle())
                .setText(R.id.tvBookAuthor, item.getBook().getAuthor())
                .setText(R.id.tvBookLatelyFollower, String.format(mContext.getResources().getString
                                (R.string.subject_book_list_detail_book_lately_follower),
                        item.getBook().getLatelyFollower()))
                .setText(R.id.tvBookWordCount, String.format(mContext.getResources().getString
                                (R.string.subject_book_list_detail_book_word_count),
                        item.getBook().getWordCount() / 10000))
                .setText(R.id.tvBookDetail, item.getBook().getLongIntro());
    }
}
