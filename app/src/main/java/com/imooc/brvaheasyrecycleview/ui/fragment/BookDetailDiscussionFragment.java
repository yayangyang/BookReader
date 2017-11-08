package com.imooc.brvaheasyrecycleview.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.DiscussionList;
import com.imooc.brvaheasyrecycleview.Bean.support.SelectionEvent;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.BaseRVFragment;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.component.DaggerBookComponent;
import com.imooc.brvaheasyrecycleview.ui.activity.BookDiscussionDetailActivity;
import com.imooc.brvaheasyrecycleview.ui.adapter.BookDiscussionAdapter;
import com.imooc.brvaheasyrecycleview.ui.contract.BookDetailDiscussionContract;
import com.imooc.brvaheasyrecycleview.ui.presenter.BookDetailDiscussionPresenter;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class BookDetailDiscussionFragment extends BaseRVFragment<BookDetailDiscussionPresenter, DiscussionList.PostsBean,BaseViewHolder>
        implements BookDetailDiscussionContract.View,BaseQuickAdapter.OnItemClickListener {

    public final static String BUNDLE_ID = "bookId";

    public static BookDetailDiscussionFragment newInstance(String id) {
        BookDetailDiscussionFragment fragment = new BookDetailDiscussionFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    private String bookId;
    private ArrayList mArrayList=new ArrayList();

    private String sort = Constant.SortType.DEFAULT;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_easyrecyclerview;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerBookComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void initDatas() {
        EventBus.getDefault().register(this);
        bookId = getArguments().getString(BUNDLE_ID);
    }

    @Override
    public void configViews() {
        mRecyclerView.setHasFixedSize(true);
        initAdapter(BookDiscussionAdapter.class,
                R.layout.item_community_book_discussion_list,mArrayList, true, true);
        onRefresh();
    }

    @Override
    public void showBookDetailDiscussionList(List<DiscussionList.PostsBean> list, boolean isRefresh) {
        if(isRefresh){
            start=0;
            mAdapter.getData().clear();
            mAdapter.setEmptyView(inflate);
            mRecyclerView.scrollToPosition(0);
            mAdapter.setNewData(list);
            start = start + list.size();
        }else if(!isRefresh&&(list==null||list.isEmpty())){
            mAdapter.loadMoreEnd();
        }else{
            mAdapter.loadMoreComplete();
            mAdapter.addData(list);
            start = start + list.size();
        }
    }

    @Override
    public void showError() {
        loaddingError();
    }

    @Override
    public void complete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void initCategoryList(SelectionEvent event) {
        if (getUserVisibleHint()) {
            mSwipeRefreshLayout.setRefreshing(true);
            sort = event.sort;
            onRefresh();
        }
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        mPresenter.getBookDetailDiscussionList(bookId, sort, 0, limit);
    }

    @Override
    public void onLoadMoreRequested() {
        mPresenter.getBookDetailDiscussionList(bookId, sort, start, limit);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        DiscussionList.PostsBean data = (DiscussionList.PostsBean) mAdapter.getItem(position);
        BookDiscussionDetailActivity.startActivity(activity, data._id);
    }
}
