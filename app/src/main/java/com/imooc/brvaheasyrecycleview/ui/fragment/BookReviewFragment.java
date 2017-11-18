package com.imooc.brvaheasyrecycleview.ui.fragment;

import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.BookHelpList;
import com.imooc.brvaheasyrecycleview.Bean.BookReviewList;
import com.imooc.brvaheasyrecycleview.Bean.support.SelectionEvent;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.BaseRVFragment;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.component.DaggerCommunityComponent;
import com.imooc.brvaheasyrecycleview.ui.activity.BookReviewDetailActivity;
import com.imooc.brvaheasyrecycleview.ui.adapter.BookHelpAdapter;
import com.imooc.brvaheasyrecycleview.ui.adapter.BookReviewAdapter;
import com.imooc.brvaheasyrecycleview.ui.contract.BookReviewContract;
import com.imooc.brvaheasyrecycleview.ui.presenter.BookReviewPresenter;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;

/**
 * Created by Administrator on 2017/10/20.
 */

public class BookReviewFragment extends BaseRVFragment<BookReviewPresenter, BookReviewList.ReviewsBean,BaseViewHolder>
        implements BookReviewContract.View {

    private String sort = Constant.SortType.DEFAULT;
    private String type = Constant.BookType.ALL;
    private String distillate = Constant.Distillate.ALL;
    private ArrayList mArrayList=new ArrayList();

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        BookReviewList.ReviewsBean data = mAdapter.getItem(position);
        BookReviewDetailActivity.startActivity(activity, data._id);
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
        mPresenter.getBookReviewList(sort,type,distillate,start,limit);
    }

    @Override
    public void onLoadMoreRequested() {
        LogUtils.e("onLoadMoreRequested");
        mPresenter.getBookReviewList(sort,type,distillate,start,limit);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void initCategoryList(SelectionEvent event){
        mSwipeRefreshLayout.setRefreshing(true);
        sort = event.sort;
        type=event.type;
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
        initAdapter(BookReviewAdapter.class,
                R.layout.item_community_book_help_list,mArrayList,true,true);
        onRefresh();
//        mPresenter.getBookReviewList(sort,type,distillate,start,3);
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
        LogUtils.e("complete");
//        mAdapter.setEnableLoadMore(true);
    }

    @Override
    public void showBookReviewList(List<BookReviewList.ReviewsBean> list, int start) {
        boolean isRefresh = start == 0;
        if(isRefresh){
            if(list!=null){
                LogUtils.e("不为空"+list.size());
            }
            LogUtils.e("刷新");
            this.start=0;
            mAdapter.getData().clear();
            mAdapter.setEmptyView(inflate);
            mRecyclerView.scrollToPosition(0);
            mAdapter.setNewData(list);
            this.start = start + list.size();
        }else if(!isRefresh&&(list==null||list.isEmpty())){
            LogUtils.e("loadMoreEnd");
            mAdapter.loadMoreEnd();
            LogUtils.e("loadMoreEnd");
        }else{
            mAdapter.loadMoreComplete();
            if(this.start>start){
                List<BookReviewList.ReviewsBean> reviewsBeans = mAdapter.getData().subList(0, start);
                reviewsBeans.addAll(list);
                mAdapter.setNewData(reviewsBeans);
            }else{
                mAdapter.addData(list);
            }
            LogUtils.e("loadMoreComplete"+list.size());
            this.start = start + list.size();
        }
    }
}
