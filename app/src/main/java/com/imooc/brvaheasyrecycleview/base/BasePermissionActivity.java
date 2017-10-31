package com.imooc.brvaheasyrecycleview.base;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class BasePermissionActivity extends AppCompatActivity {
    private Map<Integer, Runnable> allowablePermissionRunnables = new HashMap<>();
    private Map<Integer, Runnable> disallowablePermissionRunnables = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 请求权限
     * @param id 请求授权的id 唯一标识即可
     * @param permission 请求的权限
     * @param allowableRunnable 同意授权后的操作
     * @param disallowableRunnable 禁止权限后的操作
     */
    public void requestPermission(int id, String[] permission, Runnable allowableRunnable, Runnable disallowableRunnable) {
        if (allowableRunnable == null||disallowableRunnable==null) {
            return;
        }
        allowablePermissionRunnables.put(id, allowableRunnable);
        disallowablePermissionRunnables.put(id, disallowableRunnable);

        //版本判断
        if (Build.VERSION.SDK_INT >= 23) {
            //检查是否拥有权限
            int checkCallPhonePermission=PackageManager.PERMISSION_GRANTED;
            for(int i=0;i<permission.length;i++){
                checkCallPhonePermission=ContextCompat.checkSelfPermission(getApplicationContext(), permission[i]);
                if(checkCallPhonePermission!=PackageManager.PERMISSION_GRANTED){
                    checkCallPhonePermission=PackageManager.PERMISSION_DENIED;
                    break;
                }
            }
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                //弹出对话框接收权限
                ActivityCompat.requestPermissions(BasePermissionActivity.this, permission, id);
//                return;
            } else {
                allowableRunnable.run();
            }
        } else {
            allowableRunnable.run();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length>0){
            for(int i=0;i<grantResults.length;i++){
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Runnable allowRun = allowablePermissionRunnables.get(requestCode);
                    allowRun.run();
                } else {
                    Runnable disallowRun = disallowablePermissionRunnables.get(requestCode);
                    disallowRun.run();
                }
            }
        }
    }
}