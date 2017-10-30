package com.imooc.brvaheasyrecycleview.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.HotReview;
import com.imooc.brvaheasyrecycleview.Bean.support.SelectionEvent;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.BaseRVFragment;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.component.DaggerBookComponent;
import com.imooc.brvaheasyrecycleview.ui.activity.BookReviewDetailActivity;
import com.imooc.brvaheasyrecycleview.ui.adapter.BookDetailReviewAdapter;
import com.imooc.brvaheasyrecycleview.ui.contract.BookDetailReviewContract;
import com.imooc.brvaheasyrecycleview.ui.presenter.BookDetailReviewPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class BookDetailReviewFragment extends BaseRVFragment<BookDetailReviewPresenter, HotReview.Reviews,BaseViewHolder>
        implements BookDetailReviewContract.View {

    public final static String BUNDLE_ID = "bookId";

    public static BookDetailReviewFragment newInstance(String id) {
        BookDetailReviewFragment fragment = new BookDetailReviewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    private String bookId;
    private ArrayList mArrayList=new ArrayList();

    private String sort = Constant.SortType.DEFAULT;
    private String type = Constant.BookType.ALL;

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
        initAdapter(BookDetailReviewAdapter.class,
                R.layout.item_book_detai_hot_review_list,mArrayList, true, true);
        onRefresh();
    }

    @Override
    public void showBookDetailReviewList(List<HotReview.Reviews> list, boolean isRefresh) {
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
        mPresenter.getBookDetailReviewList(bookId, sort, 0, limit);
    }

    @Override
    public void onLoadMoreRequested() {
        mPresenter.getBookDetailReviewList(sort, type, start, limit);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        HotReview.Reviews item = mAdapter.getItem(position);
        BookReviewDetailActivity.startActivity(activity, item._id);
    }
}
