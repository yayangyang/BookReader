package com.imooc.brvaheasyrecycleview.base;


/**
 * Created by Administrator on 2017/10/19.
 */

import com.imooc.brvaheasyrecycleview.Bean.support.SelectionEvent;
import com.imooc.brvaheasyrecycleview.R;
import com.imooc.brvaheasyrecycleview.utils.LogUtils;
import com.imooc.brvaheasyrecycleview.view.SelectionLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public abstract class BaseCommuniteActivity extends BaseActivity implements SelectionLayout.OnSelectListener {

    @BindView(R.id.slOverall)
    SelectionLayout slOverall;

    protected List<List<String>> list;

    protected List<List<String>> list1 = new ArrayList<List<String>>() {{
        add(new ArrayList<String>() {{
            add("全部");
            add("精品");
        }});
        add(new ArrayList<String>() {{
            add("默认排序");
            add("最新发布");
            add("最多评论");
        }});
    }};

    protected List<List<String>> list2 = new ArrayList<List<String>>() {{
        add(new ArrayList<String>() {{
            add("全部");
            add("精品");
        }});
        add(new ArrayList<String>() {{
            add("全部类型");
            add("玄幻奇幻");
            add("武侠仙侠");
            add("都市异能");
            add("历史军事");
            add("游戏竞技");
            add("科幻灵异");
            add("穿越架空");
            add("豪门总裁");
            add("现代言情");
            add("古代言情");
            add("幻想言情");
            add("耽美同人");
        }});
        add(new ArrayList<String>() {{
            add("默认排序");
            add("最新发布");
            add("最多评论");
            add("最有用的");
        }});
    }};

    @Constant.Distillate
    private String distillate = Constant.Distillate.ALL;
    @Constant.BookType
    private String type = Constant.BookType.ALL;
    @Constant.SortType
    private String sort = Constant.SortType.DEFAULT;

    @Override
    public void initDatas() {
        list = getTabList();
        if (slOverall != null) {
            slOverall.setData(list.toArray(new List[list.size()]));
            slOverall.setOnSelectListener(this);
        }
    }

    protected abstract List<List<String>> getTabList();

    @Override
    public void onSelect(int index, int position, String title) {
        switch (index) {
            case 0:
                switch (position) {
                    case 0:
                        distillate = Constant.Distillate.ALL;
                        break;
                    case 1:
                        distillate = Constant.Distillate.DISTILLATE;
                        break;
                    default:
                        break;
                }
                break;
            case 1:
                if (list.size() == 2) {
                    sort = Constant.sortTypeList.get(position);
                } else if (list.size() == 3) {
                    type = Constant.bookTypeList.get(position);
                }
                break;
            case 2:
                sort = Constant.sortTypeList.get(position);
                break;
            default:
                break;
        }

        LogUtils.e(distillate+"ww"+type+"ww"+sort);
        EventBus.getDefault().post(new SelectionEvent(distillate, type, sort));//可看出EventBus立即执行
        LogUtils.e(distillate+"ww"+type+"ww"+sort);
    }
}

