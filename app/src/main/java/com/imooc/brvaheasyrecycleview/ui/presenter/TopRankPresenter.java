package com.imooc.brvaheasyrecycleview.ui.presenter;

import com.imooc.brvaheasyrecycleview.Bean.RankingList;
import com.imooc.brvaheasyrecycleview.api.BookApi;
import com.imooc.brvaheasyrecycleview.base.RxPresenter;
import com.imooc.brvaheasyrecycleview.ui.contract.TopRankContract;
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

import static android.R.attr.data;

public class TopRankPresenter extends RxPresenter<TopRankContract.View>
        implements TopRankContract.Presenter<TopRankContract.View> {

    private BookApi bookApi;

    @Inject
    public TopRankPresenter(BookApi bookApi) {
        this.bookApi = bookApi;
    }

    @Override
    public void getRankList() {
        LogUtils.e("getRankList");
        String key = StringUtils.creatAcacheKey("book-ranking-list");
        Observable<RankingList> fromNetWork = bookApi.getRanking()
                .compose(RxUtil.<RankingList>rxCacheBeanHelper(key));

        //依次检查disk、network
        Disposable rxDisposable = Observable.concat(RxUtil.rxCreateDiskObservable(key,1,RankingList.class), fromNetWork)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<RankingList>() {
                            @Override
                            public void accept(RankingList data) throws Exception {
                                if (data != null && mView != null) {
                                    mView.showRankList(data);
                                }
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                LogUtils.e("getRankList:" + e.toString());
                                if (mView != null) {
                                    mView.showError();
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
