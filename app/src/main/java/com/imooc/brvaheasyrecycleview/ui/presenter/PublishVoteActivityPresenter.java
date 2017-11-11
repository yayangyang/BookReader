package com.imooc.brvaheasyrecycleview.ui.presenter;

import com.imooc.brvaheasyrecycleview.Bean.MyBean.MyDiscussion;
import com.imooc.brvaheasyrecycleview.Bean.user.Login;
import com.imooc.brvaheasyrecycleview.api.BookApi;
import com.imooc.brvaheasyrecycleview.base.RxPresenter;
import com.imooc.brvaheasyrecycleview.ui.contract.PublishVoteContract;
import com.imooc.brvaheasyrecycleview.utils.AppUtils;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;
import com.imooc.brvaheasyrecycleview.utils.NetworkUtils;
import com.imooc.brvaheasyrecycleview.utils.ToastUtils;

import java.util.Map;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/11/11.
 */

public class PublishVoteActivityPresenter extends RxPresenter<PublishVoteContract.View>
        implements PublishVoteContract.Presenter{

    private BookApi bookApi;

    @Inject
    public PublishVoteActivityPresenter(BookApi bookApi) {
        this.bookApi = bookApi;
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
                                ToastUtils.showToast("登录失败");
                                LogUtils.e("login" + e.toString());
                                if(!NetworkUtils.isAvailable(AppUtils.getAppContext())){
                                    ToastUtils.showToast("没有网络");
                                }
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

    @Override
    public void publishVote(Map<String, String> params) {
        Disposable disposable = bookApi.publishVote(params).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<MyDiscussion>() {
                            @Override
                            public void accept(MyDiscussion data) throws Exception {
                                LogUtils.e("accept"+data);
                                if(data!=null&&mView!=null){
                                    mView.publishVoteResult(data);
                                }
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                LogUtils.e(e.toString()+"错误");
                                if(!NetworkUtils.isAvailable(AppUtils.getAppContext())){
                                    ToastUtils.showToast("没有网络");
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
