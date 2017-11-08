package com.imooc.brvaheasyrecycleview.ui.fragment;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.BookMixAToc;
import com.imooc.brvaheasyrecycleview.Bean.Recommend;
import com.imooc.brvaheasyrecycleview.Bean.support.DownloadMessage;
import com.imooc.brvaheasyrecycleview.Bean.support.DownloadProgress;
import com.imooc.brvaheasyrecycleview.Bean.support.DownloadQueue;
import com.imooc.brvaheasyrecycleview.Bean.support.RefreshCollectionListEvent;
import com.imooc.brvaheasyrecycleview.Bean.support.UserSexChooseFinishedEvent;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.manager.EventManager;
import com.imooc.brvaheasyrecycleview.service.DownloadBookService;
import com.imooc.brvaheasyrecycleview.ui.adapter.RecommendAdapter;
import com.imooc.brvaheasyrecycleview.base.BaseRVFragment;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.component.DaggerMainComponent;
import com.imooc.brvaheasyrecycleview.manager.CollectionsManager;
import com.imooc.brvaheasyrecycleview.ui.activity.BookDetailActivity;
import com.imooc.brvaheasyrecycleview.ui.activity.MainActivity;
import com.imooc.brvaheasyrecycleview.ui.activity.ReadActivity;
import com.imooc.brvaheasyrecycleview.ui.contract.RecommendContract;
import com.imooc.brvaheasyrecycleview.ui.presenter.RecommendPresenter;
import com.imooc.brvaheasyrecycleview.utils.AppUtils;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;
import com.imooc.brvaheasyrecycleview.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/10/17.
 */

public class RecommendFragment extends BaseRVFragment<RecommendPresenter,Recommend.RecommendBooks,BaseViewHolder>
        implements RecommendContract.View,BaseQuickAdapter.OnItemClickListener,BaseQuickAdapter.OnItemLongClickListener{

    @BindView(R.id.llBatchManagement)
    LinearLayout llBatchManagement;
    @BindView(R.id.tvSelectAll)
    TextView tvSelectAll;
    @BindView(R.id.tvDelete)
    TextView tvDelete;

    private boolean isSelectAll = false;

    private ArrayList<BookMixAToc.mixToc.Chapters> chaptersList = new ArrayList<>();

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_recommend;
    }

    @Override
    public void initDatas() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {
        initAdapter(RecommendAdapter.class,R.layout.item_recommend_list,null, true, false);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
        footView = LayoutInflater.from(activity).inflate(R.layout.foot_view_shelf, null, false);
        footView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) activity).setCurrentItem(2);
            }
        });
//        mAdapter.setFooterView(footView);//在这里添加脚布局,若数据为空时第一次设置empty布局会crash(不知原因)
        inflate=View.inflate(AppUtils.getAppContext(),R.layout.bookshelf_empty_view,null);
        inflate.findViewById(R.id.btnToAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) activity).setCurrentItem(2);
            }
        });
        onRefresh();
        Log.e("ww","我5");
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMainComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void showRecommendList(List<Recommend.RecommendBooks> list) {
        mAdapter.getData().clear();
        mAdapter.addData(list);
        mAdapter.setFooterView(footView);
        //推荐列表默认加入收藏
        for (Recommend.RecommendBooks bean : list) {
            //TODO 此处可优化：批量加入收藏->加入前需先判断是否收藏过(作者已优化)
            Log.e("showRecommendList","showRecommendList");
            CollectionsManager.getInstance().add(bean,false);
        }
    }

    @Override
    public void showBookToc(String bookId, List<BookMixAToc.mixToc.Chapters> list) {
        chaptersList.clear();
        chaptersList.addAll(list);
        DownloadBookService.post(new DownloadQueue(bookId, list, 1, list.size()));
        dismissDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void downloadMessage(final DownloadMessage msg) {
        LogUtils.e("downloadMessage");
//        mRecyclerView.setTipViewText(msg.message);
//        if (msg.isComplete) {
//            mRecyclerView.hideTipView(2200);
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showDownProgress(DownloadProgress progress) {
//        mRecyclerView.setTipViewText(progress.message);
        LogUtils.e("showDownProgress");
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (isVisible(llBatchManagement)) //批量管理时，屏蔽点击事件
            return;
        ReadActivity.startActivity(activity, mAdapter.getItem(position), mAdapter.getItem(position).isFromSD);
    }

    @Override
    public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
        //批量管理时，屏蔽长按事件
        if (isVisible(llBatchManagement)) return false;
        showLongClickDialog(position);
        return false;
    }

    /**
     * 显示长按对话框
     *
     * @param position
     */
    private void showLongClickDialog(final int position) {
        final boolean isTop = CollectionsManager.getInstance().isTop(mAdapter.getItem(position)._id);
        String[] items;
        DialogInterface.OnClickListener listener;
        if (mAdapter.getItem(position).isFromSD) {
            items = getResources().getStringArray(R.array.recommend_item_long_click_choice_local);
            listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            //置顶、取消置顶
                            CollectionsManager.getInstance().top(mAdapter.getItem(position)._id, !isTop);
                            break;
                        case 1:
                            //删除
                            List<Recommend.RecommendBooks> removeList = new ArrayList<>();
                            removeList.add(mAdapter.getItem(position));
                            showDeleteCacheDialog(removeList);
                            break;
                        case 2:
                            //批量管理
                            showBatchManagementLayout();
                            break;
                        default:
                            break;
                    }
                    dialog.dismiss();
                }
            };
        } else {
            items = getResources().getStringArray(R.array.recommend_item_long_click_choice);
            listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            //置顶、取消置顶
                            CollectionsManager.getInstance().top(mAdapter.getItem(position)._id, !isTop);
                            break;
                        case 1:
                            //书籍详情
                            BookDetailActivity.startActivity(activity,
                                    mAdapter.getItem(position)._id);
                            break;
                        case 2:
                            //移入养肥区
                            ToastUtils.showToast("正在拼命开发中");
                            break;
                        case 3:
                            //缓存全本
                            if (mAdapter.getItem(position).isFromSD) {
                                ToastUtils.showToast("本地文件不支持该选项哦");
                            } else {
                                showDialog();
                                mPresenter.getTocList(mAdapter.getItem(position)._id);
                            }
                            break;
                        case 4:
                            //删除
                            List<Recommend.RecommendBooks> removeList = new ArrayList<>();
                            removeList.add(mAdapter.getItem(position));
                            showDeleteCacheDialog(removeList);
                            break;
                        case 5:
                            //批量管理
                            showBatchManagementLayout();
                            break;
                        default:
                            break;
                    }
                    dialog.dismiss();
                }
            };
        }
        if (isTop) items[0] = getString(R.string.cancle_top);
        new AlertDialog.Builder(activity)
                .setTitle(mAdapter.getItem(position).title)
                .setItems(items, listener)
                .setNegativeButton(null, null)
                .create().show();
    }

    /**
     * 显示删除本地缓存对话框
     *
     * @param removeList
     */
    private void showDeleteCacheDialog(final List<Recommend.RecommendBooks> removeList) {
        final boolean selected[] = {true};
        new AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.remove_selected_book))
                .setMultiChoiceItems(new String[]{activity.getString(R.string.delete_local_cache)}, selected,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                selected[0] = isChecked;
                            }
                        })
                .setPositiveButton(activity.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @SuppressLint("StaticFieldLeak")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        new AsyncTask<String, String, String>() {
                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                showDialog();
                            }

                            @Override
                            protected String doInBackground(String... params) {
                                CollectionsManager.getInstance().removeSome(removeList, selected[0]);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(String s) {
                                super.onPostExecute(s);
                                ToastUtils.showToast("成功移除书籍");
                                for (Recommend.RecommendBooks bean : removeList) {
                                    mAdapter.getData().remove(bean);
                                    mAdapter.notifyDataSetChanged();
                                }
                                if (isVisible(llBatchManagement)) {
                                    //批量管理完成后，隐藏批量管理布局并刷新页面
                                    goneBatchManagementAndRefreshUI();
                                }
                                hideDialog();
                            }
                        }.execute();

                    }
                })
                .setNegativeButton(activity.getString(R.string.cancel), null)
                .create().show();
    }

    /**
     * 隐藏批量管理布局并刷新页面
     */
    public void goneBatchManagementAndRefreshUI() {
        if (mAdapter == null) return;
        gone(llBatchManagement);
        for (Recommend.RecommendBooks bean :
                mAdapter.getData()) {
            bean.showCheckBox = false;
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 显示批量管理布局
     */
    private void showBatchManagementLayout() {
        visible(llBatchManagement);
        for (Recommend.RecommendBooks bean : mAdapter.getData()) {
            bean.showCheckBox = true;
        }
        mAdapter.notifyDataSetChanged();
    }


    @OnClick(R.id.tvSelectAll)
    public void selectAll() {
        isSelectAll = !isSelectAll;
        tvSelectAll.setText(isSelectAll ? activity.getString(R.string.cancel_selected_all) : activity.getString(R.string.selected_all));
        for (Recommend.RecommendBooks bean : mAdapter.getData()) {
            bean.isSeleted = isSelectAll;
        }
        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.tvDelete)
    public void delete() {
        List<Recommend.RecommendBooks> removeList = new ArrayList<>();
        for (Recommend.RecommendBooks bean : mAdapter.getData()) {
            if (bean.isSeleted) removeList.add(bean);
        }
        if (removeList.isEmpty()) {
            ToastUtils.showToast(activity.getString(R.string.has_not_selected_delete_book));
        } else {
            showDeleteCacheDialog(removeList);
        }
    }

    @Override
    public void onRefresh() {
        Log.e("ww","我6");
        super.onRefresh();
        StackTraceElement stack[] = (new Throwable()).getStackTrace();
        boolean hasRefBookShelfInCallStack = false;
        boolean isMRefresh = false;
        for (int i = 0; i < stack.length; i++) {
            StackTraceElement ste = stack[i];
            if (ste.getMethodName().equals("pullSyncBookShelf")) {
                hasRefBookShelfInCallStack = true;
            }
            if (ste.getMethodName().equals("onAnimationEnd") && ste.getFileName().equals("SwipeRefreshLayout.java")) {
                isMRefresh = true;
            }
        }
        Log.e("has",""+hasRefBookShelfInCallStack);
        Log.e("isMRefresh",""+isMRefresh);
        if (!hasRefBookShelfInCallStack && isMRefresh) {
            Log.e("ww","我7");
            ((MainActivity) activity).pullSyncBookShelf();
            return;
        }

        gone(llBatchManagement);
        List<Recommend.RecommendBooks> data = CollectionsManager.getInstance().getCollectionListBySort();
        mAdapter.getData().clear();
        if(data!=null){
            mAdapter.addData(data);
        }
        mAdapter.setEmptyView(inflate);
        //不加下面这句代码会导致，添加本地书籍的时候，部分书籍添加后直接崩溃
        //报错：Scrapped or attached views may not be recycled. isScrap:false isAttached:true
        mAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void RefreshCollectionList(RefreshCollectionListEvent event) {
        mSwipeRefreshLayout.setRefreshing(true);
        onRefresh();
        LogUtils.e("刷新了");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UserSexChooseFinished(UserSexChooseFinishedEvent event) {
        //首次进入APP，选择性别后，获取推荐列表
        mPresenter.getRecommendList();
    }

    public void showError() {
        loaddingError();
        dismissDialog();
    }

    public void complete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!getUserVisibleHint()) {
            goneBatchManagementAndRefreshUI();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //这样监听返回键有个缺点就是没有拦截Activity的返回监听，如果有更优方案可以改掉
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    if (isVisible(llBatchManagement)) {
                        goneBatchManagementAndRefreshUI();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    private boolean isForeground() {
        ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (MainActivity.class.getName().contains(cpn.getClassName())) {
                return true;
            }
        }

        return false;
    }


    @Override
    public void onLoadMoreRequested() {

    }

}
