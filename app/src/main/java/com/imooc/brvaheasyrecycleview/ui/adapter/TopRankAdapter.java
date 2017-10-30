package com.imooc.brvaheasyrecycleview.ui.adapter;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.imooc.brvaheasyrecycleview.Bean.RankingList;
import com.imooc.brvaheasyrecycleview.Bean.expand.ExpandItem;
import com.imooc.brvaheasyrecycleview.Bean.expand.Lv0;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.transform.GlideCircleTransform;
import com.imooc.brvaheasyrecycleview.ui.activity.SubOtherHomeRankActivity;
import com.imooc.brvaheasyrecycleview.ui.activity.SubRankActivity;

import java.util.List;

/**
 * Created by Administrator on 2017/10/24.
 */

public class TopRankAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity,BaseViewHolder> {

    public static final int TYPE_LEVEL_0 = 0;
    public static final int TYPE_EXPANDITEM = 1;

    public TopRankAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(TYPE_LEVEL_0, R.layout.item_top_rank_group);
        addItemType(TYPE_EXPANDITEM, R.layout.item_top_rank_child);
    }

    @Override
    protected void convert(final BaseViewHolder helper, MultiItemEntity item) {
        if(item.getItemType()==TYPE_LEVEL_0){
            final Lv0 lv0 = (Lv0) item;
            if(lv0.getTitle().equals("")){
                helper.getView(R.id.ivRankArrow).setVisibility(View.GONE);
                ImageView view =helper.getView(R.id.ivRankCover);
                Glide.with(mContext).load(Constant.IMG_BASE_URL + ((Lv0) item).getBean().cover)
                        .placeholder(R.drawable.avatar_default) .transform(new GlideCircleTransform
                        (mContext)).into(view);
                helper.setText(R.id.tvRankGroupName,lv0.getTitle());
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RankingList.MaleBean bean = lv0.getBean();
                        SubRankActivity.startActivity(mContext,bean._id,bean.monthRank,bean.totalRank,bean.title);
                    }
                });
            }else{
                helper.getView(R.id.ivRankArrow).setVisibility(View.VISIBLE);
                helper.setImageResource(R.id.ivRankCover,R.drawable.ic_rank_collapse);
                helper.setText(R.id.tvRankGroupName,lv0.getTitle());
                helper.getView(R.id.ivRankArrow).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageView view = (ImageView) v;
                        if(lv0.isExpanded()){
                            view.setImageResource(R.drawable.rank_arrow_up);
                            collapse(helper.getLayoutPosition());
                        }else{
                            view.setImageResource(R.drawable.rank_arrow_down);
                            expand(helper.getLayoutPosition());
                        }
                    }
                });
            }
        }else if(item.getItemType()==TYPE_EXPANDITEM){
            final ExpandItem expandItem = (ExpandItem) item;
            helper.setText(R.id.tvRankChildName,((ExpandItem) item).getBean().title);
            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RankingList.MaleBean bean = expandItem.getBean();
                    SubOtherHomeRankActivity.startActivity(mContext, bean._id,bean.title);
                }
            });
        }
    }
}
