package com.imooc.brvaheasyrecycleview.ui.fragment;

import android.os.Bundle;
import android.view.View;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.BookLists;
import com.imooc.brvaheasyrecycleview.Bean.support.TagEvent;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.BaseRVFragment;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.component.DaggerFindComponent;
import com.imooc.brvaheasyrecycleview.manager.SettingManager;
import com.imooc.brvaheasyrecycleview.ui.activity.SubjectBookListDetailActivity;
import com.imooc.brvaheasyrecycleview.ui.adapter.SubjectBookListAdapter;
import com.imooc.brvaheasyrecycleview.ui.contract.SubjectFragmentContract;
import com.imooc.brvaheasyrecycleview.ui.presenter.SubjectFragmentPresenter;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class SubjectFragment extends BaseRVFragment<SubjectFragmentPresenter, BookLists.BookListsBean,BaseViewHolder>
        implements SubjectFragmentContract.View {

    public final static String BUNDLE_TAG = "tag";
    public final static String BUNDLE_TAB = "tab";

    public String currendTag;
    public int currentTab;

    public String duration = "";
    public String sort = "";

    private ArrayList mArrayList=new ArrayList();

    public static SubjectFragment newInstance(String tag, int tab) {
        SubjectFragment fragment = new SubjectFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_TAG, tag);
        bundle.putInt(BUNDLE_TAB, tab);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_easyrecyclerview;
    }

    @Override
    public void initDatas() {
        EventBus.getDefault().register(this);

        currentTab = getArguments().getInt(BUNDLE_TAB);
        switch (currentTab) {
            case 0:
                duration = "last-seven-days";
                sort = "collectorCount";
                break;
            case 1:
                duration = "all";
                sort = "created";
                break;
            case 2:
            default:
                duration = "all";
                sort = "collectorCount";
                break;
        }
    }

    @Override
    public void configViews() {
        initAdapter(SubjectBookListAdapter.class,R.layout.item_sub_category_list,mArrayList, true, true);
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
    public void showBookList(List<BookLists.BookListsBean> list, boolean isRefresh) {
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
    public void showMyError(boolean isRefresh) {
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
    public void initCategoryList(TagEvent event) {
        LogUtils.e("initCategoryList");
        currendTag = event.tag;
        if (getUserVisibleHint()) {//大概理解为是否当前fragment显示中
            LogUtils.e("duration:"+duration);
            LogUtils.e("sort:"+sort);
            LogUtils.e("currendTag:"+currendTag);
            mPresenter.getBookLists(duration, sort, 0, limit, currendTag, SettingManager.getInstance().getUserChooseSex());
        }
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        SubjectBookListDetailActivity.startActivity(activity, mAdapter.getItem(position));
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        mAdapter.setEnableLoadMore(false);
        LogUtils.e("duration"+duration+"sort"+sort+"currendTag"+currendTag);
        mPresenter.getBookLists(duration, sort, 0, limit, currendTag, SettingManager.getInstance().getUserChooseSex());
    }

    @Override
    public void onLoadMoreRequested() {
        mPresenter.getBookLists(duration, sort, start, limit, currendTag, SettingManager.getInstance().getUserChooseSex());
    }

}
