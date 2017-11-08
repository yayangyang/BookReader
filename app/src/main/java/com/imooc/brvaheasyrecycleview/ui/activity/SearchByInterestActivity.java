package com.imooc.brvaheasyrecycleview.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.BooksByTag;
import com.imooc.brvaheasyrecycleview.Bean.InterestBookList;
import com.imooc.brvaheasyrecycleview.Bean.SearchDetail;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.BaseRVActivity;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.component.DaggerBookComponent;
import com.imooc.brvaheasyrecycleview.ui.adapter.SearchAdapter;
import com.imooc.brvaheasyrecycleview.ui.adapter.SearchInterestAdapter;
import com.imooc.brvaheasyrecycleview.ui.presenter.SearchByAuthorPresenter;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Administrator on 2017/11/7.
 */

public class SearchByInterestActivity extends BaseRVActivity<InterestBookList.InterestBook,BaseViewHolder>{
    public static final String INTENT_INTEREST_BOOK = "interestBookList";

    public static void startActivity(Context context, ArrayList<InterestBookList.InterestBook> interestBookList) {
        context.startActivity(new Intent(context, SearchByInterestActivity.class)
                .putExtra(INTENT_INTEREST_BOOK, interestBookList));
    }

    private ArrayList<InterestBookList.InterestBook> interestBookList;
    private ArrayList mArrayList=new ArrayList();

    @Override
    public int getLayoutId() {
        return R.layout.activity_common_recyclerview;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    public void initToolBar() {
        LogUtils.e("initToolBar");
        interestBookList = (ArrayList<InterestBookList.InterestBook>) getIntent().getSerializableExtra(INTENT_INTEREST_BOOK);
        if(interestBookList!=null){
            LogUtils.e("interestBookList不为空");
        }else{
            LogUtils.e("interestBookList为空");
        }
        mCommonToolbar.setTitle("你可能感兴趣");
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void initDatas() {
        mRecyclerView.setHasFixedSize(true);
        initAdapter(SearchInterestAdapter.class,R.layout.item_search_interest_result_list,
                interestBookList, false, false);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void configViews() {

    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        InterestBookList.InterestBook item = (InterestBookList.InterestBook) adapter.getItem(position);
        BookDetailActivity.startActivity(mContext,item._id);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
