package com.imooc.brvaheasyrecycleview.ui.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.BookSource;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.view.LetterView;

import java.util.List;

public class BookSourceAdapter extends BaseQuickAdapter<BookSource,BaseViewHolder> {

    public BookSourceAdapter(int layoutResId, @Nullable List<BookSource> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookSource item) {
        helper.setText(R.id.tv_source_title, item.host)
                .setText(R.id.tv_source_content, item.lastChapter);

        LetterView letterView = helper.getView(R.id.letter_view);
        letterView.setText(item.host);
    }
}
