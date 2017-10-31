package com.imooc.brvaheasyrecycleview.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.BaseActivity;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.view.ProgressWebView;

import butterknife.BindView;

public class FeedbackActivity extends BaseActivity {

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, FeedbackActivity.class));
    }

    @BindView(R.id.feedbackView)
    ProgressWebView feedbackView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
    }

    @Override
    public void initToolBar() {
        mCommonToolbar.setTitle("反馈建议");
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {
        feedbackView.loadUrl("https://github.com/JustWayward/BookReader/issues/new");
    }
}
