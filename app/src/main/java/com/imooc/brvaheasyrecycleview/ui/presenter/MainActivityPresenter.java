package com.imooc.brvaheasyrecycleview.ui.presenter;

import android.util.Log;

import com.imooc.brvaheasyrecycleview.Bean.BookMixAToc;
import com.imooc.brvaheasyrecycleview.Bean.Recommend;
import com.imooc.brvaheasyrecycleview.Bean.user.Login;
import com.imooc.brvaheasyrecycleview.api.BookApi;
import com.imooc.brvaheasyrecycleview.base.RxPresenter;
import com.imooc.brvaheasyrecycleview.manager.CollectionsManager;
import com.imooc.brvaheasyrecycleview.ui.contract.MainContract;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;
import com.imooc.brvaheasyrecycleview.utils.ToastUtils;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import static org.eclipse.jdt.internal.core.JavadocConstants.P;

/**
 * Created by Administrator on 2017/10/16.
 */

public class MainActivityPresenter extends RxPresenter<MainContract.View> implements MainContract.Presenter<MainContract.View>{

    public static boolean isLastSyncUpdateed = false;
    private BookApi bookApi;

    @Inject
    public MainActivityPresenter(BookApi bookApi){
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
                                if (data != null && mView != null && data.ok) {
                                    mView.loginSuccess();
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

                            }
                        }
                );
        addDisposable(rxDisposable);
    }

    @Override
    public void syncBookShelf() {
        List<Recommend.RecommendBooks> list = CollectionsManager.getInstance().getCollectionList();
        List<Observable<BookMixAToc.mixToc>> observables = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            for (Recommend.RecommendBooks bean : list) {
                if (!bean.isFromSD) {
                    Observable<BookMixAToc.mixToc> fromNetWork = bookApi.getBookMixAToc(bean._id, "chapters")
                            .filter(new Predicate<BookMixAToc>() {
                                @Override
                                public boolean test(@NonNull BookMixAToc bookMixAToc) throws Exception {
                                    return bookMixAToc.mixToc!=null;
                                }
                            })
                            .map(new Function<BookMixAToc, BookMixAToc.mixToc>() {
                                @Override
                                public BookMixAToc.mixToc apply(BookMixAToc data) throws Exception {
                                    if(data.mixToc==null){
                                        Log.e("data11","为空");
                                    }
                                    return data.mixToc;
                                }
                            })
//                    .compose(RxUtil.<BookMixAToc.mixToc>rxCacheListHelper(
//                            StringUtils.creatAcacheKey("book-toc", bean._id, "chapters")))
                            ;
                    observables.add(fromNetWork);
                }
            }
        } else {
            ToastUtils.showSingleToast("书架空空如也...");
            mView.syncBookShelfCompleted();
            return;
        }
        isLastSyncUpdateed = false;
        Log.e("size","ww"+observables.size());
        Disposable rxDisposable = Observable.mergeDelayError(observables)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    new Consumer<BookMixAToc.mixToc>() {
                           @Override
                           public void accept(BookMixAToc.mixToc data) throws Exception {
                               Log.e("accept","accept"+data);
                                //相当于onNext()
                               if(data==null){
                                   Log.e("data22","为空");
                               }else{
                                   String lastChapter = data.chapters.get(data.chapters.size() - 1).title;
                                   CollectionsManager.getInstance().setLastChapterAndLatelyUpdate(data.book, lastChapter, data.chaptersUpdated);
                               }
                           }
                       },
                    new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable e) throws Exception {
                            //相当于onError()
                            LogUtils.e("onError: " + e);
                            mView.showError();
                        }
                    },
                    new Action() {
                        @Override
                        public void run() throws Exception {
                            //相当于onComplete()
                            mView.syncBookShelfCompleted();
                            Log.e("onCompleted","onCompleted");
                            if(isLastSyncUpdateed){
                                ToastUtils.showSingleToast("小説已更新");
                            }else{
                                ToastUtils.showSingleToast("你追的小説沒有更新");
                            }
                        }
                    });
        addDisposable(rxDisposable);
    }
}
