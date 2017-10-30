package com.imooc.brvaheasyrecycleview.ui.fragment;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.BookHelpList;
import com.imooc.brvaheasyrecycleview.Bean.DiscussionList;
import com.imooc.brvaheasyrecycleview.Bean.support.SelectionEvent;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.BaseRVFragment;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.component.DaggerCommunityComponent;
import com.imooc.brvaheasyrecycleview.ui.activity.BookDiscussionDetailActivity;
import com.imooc.brvaheasyrecycleview.ui.adapter.BookDiscussionAdapter;
import com.imooc.brvaheasyrecycleview.ui.adapter.BookHelpAdapter;
import com.imooc.brvaheasyrecycleview.ui.contract.BookHelpContract;
import com.imooc.brvaheasyrecycleview.ui.contract.GirlBookDiscussionContract;
import com.imooc.brvaheasyrecycleview.ui.presenter.BookHelpPresenter;
import com.imooc.brvaheasyrecycleview.ui.presenter.GirlBookDiscussionPresenter;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/20.
 */

public class GirlBookDiscussionFragment extends BaseRVFragment<GirlBookDiscussionPresenter,
        DiscussionList.PostsBean,BaseViewHolder> implements GirlBookDiscussionContract.View{

    private String sort = Constant.SortType.DEFAULT;
    private String distillate = Constant.Distillate.ALL;
    private ArrayList mArrayList=new ArrayList();

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        DiscussionList.PostsBean data = mAdapter.getItem(position);
        BookDiscussionDetailActivity.startActivity(activity, data._id);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_easyrecyclerview;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerCommunityComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        mAdapter.setEnableLoadMore(false);
        mPresenter.getGirlBookDisscussionList(sort,distillate,start,limit);
    }

    @Override
    public void onLoadMoreRequested() {
        LogUtils.e("onLoadMoreRequested");
        mPresenter.getGirlBookDisscussionList(sort,distillate,start,limit);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void initCategoryList(SelectionEvent event){
        mSwipeRefreshLayout.setRefreshing(true);
        sort = event.sort;
        distillate = event.distillate;
        onRefresh();
    }

    @Override
    public void initDatas() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {
        LogUtils.e("configViews");
        initAdapter(BookDiscussionAdapter.class,
                R.layout.item_community_book_discussion_list,mArrayList,true,true);
        onRefresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void showError() {

    }

    public void showMyError(boolean isRefresh) {
        loaddingError();
        if(!isRefresh){
            mAdapter.loadMoreFail();
        }
    }

    @Override
    public void complete() {
        mSwipeRefreshLayout.setRefreshing(false);
//        mAdapter.setEnableLoadMore(true);
    }

    @Override
    public void showGirlBookDisscussionList(List<DiscussionList.PostsBean> list, boolean isRefresh) {
        if(isRefresh){
            if(list!=null){
                LogUtils.e("不为空"+list.size());
            }
            LogUtils.e("刷新");
            start=0;
            mAdapter.getData().clear();
            mAdapter.setEmptyView(inflate);
            mRecyclerView.scrollToPosition(0);
            mAdapter.setNewData(list);
            start = start + list.size();
        }else if(!isRefresh&&(list==null||list.isEmpty())){
            LogUtils.e("loadMoreEnd");
            mAdapter.loadMoreEnd();
            LogUtils.e("loadMoreEnd");
        }else{
            LogUtils.e("loadMoreComplete");
            mAdapter.loadMoreComplete();
            mAdapter.addData(list);
            start = start + list.size();
        }
    }
}
