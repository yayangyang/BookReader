package com.imooc.brvaheasyrecycleview.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.utils.AppUtils;
import com.imooc.brvaheasyrecycleview.utils.ScreenUtils;

import java.util.List;

public class MinorAdapter extends BaseAdapter {

    private int current = 0;

    private List<String> mList;

    public MinorAdapter(List<String> data) {
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
            convertView = View.inflate(AppUtils.getAppContext(), R.layout.item_minor_list, null);
            holder=new ViewHolder(convertView);

            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        holder.tvMinorItem.setText(getItem(position));
        if(current==position){
            holder.ivMinorChecked.setVisibility(View.VISIBLE);
        }else{
            holder.ivMinorChecked.setVisibility(View.GONE);
        }
        if (position != 0) { // 子项右移
            TextView textView = holder.tvMinorItem;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textView.getLayoutParams();
            params.leftMargin = ScreenUtils.dpToPxInt(25);
            textView.setLayoutParams(params);
        }
        return convertView;
    }

    static class ViewHolder{
        public TextView tvMinorItem;
        public ImageView ivMinorChecked;
        public ViewHolder(View view){
            tvMinorItem=view.findViewById(R.id.tvMinorItem);
            ivMinorChecked=view.findViewById(R.id.ivMinorChecked);
        }
    }

    public void setChecked(int position) {
        current = position;
        notifyDataSetChanged();
    }
}
