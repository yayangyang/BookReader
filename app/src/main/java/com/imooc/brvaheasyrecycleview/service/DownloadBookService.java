package com.imooc.brvaheasyrecycleview.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.text.TextUtils;

import com.imooc.brvaheasyrecycleview.Bean.BookMixAToc;
import com.imooc.brvaheasyrecycleview.Bean.ChapterRead;
import com.imooc.brvaheasyrecycleview.Bean.support.DownloadMessage;
import com.imooc.brvaheasyrecycleview.Bean.support.DownloadProgress;
import com.imooc.brvaheasyrecycleview.Bean.support.DownloadQueue;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.api.BookApi;
import com.imooc.brvaheasyrecycleview.api.support.LoggingInterceptor;
import com.imooc.brvaheasyrecycleview.app.ReaderApplication;
import com.imooc.brvaheasyrecycleview.manager.CacheManager;
import com.imooc.brvaheasyrecycleview.utils.AppUtils;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;
import com.imooc.brvaheasyrecycleview.utils.NetworkUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DownloadBookService extends Service {

    public static List<DownloadQueue> downloadQueues = new ArrayList<>();

    public BookApi bookApi;
    protected CompositeDisposable mCompositeDisposable;

    public boolean isBusy = false; // 当前是否有下载任务在进行

    public static boolean canceled = false;

    @Override
    public void onCreate() {
        LogUtils.e("DownloadBookService建立了");
        super.onCreate();
        EventBus.getDefault().register(this);
        LoggingInterceptor logging = new LoggingInterceptor(new LoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                LogUtils.e("拦截了");
            }
        });
        logging.setLevel(LoggingInterceptor.Level.BODY);
        bookApi = ReaderApplication.getsInstance().getAppComponent().getReaderApi();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearDisposable();
        EventBus.getDefault().unregister(this);
    }

    public static void post(DownloadQueue downloadQueue) {
        LogUtils.e("DownloadBookService-post");
        EventBus.getDefault().post(downloadQueue);
    }

    public void post(DownloadProgress progress) {
        EventBus.getDefault().post(progress);
    }

    private void post(DownloadMessage message) {
        EventBus.getDefault().post(message);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void addToDownloadQueue(DownloadQueue queue) {
        LogUtils.e("addToDownloadQueue");
        if (!TextUtils.isEmpty(queue.bookId)) {
            boolean exists = false;
            // 判断当前书籍缓存任务是否存在
            for (int i = 0; i < downloadQueues.size(); i++) {
                if (downloadQueues.get(i).bookId.equals(queue.bookId)) {
                    LogUtils.e("addToDownloadQueue:exists");
                    exists = true;
                    break;
                }
            }
            if (exists) {
                post(new DownloadMessage(queue.bookId, "当前缓存任务已存在", false));
                return;
            }

            // 添加到下载队列
            downloadQueues.add(queue);
            LogUtils.e("addToDownloadQueue:" + queue.bookId);
            post(new DownloadMessage(queue.bookId, "成功加入缓存队列", false));
        }
        // 从队列顺序取出第一条下载
        if (downloadQueues.size() > 0 && !isBusy) {
            isBusy = true;
            downloadBook(downloadQueues.get(0));
        }
    }

    public synchronized void downloadBook(final DownloadQueue downloadQueue) {
        LogUtils.e("downloadBook");
        AsyncTask<Integer, Integer, Integer> downloadTask = new AsyncTask<Integer, Integer, Integer>() {

            List<BookMixAToc.mixToc.Chapters> list = downloadQueue.list;
            String bookId = downloadQueue.bookId;
            int start = downloadQueue.start; // 起始章节
            int end = downloadQueue.end; // 结束章节

            @Override
            protected Integer doInBackground(Integer... params) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                int failureCount = 0;
                for (int i = start; i <= end && i <= list.size(); i++) {
                    if (canceled) {
                        break;
                    }
                    // 网络异常，取消下载
                    if (!NetworkUtils.isAvailable(AppUtils.getAppContext())) {
                        downloadQueue.isCancel = true;
                        post(new DownloadMessage(bookId, getString(R.string.book_read_download_error), true));
                        failureCount = -1;
                        break;
                    }
                    if (!downloadQueue.isFinish && !downloadQueue.isCancel) {
                        // 章节文件不存在,则下载，否则跳过
                        if (CacheManager.getInstance().getChapterFile(bookId, i) == null) {
                            BookMixAToc.mixToc.Chapters chapters = list.get(i - 1);
                            String url = chapters.link;
                            int ret = download(url, bookId, chapters.title, i, list.size());
                            if (ret != 1) {
                                failureCount++;
                            }
                        } else {
                            post(new DownloadProgress(bookId, String.format(
                                    getString(R.string.book_read_alreday_download), list.get(i - 1).title, i, list.size()),
                                    true));
                        }
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
                return failureCount;
            }


            @Override
            protected void onPostExecute(Integer failureCount) {
                super.onPostExecute(failureCount);
                downloadQueue.isFinish = true;
                if (failureCount > -1) {
                    // 完成通知
                    post(new DownloadMessage(bookId,
                            String.format(getString(R.string.book_read_download_complete), failureCount), true));
                }
                // 下载完成，从队列里移除
                downloadQueues.remove(downloadQueue);
                // 释放 空闲状态
                isBusy = false;
                if (!canceled) {
                    // post一个空事件，通知继续执行下一个任务
                    post(new DownloadQueue());
                } else {
                    downloadQueues.clear();
                }
                canceled = false;
                LogUtils.i(bookId + "缓存完成，失败" + failureCount + "章");
            }
        };
        downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private int download(String url, final String bookId, final String title, final int chapter, final int chapterSize) {

        final int[] result = {-1};

        Disposable disposable = bookApi.getChapterRead(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<ChapterRead>() {
                            @Override
                            public void accept(ChapterRead data) throws Exception {
                                if (data.chapter != null) {
                                    post(new DownloadProgress(bookId, String.format(
                                            getString(R.string.book_read_download_progress), title, chapter,
                                            chapterSize),
                                            true));
                                    CacheManager.getInstance().saveChapterFile(bookId, chapter, data.chapter);
                                    result[0] = 1;
                                } else {
                                    result[0] = 0;
                                }
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                result[0] = 0;
                            }
                        },
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                result[0] = 1;
                            }
                        }
                );

        addDisposable(disposable);

        while (result[0] == -1) {
            try {
                Thread.sleep(350);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result[0];
    }

    public static void cancel() {
        canceled = true;
    }

    protected void clearDisposable() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }

    protected void addDisposable(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }
}
