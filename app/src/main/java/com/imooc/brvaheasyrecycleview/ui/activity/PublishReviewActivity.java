package com.imooc.brvaheasyrecycleview.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import com.imooc.brvaheasyrecycleview.Bean.BookReview;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.BaseActivity;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.component.AppComponent;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;
import com.imooc.brvaheasyrecycleview.utils.ToastUtils;


/**
 * Created by Administrator on 2017/11/8.
 */

public class PublishReviewActivity extends BaseActivity implements View.OnClickListener{

    private int[] idz={R.id.rb01,R.id.rb02,R.id.rb03,R.id.rb04,R.id.rb05};
    private RadioButton[] rbz=new RadioButton[idz.length];

    private int rating=-1;
    private BookReview mBookReview;

    public static void startActivity(Context context,BookReview bookReview){
        context.startActivity(new Intent(context,PublishReviewActivity.class)
            .putExtra(Constant.BOOK_REVIEW,bookReview));
    }

    @Override
    public int getLayoutId() {
        mBookReview = (BookReview) getIntent().getSerializableExtra(Constant.BOOK_REVIEW);
        return R.layout.activity_publish_book_reviews;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    public void initToolBar() {
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
        mCommonToolbar.setTitle("打分");
    }

    @Override
    public void initDatas() {
        for(int i=0;i<rbz.length;i++){
            rbz[i]=findViewById(idz[i]);
            rbz[i].setOnClickListener(this);
        }

        if(!TextUtils.isEmpty(mBookReview.review.content)){
            int index=mBookReview.review.rating-1;
            rbz[index].setChecked(true);
            LogUtils.e(rating+"");
            rating=index+1;
        }
    }

    @Override
    public void configViews() {
        mCommonToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rating!=-1){
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_publish_book_reviews, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.next){
            if(rating!=-1){
                startActivityForResult(new Intent(this,PublishOptionActivity.class)
                        .putExtra(Constant.IS_REVIEW,true)
                        .putExtra(Constant.RATING,rating)
                        .putExtra(Constant.BOOK_REVIEW,mBookReview),0);
            }else{
                ToastUtils.showToast("给书籍打个分数吧");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.e("onActivityResult"+resultCode);
        if(resultCode==Constant.FINISH_ACTIVITY){
            finish();
        }else if(resultCode==Constant.RETURN_DATA){
            mBookReview.review.title=data.getStringExtra(Constant.TITLE);
            mBookReview.review.content=data.getStringExtra(Constant.CONTENT);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        if(rbz!=null){
            RadioButton radioButton = (RadioButton) v;
            for(int i=0;i<rbz.length;i++){
                if(rbz[i]==radioButton){
                    radioButton.setChecked(true);
                    rating=i+1;
                }else{
                    rbz[i].setChecked(false);
                }
            }
        }
    }
}
