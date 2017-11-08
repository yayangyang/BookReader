package com.imooc.brvaheasyrecycleview.ui.presenter;

import com.imooc.brvaheasyrecycleview.Bean.MyBean.CheckLogin;
import com.imooc.brvaheasyrecycleview.api.BookApi;
import com.imooc.brvaheasyrecycleview.base.RxPresenter;
import com.imooc.brvaheasyrecycleview.ui.contract.SplashContract;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/11/5.
 */

public class SplashActivityPresenter extends RxPresenter<SplashContract.View>
        implements SplashContract.Presenter<SplashContract.View> {

    private BookApi bookApi;

    @Inject
    public SplashActivityPresenter(BookApi bookApi){
        this.bookApi=bookApi;
    }

    @Override
    public void checkLogin(String token) {
        Disposable disposable=bookApi.checkLogin(token).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<CheckLogin>() {
                            @Override
                            public void accept(CheckLogin data) throws Exception {
                                LogUtils.e("accept");
                                if(data!=null&&mView!=null){
                                    mView.showIsLogin(data.ok);
                                }
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                if(mView!=null){
                                    LogUtils.e(e.toString());
                                    mView.showError();
                                }
                            }
                        },
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                LogUtils.e("完成");
                            }
                        }
                );
        addDisposable(disposable);
    }
}
