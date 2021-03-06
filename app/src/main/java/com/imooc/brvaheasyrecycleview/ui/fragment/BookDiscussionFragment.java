package com.imooc.brvaheasyrecycleview.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.DiscussionList;
import com.imooc.brvaheasyrecycleview.Bean.Disscussion;
import com.imooc.brvaheasyrecycleview.Bean.HotReview;
import com.imooc.brvaheasyrecycleview.Bean.support.SelectionEvent;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.app.ReaderApplication;
import com.imooc.brvaheasyrecycleview.base.BaseActivity;
import com.imooc.brvaheasyrecycleview.base.BaseRVFragment;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.component.DaggerCommunityComponent;
import com.imooc.brvaheasyrecycleview.ui.activity.BookDiscussionDetailActivity;
import com.imooc.brvaheasyrecycleview.ui.adapter.BookDiscussionAdapter;
import com.imooc.brvaheasyrecycleview.ui.contract.BookDiscussionContract;
import com.imooc.brvaheasyrecycleview.ui.presenter.BookDiscussionPresenter;
import com.imooc.brvaheasyrecycleview.utils.AppUtils;
import com.imooc.brvaheasyrecycleview.utils.KeybordS;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/10/19.
 */

public class BookDiscussionFragment extends BaseRVFragment<BookDiscussionPresenter,
        DiscussionList.PostsBean,BaseViewHolder> implements BookDiscussionContract.View{

    private static final String BUNDLE_BLOCK="block";
    private String block = "ramble";
    private String sort = Constant.SortType.DEFAULT;
    private String distillate = Constant.Distillate.ALL;
    private ArrayList mArrayList=new ArrayList();

    public static BookDiscussionFragment newInstance(String block) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_BLOCK,block);
        BookDiscussionFragment fragment = new BookDiscussionFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

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
        mPresenter.getBookDisscussionList(block,sort,distillate,start,limit);
    }

    @Override
    public void onLoadMoreRequested() {
        LogUtils.e("onLoadMoreRequested");
        mPresenter.getBookDisscussionList(block,sort,distillate,start,limit);
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
        block = getArguments().getString(BUNDLE_BLOCK);
        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {
        LogUtils.e("configViews");
        initAdapter(BookDiscussionAdapter.class,
                R.layout.item_community_book_discussion_list,mArrayList,true,true);
        onRefresh();
//        mPresenter.getBookDisscussionList(block,sort,distillate,start,3);
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
    public void showBookDisscussionList(List<DiscussionList.PostsBean> list, int start) {
        boolean isRefresh = start == 0;
        if(isRefresh){
            if(list!=null){
                LogUtils.e("不为空"+list.size());
            }
            LogUtils.e("刷新");
            this.start=0;
            mAdapter.getData().clear();
            inflate.setVisibility(View.VISIBLE);
            mAdapter.setEmptyView(inflate);
            mRecyclerView.scrollToPosition(0);
            mAdapter.setNewData(list);
            this.start = start + list.size();
        }else if(!isRefresh&&list==null){
            LogUtils.e("loadMoreEnd");
            mAdapter.loadMoreEnd();
            LogUtils.e("loadMoreEnd");
        }else{
            mAdapter.loadMoreComplete();
            if(this.start>start){
                List<DiscussionList.PostsBean> postsBeans = mAdapter.getData().subList(0, start);
                postsBeans.addAll(list);
                mAdapter.setNewData(postsBeans);
            }else{
                mAdapter.addData(list);
            }
            LogUtils.e("loadMoreComplete"+list.size());
            this.start = start + list.size();
        }
    }

}
