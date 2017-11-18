package com.imooc.brvaheasyrecycleview.ui.contract;

import com.imooc.brvaheasyrecycleview.Bean.CommentList;
import com.imooc.brvaheasyrecycleview.Bean.Disscussion;
import com.imooc.brvaheasyrecycleview.Bean.MyBean.Comment;
import com.imooc.brvaheasyrecycleview.base.BaseContract;
import com.imooc.brvaheasyrecycleview.base.BaseLoginContract;

public interface BookDiscussionDetailContract {

    interface View extends BaseLoginContract.View {

        void showBookDisscussionDetail(Disscussion disscussion);

        void showBestComments(CommentList list);

        void showBookDisscussionComments(CommentList list,int start);

        void publishReviewResult(Comment comment,String content);
    }

    interface Presenter extends BaseLoginContract.Presenter<View> {

        void getBookDisscussionDetail(String id);

        void getBestComments(String disscussionId);

        void getBookDisscussionComments(String disscussionId, int start, int limit);

        void publishReview(String section, String content, String token);
    }

}
