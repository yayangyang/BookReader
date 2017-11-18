package com.imooc.brvaheasyrecycleview.ui.presenter;

import android.content.Context;
import android.util.Log;

import com.imooc.brvaheasyrecycleview.Bean.BookMixAToc;
import com.imooc.brvaheasyrecycleview.Bean.ChapterRead;
import com.imooc.brvaheasyrecycleview.api.BookApi;
import com.imooc.brvaheasyrecycleview.base.RxPresenter;
import com.imooc.brvaheasyrecycleview.ui.contract.BookReadContract;
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
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class BookReadPresenter extends RxPresenter<BookReadContract.View>
        implements BookReadContract.Presenter<BookReadContract.View> {

    private Context mContext;
    private BookApi bookApi;

    @Inject
    public BookReadPresenter(Context mContext, BookApi bookApi) {
        this.mContext = mContext;
        this.bookApi = bookApi;
    }

    @Override
    public void getBookMixAToc(final String bookId, String viewChapters) {
        Log.e("getBookMixAToc","getBookMixAToc");
        String key = StringUtils.creatAcacheKey("book-toc", bookId, viewChapters);
        Observable<BookMixAToc.mixToc> fromNetWork = bookApi.getBookMixAToc(bookId, viewChapters)
                .map(new Function<BookMixAToc, BookMixAToc.mixToc>() {
                    @Override
                    public BookMixAToc.mixToc apply(BookMixAToc data) throws Exception {
                        Log.e("data",""+data.mixToc.chapters.size());
                        return data.mixToc;
                    }
                })
                .compose(RxUtil.<BookMixAToc.mixToc>rxCacheListHelper(key));

        //依次检查disk、network
        //上面的依次检查是指依次从硬盘和网络读取,使网络异常时仍能显示已缓存章节才读取两次(自己添加)
        //若要实现从内存读取到不为null不再请求网络可在concat后面加first方法(自己添加)
        Disposable rxDisposable = Observable
                .concat(RxUtil.rxCreateDiskObservable(key,1,BookMixAToc.mixToc.class),fromNetWork)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<BookMixAToc.mixToc>() {
                            @Override
                            public void accept(BookMixAToc.mixToc data) throws Exception {
                                Log.e("uuuuuuuuuuuuuuuuuuu","gggggggg"+data.chapters.size());
                                List<BookMixAToc.mixToc.Chapters> list = data.chapters;
                                if (list != null && !list.isEmpty() && mView != null) {
                                    mView.showBookToc(list);
                                }
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                LogUtils.e("onError: " + e);
                                if (mView != null) {
                                    mView.netError(0);
                                }
                            }
                        },
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                Log.e("onCompleted","onCompleted");
                            }
                        }
                );
        addDisposable(rxDisposable);
    }

    @Override
    public void getChapterRead(String url, final int chapter) {
        Disposable rxDisposable = bookApi.getChapterRead(url).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<ChapterRead>() {
                            @Override
                            public void accept(ChapterRead data) throws Exception {
                                if (data.chapter != null && mView != null) {
                                    mView.showChapterRead(data.chapter, chapter);
                                } else {
                                    mView.netError(chapter);
                                }
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable e) throws Exception {
                                LogUtils.e("onError: " + e);
                                mView.netError(chapter);
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