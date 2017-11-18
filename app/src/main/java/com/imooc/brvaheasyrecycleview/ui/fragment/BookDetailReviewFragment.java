package com.imooc.brvaheasyrecycleview.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.BookReview;
import com.imooc.brvaheasyrecycleview.Bean.HotReview;
import com.imooc.brvaheasyrecycleview.Bean.support.SelectionEvent;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.app.ReaderApplication;
import com.imooc.brvaheasyrecycleview.base.BaseRVFragment;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.component.DaggerBookComponent;
import com.imooc.brvaheasyrecycleview.manager.SettingManager;
import com.imooc.brvaheasyrecycleview.ui.activity.BookReviewDetailActivity;
import com.imooc.brvaheasyrecycleview.ui.activity.PublishReviewActivity;
import com.imooc.brvaheasyrecycleview.ui.adapter.BookDetailReviewAdapter;
import com.imooc.brvaheasyrecycleview.ui.contract.BookDetailReviewContract;
import com.imooc.brvaheasyrecycleview.ui.presenter.BookDetailReviewPresenter;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;
import com.imooc.brvaheasyrecycleview.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class BookDetailReviewFragment extends BaseRVFragment<BookDetailReviewPresenter, HotReview.Reviews,BaseViewHolder>
        implements BookDetailReviewContract.View {

    public static BookDetailReviewFragment newInstance(String id) {
        BookDetailReviewFragment fragment = new BookDetailReviewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constant.BOOK_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    private String bookId;
    private BookReview mBookReview=new BookReview();
    private BookReview.ReviewBean reviewBean = new BookReview.ReviewBean();
    private BookReview.ReviewBean.BookBean bookBean = new BookReview.ReviewBean.BookBean();
    private ArrayList mArrayList=new ArrayList();

    private AlertDialog mAlertDialog;

    private String sort = Constant.SortType.DEFAULT;
    private String type = Constant.BookType.ALL;

    @BindView(R.id.bt_create_reviews)
    FloatingActionButton bt_create_reviews;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_book_detail_review;
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
        bookId = getArguments().getString(Constant.BOOK_ID);
    }

    @Override
    public void configViews() {
        initAdapter(BookDetailReviewAdapter.class,
                R.layout.item_book_detai_hot_review_list,mArrayList, true, true);
        onRefresh();
    }

    @Override
    public void showBookDetailReviewList(List<HotReview.Reviews> list, int start) {
        boolean isRefresh = start == 0;
        if(isRefresh){
            this.start=0;
            mAdapter.getData().clear();
            mAdapter.setEmptyView(inflate);
            mRecyclerView.scrollToPosition(0);
            mAdapter.setNewData(list);
            this.start = start + list.size();

            if(list==null){
                bt_create_reviews.setVisibility(View.GONE);
            }else{
                bt_create_reviews.setVisibility(View.VISIBLE);
            }
        }else if(!isRefresh&&(list==null||list.isEmpty())){
            mAdapter.loadMoreEnd();
        }else{
//            mAdapter.loadMoreComplete();
//            mAdapter.addData(list);
//            start = start + list.size();

            mAdapter.loadMoreComplete();
            if(this.start>start){
                List<HotReview.Reviews> reviews = mAdapter.getData().subList(0, start);
                reviews.addAll(list);
                mAdapter.setNewData(reviews);
            }else{
                mAdapter.addData(list);
            }
            LogUtils.e("loadMoreComplete"+list.size());
            this.start = start + list.size();
        }
    }

    @Override
    public void showHistoryBookReview(final BookReview bookReview) {
        if(bookReview.ok){
            if(bookReview.review!=null){
                new AlertDialog.Builder(mContext)
                        .setTitle("再次编辑")
                        .setMessage("即将载入之前发布的书评")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                bookBean._id=bookId;
                                bookReview.review.book=bookBean;
                                PublishReviewActivity.startActivity(getActivity(),bookReview);
                            }
                        }).create().show();
            }else{
                bookBean._id=bookId;
                reviewBean.book=bookBean;
                mBookReview.review=reviewBean;
                PublishReviewActivity.startActivity(getActivity(),mBookReview);
            }
        }else{
            ReaderApplication.sLogin=null;
            SettingManager.getInstance().saveLoginInfo(ReaderApplication.sLogin);
            ToastUtils.showToast("发送失败,token可能过期,请重新登录");
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
//        loaddingError();
    }

    @Override
    public void complete() {
        mSwipeRefreshLayout.setRefreshing(false);
//        ToastUtils.showToast("隐藏进度条");
        if(mAlertDialog!=null){
            mAlertDialog.hide();
        }
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
        mAdapter.setEnableLoadMore(false);
        mPresenter.getBookDetailReviewList(bookId, sort, 0, limit);
    }

    @Override
    public void onLoadMoreRequested() {
        mPresenter.getBookDetailReviewList(bookId, sort, start, limit);
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

    @OnClick(R.id.bt_create_reviews)
    public void create_reviews(){
        if(ReaderApplication.sLogin==null||TextUtils.isEmpty(ReaderApplication.sLogin.token)){
            bookBean._id=bookId;
            reviewBean.book=bookBean;
            mBookReview.review=reviewBean;
            PublishReviewActivity.startActivity(getActivity(),mBookReview);
        }else{
            mPresenter.getHistoryBookReview(bookId,ReaderApplication.sLogin.token);
            if(mAlertDialog==null){
                mAlertDialog=new AlertDialog.Builder(getActivity())
                        .setView(R.layout.dialog_book_review)
                        .create();
            }
            mAlertDialog.show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
