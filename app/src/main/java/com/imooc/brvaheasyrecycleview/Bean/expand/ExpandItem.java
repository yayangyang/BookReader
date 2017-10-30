package com.imooc.brvaheasyrecycleview.Bean.expand;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.imooc.brvaheasyrecycleview.Bean.RankingList;
import com.imooc.brvaheasyrecycleview.ui.adapter.TopRankAdapter;


/**
 * Created by Administrator on 2017/10/24.
 */

public class ExpandItem implements MultiItemEntity{
    private RankingList.MaleBean mBean;

    public ExpandItem(RankingList.MaleBean bean){
        mBean=bean;
    }

    @Override
    public int getItemType() {
        return TopRankAdapter.TYPE_EXPANDITEM;
    }

    public RankingList.MaleBean getBean() {
        return mBean;
    }

    public void setBean(RankingList.MaleBean bean) {
        mBean = bean;
    }
}
