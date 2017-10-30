package com.imooc.brvaheasyrecycleview.ui.presenter;

import com.imooc.brvaheasyrecycleview.Bean.BookReviewList;
import com.imooc.brvaheasyrecycleview.api.BookApi;
import com.imooc.brvaheasyrecycleview.base.RxPresenter;
import com.imooc.brvaheasyrecycleview.ui.contract.BookReadContract;
import com.imooc.brvaheasyrecycleview.ui.contract.BookReviewContract;
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
import static android.R.id.list;

/**
 * Created by Administrator on 2017/10/20.
 */

public class BookReviewPresenter extends RxPresenter<BookReviewContract.View> implements BookReviewContract.Presenter {

    private BookApi bookApi;

    @Inject
    public BookReviewPresenter(BookApi bookApi) {
        this.bookApi = bookApi;
    }

    //打印当前线程的名称
    public static void threadInfo(String caller) {
        LogUtils.e(caller + " => " + Thread.currentThread().getName());
    }

    @Override
    public void getBookReviewList(String sort, String type, String distillate, final int start, final int limit) {
        String key = StringUtils.creatAcacheKey("book-review-list", sort, type, distillate, start, limit);
        Observable<BookReviewList> fromNetWork = bookApi.getBookReviewList("all", sort, type, start + "", limit + "", distillate)
                .compose(RxUtil.<BookReviewList>rxCacheListHelper(key));
//        if(start==0){
//            //如果是刷新的话缓存(若取得不满屏数据导致调用加载更多这时刷新和加载更多因线程问题数据可能读取不完整或写入错误)
//            //意思就是可能同时读取写入一个文件,或同时写入一个文件导致读取或写入得到的文件不完整
//            //上面是自己看log觉得刷新取得数据不满屏自动调用加载更多觉得会出现上面所说的错误,
//            //但是正常情况下刷新取得数据不满屏自动调用加载更多不会取得数据所以应该不会出现这种问题
//            fromNetWork=fromNetWork.compose(RxUtil.<BookReviewList>rxCacheListHelper(key));
//        }

        //调用有缓存的刷新时因速度太快基本不会出现滑到加载更多而出错,调用无缓存时使用mAdapter.setEnableLoadMore(false);可屏蔽
        //考虑到的情况下应保证刷新和加载更多连续请求网络时加载更多没有数据
        //缓存的不满屏->服务器添加了数据->导致自动加载更多有数据(此时第一个网络请求同时进行)->可能导致上述错误
        //缓存刷新时不满屏最好不要显示(以上都是自己的猜想)

        //依次检查disk、network
        Disposable rxDisposable = Observable.concat(RxUtil.rxCreateDiskObservable(key,start,BookReviewList.class), fromNetWork)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<BookReviewList>() {
                            @Override
                            public void accept(BookReviewList data) throws Exception {
                                LogUtils.e("accept: get data finish"+data);
                                LogUtils.e("start:"+start+"limit"+limit);
                                boolean isRefresh = start == 0 ? true : false;
                                mView.showBookReviewList(data.reviews, isRefresh);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                LogUtils.e("onError: "+e.toString());
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
