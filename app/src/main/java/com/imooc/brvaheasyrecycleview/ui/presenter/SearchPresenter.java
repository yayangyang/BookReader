package com.imooc.brvaheasyrecycleview.ui.presenter;

import com.imooc.brvaheasyrecycleview.Bean.AutoComplete;
import com.imooc.brvaheasyrecycleview.Bean.HotWord;
import com.imooc.brvaheasyrecycleview.Bean.SearchDetail;
import com.imooc.brvaheasyrecycleview.Bean.bean;
import com.imooc.brvaheasyrecycleview.api.BookApi;
import com.imooc.brvaheasyrecycleview.base.RxPresenter;
import com.imooc.brvaheasyrecycleview.ui.contract.SearchContract;
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
import io.reactivex.schedulers.Schedulers;

public class SearchPresenter extends RxPresenter<SearchContract.View>
        implements SearchContract.Presenter<SearchContract.View> {

    private BookApi bookApi;

    @Inject
    public SearchPresenter(BookApi bookApi) {
        this.bookApi = bookApi;
    }

    public void getHotWordList() {
        String key = StringUtils.creatAcacheKey("hot-word-list");
        Observable<HotWord> fromNetWork = bookApi.getHotWord()
                .compose(RxUtil.<HotWord>rxCacheListHelper(key));

        //依次检查disk、network
        Disposable rxDisposable = Observable.concat(RxUtil.rxCreateDiskObservable(key,1,HotWord.class), fromNetWork)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<HotWord>() {
                            @Override
                            public void accept(HotWord hotWord) throws Exception {
                                List<String> list = hotWord.hotWords;
                                if (list != null && !list.isEmpty() && mView != null) {
                                    mView.showHotWordList(list);
                                }
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                LogUtils.e("onError: " + e);
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
    public void getAutoCompleteList(String query) {
        Disposable rxDisposable = bookApi.getAutoComplete(query).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<AutoComplete>() {
                            @Override
                            public void accept(AutoComplete autoComplete) throws Exception {
                                LogUtils.e("getAutoCompleteList" + autoComplete.keywords);
                                List<String> list = autoComplete.keywords;
                                if (list != null && !list.isEmpty() && mView != null) {
                                    mView.showAutoCompleteList(list);
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

                            }
                        }
                );
        addDisposable(rxDisposable);
    }

    @Override
    public void getSearchResultList(String query) {
        Disposable rxDisposable = bookApi.getSearchResult(query).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<SearchDetail>() {
                            @Override
                            public void accept(SearchDetail bean) throws Exception {
                                LogUtils.e("getSearchResultList" + bean.books);
                                List<SearchDetail.SearchBooks> list = bean.books;
                                if (list != null && !list.isEmpty() && mView != null) {
                                    mView.showSearchResultList(list);
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

                            }
                        }
                );
        addDisposable(rxDisposable);
    }
}
