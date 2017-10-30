package com.imooc.brvaheasyrecycleview.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.BooksByTag;
import com.imooc.brvaheasyrecycleview.Bean.SearchDetail;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.BaseActivity;
import com.imooc.brvaheasyrecycleview.base.BaseRVActivity;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.component.DaggerBookComponent;
import com.imooc.brvaheasyrecycleview.ui.adapter.SearchAdapter;
import com.imooc.brvaheasyrecycleview.ui.contract.SearchByAuthorContract;
import com.imooc.brvaheasyrecycleview.ui.presenter.SearchByAuthorPresenter;
import com.imooc.brvaheasyrecycleview.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 2017/10/25.
 */

public class SearchByAuthorActivity extends BaseRVActivity<SearchDetail.SearchBooks,BaseViewHolder>
        implements SearchByAuthorContract.View {

    public static final String INTENT_AUTHOR = "author";

    public static void startActivity(Context context, String author) {
        context.startActivity(new Intent(context, SearchByAuthorActivity.class)
                .putExtra(INTENT_AUTHOR, author));
    }

    @Inject
    SearchByAuthorPresenter mPresenter;

    private String author = "";
    private ArrayList mArrayList=new ArrayList();

    @Override
    public int getLayoutId() {
        return R.layout.activity_common_recyclerview;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerBookComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void initToolBar() {
        author = getIntent().getStringExtra(INTENT_AUTHOR);
        mCommonToolbar.setTitle(author);
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void initDatas() {
        mRecyclerView.setHasFixedSize(true);
        initAdapter(SearchAdapter.class,R.layout.item_search_result_list,mArrayList, false, false);
        mAdapter.setOnItemClickListener(this);
        mPresenter.attachView(this);
    }

    @Override
    public void configViews() {
        mPresenter.getSearchResultList(author);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        SearchDetail.SearchBooks item = (SearchDetail.SearchBooks) adapter.getItem(position);
        BookDetailActivity.startActivity(mContext,item._id);
    }

    @Override
    public void showSearchResultList(List<BooksByTag.TagBook> list) {
        mAdapter.setEmptyView(inflate);
        mAdapter.getData().clear();
        List<SearchDetail.SearchBooks> mList = new ArrayList<>();
        for (BooksByTag.TagBook book : list) {
            mList.add(new SearchDetail.SearchBooks(book._id, book.title, book.author, book.cover, book.retentionRatio, book.latelyFollower));
        }
        mAdapter.setNewData(mList);
    }

    @Override
    public void showError() {

    }

    @Override
    public void complete() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
