package com.imooc.brvaheasyrecycleview.ui.presenter;


import com.imooc.brvaheasyrecycleview.Bean.BookHelp;
import com.imooc.brvaheasyrecycleview.Bean.CommentList;
import com.imooc.brvaheasyrecycleview.Bean.MyBean.Comment;
import com.imooc.brvaheasyrecycleview.Bean.user.Login;
import com.imooc.brvaheasyrecycleview.api.BookApi;
import com.imooc.brvaheasyrecycleview.base.RxPresenter;
import com.imooc.brvaheasyrecycleview.ui.contract.BookHelpDetailContract;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;

import org.reactivestreams.Subscription;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class BookHelpDetailPresenter extends RxPresenter<BookHelpDetailContract.View>
        implements BookHelpDetailContract.Presenter {

    private BookApi bookApi;

    @Inject
    public BookHelpDetailPresenter(BookApi bookApi) {
        this.bookApi = bookApi;
    }

    @Override
    public void getBookHelpDetail(String id) {
        Disposable rxDisposable = bookApi.getBookHelpDetail(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<BookHelp>() {
                            @Override
                            public void accept(BookHelp data) throws Exception {
                                if(data!=null&&mView!=null){
                                    mView.showBookHelpDetail(data);
                                }
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                LogUtils.e("getBookHelpDetail:" + e.toString());
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
                            public void accept(CommentList list) throws Exception {
                                mView.showBestComments(list);
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
    public void getBookHelpComments(String disscussionId, final int start, int limit) {
        Disposable rxDisposable = bookApi.getBookReviewComments(disscussionId, start + "", limit + "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<CommentList>() {
                            @Override
                            public void accept(CommentList list) throws Exception {
                                mView.showBookHelpComments(list,start);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                LogUtils.e("getBookHelpComments:" + e.toString());
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
