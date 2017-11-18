package com.imooc.brvaheasyrecycleview.ui.presenter;


import com.imooc.brvaheasyrecycleview.Bean.CategoryList;
import com.imooc.brvaheasyrecycleview.api.BookApi;
import com.imooc.brvaheasyrecycleview.base.RxPresenter;
import com.imooc.brvaheasyrecycleview.ui.contract.TopCategoryListContract;
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

public class TopCategoryListPresenter extends RxPresenter<TopCategoryListContract.View>
        implements TopCategoryListContract.Presenter<TopCategoryListContract.View> {

    private BookApi bookApi;

    @Inject
    public TopCategoryListPresenter(BookApi bookApi) {
        this.bookApi = bookApi;
    }

    @Override
    public void getCategoryList() {
        String key = StringUtils.creatAcacheKey("book-category-list");
        Observable<CategoryList> fromNetWork = bookApi.getCategoryList()
                .compose(RxUtil.<CategoryList>rxCacheBeanHelper(key));

        //依次检查disk、network
        Disposable rxDisposable = Observable.concat(RxUtil.rxCreateDiskObservable(key,1,CategoryList.class), fromNetWork)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<CategoryList>() {
                            @Override
                            public void accept(CategoryList data) throws Exception {
                                if (data != null && mView != null) {
                                    mView.showCategoryList(data);
                                }
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                LogUtils.e("wwwwwwwwwwwwwwwwwwwwwwww");
                                LogUtils.e(e.toString());
                                if (mView != null) {
                                    mView.showError();
                                }
                            }
                        },
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                LogUtils.i("complete");
                                if (mView != null) {
                                    mView.complete();
                                }
                            }
                        }
                );
        addDisposable(rxDisposable);
    }
}
