package com.imooc.brvaheasyrecycleview.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.imooc.brvaheasyrecycleview.Bean.BookDetail;
import com.imooc.brvaheasyrecycleview.Bean.BookLists;
import com.imooc.brvaheasyrecycleview.Bean.HotReview;
import com.imooc.brvaheasyrecycleview.Bean.InterestBookList;
import com.imooc.brvaheasyrecycleview.Bean.Recommend;
import com.imooc.brvaheasyrecycleview.Bean.RecommendBookList;
import com.imooc.brvaheasyrecycleview.Bean.support.RefreshCollectionIconEvent;
import com.imooc.brvaheasyrecycleview.Bean.user.Login;
import com.imooc.brvaheasyrecycleview.Bean.user.TencentLoginResult;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.app.ReaderApplication;
import com.imooc.brvaheasyrecycleview.base.BaseActivity;
import com.imooc.brvaheasyrecycleview.base.BaseLoginActivity;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.component.DaggerBookComponent;
import com.imooc.brvaheasyrecycleview.manager.CollectionsManager;
import com.imooc.brvaheasyrecycleview.transform.GlideRoundTransform;
import com.imooc.brvaheasyrecycleview.ui.adapter.HotReviewAdapter;
import com.imooc.brvaheasyrecycleview.ui.adapter.InterestBookAdapter;
import com.imooc.brvaheasyrecycleview.ui.adapter.RecommendBookListAdapter;
import com.imooc.brvaheasyrecycleview.ui.contract.BookDetailContract;
import com.imooc.brvaheasyrecycleview.ui.presenter.BookDetailPresenter;
import com.imooc.brvaheasyrecycleview.utils.FormatUtils;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;
import com.imooc.brvaheasyrecycleview.utils.ToastUtils;
import com.imooc.brvaheasyrecycleview.view.DrawableCenterButton;
import com.imooc.brvaheasyrecycleview.view.TagColor;
import com.imooc.brvaheasyrecycleview.view.TagGroup;
import com.tencent.open.SocialConstants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

import static android.R.attr.data;
import static com.imooc.brvaheasyrecycleview.manager.EventManager.refreshCollectionIcon;

/**
 * Created by Administrator on 2017/10/18.
 */

public class BookDetailActivity extends BaseLoginActivity
        implements BookDetailContract.View,BaseQuickAdapter.OnItemClickListener{

    public static String INTENT_BOOK_ID = "bookId";

    public static void startActivity(Context context, String bookId) {
        LogUtils.e("bookId:"+bookId);
        context.startActivity(new Intent(context, BookDetailActivity.class)
                .putExtra(INTENT_BOOK_ID, bookId));
    }

    @BindView(R.id.ivBookCover)
    ImageView mIvBookCover;
    @BindView(R.id.tvBookListTitle)
    TextView mTvBookTitle;
    @BindView(R.id.tvBookListAuthor)
    TextView mTvAuthor;
    @BindView(R.id.tvCatgory)
    TextView mTvCatgory;
    @BindView(R.id.tvWordCount)
    TextView mTvWordCount;
    @BindView(R.id.tvLatelyUpdate)
    TextView mTvLatelyUpdate;
    @BindView(R.id.btnRead)
    DrawableCenterButton mBtnRead;
    @BindView(R.id.btnJoinCollection)
    DrawableCenterButton mBtnJoinCollection;
    @BindView(R.id.tvLatelyFollower)
    TextView mTvLatelyFollower;
    @BindView(R.id.tvRetentionRatio)
    TextView mTvRetentionRatio;
    @BindView(R.id.tvSerializeWordCount)
    TextView mTvSerializeWordCount;
    @BindView(R.id.tag_group)
    TagGroup mTagGroup;
    @BindView(R.id.tvlongIntro)
    TextView mTvlongIntro;
    @BindView(R.id.tvMoreReview)
    TextView mTvMoreReview;
    @BindView(R.id.rvHotReview)
    RecyclerView mRvHotReview;
    @BindView(R.id.rlCommunity)
    RelativeLayout mRlCommunity;
    @BindView(R.id.tvCommunity)
    TextView mTvCommunity;
    @BindView(R.id.tvHelpfulYes)
    TextView mTvPostCount;
    @BindView(R.id.tvRecommendBookList)
    TextView mTvRecommendBookList;

    @BindView(R.id.rvRecommendBoookList)
    RecyclerView mRvRecommendBoookList;

    @BindView(R.id.tvMoreInterest)
    TextView mTvMoreInterest;
    @BindView(R.id.rvInterestBook)
    RecyclerView mRvInterestBook;

    @Inject
    BookDetailPresenter mPresenter;

    private List<String> tagList = new ArrayList<>();
    private int times = 0;
    private HotReviewAdapter mHotReviewAdapter;
    private List<HotReview.Reviews> mHotReviewList = new ArrayList<>();
    private RecommendBookListAdapter mRecommendBookListAdapter;
    private InterestBookAdapter mInterestBookAdapter;
    private List<RecommendBookList.RecommendBook> mRecommendBookList = new ArrayList<>();
    private ArrayList<InterestBookList.InterestBook> mInterestBookList = new ArrayList<>();
    private String bookId;

    private boolean collapseLongIntro = true;
    private Recommend.RecommendBooks recommendBooks;
    private boolean isJoinedCollections = false;

    @Override
    public int getLayoutId() {
        return R.layout.activity_book_detail;
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
        mCommonToolbar.setTitle("书籍详情");
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void initDatas() {
        bookId = getIntent().getStringExtra(INTENT_BOOK_ID);
        mPresenter.attachView(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {
        mRvHotReview.setHasFixedSize(true);
        mRvHotReview.setLayoutManager(new LinearLayoutManager(this));
        mHotReviewAdapter=new HotReviewAdapter(R.layout.item_book_detai_hot_review_list,mHotReviewList);
        mHotReviewAdapter.setOnItemClickListener(this);
        mRvHotReview.setAdapter(mHotReviewAdapter);

        mRvInterestBook.setHasFixedSize(true);
        mRvInterestBook.setLayoutManager(new GridLayoutManager(this,4));
        mInterestBookAdapter=new InterestBookAdapter(R.layout.item_book_detail_interest_book,mInterestBookList);
        mInterestBookAdapter.setOnItemClickListener(this);
        mRvInterestBook.setAdapter(mInterestBookAdapter);

        mRvRecommendBoookList.setHasFixedSize(true);
        mRvRecommendBoookList.setLayoutManager(new LinearLayoutManager(this));
        mRecommendBookListAdapter=new RecommendBookListAdapter(R.layout.item_book_detail_recommend_book_list,mRecommendBookList);
        mRecommendBookListAdapter.setOnItemClickListener(this);
        mRvRecommendBoookList.setAdapter(mRecommendBookListAdapter);

        mTagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                startActivity(new Intent(BookDetailActivity.this, BooksByTagActivity.class)
                        .putExtra("tag", tag));
            }
        });


        mPresenter.getBookDetail(bookId);
        mPresenter.getHotReview(bookId);
        mPresenter.getInterestBook(bookId);
        mPresenter.getRecommendBookList(bookId,"3");
    }

    @Override
    public void showBookDetail(BookDetail data) {
        Glide.with(mContext)
                .load(Constant.IMG_BASE_URL + data.cover)
                .placeholder(R.drawable.cover_default)
                .transform(new GlideRoundTransform(mContext))
                .into(mIvBookCover);

        mTvBookTitle.setText(data.title);
        mTvAuthor.setText(String.format(getString(R.string.book_detail_author), data.author));
        mTvCatgory.setText(String.format(getString(R.string.book_detail_category), data.cat));
        mTvWordCount.setText(FormatUtils.formatWordCount(data.wordCount));
        mTvLatelyUpdate.setText(FormatUtils.getDescriptionTimeFromDateString(data.updated));
        mTvLatelyFollower.setText(String.valueOf(data.latelyFollower));
        mTvRetentionRatio.setText(TextUtils.isEmpty(data.retentionRatio) ?
                "-" : String.format(getString(R.string.book_detail_retention_ratio),
                data.retentionRatio));
        mTvSerializeWordCount.setText(data.serializeWordCount < 0 ? "-" :
                String.valueOf(data.serializeWordCount));

        tagList.clear();
        tagList.addAll(data.tags);
        times = 0;
        showHotWord();

        mTvlongIntro.setText(data.longIntro);
        mTvCommunity.setText(String.format(getString(R.string.book_detail_community), data.title));
        mTvPostCount.setText(String.format(getString(R.string.book_detail_post_count), data.postCount));

        recommendBooks = new Recommend.RecommendBooks();
        recommendBooks.title = data.title;
        recommendBooks._id = data._id;
        recommendBooks.cover = data.cover;
        recommendBooks.lastChapter = data.lastChapter;
        recommendBooks.updated = data.updated;

        refreshCollectionIcon();
    }

    private void refreshCollectionIcon() {
        if (CollectionsManager.getInstance().isCollected(recommendBooks._id)) {
            initCollection(false);
        } else {
            initCollection(true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void RefreshCollectionIcon(RefreshCollectionIconEvent event) {
        refreshCollectionIcon();
    }

    /**
     * 每次显示8个
     */
    private void showHotWord() {
        int start, end;
        if (times < tagList.size() && times + 8 <= tagList.size()) {
            start = times;
            end = times + 8;
        } else if (times < tagList.size() - 1 && times + 8 > tagList.size()) {
            start = times;
            end = tagList.size() - 1;
        } else {
            start = 0;
            end = tagList.size() > 8 ? 8 : tagList.size();
        }
        times = end;
        if (end - start > 0) {
            List<String> batch = tagList.subList(start, end);
            List<TagColor> colors = TagColor.getRandomColors(batch.size());
            mTagGroup.setTags(colors, (String[]) batch.toArray(new String[batch.size()]));
        }
    }

    @Override
    public void showHotReview(List<HotReview.Reviews> data) {
        mHotReviewList.clear();
        Log.e("list.size()","ww"+data.size());
        mHotReviewList.addAll(data);
        mHotReviewAdapter.notifyDataSetChanged();
    }

    @Override
    public void showInterestBook(List<InterestBookList.InterestBook> data) {
        LogUtils.e("showInterestBook");
        if(data!=null&&!data.isEmpty()){
            ArrayList<InterestBookList.InterestBook> interestBooks = new ArrayList<>();
            for(int i=0;i<4;i++){
                interestBooks.add(data.get(i));
            }
            mInterestBookAdapter.setNewData(interestBooks);
        }
    }

    @Override
    public void showRecommendBookList(List<RecommendBookList.RecommendBook> data) {
        if (!data.isEmpty()) {
            mTvRecommendBookList.setVisibility(View.VISIBLE);
            mRecommendBookList.clear();
            mRecommendBookList.addAll(data);
            mRecommendBookListAdapter.notifyDataSetChanged();
        }
    }

    @OnClick(R.id.btnJoinCollection)
    public void onClickJoinCollection() {
        if (!isJoinedCollections) {
            if (recommendBooks != null) {
                CollectionsManager.getInstance().add(recommendBooks,true);
                ToastUtils.showToast(String.format(getString(
                        R.string.book_detail_has_joined_the_book_shelf), recommendBooks.title));
                initCollection(false);
            }
        } else {
            CollectionsManager.getInstance().remove(recommendBooks._id);
            ToastUtils.showToast(String.format(getString(
                    R.string.book_detail_has_remove_the_book_shelf), recommendBooks.title));
            initCollection(true);
        }
    }

    private void initCollection(boolean coll) {
        if (coll) {
            mBtnJoinCollection.setText(R.string.book_detail_join_collection);
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.book_detail_info_add_img);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mBtnJoinCollection.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.shape_common_btn_solid_normal));
            mBtnJoinCollection.setCompoundDrawables(drawable, null, null, null);
            mBtnJoinCollection.postInvalidate();
            isJoinedCollections = false;
        } else {
            mBtnJoinCollection.setText(R.string.book_detail_remove_collection);
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.book_detail_info_del_img);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mBtnJoinCollection.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.btn_join_collection_pressed));
            mBtnJoinCollection.setCompoundDrawables(drawable, null, null, null);
            mBtnJoinCollection.postInvalidate();
            isJoinedCollections = true;
        }
    }

    @OnClick(R.id.btnRead)
    public void onClickRead() {
        if (recommendBooks == null) return;
        ReadActivity.startActivity(this, recommendBooks);
    }

    @OnClick(R.id.tvBookListAuthor)
    public void searchByAuthor() {
        String author = mTvAuthor.getText().toString().replaceAll(" ", "");
        SearchByAuthorActivity.startActivity(this, author);
    }

    @OnClick(R.id.tvlongIntro)
    public void collapseLongIntro() {
        if (collapseLongIntro) {
            mTvlongIntro.setMaxLines(20);
            collapseLongIntro = false;
        } else {
            mTvlongIntro.setMaxLines(4);
            collapseLongIntro = true;
        }
    }

    @OnClick(R.id.tvMoreReview)
    public void onClickMoreReview() {
        BookDetailCommunityActivity.startActivity(this, bookId, mTvBookTitle.getText().toString(), 1);
    }

    @OnClick(R.id.tvMoreInterest)
    public void onClickMoreInterest() {
        ArrayList<InterestBookList.InterestBook> data = (ArrayList<InterestBookList.InterestBook>) mInterestBookAdapter.getData();
        if(data!=null){
            LogUtils.e("id:"+data.get(0)._id);
            SearchByInterestActivity.startActivity(this, data);
        }
    }

    @OnClick(R.id.rlCommunity)
    public void onClickCommunity() {
        BookDetailCommunityActivity.startActivity(this, bookId, mTvBookTitle.getText().toString(), 0);
    }

    @Override
    public void showError() {

    }

    @Override
    public void complete() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Object data = adapter.getItem(position);
        if (data instanceof HotReview.Reviews) {
            BookReviewDetailActivity.startActivity(this, ((HotReview.Reviews) data)._id);
        } else if (data instanceof RecommendBookList.RecommendBook) {
            RecommendBookList.RecommendBook recommendBook = (RecommendBookList.RecommendBook) data;

            BookLists.BookListsBean bookListsBean = new BookLists.BookListsBean();
            bookListsBean._id = recommendBook.id;
            bookListsBean.author = recommendBook.author;
            bookListsBean.bookCount = recommendBook.bookCount;
            bookListsBean.collectorCount = recommendBook.collectorCount;
            bookListsBean.cover = recommendBook.cover;
            bookListsBean.desc = recommendBook.desc;
            bookListsBean.title = recommendBook.title;

            SubjectBookListDetailActivity.startActivity(this, bookListsBean);
        }else if (data instanceof InterestBookList.InterestBook) {
            InterestBookList.InterestBook interestBook = (InterestBookList.InterestBook) adapter.getItem(position);
            BookDetailActivity.startActivity(this,interestBook._id);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.share){
            share();
        }
        return super.onOptionsItemSelected(item);
    }

    public void share() {
        LogUtils.e("share");
        if(loginListener==null){
            loginListener=new BaseUIListener();
        }
        Bundle bundle = new Bundle();
        //这条分享消息被好友点击后的跳转URL。
        bundle.putString(SocialConstants.PARAM_TARGET_URL, "http://connect.qq.com/");
        //分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_	SUMMARY不能全为空，最少必须有一个是有值的。
        bundle.putString(SocialConstants.PARAM_TITLE, "我在测试");
        //分享的图片URL
        bundle.putString(SocialConstants.PARAM_IMAGE_URL,
                "http://img3.cache.netease.com/photo/0005/2013-03-07/8PBKS8G400BV0005.jpg");
        //分享的消息摘要，最长50个字
        bundle.putString(SocialConstants.PARAM_SUMMARY, "测试");
        //手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替
        bundle.putString(SocialConstants.PARAM_APPNAME, "??我在测试");
        //标识该消息的来源应用，值为应用名称+AppId。
        bundle.putString(SocialConstants.PARAM_APP_SOURCE, "星期几" + "222222");

        ReaderApplication.mTencent.shareToQQ(this, bundle , loginListener);
    }

    @Override
    protected void loginZhuiShu(TencentLoginResult result) {
        LogUtils.e("取得数据"+result);
        ToastUtils.showToast("分享成功");
    }

    @Override
    protected void loginCancel() {
//        ToastUtils.showToast("分享被取消");
    }

    @Override
    public void loginSuccess(Login login) {

    }

    @Override
    public void onLogin(ImageView view, String type) {

    }
}
