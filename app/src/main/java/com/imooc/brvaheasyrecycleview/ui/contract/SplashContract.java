package com.imooc.brvaheasyrecycleview.ui.contract;

import com.imooc.brvaheasyrecycleview.Bean.user.Login;
import com.imooc.brvaheasyrecycleview.base.BaseContract;

/**
 * Created by Administrator on 2017/11/5.
 */

public interface SplashContract {

    public interface View extends BaseContract.BaseView{
        void showIsLogin(boolean isLogin);
    }

    public interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void checkLogin(String token);
    }
}
