package com.imooc.brvaheasyrecycleview.ui.contract;

import com.imooc.brvaheasyrecycleview.Bean.MyBean.MyBookReview;
import com.imooc.brvaheasyrecycleview.Bean.MyBean.MyDiscussion;
import com.imooc.brvaheasyrecycleview.base.BaseContract;
import com.imooc.brvaheasyrecycleview.base.BaseLoginContract;

import java.util.Map;

/**
 * Created by Administrator on 2017/11/10.
 */

public interface PublishOptionContract {
    interface View extends BaseLoginContract.View {
        void publishBookReviewResult(MyBookReview bookReview);

        void publishConversationResult(MyDiscussion myConversation);
    }

    interface Presenter<T> extends BaseLoginContract.Presenter<T> {
        void publishBookReview(Map<String, String> params);

        void publishConversation(Map<String, String> params);
    }
}
