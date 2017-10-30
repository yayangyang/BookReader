package com.imooc.brvaheasyrecycleview.ui.activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.CategoryList;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.BaseActivity;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.component.DaggerFindComponent;
import com.imooc.brvaheasyrecycleview.ui.adapter.TopCategoryListAdapter;
import com.imooc.brvaheasyrecycleview.ui.contract.TopCategoryListContract;
import com.imooc.brvaheasyrecycleview.ui.presenter.TopCategoryListPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public class TopCategoryListActivity extends BaseActivity
        implements TopCategoryListContract.View,BaseQuickAdapter.OnItemClickListener{

    @BindView(R.id.rvMaleCategory)
    RecyclerView mRvMaleCategory;
    @BindView(R.id.rvFemaleCategory)
    RecyclerView mRvFeMaleCategory;

    @Inject
    TopCategoryListPresenter mPresenter;

    private TopCategoryListAdapter mMaleCategoryListAdapter;
    private TopCategoryListAdapter mFemaleCategoryListAdapter;
    private List<CategoryList.MaleBean> mMaleCategoryList = new ArrayList<>();
    private List<CategoryList.MaleBean> mFemaleCategoryList = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_top_category_list;
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
        mCommonToolbar.setTitle(getString(R.string.category));
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {
        showDialog();
        mRvMaleCategory.setHasFixedSize(true);
        mRvMaleCategory.setLayoutManager(new GridLayoutManager(this, 3));
        mMaleCategoryListAdapter = new TopCategoryListAdapter(R.layout.item_top_category_list, mMaleCategoryList);
        mMaleCategoryListAdapter.setGender(Constant.Gender.MALE);
        mMaleCategoryListAdapter.setOnItemClickListener(this);
        mRvMaleCategory.setAdapter(mMaleCategoryListAdapter);
//        mRvMaleCategory.addItemDecoration(new SupportGridItemDecoration(this));
        mRvFeMaleCategory.setHasFixedSize(true);
        mRvFeMaleCategory.setLayoutManager(new GridLayoutManager(this, 3));
        mFemaleCategoryListAdapter = new TopCategoryListAdapter(R.layout.item_top_category_list, mFemaleCategoryList);
        mFemaleCategoryListAdapter.setGender(Constant.Gender.FEMALE);
        mFemaleCategoryListAdapter.setOnItemClickListener(this);
        mRvFeMaleCategory.setAdapter(mFemaleCategoryListAdapter);
//        mRvFeMaleCategory.addItemDecoration(new SupportGridItemDecoration(this));

        mPresenter.attachView(this);
        mPresenter.getCategoryList();
    }


    @Override
    public void showCategoryList(CategoryList data) {
        mMaleCategoryList.clear();
        mFemaleCategoryList.clear();
        mMaleCategoryList.addAll(data.male);
        mFemaleCategoryList.addAll(data.female);
        mMaleCategoryListAdapter.notifyDataSetChanged();
        mFemaleCategoryListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError() {

    }

    @Override
    public void complete() {
        dismissDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        TopCategoryListAdapter mAdapter = (TopCategoryListAdapter) adapter;
        CategoryList.MaleBean item = ((TopCategoryListAdapter) adapter).getItem(position);
        SubCategoryListActivity.startActivity(mContext, item.name, mAdapter.getGender());
    }
}
