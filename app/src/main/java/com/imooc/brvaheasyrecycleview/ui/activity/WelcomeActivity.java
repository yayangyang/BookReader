package com.imooc.brvaheasyrecycleview.ui.activity;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.imooc.brvaheasyrecycleview.Bean.user.Login;
import com.imooc.brvaheasyrecycleview.Bean.user.TencentLoginResult;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.app.ReaderApplication;
import com.imooc.brvaheasyrecycleview.base.BaseLoginActivity;
import com.imooc.brvaheasyrecycleview.base.BaseLoginRvActivity;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.component.DaggerMyComponent;
import com.imooc.brvaheasyrecycleview.manager.SettingManager;
import com.imooc.brvaheasyrecycleview.ui.presenter.WelcomeActivityPresenter;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;
import com.imooc.brvaheasyrecycleview.utils.ToastUtils;
import com.imooc.brvaheasyrecycleview.view.LoginPopupWindow;
import com.tencent.tauth.Tencent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/11/5.
 */

public class WelcomeActivity extends BaseLoginActivity {

    private ArrayList mArrayList=new ArrayList();
    private ViewPagerAdapter mViewPagerAdapter;

    private int tuz[]={R.mipmap.welcome01,R.mipmap.welcome02};
    private int imagesz[]={R.id.image01,R.id.image02};
    private ImageView imageViewzu[]=new ImageView[imagesz.length];

    @BindView(R.id.myViewPager)
    ViewPager mViewPager;

    @Inject
    WelcomeActivityPresenter mPresenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMyComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void initToolBar() {
        mCommonToolbar.setVisibility(View.GONE);
    }

    @Override
    public void initDatas() {
//        mTencent=Tencent.createInstance("222222", this);
        mPresenter.attachView(this);

        for(int i=0;i<imageViewzu.length;i++){
            imageViewzu[i]=findViewById(imagesz[i]);
        }
    }

    @Override
    public void configViews() {
        ImageView view = (ImageView) View.inflate(this, R.layout.welcome_view, null);
        view.setImageResource(R.mipmap.welcome01);
        mArrayList.add(view);
        view = (ImageView) View.inflate(this, R.layout.welcome_view, null);
        view.setImageResource(R.mipmap.welcome02);
        mArrayList.add(view);
        mViewPagerAdapter=new ViewPagerAdapter(mArrayList);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for(int i=0;i<imageViewzu.length;i++){
                    if(i==position){
                        imageViewzu[i].setImageResource(R.drawable.bg_my_checked_point);
                    }else{
                        imageViewzu[i].setImageResource(R.drawable.bg_my_unchecked_point);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick(R.id.btn_login)
    public void login(){
        if (popupWindow == null) {
            popupWindow = new LoginPopupWindow(this);
            popupWindow.setLoginTypeListener(this);
        }
        //在屏幕上偏移,第一个参数由方法getWindowToken()都行(自己添加)
        popupWindow.showAtLocation(mViewPager, Gravity.CENTER, 0, 0);
    }

    @OnClick(R.id.btn_enter)
    public void enter(){
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }

    public class ViewPagerAdapter extends PagerAdapter {
        private List<View> views;
        public ViewPagerAdapter(List<View> views) {
            this.views=views;
        }
        @Override//获得当前界面数
        public int getCount() {
            return this.views.size();
        }
        @Override//初始化position位置的界面
        public Object instantiateItem(ViewGroup container, int position) {
            Log.i("create", position+"");
            ((ViewPager)container).addView(views.get(position));
            return views.get(position);
        }
        @Override//判断是否由对象生成界面
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0==arg1;
        }
        @Override
        public void destroyItem(View container, int position, Object object) {
            Log.i("destory", position+"");
            ((ViewPager)container).removeView(views.get(position));
        }
    }

    @Override
    public void loginSuccess(Login login) {
        if(login.ok){
            ToastUtils.showToast("登录成功");
            ReaderApplication.sLogin=login;
            SettingManager.getInstance().saveLoginInfo(login);//保存登录信息,以便下次登录app使用
        }else{
            ToastUtils.showToast("登录被拒绝");
            ReaderApplication.sLogin=null;
            SettingManager.getInstance().saveLoginInfo(login);
        }
    }

    @Override
    public void showError() {

    }

    @Override
    public void complete() {
        startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
        finish();
    }

    @Override
    public void onLogin(ImageView view, String type) {
        if (type.equals("QQ")) {
            if (!mTencent.isSessionValid()) {
                if (loginListener == null) loginListener = new WelcomeActivity.BaseUIListener();
                LogUtils.e("loginListener不为null,开始登陆");
                mTencent.login(this, "all", loginListener);
            }
        }
    }

    @Override
    protected void loginZhuiShu(TencentLoginResult result) {
        mPresenter.login(result.openid, result.access_token, "QQ");
    }

    @Override
    protected void loginCancel() {
        startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
