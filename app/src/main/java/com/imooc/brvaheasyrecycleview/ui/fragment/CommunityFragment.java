package com.imooc.brvaheasyrecycleview.ui.fragment;

/**
 * Created by Administrator on 2017/10/19.
 */

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.imooc.brvaheasyrecycleview.Bean.support.FindBean;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.BaseFragment;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.ui.activity.BookDiscussionActivity;
import com.imooc.brvaheasyrecycleview.ui.activity.BookHelpActivity;
import com.imooc.brvaheasyrecycleview.ui.activity.BookReviewActivity;
import com.imooc.brvaheasyrecycleview.ui.activity.GirlBookDiscussionActivity;
import com.imooc.brvaheasyrecycleview.ui.adapter.FindAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class CommunityFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private FindAdapter mAdapter;
    private List<FindBean> mList = new ArrayList<>();

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_find;
    }

    @Override
    public void initDatas() {
        mList.clear();
        mList.add(new FindBean("综合讨论区", R.drawable.discuss_section));
        mList.add(new FindBean("书评区", R.drawable.comment_section));
        mList.add(new FindBean("书荒互助区", R.drawable.helper_section));
        mList.add(new FindBean("女生区", R.drawable.girl_section));
        mList.add(new FindBean("原创区",R.drawable.yuanchuang));
    }

    @Override
    public void configViews() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mRecyclerView.addItemDecoration(new SupportDividerItemDecoration(mContext, LinearLayoutManager.VERTICAL, true));

        mAdapter = new FindAdapter(R.layout.item_find, mList);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    public void attachView() {

    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        switch (position) {
            case 0:
                BookDiscussionActivity.startActivity(activity,true);
                break;
            case 1:
                BookReviewActivity.startActivity(activity);
                break;
            case 2:
                BookHelpActivity.startActivity(activity);
                break;
            case 3:
                GirlBookDiscussionActivity.startActivity(activity);
                break;
            case 4:
                BookDiscussionActivity.startActivity(activity,false);
                break;
            default:
                break;
        }
    }
}

