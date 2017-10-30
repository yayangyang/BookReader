package com.imooc.brvaheasyrecycleview.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.BookListDetail;
import com.imooc.brvaheasyrecycleview.Bean.BookLists;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.BaseRVActivity;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.component.DaggerFindComponent;
import com.imooc.brvaheasyrecycleview.manager.CacheManager;
import com.imooc.brvaheasyrecycleview.transform.GlideCircleTransform;
import com.imooc.brvaheasyrecycleview.ui.adapter.SubjectBookListDetailBooksAdapter;
import com.imooc.brvaheasyrecycleview.ui.contract.SubjectBookListDetailContract;
import com.imooc.brvaheasyrecycleview.ui.presenter.SubjectBookListDetailPresenter;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.id.list;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class SubjectBookListDetailActivity extends BaseRVActivity<BookListDetail.BookListBean.BooksBean,BaseViewHolder> implements SubjectBookListDetailContract.View {

    private HeaderViewHolder headerViewHolder;

    static class HeaderViewHolder {
        @BindView(R.id.tvBookListTitle)
        TextView tvBookListTitle;
        @BindView(R.id.tvBookListDesc)
        TextView tvBookListDesc;
        @BindView(R.id.ivAuthorAvatar)
        ImageView ivAuthorAvatar;
        @BindView(R.id.tvBookListAuthor)
        TextView tvBookListAuthor;
        @BindView(R.id.btnShare)
        TextView btnShare;

        public HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private ArrayList<BookListDetail.BookListBean.BooksBean> mAllBooks = new ArrayList<>();
    private ArrayList mArrayList=new ArrayList();

    private int start = 0;
    private int limit = 20;


    @Inject
    SubjectBookListDetailPresenter mPresenter;

    public static final String INTENT_BEAN = "bookListsBean";

    private BookLists.BookListsBean bookListsBean;

    public static void startActivity(Context context, BookLists.BookListsBean bookListsBean) {
        context.startActivity(new Intent(context, SubjectBookListDetailActivity.class)
                .putExtra(INTENT_BEAN, bookListsBean));
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_subject_book_list_detail;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerFindComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void initToolBar() {
        mCommonToolbar.setTitle(R.string.subject_book_list_detail);
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void initDatas() {
        mRecyclerView.setVisibility(View.GONE);
        showDialog();

        bookListsBean = (BookLists.BookListsBean) getIntent().getSerializableExtra(INTENT_BEAN);
    }

    @Override
    public void configViews() {
        initAdapter(SubjectBookListDetailBooksAdapter.class,
                R.layout.item_subject_book_list_detail,mArrayList, false, true);
//        mRecyclerView.removeAllItemDecoration();
        View headerView = View.inflate(mContext,R.layout.header_view_book_list_detail, null);
        headerViewHolder = new HeaderViewHolder(headerView);
        mAdapter.addHeaderView(headerView);

        mPresenter.attachView(this);
        onRefresh();
    }

    @Override
    public void showBookListDetail(BookListDetail data) {
        headerViewHolder.tvBookListTitle.setText(data.getBookList().getTitle());
        headerViewHolder.tvBookListDesc.setText(data.getBookList().getDesc());
        //一些书getAuthor()引用取得可能为空,这里报错仍然会调用mPresenter的错误方法
        headerViewHolder.tvBookListAuthor.setText(data.getBookList().getAuthor().getNickname());

        Glide.with(mContext)
                .load(Constant.IMG_BASE_URL + data.getBookList().getAuthor().getAvatar())
                .placeholder(R.drawable.avatar_default)
                .transform(new GlideCircleTransform(mContext))
                .into(headerViewHolder.ivAuthorAvatar);

        List<BookListDetail.BookListBean.BooksBean> list = data.getBookList().getBooks();
        mAllBooks.addAll(list);
        LogUtils.e(mAllBooks.size());
        if(mAllBooks.size()==0){
            mAdapter.setEmptyView(inflate);
        }else{
            loadNextPage();
            //有数据才展示
            mRecyclerView.setVisibility(View.VISIBLE);
            dismissDialog();
        }
    }

    private void loadNextPage() {
        LogUtils.e("loadNextPage");
        if (start < mAllBooks.size()) {
            if (mAllBooks.size() - start > limit) {
                mAdapter.addData(mAllBooks.subList(start, start + limit));
                mAdapter.loadMoreComplete();
            } else {
                mAdapter.addData(mAllBooks.subList(start, mAllBooks.size()));
                mAdapter.loadMoreEnd();
            }
            start = start + limit;
        }
    }

    @Override
    public void showError() {
        loaddingError();
    }

    @Override
    public void complete() {

    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        BookListDetail.BookListBean.BooksBean item = mAdapter.getItem(position);
        BookDetailActivity.startActivity(this, item.getBook().get_id());
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        mPresenter.getBookListDetail(bookListsBean._id);
    }

    @Override
    public void onLoadMoreRequested() {
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadNextPage();
            }
        }, 500);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_subject_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_collect) {
            CacheManager.getInstance().addCollection(bookListsBean);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }
}
