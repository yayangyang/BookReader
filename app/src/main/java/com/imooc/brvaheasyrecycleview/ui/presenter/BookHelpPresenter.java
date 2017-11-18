package com.imooc.brvaheasyrecycleview.ui.presenter;

import com.imooc.brvaheasyrecycleview.Bean.BookHelpList;
import com.imooc.brvaheasyrecycleview.api.BookApi;
import com.imooc.brvaheasyrecycleview.base.RxPresenter;
import com.imooc.brvaheasyrecycleview.ui.contract.BookHelpContract;
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

/**
 * Created by Administrator on 2017/10/20.
 */

public class BookHelpPresenter extends RxPresenter<BookHelpContract.View> implements BookHelpContract.Presenter {

    private BookApi bookApi;

    @Inject
    public BookHelpPresenter(BookApi bookApi) {
        this.bookApi = bookApi;
    }

    @Override
    public void getBookHelpList(String sort, String distillate, final int start, int limit) {
        String key = StringUtils.creatAcacheKey("book-help-list", "all", sort, start + "", limit + "", distillate);
        Observable<BookHelpList> fromNetWork = bookApi.getBookHelpList("all", sort, start + "", limit + "", distillate)
                .compose(RxUtil.<BookHelpList>rxCacheListHelper(key));
        LogUtils.e(key+"BookHelpPresenter");
        //依次检查disk、network
        Disposable rxDisposable = Observable.concat(RxUtil.rxCreateDiskObservable(key,start, BookHelpList.class), fromNetWork)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<BookHelpList>() {
                            @Override
                            public void accept(BookHelpList list) throws Exception {
                                if (list != null && mView != null) {
                                    mView.showBookHelpList(list.helps, start);
                                }
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                LogUtils.e("getBookHelpList:" + e.toString());
                                if (mView != null) {
                                    mView.showMyError(start == 0);
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
