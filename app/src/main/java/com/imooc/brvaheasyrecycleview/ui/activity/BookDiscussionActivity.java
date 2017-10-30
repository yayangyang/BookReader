package com.imooc.brvaheasyrecycleview.ui.activity;

/**
 * Created by Administrator on 2017/10/19.
 */

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.BaseActivity;
import com.imooc.brvaheasyrecycleview.base.BaseCommuniteActivity;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.ui.fragment.BookDiscussionFragment;
import com.imooc.brvaheasyrecycleview.view.SelectionLayout;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import java.util.List;

import butterknife.BindView;


/**
 * 综合讨论区
 */
public class BookDiscussionActivity extends BaseCommuniteActivity {

    private static final String INTENT_DIS = "isDis";

    public static void startActivity(Context context, boolean isDiscussion) {
        context.startActivity(new Intent(context, BookDiscussionActivity.class)
                .putExtra(INTENT_DIS, isDiscussion));
    }

    private boolean mIsDiscussion;

    @Override
    public int getLayoutId() {
        return R.layout.activity_community_book_discussion;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    public void initToolBar() {
        mIsDiscussion = getIntent().getBooleanExtra(INTENT_DIS, false);
        if (mIsDiscussion) {
            mCommonToolbar.setTitle("综合讨论区");
        } else {
            mCommonToolbar.setTitle("原创区");
        }
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void initDatas() {
        super.initDatas();
    }

    @Override
    protected List<List<String>> getTabList() {
        return list1;
    }

    @Override
    public void configViews() {
        BookDiscussionFragment fragment = BookDiscussionFragment.newInstance(mIsDiscussion ? "ramble" : "original");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentCO, fragment).commit();
    }

}

