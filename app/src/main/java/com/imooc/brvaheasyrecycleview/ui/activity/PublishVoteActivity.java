package com.imooc.brvaheasyrecycleview.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.imooc.brvaheasyrecycleview.Bean.BookReview;
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
import com.imooc.brvaheasyrecycleview.ui.adapter.PublishVoteAdapter;
import com.imooc.brvaheasyrecycleview.ui.contract.PublishVoteContract;
import com.imooc.brvaheasyrecycleview.ui.presenter.PublishVoteActivityPresenter;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;
import com.imooc.brvaheasyrecycleview.utils.ToastUtils;
import com.imooc.brvaheasyrecycleview.view.LoginPopupWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/11/11.
 */

public class PublishVoteActivity extends BaseLoginActivity
        implements PublishVoteContract.View,BaseQuickAdapter.OnItemClickListener{

    private String title,content;

    private Map<String, String> params;
    private BookReview mBookReview;
    private ArrayList<String> mArrayList;
    private PublishVoteAdapter mAdapter;

    @Inject
    public PublishVoteActivityPresenter mPresenter;

    @BindView(R.id.rootView)
    LinearLayout rootView;
    @BindView(R.id.add_vote_item)
    TextView add_vote_item;
    @BindView(R.id.publish_vote)
    TextView publish_vote;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_publish_vote;
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
        mCommonToolbar.setTitle("编辑投票项");
    }

    @Override
    public void initDatas() {
        mPresenter.attachView(this);

        title=getIntent().getStringExtra(Constant.TITLE);
        content=getIntent().getStringExtra(Constant.CONTENT);
        mBookReview= (BookReview) getIntent().getSerializableExtra(Constant.BOOK_REVIEW);
        mArrayList=getIntent().getStringArrayListExtra(Constant.VOTE_STRING);

        mCommonToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> arrayList = new ArrayList();
                LogUtils.e("mArrayList.size:"+mArrayList.size());
                for(int i=0;i<mArrayList.size();i++){
                    if(i>1&&mArrayList.get(i).length()>0||i<=1){
                        arrayList.add(mArrayList.get(i));
                        LogUtils.e("arrayList:"+mArrayList.get(i));
                    }
                }
                setResult(2,new Intent().putStringArrayListExtra(Constant.VOTE_STRING,arrayList));
                finish();
            }
        });
    }

    @Override
    public void configViews() {
        mAdapter=new PublishVoteAdapter(R.layout.item_publish_vote,mArrayList);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void showError() {

    }

    @Override
    public void complete() {

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
    public void publishVoteResult(MyDiscussion data) {
        if(data.ok){
            setResult(1);
            finish();
            LogUtils.e("发布成功"+data);
            ToastUtils.showToast("发布成功");
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
    public void onLogin(ImageView view, String type) {
        if (type.equals("QQ")) {
            if (!ReaderApplication.mTencent.isSessionValid()) {
                if (loginListener == null) loginListener = new BaseLoginActivity.BaseUIListener();
                LogUtils.e("loginListener不为null,开始登陆");
                ReaderApplication.mTencent.login(this, "all", loginListener);
            }
        }
    }

    @OnClick(R.id.add_vote_item)
    public void add_vote_item(){
        if(mAdapter.getData().size()<7){
            mAdapter.addData("");
        }else{
            ToastUtils.showToast("不能再添加投票项啦");
        }
    }

    @OnClick(R.id.publish_vote)
    public void publish_vote(){
        String firstStr = mAdapter.getData().get(0);
        String secondStr =  mAdapter.getData().get(1);
        LogUtils.e("firstStr:"+firstStr);
        LogUtils.e("secondStr:"+secondStr);
        if(firstStr.length()<1||secondStr.length()<1){
            ToastUtils.showToast("至少需要2个投票项");
        }else{
            new AlertDialog.Builder(mContext)
                    .setTitle("发布")
                    .setMessage(R.string.publish_discussion_tip)
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
                            getData();
                            if(params!=null){
                                mPresenter.publishVote(params);
                            }
                        }
                    }).create().show();
        }
    }

    private void getData() {
        String token="";
        if(ReaderApplication.sLogin!=null&&!TextUtils.isEmpty(ReaderApplication.sLogin.token)){
            token=ReaderApplication.sLogin.token;
            String str=format();
            params=new HashMap<>();
            params.put("votes",str);
            params.put("title",title);
            params.put("content",content);
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

    private String format() {
        String str="";
        for(int i=0;i<mArrayList.size();i++){
            str+=","+mArrayList.get(i);
        }
        str.replaceFirst(",","");
        return str;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, final View view, final int position) {
        final View inflate = View.inflate(this, R.layout.dialog_publish_vote, null);
        new AlertDialog.Builder(mContext)
                .setTitle("投票项")
                .setView(inflate)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        RelativeLayout tv = (RelativeLayout) view;
                        EditText et_title = inflate.findViewById(R.id.et_title);
                        mAdapter.getData().set(position,et_title.getText().toString());
                        mAdapter.notifyItemChanged(position);
                    }
                }).create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPresenter!=null){
            mPresenter.detachView();
        }
    }
}
