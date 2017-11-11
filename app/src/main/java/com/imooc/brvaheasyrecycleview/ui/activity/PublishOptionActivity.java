package com.imooc.brvaheasyrecycleview.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.imooc.brvaheasyrecycleview.Bean.BookReview;
import com.imooc.brvaheasyrecycleview.Bean.MyBean.MyBookReview;
import com.imooc.brvaheasyrecycleview.Bean.MyBean.MyDiscussion;
import com.imooc.brvaheasyrecycleview.Bean.user.Login;
import com.imooc.brvaheasyrecycleview.Bean.user.TencentLoginResult;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.app.ReaderApplication;
import com.imooc.brvaheasyrecycleview.base.BaseLoginActivity;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.component.DaggerMyComponent;
import com.imooc.brvaheasyrecycleview.manager.SettingManager;
import com.imooc.brvaheasyrecycleview.ui.contract.PublishOptionContract;
import com.imooc.brvaheasyrecycleview.ui.presenter.PublishOptionPresenter;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;
import com.imooc.brvaheasyrecycleview.utils.ToastUtils;
import com.imooc.brvaheasyrecycleview.view.LoginPopupWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;


/**
 * Created by Administrator on 2017/11/9.
 */

public class PublishOptionActivity extends BaseLoginActivity implements PublishOptionContract.View{

    private boolean isReview,isVote;
    private int rating=0,index=0;
    private BookReview mBookReview;
    private Map<String, String> params;

    private String titleIsEmpty[]={"请输入书评标题","请输入帖子标题"};
    private String contentIsEmpty[]={"请输入书评正文","请输入正文"};
    private String isFitTitle[]={"标题字数不能少于4字","标题字数太少了哦"};

    private ArrayList<String> voteString=new ArrayList();

    @Inject
    PublishOptionPresenter mPresenter;

    @BindView(R.id.et_title)
    EditText et_title;

    @BindView(R.id.et_content)
    EditText et_content;

    public static void startActivity(Context context, boolean isReview, boolean isVote,BookReview bookReview){
        context.startActivity(new Intent(context,PublishOptionActivity.class)
                .putExtra(Constant.IS_REVIEW,isReview)
                .putExtra(Constant.IS_VOTE,isVote)
                .putExtra(Constant.BOOK_REVIEW,bookReview));
    }

    @Override
    public int getLayoutId() {
        isReview = getIntent().getBooleanExtra(Constant.IS_REVIEW, false);
        isVote = getIntent().getBooleanExtra(Constant.IS_VOTE, false);
        rating = getIntent().getIntExtra(Constant.RATING, 0);
        mBookReview = (BookReview) getIntent().getSerializableExtra(Constant.BOOK_REVIEW);
        return R.layout.activity_publish_book;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMyComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void initToolBar() {
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
        if(isReview){
            index=0;
            mCommonToolbar.setTitle("编辑书评");

            if(!TextUtils.isEmpty(mBookReview.review.title)||
                    !TextUtils.isEmpty(mBookReview.review.content)){
                et_title.setText(mBookReview.review.title);
                et_content.setText(mBookReview.review.content);
            }

        }else{
            index=1;
            if(isVote){
                mCommonToolbar.setTitle("编辑投票");
            }else{
                mCommonToolbar.setTitle("编辑话题");
            }
        }
    }

    @Override
    public void initDatas() {
        mPresenter.attachView(this);
    }

    @Override
    public void configViews() {
        mCommonToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e("isReview:"+isReview);
                if(isReview){
                    setResult(2,new Intent()
                                    .putExtra(Constant.TITLE,et_title.getText().toString())
                                    .putExtra(Constant.CONTENT,et_content.getText().toString()));
                    finish();
                }else if(et_title.getText()!=null|| et_content.getText()!=null){
                    new AlertDialog.Builder(mContext)
                            .setTitle("提示")
                            .setMessage("离开将丢失已输入的内容,确定离开?")
                            .setNegativeButton("留在此页", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("离开", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            }).create().show();
                }else{
                    finish();
                }
            }
        });

        if(isReview){
            et_title.setHint("请输入标题");
            et_content.setHint(mContext.getString(R.string.content_discussion));
        }

        voteString.add("");
        voteString.add("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_publish_book_option, menu);
        MenuItem item = menu.findItem(R.id.publish);
        if(isVote){
            item.setTitle("下一步");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.publish){
            String title = et_title.getText().toString();
            String content = et_content.getText().toString();
            if(title.length()==0){
                ToastUtils.showToast(titleIsEmpty[index]);
            }else if(content.length()==0){
                ToastUtils.showToast(contentIsEmpty[index]);
            }else if(title.length()<4){
                ToastUtils.showToast(isFitTitle[index]);
            }else{
                if(content.length()<50&&isReview){
                    ToastUtils.showToast("内容字数不能少于50字");
                }else if(!isVote){
                    new AlertDialog.Builder(mContext)
                            .setTitle("发布")
                            .setMessage(getString(isReview?R.string.publish_review_tip:R.string.publish_discussion_tip))
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("确定发布", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    getData(isReview);
                                    if(params!=null){
                                        if(isReview){
                                            mPresenter.publishBookReview(params);
                                        }else{
                                            mPresenter.publishConversation(params);
                                        }
                                    }
                                }
                            }).create().show();
                }else{
                    startActivityForResult(new Intent(this,PublishVoteActivity.class)
                            .putExtra(Constant.TITLE,et_title.getText().toString())
                            .putExtra(Constant.CONTENT,et_content.getText().toString())
                            .putExtra(Constant.BOOK_REVIEW,mBookReview)
                            .putStringArrayListExtra(Constant.VOTE_STRING,voteString),0);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void getData(boolean isReview) {
        String token="";
        if(ReaderApplication.sLogin!=null&&!TextUtils.isEmpty(ReaderApplication.sLogin.token)){
            token=ReaderApplication.sLogin.token;
            params=new HashMap<>();
            if(isReview){
                params.put("rating",rating+"");
                params.put("from","book");
            }
            params.put("title",et_title.getText().toString());
            params.put("content",et_content.getText().toString());
            params.put("book",mBookReview.review.book._id);
            params.put("token",ReaderApplication.sLogin.token);
        }else{
            if (popupWindow == null) {
                popupWindow = new LoginPopupWindow(this);
                popupWindow.setLoginTypeListener(this);
            }
            popupWindow.showAtLocation(mCommonToolbar, Gravity.CENTER, 0, 0);
            params=null;
        }
    }

    @Override
    public void publishBookReviewResult(MyBookReview data) {
        if(data.ok){
            setResult(1);
            finish();
            LogUtils.e("书评发布成功"+data);
            ToastUtils.showToast("书评发布成功");
        }else{
            ReaderApplication.sLogin=null;
            SettingManager.getInstance().saveLoginInfo(ReaderApplication.sLogin);
            ToastUtils.showToast("发送失败,token可能过期,请重新登录");
        }
    }

    @Override
    public void publishConversationResult(MyDiscussion data) {
        if(data.ok){
            finish();
            LogUtils.e("话题发布成功"+data);
            ToastUtils.showToast("话题发布成功");
        }else{
            if(data.msg.equals("")){
                ReaderApplication.sLogin=null;
                SettingManager.getInstance().saveLoginInfo(ReaderApplication.sLogin);
                ToastUtils.showToast("发送失败,token可能过期,请重新登录");
            }else if(data.msg.equals("LV_NOT_ENOUGH")){
                ToastUtils.showToast("未达到指定等级");
            }
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
    public void onLogin(ImageView view, String type) {
        if (type.equals("QQ")) {
            if (!ReaderApplication.mTencent.isSessionValid()) {
                if (loginListener == null) loginListener = new BaseLoginActivity.BaseUIListener();
                LogUtils.e("loginListener不为null,开始登陆");
                ReaderApplication.mTencent.login(this, "all", loginListener);
            }
        }
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==Constant.FINISH_ACTIVITY){
            finish();
        }else if(resultCode==Constant.RETURN_DATA){
            voteString=data.getStringArrayListExtra(Constant.VOTE_STRING);
        }
    }
}
