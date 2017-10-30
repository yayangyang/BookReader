package com.imooc.brvaheasyrecycleview.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.imooc.brvaheasyrecycleview.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/10/15.
 */

public class SplashActivity extends AppCompatActivity{
    @BindView(R.id.test)
    public TextView tv01;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        ButterKnife.bind(this);
        if(tv01==null){
            Log.e("tv01","为空");
        }else{
            Log.e("tv01","不为空");
        }
    }
}
