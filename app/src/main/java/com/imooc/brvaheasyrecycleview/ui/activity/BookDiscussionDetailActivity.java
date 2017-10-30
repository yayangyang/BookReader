package com.imooc.brvaheasyrecycleview.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.CommentList;
import com.imooc.brvaheasyrecycleview.Bean.Disscussion;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.BaseRVActivity;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.component.DaggerCommunityComponent;
import com.imooc.brvaheasyrecycleview.transform.GlideCircleTransform;
import com.imooc.brvaheasyrecycleview.ui.adapter.BestCommentListAdapter;
import com.imooc.brvaheasyrecycleview.ui.adapter.CommentListAdapter;
import com.imooc.brvaheasyrecycleview.ui.contract.BookDiscussionDetailContract;
import com.imooc.brvaheasyrecycleview.ui.presenter.BookDiscussionDetailPresenter;
import com.imooc.brvaheasyrecycleview.utils.FormatUtils;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;
import com.imooc.brvaheasyrecycleview.utils.ViewUtils;
import com.imooc.brvaheasyrecycleview.view.BookContentTextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/10/24.
 */

public class BookDiscussionDetailActivity extends BaseRVActivity<CommentList.CommentsBean,BaseViewHolder>
        implements BookDiscussionDetailContract.View{

    private static final String INTENT_ID = "id";

    public static void startActivity(Context context, String id) {
        context.startActivity(new Intent(context, BookDiscussionDetailActivity.class)
                .putExtra(INTENT_ID, id));
    }

    private String id;
    private List<CommentList.CommentsBean> mBestCommentList = new ArrayList<>();
    private ArrayList mList=new ArrayList();
    private BestCommentListAdapter mBestCommentListAdapter;
    private HeaderViewHolder headerViewHolder;

    @Inject
    BookDiscussionDetailPresenter mPresenter;

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_community_book_discussion_detail;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerCommunityComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void onLoadMoreRequested() {
        mPresenter.getBookDisscussionComments(id,start, limit);
    }

    @Override
    public void initToolBar() {
        mCommonToolbar.setTitle("详情");
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void initDatas() {
        id = getIntent().getStringExtra(INTENT_ID);

        mPresenter.attachView(this);
        mPresenter.getBookDisscussionDetail(id);
        mPresenter.getBestComments(id);
        mPresenter.getBookDisscussionComments(id,start, limit);
    }

    @Override
    public void configViews() {
        initAdapter(CommentListAdapter.class, R.layout.item_comment_list,mList,false, true);

        View headerView = View.inflate(this, R.layout.header_view_book_review_detail, null);
        headerViewHolder = new HeaderViewHolder(headerView);
        mAdapter.addHeaderView(headerView);
        mAdapter.setEmptyView(inflate);
        ViewUtils.setEmptyViewLayoutParams(-1,-2,mAdapter.getEmptyView());//改变空布局的宽高参数
    }

    @Override
    public void showBookDisscussionDetail(Disscussion disscussion) {
        Glide.with(mContext)
                .load(Constant.IMG_BASE_URL + disscussion.post.author.avatar)
                .placeholder(R.drawable.avatar_default)
                .transform(new GlideCircleTransform(mContext))
                .into(headerViewHolder.ivAvatar);

        headerViewHolder.tvNickName.setText(disscussion.post.author.nickname);
        headerViewHolder.tvTime.setText(FormatUtils.getDescriptionTimeFromDateString(disscussion.post.created));
        headerViewHolder.tvTitle.setText(disscussion.post.title);
        headerViewHolder.tvContent.setText(disscussion.post.content);
        headerViewHolder.tvCommentCount.setText(String.format(mContext.getString(R.string.comment_comment_count), disscussion.post.commentCount));
    }

    @Override
    public void showBestComments(CommentList list) {
        if (list.comments.isEmpty()) {
            gone(headerViewHolder.tvBestComments, headerViewHolder.rvBestComments);
        } else {
            mBestCommentList.addAll(list.comments);
            headerViewHolder.rvBestComments.setHasFixedSize(true);
            headerViewHolder.rvBestComments.setLayoutManager(new LinearLayoutManager(this));
//            headerViewHolder.rvBestComments.addItemDecoration(new SupportDividerItemDecoration(mContext, LinearLayoutManager.VERTICAL, true));
            mBestCommentListAdapter = new BestCommentListAdapter(R.layout.item_comment_best_list, mBestCommentList);
            mBestCommentListAdapter.setOnItemClickListener(this);
            headerViewHolder.rvBestComments.setAdapter(mBestCommentListAdapter);
            visible(headerViewHolder.tvBestComments, headerViewHolder.rvBestComments);
        }
    }

    @Override
    public void showBookDisscussionComments(CommentList list) {
        if(list.comments==null||list.comments.isEmpty()){
            mAdapter.loadMoreEnd();
            mAdapter.notifyDataSetChanged();
        }else{
            mAdapter.loadMoreComplete();
            mAdapter.addData(list.comments);
            start = start + list.comments.size();
        }
    }

    @Override
    public void showError() {
        loaddingError();
        mAdapter.loadMoreFail();
    }

    @Override
    public void complete() {
        LogUtils.e("complete");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    static class HeaderViewHolder {
        @BindView(R.id.ivBookCover)
        ImageView ivAvatar;
        @BindView(R.id.tvBookTitle)
        TextView tvNickName;
        @BindView(R.id.tvTime)
        TextView tvTime;
        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.tvContent)
        BookContentTextView tvContent;
        @BindView(R.id.tvBestComments)
        TextView tvBestComments;
        @BindView(R.id.rvBestComments)
        RecyclerView rvBestComments;
        @BindView(R.id.tvCommentCount)
        TextView tvCommentCount;

        public HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);   //view绑定
        }
    }

}
