package com.imooc.brvaheasyrecycleview.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.BookSource;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.BaseRVActivity;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.component.DaggerBookComponent;
import com.imooc.brvaheasyrecycleview.ui.adapter.BookSourceAdapter;
import com.imooc.brvaheasyrecycleview.ui.contract.BookSourceContract;
import com.imooc.brvaheasyrecycleview.ui.presenter.BookSourcePresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class BookSourceActivity extends BaseRVActivity<BookSource,BaseViewHolder>
        implements BookSourceContract.View,BaseQuickAdapter.OnItemClickListener {

    public static final String INTENT_BOOK_ID = "bookId";

    public static void start(Activity activity, String bookId, int reqId) {
        activity.startActivityForResult(new Intent(activity, BookSourceActivity.class)
                .putExtra(INTENT_BOOK_ID, bookId), reqId);
    }

    @Inject
    BookSourcePresenter mPresenter;

    private String bookId = "";

    private ArrayList mArrayList=new ArrayList();

    @Override
    public int getLayoutId() {
        return R.layout.activity_common_recyclerview;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerBookComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void initToolBar() {
        bookId = getIntent().getStringExtra(INTENT_BOOK_ID);
        mCommonToolbar.setTitle("选择来源");
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void initDatas() {
        initAdapter(BookSourceAdapter.class,
                R.layout.item_book_source,mArrayList, false, false);
    }

    @Override
    public void configViews() {
        mPresenter.attachView(this);
        mPresenter.getBookSource("summary", bookId);

        new AlertDialog.Builder(this)
                .setMessage("换源功能暂未实现，后续更新...")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        BookSource data = mAdapter.getItem(position);
        Intent intent = new Intent();
        intent.putExtra("source", data);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void showBookSource(List<BookSource> list) {
        mAdapter.setEmptyView(inflate);
        mAdapter.getData().clear();
        mAdapter.setNewData(list);
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
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

}
