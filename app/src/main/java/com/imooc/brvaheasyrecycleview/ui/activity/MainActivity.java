package com.imooc.brvaheasyrecycleview.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.ActionProvider;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.imooc.brvaheasyrecycleview.Bean.user.Login;
import com.imooc.brvaheasyrecycleview.Bean.user.TencentLoginResult;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.app.ReaderApplication;
import com.imooc.brvaheasyrecycleview.base.BaseActivity;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.component.DaggerMainComponent;
import com.imooc.brvaheasyrecycleview.manager.EventManager;
import com.imooc.brvaheasyrecycleview.manager.SettingManager;
import com.imooc.brvaheasyrecycleview.service.DownloadBookService;
import com.imooc.brvaheasyrecycleview.transform.GlideCircleTransform;
import com.imooc.brvaheasyrecycleview.ui.contract.MainContract;
import com.imooc.brvaheasyrecycleview.ui.fragment.CommunityFragment;
import com.imooc.brvaheasyrecycleview.ui.fragment.FindFragment;
import com.imooc.brvaheasyrecycleview.ui.fragment.RecommendFragment;
import com.imooc.brvaheasyrecycleview.ui.presenter.MainActivityPresenter;
import com.imooc.brvaheasyrecycleview.utils.AppUtils;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;
import com.imooc.brvaheasyrecycleview.utils.SharedPreferencesUtil;
import com.imooc.brvaheasyrecycleview.utils.ToastUtils;
import com.imooc.brvaheasyrecycleview.view.GenderPopupWindow;
import com.imooc.brvaheasyrecycleview.view.LoginPopupWindow;
import com.imooc.brvaheasyrecycleview.view.RVPIndicator;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

//这里没使用EasyRecyclerview这个类(还没确定与brvah是否兼容)
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

    private Tencent mTencent;
    private BaseUIListener loginListener;

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

        mTencent = Tencent.createInstance("222222", MainActivity.this);

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

        mRVPIndicator.post(new Runnable() {
            @Override
            public void run() {
                LogUtils.e("wwwwwwwwwwwwwwwwwwwww");
            }
        });
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
        LogUtils.e("onCreateOptionsMenu");//1
        getMenuInflater().inflate(R.menu.menu_main, menu);
//        MenuItem item = menu.findItem(R.id.action_login);
//        item.setActionView(R.layout.menu_item);//ActionView应该不是覆盖布局,而是点击了这个item就如同点击这个ActionView对象的效果
        setIconEnable(menu,true);//调用方法设置menu的icon可显示
        return true;
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        LogUtils.e("onPrepareOptionsPanel");//2
        //菜单缩在一起时弹出会调用此方法
        return super.onPrepareOptionsPanel(view, menu);//@hide方法需反射调用,但这里是返回值不知怎么做
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        LogUtils.e("onPrepareOptionsMenu");//3
        //菜单缩在一起时弹出会调用此方法
        final MenuItem item = menu.findItem(R.id.action_login);

        if(ReaderApplication.sLogin!=null&&ReaderApplication.sLogin.user!=null){
            item.setTitle(ReaderApplication.sLogin.user.nickname);
            //设置的图标大小太小(目前还没解决)
            Glide.with(mContext).load(Constant.IMG_BASE_URL + ReaderApplication.sLogin.user.avatar)
                    .asBitmap().transform(new GlideCircleTransform(mContext)).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(resource);
                    item.setIcon(bitmapDrawable);
                }
            });
        }else{
            item.setTitle("请登录");
            item.setIcon(R.drawable.home_menu_0);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean isLogin=false;
        if(ReaderApplication.sLogin!=null&&ReaderApplication.sLogin.user!=null){
            isLogin=ReaderApplication.sLogin.ok;
        }
        switch (id) {
            case R.id.action_search:
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                break;
            case R.id.action_login:
                if(isLogin){
                    startActivity(new Intent(MainActivity.this, UserInfoActivity.class));
                }else{
                    if (popupWindow == null) {
                        popupWindow = new LoginPopupWindow(this);
                        popupWindow.setLoginTypeListener(this);
                    }
                    popupWindow.showAtLocation(mCommonToolbar, Gravity.CENTER, 0, 0);
                }
                break;
            case R.id.action_my_message:
                if(isLogin){
                    startActivity(new Intent(MainActivity.this, UserMessageActivity.class));
                }else{
                    if (popupWindow == null) {
                        popupWindow = new LoginPopupWindow(this);
                        popupWindow.setLoginTypeListener(this);
                    }
                    popupWindow.showAtLocation(mCommonToolbar, Gravity.CENTER, 0, 0);
                }
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
                SettingActivity.startActivity(this,false);
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
    public void loginSuccess(Login login) {
        ToastUtils.showToast("登录成功");

        ReaderApplication.sLogin=login;
        SettingManager.getInstance().saveLoginInfo(login);//保存登录信息,以便下次登录app使用
    }

    @Override
    public void syncBookShelfCompleted() {
        Log.e("syncBookShelfCompleted","syncBookShelfCompleted");
        dismissDialog();
        EventManager.refreshCollectionList();
    }

    public void setCurrentItem(int i) {
        mViewPager.setCurrentItem(i);
    }

    @Override
    public void onLogin(ImageView view, String type) {
        if (type.equals("QQ")) {
            if (!mTencent.isSessionValid()) {
                if (loginListener == null) loginListener = new BaseUIListener();
                LogUtils.e("loginListener不为null,开始登陆");
                mTencent.login(this, "all", loginListener);
            }
        }
        //4f45e920ff5d1a0e29d997986cd97181
    }

    public class BaseUIListener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            LogUtils.e("onComplete");
            JSONObject jsonObject = (JSONObject) o;
            String json = jsonObject.toString();
            Gson gson = new Gson();
            TencentLoginResult result = gson.fromJson(json, TencentLoginResult.class);
            LogUtils.e(result.toString());
            mPresenter.login(result.openid, result.access_token, "QQ");
        }

        @Override
        public void onError(UiError uiError) {
        }

        @Override
        public void onCancel() {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DownloadBookService.cancel();
        stopService(new Intent(this, DownloadBookService.class));
        if (mPresenter != null) {
            mPresenter.detachView();
        }

    }

}
