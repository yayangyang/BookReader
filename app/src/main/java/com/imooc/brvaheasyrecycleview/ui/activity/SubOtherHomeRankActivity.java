package com.imooc.brvaheasyrecycleview.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.BooksByCats;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.BaseRVActivity;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.component.DaggerFindComponent;
import com.imooc.brvaheasyrecycleview.ui.adapter.SubCategoryAdapter;
import com.imooc.brvaheasyrecycleview.ui.contract.SubRankContract;
import com.imooc.brvaheasyrecycleview.ui.presenter.SubRankPresenter;

import java.util.ArrayList;

import javax.inject.Inject;

import static android.R.attr.data;

public class SubOtherHomeRankActivity extends BaseRVActivity<BooksByCats.BooksBean,BaseViewHolder>
        implements SubRankContract.View {

    public final static String BUNDLE_ID = "_id";
    public static final String INTENT_TITLE = "title";
    private String id;
    private String title;

    private ArrayList mArrayList=new ArrayList();

    public static void startActivity(Context context, String id, String title) {
        context.startActivity(new Intent(context, SubOtherHomeRankActivity.class)
                .putExtra(INTENT_TITLE, title).putExtra(BUNDLE_ID, id));
    }

    @Inject
    SubRankPresenter mPresenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_subject_book_list_detail;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerFindComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void initToolBar() {
        title = getIntent().getStringExtra(INTENT_TITLE).split(" ")[0];
        id = getIntent().getStringExtra(BUNDLE_ID);

        mCommonToolbar.setTitle(title);
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {
        initAdapter(SubCategoryAdapter.class,R.layout.item_sub_category_list,mArrayList, true, false);
        mPresenter.attachView(this);
        onRefresh();
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
        BookDetailActivity.startActivity(this, mAdapter.getItem(position)._id);
    }

    @Override
    public void onRefresh() {
        mPresenter.getRankList(id);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.detachView();
    }

}
