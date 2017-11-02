package com.imooc.brvaheasyrecycleview.ui.presenter;

import com.imooc.brvaheasyrecycleview.Bean.DiscussionList;
import com.imooc.brvaheasyrecycleview.api.BookApi;
import com.imooc.brvaheasyrecycleview.base.RxPresenter;
import com.imooc.brvaheasyrecycleview.ui.contract.GirlBookDiscussionContract;
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

/**
 * Created by Administrator on 2017/10/20.
 */

public class GirlBookDiscussionPresenter extends RxPresenter<GirlBookDiscussionContract.View> implements GirlBookDiscussionContract.Presenter{

    private BookApi bookApi;

    @Inject
    public GirlBookDiscussionPresenter(BookApi bookApi) {
        this.bookApi = bookApi;
    }

    @Override
    public void getGirlBookDisscussionList(String sort, String distillate, final int start, int limit) {
        String key = StringUtils.creatAcacheKey("girl-book-discussion-list", "girl", "all", sort, "all", start + "", limit + "", distillate);
        Observable<DiscussionList> fromNetWork = bookApi.getGirlBookDisscussionList("girl", "all", sort, "all", start + "", limit + "", distillate)
                .compose(RxUtil.<DiscussionList>rxCacheListHelper(key));
        LogUtils.e(key+"GirlBookDiscussionPresenter");
        //依次检查disk、network
        Disposable rxDisposable = Observable.concat(RxUtil.rxCreateDiskObservable(key,start, DiscussionList.class), fromNetWork)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<DiscussionList>() {
                            @Override
                            public void accept(DiscussionList list) throws Exception {
                                boolean isRefresh = start == 0 ? true : false;
                                mView.showGirlBookDisscussionList(list.posts, isRefresh);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                LogUtils.e("onError: " + e.toString());
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
