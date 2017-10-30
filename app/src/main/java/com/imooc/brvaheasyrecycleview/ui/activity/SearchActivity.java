package com.imooc.brvaheasyrecycleview.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.SearchDetail;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.BaseRVActivity;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.component.DaggerBookComponent;
import com.imooc.brvaheasyrecycleview.manager.CacheManager;
import com.imooc.brvaheasyrecycleview.ui.adapter.AutoCompleteAdapter;
import com.imooc.brvaheasyrecycleview.ui.adapter.SearchAdapter;
import com.imooc.brvaheasyrecycleview.ui.adapter.SearchHistoryAdapter;
import com.imooc.brvaheasyrecycleview.ui.contract.SearchContract;
import com.imooc.brvaheasyrecycleview.ui.presenter.SearchPresenter;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;
import com.imooc.brvaheasyrecycleview.utils.ToastUtils;
import com.imooc.brvaheasyrecycleview.view.TagColor;
import com.imooc.brvaheasyrecycleview.view.TagGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class SearchActivity extends BaseRVActivity<SearchDetail.SearchBooks,BaseViewHolder>
        implements SearchContract.View {

    public static final String INTENT_QUERY = "query";

    public static void startActivity(Context context, String query) {
        context.startActivity(new Intent(context, SearchActivity.class)
                .putExtra(INTENT_QUERY, query));
    }

    @BindView(R.id.tvChangeWords)
    TextView mTvChangeWords;
    @BindView(R.id.tag_group)
    TagGroup mTagGroup;
    @BindView(R.id.rootLayout)
    LinearLayout mRootLayout;
    @BindView(R.id.layoutHotWord)
    RelativeLayout mLayoutHotWord;
    @BindView(R.id.rlHistory)
    RelativeLayout rlHistory;
    @BindView(R.id.tvClear)
    TextView tvClear;
    @BindView(R.id.lvSearchHistory)
    ListView lvSearchHistory;

    @Inject
    SearchPresenter mPresenter;

    private List<String> tagList = new ArrayList<>();
    private int times = 0;

    private AutoCompleteAdapter mAutoAdapter;
    private List<String> mAutoList = new ArrayList<>();

    private SearchHistoryAdapter mHisAdapter;
    private List<String> mHisList = new ArrayList<>();

    private ArrayList mArrayList=new ArrayList();
    private String key;
    private MenuItem searchMenuItem;
    private SearchView searchView;

    private ListPopupWindow mListPopupWindow;

    int hotIndex = 0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerBookComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void initToolBar() {
        mCommonToolbar.setTitle("");
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void initDatas() {
        key = getIntent().getStringExtra(INTENT_QUERY);

        mHisAdapter = new SearchHistoryAdapter(mHisList);
        lvSearchHistory.setAdapter(mHisAdapter);
        lvSearchHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                search(mHisList.get(position));
            }
        });
        initSearchHistory();
    }

    @Override
    public void configViews() {
        initAdapter(SearchAdapter.class,
                R.layout.item_search_result_list,mArrayList, false, false);

        initAutoList();

        mTagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                search(tag);
            }
        });

        mTvChangeWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHotWord();
            }
        });

        mPresenter.attachView(this);
        mPresenter.getHotWordList();
        Log.e("configViews","configViews");
    }

    private void initAutoList() {
        mAutoAdapter = new AutoCompleteAdapter(mAutoList);
        mListPopupWindow = new ListPopupWindow(this);
        mListPopupWindow.setAdapter(mAutoAdapter);
        mListPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mListPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mListPopupWindow.setAnchorView(mCommonToolbar);
        mListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListPopupWindow.dismiss();
                TextView tv = view.findViewById(R.id.tvAutoCompleteItem);
                String str = tv.getText().toString();
                search(str);
            }
        });
    }

    @Override
    public synchronized void showHotWordList(List<String> list) {
        visible(mTvChangeWords);
        tagList.clear();
        tagList.addAll(list);
        times = 0;
        showHotWord();
    }

    /**
     * 每次显示8个热搜词
     */
    private synchronized void showHotWord() {
        int tagSize = 8;
        String[] tags = new String[tagSize];
        for (int j = 0; j < tagSize && j < tagList.size(); hotIndex++, j++) {
            tags[j] = tagList.get(hotIndex % tagList.size());
        }
        List<TagColor> colors = TagColor.getRandomColors(tagSize);
        mTagGroup.setTags(colors, tags);
    }

    @Override
    public void showAutoCompleteList(List<String> list) {
        mAutoList.clear();
        mAutoList.addAll(list);

        if (!mListPopupWindow.isShowing()) {
            mListPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            mListPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            mListPopupWindow.show();
        }
        mAutoAdapter.notifyDataSetChanged();

    }

    @Override
    public void showSearchResultList(List<SearchDetail.SearchBooks> list) {
        mAdapter.getData().clear();
        mAdapter.setNewData(list);
        mAdapter.notifyDataSetChanged();
        showMyRc();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e("onCreateOptionsMenu","onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        searchMenuItem = menu.findItem(R.id.action_search);//在菜单中找到对应控件的item
        searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                LogUtils.e("提交");
                key = query;
                mPresenter.getSearchResultList(query);
                saveSearchHistory(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    if (mListPopupWindow.isShowing())
                        mListPopupWindow.dismiss();
                    LogUtils.e("onQueryTextChange-集合为空");
                    goneMyRc();
                } else {
                    LogUtils.e("onQueryTextChange-getAutoCompleteList");
                    mPresenter.getAutoCompleteList(newText);
                }
                return false;
            }
        });
        search(key); // 外部调用搜索，则打开页面立即进行搜索
        MenuItemCompat.setOnActionExpandListener(searchMenuItem,
                new MenuItemCompat.OnActionExpandListener() {//设置打开关闭动作监听
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        LogUtils.e("onMenuItemActionCollapse");
                        goneMyRc();
                        return true;
                    }
                });
        return true;
    }

    /**
     * 保存搜索记录.不重复，最多保存20条
     *
     * @param query
     */
    private void saveSearchHistory(String query) {
        List<String> list = CacheManager.getInstance().getSearchHistory();
        if (list == null) {
            list = new ArrayList<>();
            list.add(query);
        } else {
            Iterator<String> iterator = list.iterator();
            while (iterator.hasNext()) {
                String item = iterator.next();
                if (TextUtils.equals(query, item)) {
                    iterator.remove();
                }
            }
            list.add(0, query);
        }
        int size = list.size();
        if (size > 20) { // 最多保存20条
            for (int i = size - 1; i >= 20; i--) {
                list.remove(i);
            }
        }
        CacheManager.getInstance().saveSearchHistory(list);
        initSearchHistory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume","onResume");
    }

    private void initSearchHistory() {
        List<String> list = CacheManager.getInstance().getSearchHistory();
        mHisList.clear();
        if (list != null && list.size() > 0) {
            tvClear.setEnabled(true);
            mHisList.addAll(list);
        } else {
            tvClear.setEnabled(false);
        }
        mHisAdapter.notifyDataSetChanged();
    }

    /**
     * 展开SearchView进行查询
     *
     * @param key
     */
    private void search(String key) {
        MenuItemCompat.expandActionView(searchMenuItem);
        if (!TextUtils.isEmpty(key)) {
            searchView.setQuery(key, true);
            saveSearchHistory(key);
        }else{
            ToastUtils.showToast("key为空");
        }
    }

    private void showMyRc() {
        gone(mTagGroup, mLayoutHotWord, rlHistory);
        visible(mRecyclerView);
        if (mListPopupWindow.isShowing())
            mListPopupWindow.dismiss();
    }

    private void goneMyRc() {
        visible(mTagGroup, mLayoutHotWord, rlHistory);
        gone(mRecyclerView);
        if (mListPopupWindow.isShowing())
            mListPopupWindow.dismiss();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        SearchDetail.SearchBooks data = mAdapter.getItem(position);
        BookDetailActivity.startActivity(this, data._id);
    }

    @OnClick(R.id.tvClear)
    public void clearSearchHistory() {
        CacheManager.getInstance().saveSearchHistory(null);
        initSearchHistory();
    }

    @Override
    public void showError() {
        loaddingError();
    }

    @Override
    public void complete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

}
