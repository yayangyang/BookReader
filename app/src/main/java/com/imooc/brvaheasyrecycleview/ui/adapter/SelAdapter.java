package com.imooc.brvaheasyrecycleview.ui.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/10/19.
 */

public class SelAdapter extends BaseAdapter{
    int selPosition = 0;
    private List<String> mList;

    public SelAdapter(List<String> data) {
        mList=data;
    }

    public void setSelPosition(int position) {
        selPosition = position;
        notifyDataSetChanged();
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
            convertView = View.inflate(AppUtils.getAppContext(), R.layout.item_selection_view, null);
            holder=new ViewHolder();
            holder.tvSelTitleItem= (TextView) convertView.findViewById(R.id.tvSelTitleItem);

            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        holder.tvSelTitleItem.setText(getItem(position));
        return convertView;
    }

    static class ViewHolder{
        public TextView tvSelTitleItem;
    }
}
