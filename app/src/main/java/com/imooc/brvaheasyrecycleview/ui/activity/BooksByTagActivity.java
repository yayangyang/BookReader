package com.imooc.brvaheasyrecycleview.ui.activity;


import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.BooksByTag;
import com.imooc.brvaheasyrecycleview.Bean.DiscussionList;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.api.BookApi;
import com.imooc.brvaheasyrecycleview.base.BaseActivity;
import com.imooc.brvaheasyrecycleview.base.BaseRVActivity;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.component.DaggerBookComponent;
import com.imooc.brvaheasyrecycleview.ui.adapter.BooksByTagAdapter;
import com.imooc.brvaheasyrecycleview.ui.contract.BooksByTagContract;
import com.imooc.brvaheasyrecycleview.ui.presenter.BooksByTagPresenter;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

import static android.R.attr.data;
import static android.R.attr.type;

public class BooksByTagActivity extends BaseRVActivity<BooksByTag.TagBook,BaseViewHolder>
        implements BooksByTagContract.View,BaseQuickAdapter.OnItemClickListener {

    private ArrayList<BooksByTag.TagBook> mList = new ArrayList<>();

    private String tag="";

    @Inject
    BooksByTagPresenter mPresenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_books_by_tag;
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
        mCommonToolbar.setTitle(getIntent().getStringExtra("tag"));
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        mAdapter.setEnableLoadMore(false);
        mPresenter.getBooksByTag(tag, start, limit);
    }

    @Override
    public void onLoadMoreRequested() {
        mPresenter.getBooksByTag(tag, start, limit);
    }

    @Override
    public void initDatas() {
        tag = getIntent().getStringExtra("tag");
    }

    @Override
    public void configViews() {
        mRecyclerView.setHasFixedSize(true);
        initAdapter(BooksByTagAdapter.class,R.layout.item_tag_book_list, mList,true,true);
        mPresenter.attachView(this);

        onRefresh();
    }


    @Override
    public void showBooksByTag(List<BooksByTag.TagBook> list, int start) {
        boolean isRefresh = start == 0;
        if(isRefresh){
            this.start=0;
            mAdapter.getData().clear();
            mAdapter.setEmptyView(inflate);
            mRecyclerView.scrollToPosition(0);
            mAdapter.setNewData(list);
            this.start = start + list.size();
        }else if(!isRefresh&&(list==null||list.isEmpty())){
            mAdapter.loadMoreEnd();
        }else{
            mAdapter.loadMoreComplete();
            if(this.start>start){
                List<BooksByTag.TagBook> tagBooks = mAdapter.getData().subList(0, start);
                tagBooks.addAll(list);
                mAdapter.setNewData(tagBooks);
            }else{
                mAdapter.addData(list);
            }
            LogUtils.e("loadMoreComplete"+list.size());
            this.start = start + list.size();
        }
    }

    @Override
    public void showError(boolean isRefresh) {
        loaddingError();
        if(!isRefresh){
            mAdapter.loadMoreFail();
        }
    }

    @Override
    public void showError() {

    }

    @Override
    public void complete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        BooksByTag.TagBook item = (BooksByTag.TagBook) adapter.getItem(position);
        startActivity(new Intent(BooksByTagActivity.this, BookDetailActivity.class)
                .putExtra("bookId", item._id));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }
}
