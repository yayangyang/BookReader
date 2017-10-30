package com.imooc.brvaheasyrecycleview.ui.fragment;

import android.os.Bundle;
import android.view.View;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.BooksByCats;
import com.imooc.brvaheasyrecycleview.Bean.support.SubEvent;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.BaseRVFragment;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.component.DaggerFindComponent;
import com.imooc.brvaheasyrecycleview.ui.activity.BookDetailActivity;
import com.imooc.brvaheasyrecycleview.ui.adapter.SubCategoryAdapter;
import com.imooc.brvaheasyrecycleview.ui.contract.SubCategoryFragmentContract;
import com.imooc.brvaheasyrecycleview.ui.presenter.SubCategoryFragmentPresenter;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class SubCategoryFragment extends BaseRVFragment<SubCategoryFragmentPresenter, BooksByCats.BooksBean,BaseViewHolder>
        implements SubCategoryFragmentContract.View {

    public final static String BUNDLE_MAJOR = "major";
    public final static String BUNDLE_MINOR = "minor";
    public final static String BUNDLE_GENDER = "gender";
    public final static String BUNDLE_TYPE = "type";

    public static SubCategoryFragment newInstance(String major, String minor, String gender,
                                                  @Constant.CateType String type) {
        SubCategoryFragment fragment = new SubCategoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_MAJOR, major);
        bundle.putString(BUNDLE_GENDER, gender);
        bundle.putString(BUNDLE_MINOR, minor);
        bundle.putString(BUNDLE_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    private String major = "";
    private String minor = "";
    private String gender = "";
    private String type = "";

    private ArrayList mArrayList=new ArrayList();

    @Override
    public int getLayoutResId() {
        return R.layout.activity_easyrecyclerview;
    }

    @Override
    public void initDatas() {
        EventBus.getDefault().register(this);
        major = getArguments().getString(BUNDLE_MAJOR);
        gender = getArguments().getString(BUNDLE_GENDER);
        minor = getArguments().getString(BUNDLE_MINOR);
        type = getArguments().getString(BUNDLE_TYPE);
    }

    @Override
    public void configViews() {
        initAdapter(SubCategoryAdapter.class,
                R.layout.item_sub_category_list,mArrayList, true, true);
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
    public void showCategoryList(BooksByCats data, boolean isRefresh) {
        if(isRefresh){
            start=0;
            mAdapter.getData().clear();
            mAdapter.setEmptyView(inflate);
            mRecyclerView.scrollToPosition(0);
            mAdapter.setNewData(data.books);
            start = start + data.books.size();
        }else if(!isRefresh&&(data.books==null||data.books.isEmpty())){
            mAdapter.loadMoreEnd();
        }else{
            mAdapter.loadMoreComplete();
            mAdapter.addData(data.books);
            start = start + data.books.size();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void initCategoryList(SubEvent event) {
        minor = event.minor;
        String type = event.type;
        if (this.type.equals(type)) {
            onRefresh();
        }
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        BooksByCats.BooksBean data = mAdapter.getItem(position);
        BookDetailActivity.startActivity(activity, data._id);
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        LogUtils.e("major:"+major);
        LogUtils.e("minor:"+minor);
        LogUtils.e("type:"+type);
        mPresenter.getCategoryList(gender, major, minor, this.type, 0, limit);
    }


    @Override
    public void onLoadMoreRequested() {
        mPresenter.getCategoryList(gender, major, minor, this.type, start, limit);
    }

}
