package com.imooc.brvaheasyrecycleview.ui.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.R;

import java.util.List;

/**
 * Created by Administrator on 2017/11/11.
 */

public class PublishVoteAdapter extends BaseQuickAdapter<String,BaseViewHolder> {

    public PublishVoteAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_vote,helper.getLayoutPosition()+1+"."+item);
        if(helper.getLayoutPosition()>=2){
            helper.setVisible(R.id.iv_delete,true);
            helper.getView(R.id.iv_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove(helper.getLayoutPosition());
                }
            });
        }
    }
}
