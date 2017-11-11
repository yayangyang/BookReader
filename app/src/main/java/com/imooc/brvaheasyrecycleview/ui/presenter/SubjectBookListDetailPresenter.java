package com.imooc.brvaheasyrecycleview.ui.presenter;

import com.imooc.brvaheasyrecycleview.Bean.BookListDetail;
import com.imooc.brvaheasyrecycleview.api.BookApi;
import com.imooc.brvaheasyrecycleview.base.RxPresenter;
import com.imooc.brvaheasyrecycleview.ui.contract.SubjectBookListDetailContract;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;

import org.reactivestreams.Subscription;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SubjectBookListDetailPresenter extends RxPresenter<SubjectBookListDetailContract.View>
        implements SubjectBookListDetailContract.Presenter<SubjectBookListDetailContract.View> {

    private BookApi bookApi;

    @Inject
    public SubjectBookListDetailPresenter(BookApi bookApi) {
        this.bookApi = bookApi;
    }

    @Override
    public void getBookListDetail(String bookListId) {
        Disposable rxDisposable = bookApi.getBookListDetail(bookListId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<BookListDetail>() {
                            @Override
                            public void accept(BookListDetail data) throws Exception {
                                mView.showBookListDetail(data);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                LogUtils.e("getBookListDetail:" + e.toString());
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
