package com.imooc.brvaheasyrecycleview.Bean.expand;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.imooc.brvaheasyrecycleview.Bean.RankingList;
import com.imooc.brvaheasyrecycleview.ui.adapter.TopRankAdapter;


/**
 * Created by Administrator on 2017/10/24.
 */

public class Lv0 extends AbstractExpandableItem<ExpandItem> implements MultiItemEntity {
    private String mTitle="";

    private RankingList.MaleBean mBean;
    @Override
    public int getItemType() {
        return TopRankAdapter.TYPE_LEVEL_0;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    public Lv0(String title){
        mTitle=title;
    }

    public Lv0(){}

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public RankingList.MaleBean getBean() {
        return mBean;
    }

    public void setBean(RankingList.MaleBean bean) {
        mBean = bean;
        bean.title=mTitle;
    }
}
