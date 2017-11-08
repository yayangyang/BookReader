package com.imooc.brvaheasyrecycleview.ui.presenter;

import com.imooc.brvaheasyrecycleview.Bean.user.Login;
import com.imooc.brvaheasyrecycleview.api.BookApi;
import com.imooc.brvaheasyrecycleview.base.RxPresenter;
import com.imooc.brvaheasyrecycleview.base.BaseLoginContract;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/11/5.
 */

public class WelcomeActivityPresenter extends RxPresenter<BaseLoginContract.View>
        implements BaseLoginContract.Presenter<BaseLoginContract.View> {

    private BookApi bookApi;

    @Inject
    public WelcomeActivityPresenter(BookApi bookApi){
        this.bookApi=bookApi;
    }

    @Override
    public void login(String uid, String token, String platform) {
        Disposable rxDisposable = bookApi.login(uid, token, platform).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<Login>() {
                            @Override
                            public void accept(Login data) throws Exception {
                                if(data.user!=null){
                                    LogUtils.e("收到了"+data.toString());
                                }else{
                                    LogUtils.e("user为空"+data.ok);
                                }
                                if (data != null && mView != null) {
                                    mView.loginSuccess(data);
                                    LogUtils.e(data.user.toString());
                                }
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                LogUtils.e("login" + e.toString());
                            }
                        },
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                LogUtils.e("完成");
                                if(mView!=null){
                                    mView.complete();
                                }
                            }
                        }
                );
        addDisposable(rxDisposable);
    }
}
