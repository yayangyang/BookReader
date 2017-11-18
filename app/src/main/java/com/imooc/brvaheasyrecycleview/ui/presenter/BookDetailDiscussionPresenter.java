package com.imooc.brvaheasyrecycleview.ui.presenter;

import com.imooc.brvaheasyrecycleview.Bean.DiscussionList;
import com.imooc.brvaheasyrecycleview.api.BookApi;
import com.imooc.brvaheasyrecycleview.base.RxPresenter;
import com.imooc.brvaheasyrecycleview.ui.contract.BookDetailDiscussionContract;
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

public class BookDetailDiscussionPresenter extends RxPresenter<BookDetailDiscussionContract.View>
        implements BookDetailDiscussionContract.Presenter<BookDetailDiscussionContract.View> {

    private BookApi bookApi;

    @Inject
    public BookDetailDiscussionPresenter(BookApi bookApi) {
        this.bookApi = bookApi;
    }

    @Override
    public void getBookDetailDiscussionList(String bookId, String sort, final int start, int limit) {
        String key = StringUtils.creatAcacheKey("book-detail-discussion-list", bookId, sort, start, limit);
        Observable<DiscussionList> fromNetWork = bookApi.getBookDetailDisscussionList(bookId, sort, "normal,vote", start + "", limit + "")
                .compose(RxUtil.<DiscussionList>rxCacheListHelper(key));

        //依次检查disk、network
        Disposable rxDisposable = Observable.concat(RxUtil.rxCreateDiskObservable(key,start, DiscussionList.class), fromNetWork)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<DiscussionList>() {
                            @Override
                            public void accept(DiscussionList list) throws Exception {
                                if (list != null && mView != null) {
                                    mView.showBookDetailDiscussionList(list.posts, start);
                                }
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                LogUtils.e("getBookDetailDiscussionList:" + e.toString());
                                if (mView != null) {
                                    mView.showMyError(start==0);
                                }
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
