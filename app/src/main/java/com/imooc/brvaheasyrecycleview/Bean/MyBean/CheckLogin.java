package com.imooc.brvaheasyrecycleview.Bean.MyBean;

import com.imooc.brvaheasyrecycleview.Bean.base.Base;

/**
 * Created by Administrator on 2017/11/5.
 */

public class CheckLogin extends Base {
    /**
     *  用user的第一条请求判断是否登录(这应该不是追书神器验证token的接口)
     *  "lastReadImportantTime": "2017-11-04T07:38:12.947Z",
     *  "lastReadUnimportantTime": "2016-08-19T16:59:20.000Z",
     *  "unimportant": 0,
     *  "important": 0,
     *  "ok": true
     */

    public String lastReadImportantTime;
    public String lastReadUnimportantTime;
    public int unimportant;
    public int important;

}
