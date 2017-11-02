package com.imooc.brvaheasyrecycleview.ui.presenter;

import com.imooc.brvaheasyrecycleview.Bean.BookLists;
import com.imooc.brvaheasyrecycleview.api.BookApi;
import com.imooc.brvaheasyrecycleview.base.RxPresenter;
import com.imooc.brvaheasyrecycleview.ui.contract.SubjectFragmentContract;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;
import com.imooc.brvaheasyrecycleview.utils.RxUtil;
import com.imooc.brvaheasyrecycleview.utils.StringUtils;
import com.imooc.brvaheasyrecycleview.utils.ToastUtils;

import org.reactivestreams.Subscription;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class SubjectFragmentPresenter extends RxPresenter<SubjectFragmentContract.View> implements SubjectFragmentContract.Presenter<SubjectFragmentContract.View> {

    private BookApi bookApi;

    @Inject
    public SubjectFragmentPresenter(BookApi bookApi) {
        this.bookApi = bookApi;
    }

    @Override
    public void getBookLists(String duration, String sort, final int start, int limit, String tag, String gender) {
        String key = StringUtils.creatAcacheKey("book-lists", duration, sort, start + "", limit + "", tag, gender);
        Observable<BookLists> fromNetWork = bookApi.getBookLists(duration, sort, start + "", limit + "", tag, gender)
                .compose(RxUtil.<BookLists>rxCacheListHelper(key));

        //依次检查disk、network
        Disposable rxDisposable = Observable.concat(RxUtil.rxCreateDiskObservable(key,start, BookLists.class), fromNetWork)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<BookLists>() {
                            @Override
                            public void accept(BookLists data) throws Exception {
                                mView.showBookList(data.bookLists, start == 0 ? true : false);
                                if(start==0){
                                    if (data.bookLists == null || data.bookLists.size() <= 0) {
                                        ToastUtils.showSingleToast("暂无相关书单");
                                    }
                                }
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                LogUtils.e("getBookLists:" + e.toString());
                                boolean isRefresh = start == 0 ? true : false;
                                mView.showMyError(isRefresh);
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
