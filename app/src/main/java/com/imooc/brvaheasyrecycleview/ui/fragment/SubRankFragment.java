package com.imooc.brvaheasyrecycleview.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.BooksByCats;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.BaseRVFragment;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.component.DaggerFindComponent;
import com.imooc.brvaheasyrecycleview.ui.activity.BookDetailActivity;
import com.imooc.brvaheasyrecycleview.ui.adapter.SubCategoryAdapter;
import com.imooc.brvaheasyrecycleview.ui.contract.SubRankContract;
import com.imooc.brvaheasyrecycleview.ui.presenter.SubRankPresenter;

import java.util.ArrayList;

public class SubRankFragment extends BaseRVFragment<SubRankPresenter, BooksByCats.BooksBean,BaseViewHolder> implements SubRankContract.View {

    public final static String BUNDLE_ID = "_id";

    public static SubRankFragment newInstance(String id) {
        SubRankFragment fragment = new SubRankFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    private String id;
    private ArrayList mArrayLis=new ArrayList();

    @Override
    public int getLayoutResId() {
        return R.layout.activity_easyrecyclerview;
    }

    @Override
    public void initDatas() {
        id = getArguments().getString(BUNDLE_ID);
    }

    @Override
    public void configViews() {
        initAdapter(SubCategoryAdapter.class,R.layout.item_sub_category_list,mArrayLis, true, false);

        onRefresh();
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerFindComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void showRankList(BooksByCats data) {
        mAdapter.getData().clear();
        mAdapter.setEmptyView(inflate);
        mRecyclerView.scrollToPosition(0);
        mAdapter.setNewData(data.books);
    }

    @Override
    public void showError() {
        loaddingError();
    }

    @Override
    public void complete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        BookDetailActivity.startActivity(activity, mAdapter.getItem(position)._id);
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        mPresenter.getRankList(id);
    }

}
