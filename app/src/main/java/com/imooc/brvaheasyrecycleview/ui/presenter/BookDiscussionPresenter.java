package com.imooc.brvaheasyrecycleview.ui.presenter;

import com.imooc.brvaheasyrecycleview.Bean.DiscussionList;
import com.imooc.brvaheasyrecycleview.api.BookApi;
import com.imooc.brvaheasyrecycleview.base.RxPresenter;
import com.imooc.brvaheasyrecycleview.ui.contract.BookDiscussionContract;
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

import static android.R.id.list;

/**
 * Created by Administrator on 2017/10/19.
 */

public class BookDiscussionPresenter extends RxPresenter<BookDiscussionContract.View> implements BookDiscussionContract.Presenter {

    private BookApi bookApi;

    @Inject
    public BookDiscussionPresenter(BookApi bookApi){
        this.bookApi=bookApi;
    }

    @Override
    public void getBookDisscussionList(String block, String sort, String distillate, final int start, final int limit) {
        String key = StringUtils.creatAcacheKey("book-discussion-list", block, "all", sort, "all", start + "", limit + "", distillate);

        Observable<DiscussionList> fromNetWork = bookApi.getBookDisscussionList(block, "all", sort, "all", start + "", limit + "", distillate)
                .compose(RxUtil.<DiscussionList>rxCacheListHelper(key));

        //依次检查disk、network
        Disposable rxDisposable = Observable.concat(RxUtil.rxCreateDiskObservable(key,start, DiscussionList.class), fromNetWork)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<DiscussionList>() {
                            @Override
                            public void accept(DiscussionList data) throws Exception {
                                LogUtils.e("DiscussionList:"+data);
                                boolean isRefresh = start == 0 ? true : false;
                                LogUtils.e("ww"+isRefresh);
                                mView.showBookDisscussionList(data.posts, isRefresh);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                LogUtils.e("getBookDisscussionList:" + e.toString());
                                boolean isRefresh = start == 0 ? true : false;
                                mView.showMyError(isRefresh);
                            }
                        },
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                LogUtils.e("complete");
                                mView.complete();
                            }
                        }
                );
        addDisposable(rxDisposable);
    }

}
