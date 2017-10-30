package com.imooc.brvaheasyrecycleview.ui.contract;

import com.imooc.brvaheasyrecycleview.base.BaseContract;

/**
 * Created by Administrator on 2017/10/16.
 */

public interface MainContract {

    public interface View extends BaseContract.BaseView{
        void loginSuccess();

        void syncBookShelfCompleted();
    }

    public interface Presenter<T> extends BaseContract.BasePresenter<T>{
        void login(String uid, String token, String platform);

        void syncBookShelf();
    }

}
