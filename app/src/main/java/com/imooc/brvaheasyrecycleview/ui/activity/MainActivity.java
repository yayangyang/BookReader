package com.imooc.brvaheasyrecycleview.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.BaseActivity;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.component.DaggerMainComponent;
import com.imooc.brvaheasyrecycleview.manager.EventManager;
import com.imooc.brvaheasyrecycleview.manager.SettingManager;
import com.imooc.brvaheasyrecycleview.service.DownloadBookService;
import com.imooc.brvaheasyrecycleview.ui.contract.MainContract;
import com.imooc.brvaheasyrecycleview.ui.fragment.CommunityFragment;
import com.imooc.brvaheasyrecycleview.ui.fragment.FindFragment;
import com.imooc.brvaheasyrecycleview.ui.fragment.RecommendFragment;
import com.imooc.brvaheasyrecycleview.ui.presenter.MainActivityPresenter;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;
import com.imooc.brvaheasyrecycleview.utils.SharedPreferencesUtil;
import com.imooc.brvaheasyrecycleview.utils.ToastUtils;
import com.imooc.brvaheasyrecycleview.view.GenderPopupWindow;
import com.imooc.brvaheasyrecycleview.view.LoginPopupWindow;
import com.imooc.brvaheasyrecycleview.view.RVPIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

//这里没使用EasyRecyclerview这个框架(还没确定与brvah是否兼容)
public class MainActivity extends BaseActivity implements MainContract.View,LoginPopupWindow.LoginTypeListener{

    @BindView(R.id.rvIndicator)
    RVPIndicator mRVPIndicator;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @Inject
    public MainActivityPresenter mPresenter;
    private List<Fragment> mTabContents=new ArrayList<>();
    private List<String> mList;
    private FragmentPagerAdapter mFragmentPagerAdapter;
    private GenderPopupWindow mGenderPopupWindow;
    private LoginPopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initDatas() {
        startService(new Intent(this, DownloadBookService.class));

//        mTencent = Tencent.createInstance("1105670298", MainActivity.this);

        mTabContents.add(new RecommendFragment());
        mTabContents.add(new CommunityFragment());
        mTabContents.add(new FindFragment());
        mList = Arrays.asList(getResources().getStringArray(R.array.home_tabs));
        mFragmentPagerAdapter=new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mTabContents.get(position);
            }

            @Override
            public int getCount() {
                return mTabContents.size();
            }
        };
    }

    @Override
    public void configViews() {
        mRVPIndicator.setTabItemTitles(mList);
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mRVPIndicator.setViewPager(mViewPager,0);
        mPresenter.attachView(this);

        mRVPIndicator.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean userChooseSex = SettingManager.getInstance().isUserChooseSex();
                Log.e("userChooseSex","ww"+userChooseSex);
                if(!SettingManager.getInstance().isUserChooseSex()
                        &&(mGenderPopupWindow==null||!mGenderPopupWindow.isShowing())){
                    showChooseSexPopupWindow();
                }else{
                    Log.e("showDialog","showDialog");
                    showDialog();
                    pullSyncBookShelf();
                }
            }

        },500);
    }

    private void showChooseSexPopupWindow() {
        if (mGenderPopupWindow == null) {
            mGenderPopupWindow = new GenderPopupWindow(MainActivity.this);
        }
        if (!SettingManager.getInstance().isUserChooseSex()
                && !mGenderPopupWindow.isShowing()) {
            mGenderPopupWindow.showAtLocation(mCommonToolbar, Gravity.CENTER, 0, 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                break;
            case R.id.action_login:
                if (popupWindow == null) {
                    popupWindow = new LoginPopupWindow(this);
                    popupWindow.setLoginTypeListener(this);
                }
                popupWindow.showAtLocation(mCommonToolbar, Gravity.CENTER, 0, 0);
                break;
            case R.id.action_my_message:
                if (popupWindow == null) {
                    popupWindow = new LoginPopupWindow(this);
                    popupWindow.setLoginTypeListener(this);
                }
                popupWindow.showAtLocation(mCommonToolbar, Gravity.CENTER, 0, 0);
                break;
            case R.id.action_sync_bookshelf:
                showDialog();
                mPresenter.syncBookShelf();
               /* if (popupWindow == null) {
                    popupWindow = new LoginPopupWindow(this);
                    popupWindow.setLoginTypeListener(this);
                }
                popupWindow.showAtLocation(mCommonToolbar, Gravity.CENTER, 0, 0);*/
                break;
            case R.id.action_scan_local_book:
                ScanLocalBookActivity.startActivity(this);
                break;
            case R.id.action_wifi_book:
                WifiBookActivity.startActivity(this);
                break;
            case R.id.action_feedback:
                FeedbackActivity.startActivity(this);
                break;
            case R.id.action_night_mode:
                if (SharedPreferencesUtil.getInstance().getBoolean(Constant.ISNIGHT, false)) {
                    SharedPreferencesUtil.getInstance().putBoolean(Constant.ISNIGHT, false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    SharedPreferencesUtil.getInstance().putBoolean(Constant.ISNIGHT, true);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                recreate();
                break;
            case R.id.action_settings:
                SettingActivity.startActivity(this);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void pullSyncBookShelf() {
        mPresenter.syncBookShelf();
    }

    @Override
    public void initToolBar() {
        mCommonToolbar.setNavigationIcon(R.mipmap.ic_launcher);
        mCommonToolbar.setTitle("test");
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMainComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
}

    @Override
    public void showError() {
        dismissDialog();
        LogUtils.e("网络异常");
    }

    @Override
    public void complete() {

    }

    @Override
    public void loginSuccess() {

    }

    @Override
    public void syncBookShelfCompleted() {
        Log.e("syncBookShelfCompleted","syncBookShelfCompleted");
        dismissDialog();
        EventManager.refreshCollectionList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    public void setCurrentItem(int i) {
        mViewPager.setCurrentItem(i);
    }

    @Override
    public void onLogin(ImageView view, String type) {

    }
}
