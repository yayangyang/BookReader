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

                //对于设置可以加载更多的RecyclerView来说,刷新时缓存小于10的时候不显示,其他的不会影响需要显示
                //默认设置数据给非加载更多的RecyclerView的start==1
                if (!TextUtils.isEmpty(json)) {
                    try{
                        //即使json转换失败还是会走一遍网络
                        T t=new Gson().fromJson(json, clazz);
                        Field[] fields = clazz.getFields();
                        for (Field field : fields) {
                            String className = field.getType().getSimpleName();
                            Log.e("className01",className);
                            // 得到属性值
                            if (className.equalsIgnoreCase("List")) {
                                List list = (List) field.get(t);
                                LogUtils.e("start:"+start);
                                LogUtils.e("list.size():"+list.size());
                                //这个判断适合类中只有一个list,而且是关键数据
                                if(start!=0||list.size()>=10){//刷新时缓存小于10的时候不显示(10按照需求定义,需保证10个item满屏或超出)
                                    e.onNext(json);
                                    break;
                                }
                            }
                        }
                    }catch (Exception ee){
                        LogUtils.e("Gson转换失败");
                        ee.printStackTrace();
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
                                            //这里当类里有List才缓存(自己根据实际情况确定缓存类是否符合list为空就不缓存,如果是,那list是关键数据)
                                            if (className.equalsIgnoreCase("List")) {
                                                try {
                                                    List list = (List) field.get(data);
                                                    LogUtils.e("list==" + list);
                                                    if (list != null && !list.isEmpty()) {
                                                        ACache.get(ReaderApplication.getsInstance())
                                                                .put(key, new Gson().toJson(data, clazz));
                                                        LogUtils.e("cache finish");
                                                        //如果缓存成功可以跳出防止类中有多个list多次缓存(自己觉得)
                                                        //类中只有一个list也可以不跳出
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
                                        LogUtils.e("get data from network finish ,start cache...");
                                        ACache.get(ReaderApplication.getsInstance())
                                                .put(key, new Gson().toJson(data, data.getClass()));
                                        LogUtils.e("cache finish");
                                    }
                                });
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 适用于不可以加载更多的RecyclerView,或者其他
     * (写第二个方法原因是使用brvah若有加载更多功能会默认加载更多,这时因为线程问题可能会造成数据错乱)
     * @param key
     * @param start
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> Observable secondRxCreateDiskObservable(final String key, final int start, final Class<T> clazz) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                BookReviewPresenter.threadInfo("本地处理");
                String json = ACache.get(ReaderApplication.getsInstance()).getAsString(key);

                if (!TextUtils.isEmpty(json)) {
                    try{
                        e.onNext(json);
                    }catch (Exception ee){
                        LogUtils.e("Gson转换失败");
                        ee.printStackTrace();
                    }
                }
                Log.e("运行到了","运行到了");
                e.onComplete();
            }
        }).map(new Function<String,T>() {
            @Override
            public T apply(String s) throws Exception {
                Log.e("call","callwwwwwwwww");
                return new Gson().fromJson(s, clazz);
            }
        })
                .subscribeOn(Schedulers.io());
    }

}
