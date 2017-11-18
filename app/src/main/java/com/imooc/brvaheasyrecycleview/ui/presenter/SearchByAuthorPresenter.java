package com.imooc.brvaheasyrecycleview.ui.presenter;

import com.imooc.brvaheasyrecycleview.Bean.BooksByTag;
import com.imooc.brvaheasyrecycleview.api.BookApi;
import com.imooc.brvaheasyrecycleview.base.RxPresenter;
import com.imooc.brvaheasyrecycleview.ui.contract.SearchByAuthorContract;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;
import com.imooc.brvaheasyrecycleview.utils.RxUtil;
import com.imooc.brvaheasyrecycleview.utils.StringUtils;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class SearchByAuthorPresenter extends RxPresenter<SearchByAuthorContract.View>
        implements SearchByAuthorContract.Presenter {

    private BookApi bookApi;

    @Inject
    public SearchByAuthorPresenter(BookApi bookApi) {
        this.bookApi = bookApi;
    }

    @Override
    public void getSearchResultList(String author) {
        String key = StringUtils.creatAcacheKey("search-by-author", author);
        Observable<BooksByTag> fromNetWork = bookApi.searchBooksByAuthor(author)
                .compose(RxUtil.<BooksByTag>rxCacheListHelper(key));

        //依次检查disk、network
        Disposable rxDisposable = fromNetWork
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<BooksByTag>() {
                            @Override
                            public void accept(BooksByTag booksByTag) throws Exception {
                                if (booksByTag!=null&&mView != null)
                                    mView.showSearchResultList(booksByTag.books);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                LogUtils.e("getSearchResultList:" + e.toString());
                                if (mView != null)
                                    mView.showError();
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
