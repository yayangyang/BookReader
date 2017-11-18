package com.imooc.brvaheasyrecycleview.ui.presenter;

import com.imooc.brvaheasyrecycleview.Bean.BooksByTag;
import com.imooc.brvaheasyrecycleview.api.BookApi;
import com.imooc.brvaheasyrecycleview.base.RxPresenter;
import com.imooc.brvaheasyrecycleview.ui.contract.BooksByTagContract;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;
import com.imooc.brvaheasyrecycleview.utils.RxUtil;
import com.imooc.brvaheasyrecycleview.utils.StringUtils;

import org.reactivestreams.Subscription;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

import static android.R.attr.data;

public class BooksByTagPresenter extends RxPresenter<BooksByTagContract.View> implements BooksByTagContract.Presenter<BooksByTagContract.View> {

    private BookApi bookApi;

    @Inject
    public BooksByTagPresenter(BookApi bookApi) {
        this.bookApi = bookApi;
    }

    @Override
    public void getBooksByTag(String tags, final int start, int limit) {
        String key = StringUtils.creatAcacheKey("books-by-tag", tags, start, limit);
        Observable<BooksByTag> fromNetWork = bookApi.getBooksByTag(tags, start+"", limit+"")
                .compose(RxUtil.<BooksByTag>rxCacheListHelper(key));

        //依次检查disk、network
        Disposable rxDisposable = Observable.concat(RxUtil.rxCreateDiskObservable(key,Integer.valueOf(start),BooksByTag.class), fromNetWork)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<BooksByTag>() {
                            @Override
                            public void accept(BooksByTag data) throws Exception {
                                if (data != null) {
                                    List<BooksByTag.TagBook> list = data.books;
                                    if (list != null && !list.isEmpty() && mView != null) {
                                        mView.showBooksByTag(list, start);
                                    }
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
                                if (mView != null) {
                                    mView.complete();
                                }
                            }
                        }
                );
            addDisposable(rxDisposable);
        }
    }