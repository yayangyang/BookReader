package com.imooc.brvaheasyrecycleview.ui.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.brvaheasyrecycleview.Bean.Recommend;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.base.Constant;
import com.imooc.brvaheasyrecycleview.manager.SettingManager;
import com.imooc.brvaheasyrecycleview.utils.FileUtils;
import com.imooc.brvaheasyrecycleview.utils.FormatUtils;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by Administrator on 2017/10/18.
 */

public class RecommendAdapter extends BaseQuickAdapter<Recommend.RecommendBooks,BaseViewHolder> {

    public RecommendAdapter(@LayoutRes int layoutResId, @Nullable List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final Recommend.RecommendBooks item) {
        String latelyUpdate = "";
        if (!TextUtils.isEmpty(FormatUtils.getDescriptionTimeFromDateString(item.updated))) {
            latelyUpdate = FormatUtils.getDescriptionTimeFromDateString(item.updated) + ":";
        }

        helper.setText(R.id.tvRecommendTitle, item.title)
                .setText(R.id.tvLatelyUpdate, latelyUpdate)
                .setText(R.id.tvRecommendShort, item.lastChapter)
                .setVisible(R.id.ivTopLabel, item.isTop)
                .setVisible(R.id.ckBoxSelect, item.showCheckBox)
                .setVisible(R.id.ivUnReadDot, FormatUtils.formatZhuiShuDateString(item.updated)
                        .compareTo(item.recentReadingTime) > 0);

        if (item.path != null && item.path.endsWith(Constant.SUFFIX_PDF)) {
            helper.setImageResource(R.id.ivRecommendCover, R.drawable.ic_shelf_pdf);
        } else if (item.path != null && item.path.endsWith(Constant.SUFFIX_EPUB)) {
            helper.setImageResource(R.id.ivRecommendCover, R.drawable.ic_shelf_epub);
        } else if (item.path != null && item.path.endsWith(Constant.SUFFIX_CHM)) {
            helper.setImageResource(R.id.ivRecommendCover, R.drawable.ic_shelf_chm);
        } else if (item.isFromSD) {
            helper.setImageResource(R.id.ivRecommendCover, R.drawable.ic_shelf_txt);
            long fileLen = FileUtils.getChapterFile(item._id, 1).length();
            if (fileLen > 10) {
                double progress = ((double) SettingManager.getInstance().getReadProgress(item._id)[2]) / fileLen;
                NumberFormat fmt = NumberFormat.getPercentInstance();
                fmt.setMaximumFractionDigits(2);
                helper.setText(R.id.tvRecommendShort, "当前阅读进度：" + fmt.format(progress));
            }
        } else if (!SettingManager.getInstance().isNoneCover()) {
            ImageView view = helper.getView(R.id.ivRecommendCover);
            Glide.with(mContext)
                    .load(Constant.IMG_BASE_URL + item.cover)
                    .placeholder(R.drawable.cover_default)
                    .into(view);
        } else {
            helper.setImageResource(R.id.ivRecommendCover, R.drawable.cover_default);
        }

        CheckBox ckBoxSelect = helper.getView(R.id.ckBoxSelect);
        ckBoxSelect.setChecked(item.isSeleted);
        ckBoxSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                item.isSeleted = isChecked;
            }
        });
    }
}
