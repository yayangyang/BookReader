package com.imooc.brvaheasyrecycleview.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.BasePermissionActivity;
import com.imooc.brvaheasyrecycleview.manager.SettingManager;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;
import com.imooc.brvaheasyrecycleview.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends BasePermissionActivity {

    @BindView(R.id.tvSkip)
    TextView tvSkip;

    private int count=0;

    private Runnable allow,refuse;
    private String[] permissionCollection={
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        if(SettingManager.getInstance().isFirstEnter()){
            init();
            requeset();
        }else{
            goHome();
        }
    }

    private void init() {
        allow=new Runnable(){
            @Override
            public void run() {
                //同意
                LogUtils.e("同意");

                count++;
                if(count==permissionCollection.length){
                    goHome();
                }
            }
        };
        refuse=new Runnable(){
            @Override
            public void run() {
                //拒绝
                LogUtils.e("拒绝");

                count++;
                if(count==permissionCollection.length){
                    goHome();
                }
            }
        };
    }

    private void requeset() {
        ToastUtils.showToast("request");

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0后诸多权限才需要申请
            requestPermission(0, permissionCollection,allow,refuse);

            //申请调节亮度需要的WRITE_SETTING权限(放在前面申请的权限后面才会弹出框)
            if (!Settings.System.canWrite(this)) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }else{
            goHome();
        }

        SettingManager.getInstance().savaFirstEnter(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.e("onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
    }

    private synchronized void goHome() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
