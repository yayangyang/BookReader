package com.imooc.brvaheasyrecycleview.ui.presenter;


import com.imooc.brvaheasyrecycleview.Bean.BooksByCats;
import com.imooc.brvaheasyrecycleview.Bean.Rankings;
import com.imooc.brvaheasyrecycleview.api.BookApi;
import com.imooc.brvaheasyrecycleview.base.RxPresenter;
import com.imooc.brvaheasyrecycleview.ui.contract.SubRankContract;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SubRankPresenter extends RxPresenter<SubRankContract.View>
        implements SubRankContract.Presenter<SubRankContract.View> {

    private BookApi bookApi;

    @Inject
    public SubRankPresenter(BookApi bookApi) {
        this.bookApi = bookApi;
    }

    @Override
    public void getRankList(String id) {
        Disposable rxDisposable = bookApi.getRanking(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<Rankings>() {
                            @Override
                            public void accept(Rankings rankings) throws Exception {
                                List<Rankings.RankingBean.BooksBean> books = rankings.ranking.books;

                                BooksByCats cats = new BooksByCats();
                                cats.books = new ArrayList<>();
                                for (Rankings.RankingBean.BooksBean bean : books) {
                                    cats.books.add(new BooksByCats.BooksBean(bean._id, bean.cover, bean.title,
                                            bean.author, bean.cat, bean.shortIntro, bean.latelyFollower, bean.retentionRatio));
                                }
                                mView.showRankList(cats);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                LogUtils.e("getRankList:" + e.toString());
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
