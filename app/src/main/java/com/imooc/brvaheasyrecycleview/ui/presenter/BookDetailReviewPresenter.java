package com.imooc.brvaheasyrecycleview.ui.presenter;

import com.imooc.brvaheasyrecycleview.Bean.HotReview;
import com.imooc.brvaheasyrecycleview.api.BookApi;
import com.imooc.brvaheasyrecycleview.base.RxPresenter;
import com.imooc.brvaheasyrecycleview.ui.contract.BookDetailReviewContract;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;
import com.imooc.brvaheasyrecycleview.utils.RxUtil;
import com.imooc.brvaheasyrecycleview.utils.StringUtils;

import org.reactivestreams.Subscription;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

import static android.R.id.list;

public class BookDetailReviewPresenter extends RxPresenter<BookDetailReviewContract.View> implements BookDetailReviewContract.Presenter<BookDetailReviewContract.View> {

    private BookApi bookApi;

    @Inject
    public BookDetailReviewPresenter(BookApi bookApi) {
        this.bookApi = bookApi;
    }

    public void getBookDetailReviewList(String bookId, String sort, final int start, int limit) {
        String key = StringUtils.creatAcacheKey("book-detail-review-list", bookId, sort, start, limit);
        Observable<HotReview> fromNetWork = bookApi.getBookDetailReviewList(bookId, sort, start + "", limit + "")
                .compose(RxUtil.<HotReview>rxCacheListHelper(key));

        //依次检查disk、network
        Disposable rxDisposable = Observable.concat(RxUtil.rxCreateDiskObservable(key, start,HotReview.class), fromNetWork)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<HotReview>() {
                            @Override
                            public void accept(HotReview data) throws Exception {
                                boolean isRefresh = start == 0 ? true : false;
                                mView.showBookDetailReviewList(data.reviews, isRefresh);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                LogUtils.e("getBookDetailReviewList:" + e.toString());
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

}
