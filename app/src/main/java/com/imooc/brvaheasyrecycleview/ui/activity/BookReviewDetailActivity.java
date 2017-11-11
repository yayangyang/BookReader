package com.imooc.brvaheasyrecycleview.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.BookReview;
import com.imooc.brvaheasyrecycleview.Bean.CommentList;
import com.imooc.brvaheasyrecycleview.Bean.MyBean.Comment;
import com.imooc.brvaheasyrecycleview.Bean.MyBean.ReviewHelpful;
import com.imooc.brvaheasyrecycleview.Bean.user.Login;
import com.imooc.brvaheasyrecycleview.Bean.user.TencentLoginResult;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.app.ReaderApplication;
import com.imooc.brvaheasyrecycleview.base.BaseLoginRvActivity;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.component.DaggerCommunityComponent;
import com.imooc.brvaheasyrecycleview.manager.SettingManager;
import com.imooc.brvaheasyrecycleview.transform.GlideCircleTransform;
import com.imooc.brvaheasyrecycleview.transform.GlideRoundTransform;
import com.imooc.brvaheasyrecycleview.ui.adapter.BestCommentListAdapter;
import com.imooc.brvaheasyrecycleview.ui.adapter.CommentListAdapter;
import com.imooc.brvaheasyrecycleview.ui.contract.BookReviewDetailContract;
import com.imooc.brvaheasyrecycleview.ui.presenter.BookReviewDetailPresenter;
import com.imooc.brvaheasyrecycleview.utils.FormatUtils;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;
import com.imooc.brvaheasyrecycleview.utils.ToastUtils;
import com.imooc.brvaheasyrecycleview.utils.ViewUtils;
import com.imooc.brvaheasyrecycleview.view.BookContentTextView;
import com.imooc.brvaheasyrecycleview.view.LoginPopupWindow;
import com.imooc.brvaheasyrecycleview.view.XLHRatingBar;
import com.imooc.brvaheasyrecycleview.view.myView.InterceptFrameLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/10/23.
 */

public class BookReviewDetailActivity extends BaseLoginRvActivity<CommentList.CommentsBean,BaseViewHolder>
        implements BookReviewDetailContract.View,View.OnClickListener{

    private static final String INTENT_ID = "id";

    public static void startActivity(Context context, String id) {
        context.startActivity(new Intent(context, BookReviewDetailActivity.class)
                .putExtra(INTENT_ID, id));
    }

    private String id;
    private List<CommentList.CommentsBean> mBestCommentList = new ArrayList<>();
    private ArrayList mList=new ArrayList();
    private BestCommentListAdapter mBestCommentListAdapter;
    private HeaderViewHolder headerViewHolder;

    private BookReview bookReview;

    @Inject
    BookReviewDetailPresenter mPresenter;

    @BindView(R.id.edit_add)
    EditText edit_add;
    @BindView(R.id.btn_send)
    FloatingActionButton btn_send;

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
        mPresenter.getBookReviewComments(id,start, limit);
    }

    @Override
    public void initToolBar() {
        mCommonToolbar.setTitle("书评详情");
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void initDatas() {
        id = getIntent().getStringExtra(INTENT_ID);

        mPresenter.attachView(this);
        mPresenter.getBookReviewDetail(id);
        mPresenter.getBestComments(id);
        mPresenter.getBookReviewComments(id,start, limit);
    }

    @Override
    public void configViews() {
        initAdapter(CommentListAdapter.class, R.layout.item_comment_list,mList,false, true);

        View headerView = View.inflate(this, R.layout.header_view_book_review_detail, null);
        headerViewHolder = new HeaderViewHolder(headerView);
        headerViewHolder.llHelpfull.setVisibility(View.VISIBLE);
        headerViewHolder.tvHelpfullYes.setOnClickListener(this);
        headerViewHolder.tvHelpfullNo.setOnClickListener(this);
        mAdapter.addHeaderView(headerView);
        mAdapter.setEmptyView(inflate);
        ViewUtils.setEmptyViewLayoutParams(-1,-2,mAdapter.getEmptyView());//改变空布局的宽高参数

        btn_send.setEnabled(false);//内容开始为空,默认不可点击
        edit_add.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = edit_add.getText().toString();
                if(TextUtils.isEmpty(content)){
                    btn_send.setEnabled(false);
                }else{
                    btn_send.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        initControl();
    }

    private void initControl() {
        screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        keyHeight = screenHeight / 3;

        findViewById(android.R.id.content).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
                    InterceptFrameLayout viewById = findViewById(R.id.control);
                    viewById.isIntercept=true;
                    LogUtils.e("键盘弹起");
                } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
                    InterceptFrameLayout viewById = findViewById(R.id.control);
                    viewById.isIntercept=false;
                    LogUtils.e("键盘落下");
                }
            }
        });
        findViewById(R.id.myll).setVisibility(View.VISIBLE);
        findViewById(R.id.control).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    LogUtils.e("ACTION_DOWN");
                    y=event.getY();
                }else if(event.getAction()==MotionEvent.ACTION_MOVE){
                    LogUtils.e("ACTION_MOVE");
                    if(event.getY()>y){
                        LogUtils.e("运行到了");
                        InterceptFrameLayout viewById = findViewById(R.id.control);
                        viewById.isIntercept=false;
                        InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        return mInputMethodManager.hideSoftInputFromWindow(BookReviewDetailActivity.this.getCurrentFocus().getWindowToken(), 0);
                    }
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    LogUtils.e("ACTION_UP");
                    y=0;
                }
                return true;//消费了事件(返回false可点击接下来就执行onclick)
            }
        });
    }

    @Override
    public void showBookReviewDetail(final BookReview data) {
        Glide.with(mContext)
                .load(Constant.IMG_BASE_URL + data.review.author.avatar)
                .placeholder(R.drawable.avatar_default)
                .transform(new GlideCircleTransform(mContext))
                .into(headerViewHolder.ivAuthorAvatar);

        headerViewHolder.tvBookAuthor.setText(data.review.author.nickname);
        headerViewHolder.tvTime.setText(FormatUtils.getDescriptionTimeFromDateString(data.review.created));
        headerViewHolder.tvTitle.setText(data.review.title);
        headerViewHolder.tvContent.setText(data.review.content);

        Glide.with(mContext)
                .load(Constant.IMG_BASE_URL + data.review.book.cover)
                .placeholder(R.drawable.cover_default)
                .transform(new GlideRoundTransform(mContext))
                .into(headerViewHolder.ivBookCover);
        headerViewHolder.tvBookTitle.setText(data.review.book.title);

        headerViewHolder.tvHelpfullYesCount.setText(String.valueOf(data.review.helpful.yes));
        headerViewHolder.tvHelpfullNoCount.setText(String.valueOf(data.review.helpful.no));

        headerViewHolder.tvCommentCount.setText(String.format(mContext.getString(R.string.comment_comment_count), data.review.commentCount));

        headerViewHolder.rlBookInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookDetailActivity.startActivity(BookReviewDetailActivity.this, data.review.book._id);
            }
        });

        headerViewHolder.ratingBar.setCountSelected(data.review.rating);

        this.bookReview=data;
        if(SettingManager.getInstance().getIsSetHelpful(bookReview.review._id).equals("yes")){
            headerViewHolder.tvHelpfullYesCount.setTextColor(Color.RED);
        }else if(SettingManager.getInstance().getIsSetHelpful(bookReview.review._id).equals("no")){
            headerViewHolder.tvHelpfullNoCount.setTextColor(Color.RED);
        }else{
            headerViewHolder.tvHelpfullYes.setEnabled(true);
            headerViewHolder.tvHelpfullNo.setEnabled(true);
        }
    }

    @Override
    public void showBestComments(CommentList list) {
        if(list.comments.isEmpty()){
            gone(headerViewHolder.tvBestComments, headerViewHolder.rvBestComments);
        }else{
            mBestCommentList.addAll(list.comments);
            headerViewHolder.rvBestComments.setHasFixedSize(true);
            headerViewHolder.rvBestComments.setLayoutManager(new LinearLayoutManager(this));
//            headerViewHolder.rvBestComments.addItemDecoration(new SupportDividerItemDecoration(mContext, LinearLayoutManager.VERTICAL, true));
            mBestCommentListAdapter = new BestCommentListAdapter( R.layout.item_comment_best_list,mBestCommentList);
            mBestCommentListAdapter.setOnItemClickListener(this);
            headerViewHolder.rvBestComments.setAdapter(mBestCommentListAdapter);
            visible(headerViewHolder.tvBestComments, headerViewHolder.rvBestComments);
        }
    }

    @Override
    public void showBookReviewComments(CommentList list) {
        if(list.comments==null||list.comments.isEmpty()){
            mAdapter.loadMoreEnd();
        }else{
            mAdapter.loadMoreComplete();
            mAdapter.addData(list.comments);
            start = start + list.comments.size();
        }
    }

    @Override
    public void publishReviewResult(Comment comment, String content) {
        if(comment.ok){
            ToastUtils.showToast("发送成功");
            CommentList.CommentsBean bean = new CommentList.CommentsBean();
            CommentList.CommentsBean.AuthorBean authorBean = new CommentList.CommentsBean.AuthorBean();
            bean.content=content;
            bean.created=FormatUtils.myFormatTime(new Date());
            LogUtils.e(bean.created);
            CommentList.CommentsBean commentsBean = (CommentList.CommentsBean) mList.get(0);
            bean.floor=commentsBean.floor+1;
            authorBean.avatar=ReaderApplication.sLogin.user.avatar;
            authorBean.nickname=ReaderApplication.sLogin.user.nickname;
            authorBean.lv=ReaderApplication.sLogin.user.lv;
            bean.author=authorBean;
            mAdapter.addData(0,bean);
        }else{
            if(!"不良内容，发布失败".equals(comment.msg)){
                ReaderApplication.sLogin=null;
                SettingManager.getInstance().saveLoginInfo(ReaderApplication.sLogin);
                ToastUtils.showToast("发送失败,token可能过期,请重新登录");
            }else{
                ToastUtils.showToast("不良内容,发布失败");
            }
        }
        edit_add.setText("");
    }

    @Override
    public void postReviewHelpfulResult(ReviewHelpful reviewHelpful, String is_helpful) {
        if(reviewHelpful.ok){
            int yesCount=Integer.valueOf(headerViewHolder.tvHelpfullYesCount.getText().toString());
            int noCount=Integer.valueOf(headerViewHolder.tvHelpfullNoCount.getText().toString());
            if(is_helpful.equals("yes")){
                headerViewHolder.tvHelpfullYesCount.setText(++yesCount+"");
            }else{
                headerViewHolder.tvHelpfullYesCount.setText(++noCount+"");
            }
            if(bookReview!=null&&bookReview.review!=null&& !TextUtils.isEmpty(bookReview.review._id)){
                SettingManager.getInstance().saveHelpful(bookReview.review._id,is_helpful);
            }
            ToastUtils.showToast("评价成功");
            headerViewHolder.tvHelpfullNo.setEnabled(false);
            headerViewHolder.tvHelpfullYes.setEnabled(false);
            if(is_helpful.equals("yes")){
                headerViewHolder.tvHelpfullYesCount.setTextColor(Color.RED);
            }else{
                headerViewHolder.tvHelpfullNoCount.setTextColor(Color.RED);
            }
        }else{
            //已经评价了
            ToastUtils.showToast("您已经评价过啦");
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

    @OnClick(R.id.btn_send)
    public void sendMessage(){
        LogUtils.e("btn_send");
        boolean hasId=false;
        if(bookReview!=null&&bookReview.review!=null&& !TextUtils.isEmpty(bookReview.review._id)){
            LogUtils.e("id:"+bookReview.review._id);
            hasId=true;
        }else{
            ToastUtils.showToast("hasId为空");
        }
        if(ReaderApplication.sLogin==null||TextUtils.isEmpty(ReaderApplication.sLogin.token)){
            ToastUtils.showToast("请先登录");
            if (popupWindow == null) {
                popupWindow = new LoginPopupWindow(this);
                popupWindow.setLoginTypeListener(this);
            }
            //在屏幕上偏移,第一个参数由方法getWindowToken()都行(自己添加)
            popupWindow.showAtLocation(mCommonToolbar, Gravity.CENTER, 0, 0);
        }else{
            String content = edit_add.getText().toString();
            if(hasId&&!TextUtils.isEmpty(ReaderApplication.sLogin.token)&&!TextUtils.isEmpty(content)){
                mPresenter.publishReview(bookReview.review._id,content,ReaderApplication.sLogin.token);
            }
        }
    }

    @Override
    public void loginSuccess(Login login) {
        if(login.ok){
            ToastUtils.showToast("登录成功");
            ReaderApplication.sLogin=login;
            SettingManager.getInstance().saveLoginInfo(login);//保存登录信息,以便下次登录app使用
        }else{
            ToastUtils.showToast("登录被拒绝");
            ReaderApplication.sLogin=null;
            SettingManager.getInstance().saveLoginInfo(login);
        }
    }

    @Override
    protected void loginZhuiShu(TencentLoginResult result) {
        mPresenter.login(result.openid, result.access_token, "QQ");
    }

    @Override
    protected void loginCancel() {

    }

    @Override
    public void onLogin(ImageView view, String type) {
        if (type.equals("QQ")) {
            if (!ReaderApplication.mTencent.isSessionValid()) {
                if (loginListener == null) loginListener = new BaseLoginRvActivity.BaseUIListener();
                LogUtils.e("loginListener不为null,开始登陆");
                ReaderApplication.mTencent.login(this, "all", loginListener);
            }
        }
    }

    @Override
    public void onClick(View v) {
        boolean hasId=false;
        if(bookReview!=null&&bookReview.review!=null&& !TextUtils.isEmpty(bookReview.review._id)){
            LogUtils.e("id:"+bookReview.review._id);
            hasId=true;
        }else{
            ToastUtils.showToast("hasId为空");
        }
        if(ReaderApplication.sLogin==null||TextUtils.isEmpty(ReaderApplication.sLogin.token)){
            ToastUtils.showToast("请先登录");
            if (popupWindow == null) {
                popupWindow = new LoginPopupWindow(this);
                popupWindow.setLoginTypeListener(this);
            }
            //在屏幕上偏移,第一个参数由方法getWindowToken()都行(自己添加)
            popupWindow.showAtLocation(mCommonToolbar, Gravity.CENTER, 0, 0);
        }else{
            String is_helpful="yes";
            if(v.getId()==R.id.tvHelpfullNoCount){
                is_helpful="no";
            }
            if(hasId&&!TextUtils.isEmpty(ReaderApplication.sLogin.token)){
                mPresenter.postReviewHelpful(bookReview.review._id,ReaderApplication.sLogin.token,is_helpful);
            }
        }
    }

    static class HeaderViewHolder {
        @BindView(R.id.ivAuthorAvatar)
        ImageView ivAuthorAvatar;
        @BindView(R.id.tvBookAuthor)
        TextView tvBookAuthor;
        @BindView(R.id.tvTime)
        TextView tvTime;
        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.tvContent)
        BookContentTextView tvContent;
        @BindView(R.id.rlBookInfo)
        RelativeLayout rlBookInfo;
        @BindView(R.id.ivBookCover)
        ImageView ivBookCover;
        @BindView(R.id.tvBookTitle)
        TextView tvBookTitle;
        @BindView(R.id.tvHelpfullYesCount)
        TextView tvHelpfullYesCount;
        @BindView(R.id.tvHelpfullNoCount)
        TextView tvHelpfullNoCount;
        @BindView(R.id.tvHelpfullYes)
        TextView tvHelpfullYes;
        @BindView(R.id.tvHelpfullNo)
        TextView tvHelpfullNo;
        @BindView(R.id.tvBestComments)
        TextView tvBestComments;
        @BindView(R.id.rvBestComments)
        RecyclerView rvBestComments;
        @BindView(R.id.tvCommentCount)
        TextView tvCommentCount;
        @BindView(R.id.rating)
        XLHRatingBar ratingBar;

        @BindView(R.id.llHelpfull)
        LinearLayout llHelpfull;

        public HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);   //view绑定
        }
    }

}
