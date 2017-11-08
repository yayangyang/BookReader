package com.imooc.brvaheasyrecycleview.ui.presenter;

import com.imooc.brvaheasyrecycleview.Bean.CommentList;
import com.imooc.brvaheasyrecycleview.Bean.Disscussion;
import com.imooc.brvaheasyrecycleview.Bean.MyBean.Comment;
import com.imooc.brvaheasyrecycleview.Bean.user.Login;
import com.imooc.brvaheasyrecycleview.api.BookApi;
import com.imooc.brvaheasyrecycleview.base.RxPresenter;
import com.imooc.brvaheasyrecycleview.ui.contract.BookDiscussionDetailContract;
import com.imooc.brvaheasyrecycleview.utils.AppUtils;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;
import com.imooc.brvaheasyrecycleview.utils.NetworkUtils;
import com.imooc.brvaheasyrecycleview.utils.ToastUtils;

import org.reactivestreams.Subscription;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class BookDiscussionDetailPresenter extends RxPresenter<BookDiscussionDetailContract.View>
        implements BookDiscussionDetailContract.Presenter {

    private BookApi bookApi;

    @Inject
    public BookDiscussionDetailPresenter(BookApi bookApi) {
        this.bookApi = bookApi;
    }

    @Override
    public void getBookDisscussionDetail(String id) {
        Disposable rxDisposable = bookApi.getBookDisscussionDetail(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<Disscussion>() {
                            @Override
                            public void accept(Disscussion data) throws Exception {
                                LogUtils.e("accept");
                                if(mView!=null&&data!=null){
                                    mView.showBookDisscussionDetail(data);
                                }
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                LogUtils.e("getBookDisscussionDetail:" + e.toString());
                            }
                        },
                        new Action() {
                            @Override
                            public void run() throws Exception {

                            }
                        }
                );
        addDisposable(rxDisposable);
    }

    @Override
    public void getBestComments(String disscussionId) {
        Disposable rxDisposable = bookApi.getBestComments(disscussionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<CommentList>() {
                            @Override
                            public void accept(CommentList data) throws Exception {
                                if(mView!=null&&data!=null){
                                    mView.showBestComments(data);
                                }
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                LogUtils.e("getBestComments:" + e.toString());
                            }
                        },
                        new Action() {
                            @Override
                            public void run() throws Exception {

                            }
                        }
                );
        addDisposable(rxDisposable);
    }

    @Override
    public void getBookDisscussionComments(String disscussionId, int start, int limit) {
        Disposable rxDisposable = bookApi.getBookDisscussionComments(disscussionId, start + "", limit + "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<CommentList>() {
                            @Override
                            public void accept(CommentList data) throws Exception {
                                if(mView!=null&&data!=null){
                                    mView.showBookDisscussionComments(data);
                                }
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                LogUtils.e("getBookDisscussionComments:" + e.toString());
                            }
                        },
                        new Action() {
                            @Override
                            public void run() throws Exception {

                            }
                        }
                );
        addDisposable(rxDisposable);
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
                                if (data != null && mView != null) {//登录失败可能仍能接收到
                                    mView.loginSuccess(data);
                                    LogUtils.e(data.user.toString());
                                }
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                LogUtils.e("login" + e.toString());
                                if(!NetworkUtils.isAvailable(AppUtils.getAppContext())){
                                    ToastUtils.showToast("没网络");
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
        addDisposable(rxDisposable);
    }

    @Override
    public void publishReview(String section, final String content, String token) {
        Disposable disposable=bookApi.publishReview(section,content,token).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<Comment>() {
                            @Override
                            public void accept(Comment data) throws Exception {
                                if(data!=null&&mView!=null){
                                    mView.publishReviewResult(data,content);
                                }
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                LogUtils.e(e.toString());
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
