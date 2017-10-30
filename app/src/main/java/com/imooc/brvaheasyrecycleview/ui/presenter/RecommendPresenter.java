package com.imooc.brvaheasyrecycleview.ui.presenter;


/**
 * Created by Administrator on 2017/10/18.
 */

import android.content.Context;
import android.util.Log;


import com.imooc.brvaheasyrecycleview.Bean.BookMixAToc;
import com.imooc.brvaheasyrecycleview.Bean.Recommend;
import com.imooc.brvaheasyrecycleview.api.BookApi;
import com.imooc.brvaheasyrecycleview.base.RxPresenter;
import com.imooc.brvaheasyrecycleview.manager.SettingManager;
import com.imooc.brvaheasyrecycleview.ui.contract.RecommendContract;
import com.imooc.brvaheasyrecycleview.utils.ACache;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;
import com.imooc.brvaheasyrecycleview.utils.RxUtil;
import com.imooc.brvaheasyrecycleview.utils.StringUtils;

import java.util.List;
import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class RecommendPresenter extends RxPresenter<RecommendContract.View>
        implements RecommendContract.Presenter<RecommendContract.View> {

    private Context mContext;
    private BookApi bookApi;

    @Inject
    public RecommendPresenter(Context mContext, BookApi bookApi) {
        this.mContext = mContext;
        this.bookApi = bookApi;
    }

    @Override
    public void getRecommendList() {
        String key = StringUtils.creatAcacheKey("recommend-list", SettingManager.getInstance().getUserChooseSex());
        Observable<Recommend> fromNetWork = bookApi.getRecommend(SettingManager.getInstance().getUserChooseSex())
                .compose(RxUtil.<Recommend>rxCacheListHelper(key));

        //依次检查disk、network
        Disposable rxDisposable = Observable.concat(RxUtil.rxCreateDiskObservable(key,1, Recommend.class), fromNetWork)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<Recommend>() {
                            @Override
                            public void accept(Recommend recommend) throws Exception {
                                if (recommend != null) {
                                    List<Recommend.RecommendBooks> list = recommend.books;
                                    if (list != null && !list.isEmpty() && mView != null) {
                                        mView.showRecommendList(list);
                                    }
                                }else{
                                    Log.e("recommend","为空");
                                }
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                LogUtils.e("getRecommendList", e.toString());
                                mView.showError();
                            }
                        },
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                mView.complete();
                            }
                        }
                );
        addDisposable(rxDisposable);
    }

    public void getTocList(final String bookId) {
        bookApi.getBookMixAToc(bookId, "chapters").subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<BookMixAToc>() {
                            @Override
                            public void accept(BookMixAToc data) throws Exception {
                                ACache.get(mContext).put(bookId + "bookToc", data);
                                List<BookMixAToc.mixToc.Chapters> list = data.mixToc.chapters;
                                if (list != null && !list.isEmpty() && mView != null) {
                                    mView.showBookToc(bookId, list);
                                }
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                LogUtils.e("onError: " + e);
                                mView.showError();
                            }
                        },
                        new Action() {
                            @Override
                            public void run() throws Exception {

                            }
                        }
                );
    }
}

