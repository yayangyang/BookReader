package com.imooc.brvaheasyrecycleview.ui.adapter;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.imooc.brvaheasyrecycleview.Bean.BookMixAToc;
import com.imooc.brvaheasyrecycleview.Bean.BookRead;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.utils.AppUtils;
import com.imooc.brvaheasyrecycleview.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class TocListAdapter extends BaseAdapter {

    private int currentChapter;
    private String bookId;
    private boolean isEpub = false;
    private ArrayList<BookMixAToc.mixToc.Chapters> mList;

    public TocListAdapter(ArrayList<BookMixAToc.mixToc.Chapters> chapterList) {
        mList=chapterList;
    }

    public void setCurrentChapter(int chapter) {
        currentChapter = chapter;
        notifyDataSetChanged();
    }

    public void setEpub(boolean isEpub) {
        this.isEpub = isEpub;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public String getItem(int position) {
        return mList.get(position).title;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            convertView = View.inflate(AppUtils.getAppContext(), R.layout.item_book_read_toc_list, null);
            holder=new ViewHolder();
            holder.tvTocItem= convertView.findViewById(R.id.tvTocItem);

            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        holder.tvTocItem.setText(getItem(position));

        Drawable drawable;
        if (currentChapter == position + 1) {//当前章节
            holder.tvTocItem.setTextColor(ContextCompat.getColor(AppUtils.getAppContext(), R.color.light_red));
            drawable = ContextCompat.getDrawable(AppUtils.getAppContext(), R.drawable.ic_toc_item_activated);
        } else if (isEpub || FileUtils.getChapterFile(bookId, position + 1).length() > 10)
        {//存在缓存
            holder.tvTocItem.setTextColor(ContextCompat.getColor(AppUtils.getAppContext(), R.color.light_black));
            drawable = ContextCompat.getDrawable(AppUtils.getAppContext(), R.drawable.ic_toc_item_download);
        } else {
            holder.tvTocItem.setTextColor(ContextCompat.getColor(AppUtils.getAppContext(), R.color.light_black));
            drawable = ContextCompat.getDrawable(AppUtils.getAppContext(), R.drawable.ic_toc_item_normal);
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        holder.tvTocItem.setCompoundDrawables(drawable, null, null, null);

        return convertView;
    }

    static class ViewHolder{
        public TextView tvTocItem;
    }

}
