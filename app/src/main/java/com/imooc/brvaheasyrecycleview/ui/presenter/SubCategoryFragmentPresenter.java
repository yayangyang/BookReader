package com.imooc.brvaheasyrecycleview.ui.presenter;

import com.imooc.brvaheasyrecycleview.Bean.BooksByCats;
import com.imooc.brvaheasyrecycleview.api.BookApi;
import com.imooc.brvaheasyrecycleview.base.RxPresenter;
import com.imooc.brvaheasyrecycleview.ui.contract.SubCategoryFragmentContract;
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

public class SubCategoryFragmentPresenter extends RxPresenter<SubCategoryFragmentContract.View>
        implements SubCategoryFragmentContract.Presenter<SubCategoryFragmentContract.View> {

    private BookApi bookApi;

    @Inject
    public SubCategoryFragmentPresenter(BookApi bookApi) {
        this.bookApi = bookApi;
    }

    @Override
    public void getCategoryList(String gender, final String major, String minor, String type, final int start, int limit) {
        String key = StringUtils.creatAcacheKey("category-list", gender, type, major, minor, start, limit);
        Observable<BooksByCats> fromNetWork = bookApi.getBooksByCats(gender, type, major, minor, start, limit)
                .compose(RxUtil.<BooksByCats>rxCacheListHelper(key));

        //依次检查disk、network
        Disposable rxDisposable =fromNetWork
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<BooksByCats>() {
                            @Override
                            public void accept(BooksByCats booksByCats) throws Exception {
                                mView.showCategoryList(booksByCats, start == 0 ? true : false);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                LogUtils.e("getCategoryList:" + e.toString());
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
