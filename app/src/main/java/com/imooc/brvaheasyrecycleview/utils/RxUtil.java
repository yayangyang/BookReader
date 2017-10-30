package com.imooc.brvaheasyrecycleview.utils;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.imooc.brvaheasyrecycleview.app.ReaderApplication;
import com.imooc.brvaheasyrecycleview.ui.presenter.BookReviewPresenter;

import java.lang.reflect.Field;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static android.R.attr.data;

public class RxUtil {

    /**
     * 统一线程处理
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> rxSchedulerHelper() {    //compose简化线程
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(io.reactivex.Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> Observable rxCreateDiskObservable(final String key, final int start, final Class<T> clazz) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                BookReviewPresenter.threadInfo("本地处理");
                LogUtils.d("get data from disk: key==" + key);
                String json = ACache.get(ReaderApplication.getsInstance()).getAsString(key);
                LogUtils.d("get data from disk finish , json==" + json);

                if (!TextUtils.isEmpty(json)) {
                    T t = new Gson().fromJson(json, clazz);
                    Field[] fields = clazz.getFields();
                    for (Field field : fields) {
                        String className = field.getType().getSimpleName();
                        Log.e("className01",className);
                        // 得到属性值
                        if (className.equalsIgnoreCase("List")) {
                            List list = (List) field.get(t);
                            if(start!=0||list.size()>=10){//刷新时缓存小于10的时候不显示(10按照需求定义,需保证10个item满屏或超出)
                                //即使json转换失败还是会走一遍网络
                                try{
//                                    json+="w";
                                    new Gson().fromJson(json, clazz);
//                                    LogUtils.e("testtestGSON");
                                    e.onNext(json);
                                }catch (Exception ee){
                                    LogUtils.e("Gson转换失败");
                                    ee.printStackTrace();
                                }
                                break;
                            }
                        }
                    }
                }
                Log.e("运行到了","运行到了");
                e.onComplete();
            }
        }).map(new Function<String,T>() {
            @Override
            public T apply(String s) throws Exception {
                Log.e("call","callwwwwwwwww");
//                s+="W";
                return new Gson().fromJson(s, clazz);
            }
        })
                .subscribeOn(Schedulers.io());
    }

    public static <T> ObservableTransformer<T, T> rxCacheListHelper(final String key) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                BookReviewPresenter.threadInfo("网络处理0000");
                return upstream
                        .subscribeOn(Schedulers.io())//指定doOnNext执行线程是新线程
                        .doOnNext(new Consumer<T>() {
                            @Override
                            public void accept(final T data) throws Exception {
                                BookReviewPresenter.threadInfo("网络处理11111");
                                Schedulers.io().createWorker().schedule(new Runnable() {
                                    @Override
                                    public void run() {
                                        BookReviewPresenter.threadInfo("网络处理");
                                        LogUtils.e("get data from network finish ,start cache...");
                                        //通过反射获取List,再判空决定是否缓存
                                        if (data == null)
                                            return;
                                        Class clazz = data.getClass();
                                        LogUtils.e("data类名:"+clazz.getName());
                                        Field[] fields = clazz.getFields();
                                        for (Field field : fields) {
                                            String className = field.getType().getSimpleName();
                                            Log.e("className",className);
                                            // 得到属性值
                                            if (className.equalsIgnoreCase("List")) {
                                                try {
                                                    List list = (List) field.get(data);
                                                    LogUtils.e("list==" + list);
                                                    if (list != null && !list.isEmpty()) {
                                                        ACache.get(ReaderApplication.getsInstance())
                                                                .put(key, new Gson().toJson(data, clazz));
                                                        LogUtils.e("cache finish");
                                                    }
                                                } catch (IllegalAccessException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> ObservableTransformer<T, T> rxCacheBeanHelper(final String key) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(io.reactivex.Observable<T> upstream) {
                return upstream
                        .subscribeOn(Schedulers.io())//指定doOnNext执行线程是新线程
                        .doOnNext(new Consumer<T>() {
                            @Override
                            public void accept(final T data) throws Exception {
                                Schedulers.io().createWorker().schedule(new Runnable() {
                                    @Override
                                    public void run() {
                                        LogUtils.d("get data from network finish ,start cache...");
                                        ACache.get(ReaderApplication.getsInstance())
                                                .put(key, new Gson().toJson(data, data.getClass()));
                                        LogUtils.d("cache finish");
                                    }
                                });
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
