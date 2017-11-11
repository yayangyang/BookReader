package com.imooc.brvaheasyrecycleview.ui.contract;

import com.imooc.brvaheasyrecycleview.Bean.Disscussion;
import com.imooc.brvaheasyrecycleview.Bean.MyBean.MyDiscussion;
import com.imooc.brvaheasyrecycleview.Bean.user.Login;
import com.imooc.brvaheasyrecycleview.base.BaseContract;
import com.imooc.brvaheasyrecycleview.base.BaseLoginContract;

import java.util.Map;

/**
 * Created by Administrator on 2017/11/11.
 */

public interface PublishVoteContract {

    interface View extends BaseLoginContract.View{
        void loginSuccess(Login login);

        void publishVoteResult(MyDiscussion disscussion);
    }

    interface Presenter extends BaseLoginContract.Presenter<View>{
        void login(String uid, String token, String platform);

        void publishVote(Map<String, String> params);
    }

}
