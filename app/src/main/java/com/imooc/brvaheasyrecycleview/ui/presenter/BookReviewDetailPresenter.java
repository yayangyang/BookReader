package com.imooc.brvaheasyrecycleview.ui.presenter;

import com.imooc.brvaheasyrecycleview.Bean.BookReview;
import com.imooc.brvaheasyrecycleview.Bean.CommentList;
import com.imooc.brvaheasyrecycleview.Bean.MyBean.Comment;
import com.imooc.brvaheasyrecycleview.Bean.MyBean.ReviewHelpful;
import com.imooc.brvaheasyrecycleview.Bean.user.Login;
import com.imooc.brvaheasyrecycleview.api.BookApi;
import com.imooc.brvaheasyrecycleview.base.RxPresenter;
import com.imooc.brvaheasyrecycleview.ui.contract.BookReviewDetailContract;
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

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class BookReviewDetailPresenter extends RxPresenter<BookReviewDetailContract.View>
        implements BookReviewDetailContract.Presenter {

    private BookApi bookApi;

    @Inject
    public BookReviewDetailPresenter(BookApi bookApi) {
        this.bookApi = bookApi;
    }

    @Override
    public void getBookReviewDetail(String id) {
        Disposable rxDisposable = bookApi.getBookReviewDetail(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<BookReview>() {
                            @Override
                            public void accept(BookReview data) throws Exception {
                                if(data!=null&&mView!=null){
                                    mView.showBookReviewDetail(data);
                                }
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                LogUtils.e("getBookReviewDetail:" + e.toString());
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
    public void getBestComments(String bookReviewId) {
        Disposable rxDisposable = bookApi.getBestComments(bookReviewId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<CommentList>() {
                            @Override
                            public void accept(CommentList data) throws Exception {
                                if(data!=null&&mView!=null){
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
    public void getBookReviewComments(String bookReviewId, int start, int limit) {
        Disposable rxDisposable = bookApi.getBookReviewComments(bookReviewId, start + "", limit + "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<CommentList>() {
                            @Override
                            public void accept(CommentList data) throws Exception {
                                if(data!=null&&mView!=null){
                                    mView.showBookReviewComments(data);
                                }
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                mView.showError();
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
                                if(NetworkUtils.isAvailable(AppUtils.getAppContext())){
                                    ToastUtils.showToast("网络异常");
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
    public void publishReview(String sectionId, final String content, String token) {
        Disposable disposable=bookApi.publishReview(sectionId,content,token).subscribeOn(Schedulers.io())
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

    @Override
    public void postReviewHelpful(String sectionId, String token, final String is_helpful) {
        Disposable disposable=bookApi.postReviewHelpful(sectionId,token,is_helpful).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<ReviewHelpful>() {
                            @Override
                            public void accept(ReviewHelpful data) throws Exception {
                                if(data!=null&&mView!=null){
                                    mView.postReviewHelpfulResult(data,is_helpful);
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
