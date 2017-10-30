package com.imooc.brvaheasyrecycleview.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.utils.AppUtils;

import java.util.List;

public class SearchHistoryAdapter extends BaseAdapter {

    private List<String> mList;

    public SearchHistoryAdapter(List<String> data) {
        mList=data;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public String getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            convertView = View.inflate(AppUtils.getAppContext(), R.layout.item_search_history, null);
            holder=new ViewHolder();
            holder.tvTitle= convertView.findViewById(R.id.tvTitle);

            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        holder.tvTitle.setText(getItem(position));
        return convertView;
    }

    static class ViewHolder{
        public TextView tvTitle;
    }
}
