package com.imooc.brvaheasyrecycleview.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.imooc.brvaheasyrecycleview.Bean.support.ReadTheme;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.manager.ThemeManager;
import com.imooc.brvaheasyrecycleview.utils.AppUtils;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;

import java.util.List;

public class ReadThemeAdapter extends BaseAdapter {

    private int selected = 0;
    List<ReadTheme> mReadThemes;

    public ReadThemeAdapter(List<ReadTheme> readThemes, int selected) {
        mReadThemes=readThemes;
        this.selected=selected;
    }

    @Override
    public int getCount() {
        return mReadThemes.size();
    }

    @Override
    public ReadTheme getItem(int position) {
        return mReadThemes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            convertView = View.inflate(AppUtils.getAppContext(), R.layout.item_read_theme, null);
            holder=new ViewHolder();
            holder.ivThemeBg= convertView.findViewById(R.id.ivThemeBg);
            holder.ivSelected= convertView.findViewById(R.id.ivSelected);

            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        if (getItem(position) != null) {
            ThemeManager.setReaderTheme(getItem(position).theme, holder.ivThemeBg);
            if (selected == position) {
                holder.ivSelected.setVisibility(View.VISIBLE);
            } else {
                holder.ivSelected.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    public void select(int position) {
        selected = position;
        LogUtils.e("curtheme=" + selected);
        notifyDataSetChanged();
    }

    static class ViewHolder{
        public ImageView ivThemeBg;
        public ImageView ivSelected;
    }

}
