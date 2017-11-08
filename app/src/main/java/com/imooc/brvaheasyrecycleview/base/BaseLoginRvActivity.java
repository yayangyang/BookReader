package com.imooc.brvaheasyrecycleview.base;

import android.content.Intent;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.imooc.brvaheasyrecycleview.Bean.user.TencentLoginResult;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.ui.activity.WelcomeActivity;
import com.imooc.brvaheasyrecycleview.utils.AppUtils;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;
import com.imooc.brvaheasyrecycleview.utils.ToastUtils;
import com.imooc.brvaheasyrecycleview.view.LoginPopupWindow;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/11/5.
 */

public abstract class BaseLoginRvActivity<T,K extends BaseViewHolder> extends BaseRVActivity<T,K>
        implements BaseLoginContract.View,LoginPopupWindow.LoginTypeListener{
    protected LoginPopupWindow popupWindow;
    protected BaseUIListener loginListener;

    protected boolean isLogin=false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
        }
        super.onActivityResult(requestCode, resultCode, data);
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
            loginZhuiShu(result);
        }

        @Override
        public void onError(UiError uiError) {
            ToastUtils.showToast("QQ授权出错");
        }

        @Override
        public void onCancel() {
            ToastUtils.showToast("QQ授权被取消");
            loginCancel();
        }
    }

    protected abstract void loginZhuiShu(TencentLoginResult result);

    protected abstract void loginCancel();

}
