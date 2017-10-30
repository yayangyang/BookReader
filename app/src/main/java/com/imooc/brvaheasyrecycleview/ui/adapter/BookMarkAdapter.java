package com.imooc.brvaheasyrecycleview.ui.adapter;

import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.imooc.brvaheasyrecycleview.Bean.support.BookMark;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.utils.AppUtils;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;

import java.util.ArrayList;

public class BookMarkAdapter extends BaseAdapter {

    private ArrayList<BookMark> mArrayList;

    public ArrayList<BookMark> getArrayList() {
        return mArrayList;
    }

    public BookMarkAdapter(ArrayList<BookMark> bookMarks) {
        mArrayList=bookMarks;
    }

    @Override
    public int getCount() {
        return mArrayList.size();
    }

    @Override
    public BookMark getItem(int position) {
        return mArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TocListAdapter.ViewHolder holder=null;
        if(convertView==null){
            convertView = View.inflate(AppUtils.getAppContext(), R.layout.item_read_mark, null);
            holder=new TocListAdapter.ViewHolder();
            holder.tvTocItem= convertView.findViewById(R.id.tvTocItem);

            convertView.setTag(holder);
        }else{
            holder= (TocListAdapter.ViewHolder) convertView.getTag();
        }

        SpannableString spanText = new SpannableString((position + 1) + ". " + getItem(position).title + ": ");
        spanText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(AppUtils.getAppContext(), R.color.light_coffee)),
                0, spanText.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        holder.tvTocItem.setText(spanText);

        if (getItem(position).desc != null) {
            holder.tvTocItem.append(getItem(position).desc
                    .replaceAll("　", "")
                    .replaceAll(" ", "")
                    .replaceAll("\n", ""));
        }

        LogUtils.e(getItem(position).desc);

        return  convertView;
    }
}
