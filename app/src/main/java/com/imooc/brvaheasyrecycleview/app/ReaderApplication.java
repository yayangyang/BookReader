package com.imooc.brvaheasyrecycleview.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.imooc.brvaheasyrecycleview.Bean.user.Login;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.base.CrashHandler;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.component.DaggerAppComponent;
import com.imooc.brvaheasyrecycleview.manager.SettingManager;
import com.imooc.brvaheasyrecycleview.module.AppModule;
import com.imooc.brvaheasyrecycleview.module.BookApiModule;
import com.imooc.brvaheasyrecycleview.utils.AppUtils;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;
import com.imooc.brvaheasyrecycleview.utils.SharedPreferencesUtil;
import com.tencent.tauth.Tencent;

public class ReaderApplication extends Application {

    private static ReaderApplication sInstance;
    public static Tencent mTencent;
    private AppComponent appComponent;


    public static Login sLogin;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        initCompoent();
        AppUtils.init(this);
        CrashHandler.getInstance().init(this);
        initPrefs();
        initNightMode();
        //initHciCloud();

        initLoginInfo();
    }

    private void initLoginInfo() {
        mTencent = Tencent.createInstance("222222", AppUtils.getAppContext());
        sLogin= SettingManager.getInstance().getLoginInfo();
    }

    public static ReaderApplication getsInstance() {
        return sInstance;
    }

    private void initCompoent() {
        appComponent = DaggerAppComponent.builder()
                .bookApiModule(new BookApiModule())
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    /**
     * 初始化SharedPreference
     */
    protected void initPrefs() {
        SharedPreferencesUtil.init(getApplicationContext(), getPackageName() + "_preference", Context.MODE_MULTI_PROCESS);
    }

    protected void initNightMode() {
        boolean isNight = SharedPreferencesUtil.getInstance().getBoolean(Constant.ISNIGHT, false);
        LogUtils.d("isNight=" + isNight);
        if (isNight) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

}
