package com.imooc.brvaheasyrecycleview.ui.activity;

import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.BaseActivity;
import com.imooc.brvaheasyrecycleview.component.AppComponent;

/**
 * Created by Administrator on 2017/11/4.
 */

public class UserMessageActivity extends BaseActivity {
    @Override
    public int getLayoutId() {
        return R.layout.activity_usermessage;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    public void initToolBar() {
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {

    }
}
