package com.imooc.brvaheasyrecycleview.ui.activity;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.imooc.brvaheasyrecycleview.Bean.RankingList;
import com.imooc.brvaheasyrecycleview.Bean.expand.ExpandItem;
import com.imooc.brvaheasyrecycleview.Bean.expand.Lv0;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.BaseActivity;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.component.DaggerFindComponent;
import com.imooc.brvaheasyrecycleview.ui.adapter.TopRankAdapter;
import com.imooc.brvaheasyrecycleview.ui.contract.TopRankContract;
import com.imooc.brvaheasyrecycleview.ui.presenter.TopRankPresenter;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/10/19.
 */

public class TopRankActivity extends BaseActivity implements TopRankContract.View{
    @BindView(R.id.elvMale)
    RecyclerView elvMale;
    @BindView(R.id.elvFeMale)
    RecyclerView elvFeMale;

    private TopRankAdapter mMaleAdapter,mFeMaleAdapter;
    private ArrayList<MultiItemEntity> mMaleItem=new ArrayList<>();
    private ArrayList<MultiItemEntity> mFeMaleItem=new ArrayList<>();

    @Inject
    TopRankPresenter mPresenter;

    public static void startActivity(FragmentActivity activity) {
        activity.startActivity(new Intent().setClass(activity,TopRankActivity.class));
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_top_rank;
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
        mCommonToolbar.setTitle("排行榜");
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void initDatas() {
        mMaleAdapter=new TopRankAdapter(mMaleItem);
        mFeMaleAdapter=new TopRankAdapter(mFeMaleItem);
        elvMale.setLayoutManager(new LinearLayoutManager(this));
        elvFeMale.setLayoutManager(new LinearLayoutManager(this));

        mPresenter.getRankList();
    }

    @Override
    public void configViews() {
        elvMale.setAdapter(mMaleAdapter);
        elvFeMale.setAdapter(mFeMaleAdapter);
        mPresenter.attachView(this);
    }

    private void updateMale(RankingList rankingList) {
        List<RankingList.MaleBean> list = rankingList.male;
        Lv0 lv0=new Lv0("别人家的排行榜");
        for (RankingList.MaleBean bean : list) {
            if (bean.collapse) { // 折叠
                lv0.addSubItem(new ExpandItem(bean));
            } else {
                Lv0 lv01 = new Lv0();
                lv01.setBean(bean);
                mMaleItem.add(lv01);
            }
        }
        if(lv0.hasSubItem()){
            mMaleItem.add(lv0);
        }
    }

    private void updateFeMale(RankingList rankingList) {
        List<RankingList.MaleBean> list = rankingList.female;
        Lv0 lv0=new Lv0("别人家的排行榜");
        for (RankingList.MaleBean bean : list) {
            if (bean.collapse) { // 折叠
                lv0.addSubItem(new ExpandItem(bean));
            } else {
                Lv0 lv01 = new Lv0();
                lv01.setBean(bean);
                mFeMaleItem.add(lv01);
            }
        }
        if(lv0.hasSubItem()){
            mFeMaleItem.add(lv0);
        }
    }

    @Override
    public void showRankList(RankingList rankingList) {
        LogUtils.e("showRankList");
        mMaleItem.clear();
        mFeMaleItem.clear();
        updateMale(rankingList);
        updateFeMale(rankingList);
        mMaleAdapter.notifyDataSetChanged();
        mFeMaleAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError() {

    }

    @Override
    public void complete() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

}
