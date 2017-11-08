package com.imooc.brvaheasyrecycleview.Bean;

import com.imooc.brvaheasyrecycleview.Bean.base.Base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/7.
 */

public class InterestBookList extends Base{
    /**
     * "_id": "59ba0dbb017336e411085a4e",
     * "title": "元尊",
     * "author": "天蚕土豆",
     * "site": "zhuishuvip",
     * "cover": "/agent/http%3A%2F%2Fimg.1391.com%2Fapi%2Fv1%2Fbookcenter%2Fcover%2F1%2F2107590%2F2107590_55d1f1bf10684e62a51d9f0ca3dd08fc.jpg%2F",
     * "shortIntro": "彼时的归途，已是一条命运倒悬的路。\r\n昔日的荣华，如白云苍狗，恐大梦一场。\r\n少年执笔，龙蛇飞动。\r\n是为一抹光芒劈开暮气沉沉之乱世，问鼎玉宇苍穹。\r\n \r\n复仇之路，与吾同行。\r\n一口玄黄真气定可吞天地日月星辰，雄视草木苍生。\r\n \r\n铁画夕照，雾霭银钩，笔走游龙冲九州。\r\n横姿天下，墨洒青山，鲸吞湖海纳百川。",
     * "lastChapter": "正文 第一百一十三章 一份造化",
     * "retentionRatio": 60.73,
     * "latelyFollower": 113468,
     * "majorCate": "玄幻",
     * "minorCate": "东方玄幻"
     */

    public ArrayList<InterestBook> books;

    public static class InterestBook implements Serializable{
        public String _id;
        public String title;
        public String author;
        public String site;
        public String cover;
        public String shortIntro;
        public String lastChapter;
        public String retentionRatio;
        public String latelyFollower;
        public String majorCate;
        public String minorCate;
    }

}
