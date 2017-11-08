package com.imooc.brvaheasyrecycleview.ui.activity;

import android.content.ContextWrapper;
import android.content.Intent;

import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.BaseActivity;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;
import com.imooc.brvaheasyrecycleview.utils.ToastUtils;

import org.eclipse.osgi.framework.internal.core.BundleContextImpl;

import butterknife.OnClick;

/**
 * Created by Administrator on 2017/11/3.
 */

public class UserInfoActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_userinfo;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    public void initToolBar() {
        mCommonToolbar.setTitle("个人信息");
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==1){
            ToastUtils.showToast("onActivityResult"+resultCode);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.setting)
    public void enterSettingActivity(){
        Intent intent = new Intent(this, SettingActivity.class);
        intent.putExtra(SettingActivity.INTENT_IS_USER_ENTER,true);
        startActivityForResult(intent,0);
    }

}
