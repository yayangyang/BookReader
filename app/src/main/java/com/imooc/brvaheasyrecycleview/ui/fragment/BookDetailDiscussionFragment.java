package com.imooc.brvaheasyrecycleview.ui.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.BookReview;
import com.imooc.brvaheasyrecycleview.Bean.DiscussionList;
import com.imooc.brvaheasyrecycleview.Bean.support.SelectionEvent;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.BaseRVFragment;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.component.DaggerBookComponent;
import com.imooc.brvaheasyrecycleview.ui.activity.BookDiscussionDetailActivity;
import com.imooc.brvaheasyrecycleview.ui.activity.PublishOptionActivity;
import com.imooc.brvaheasyrecycleview.ui.activity.PublishReviewActivity;
import com.imooc.brvaheasyrecycleview.ui.adapter.BookDiscussionAdapter;
import com.imooc.brvaheasyrecycleview.ui.contract.BookDetailDiscussionContract;
import com.imooc.brvaheasyrecycleview.ui.presenter.BookDetailDiscussionPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

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
    private BookReview mBookReview=new BookReview();
    private BookReview.ReviewBean reviewBean = new BookReview.ReviewBean();
    private BookReview.ReviewBean.BookBean bookBean = new BookReview.ReviewBean.BookBean();

    private String sort = Constant.SortType.DEFAULT;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_book_detail_discussion;
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

        bookBean._id=bookId;
        reviewBean.book=bookBean;
        mBookReview.review=reviewBean;
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
        loaddingError();mAdapter.notifyDataSetChanged();
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

    @OnClick(R.id.bt_create_reviews)
    public void create_reviews(){
        new AlertDialog.Builder(mContext)
                .setTitle("提示")
                .setSingleChoiceItems(
                        new String[]{"话题", "投票"}, 0,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which==0){
                                    PublishOptionActivity.startActivity(getActivity(),false,false,mBookReview);
                                }else{
                                    PublishOptionActivity.startActivity(getActivity(),false,true,mBookReview);
                                }
                                dialog.dismiss();
                            }
                        }).create().show();
    }

}
