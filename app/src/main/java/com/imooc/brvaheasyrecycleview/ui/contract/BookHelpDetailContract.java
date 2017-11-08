package com.imooc.brvaheasyrecycleview.ui.contract;

import com.imooc.brvaheasyrecycleview.Bean.BookHelp;
import com.imooc.brvaheasyrecycleview.Bean.CommentList;
import com.imooc.brvaheasyrecycleview.Bean.MyBean.Comment;
import com.imooc.brvaheasyrecycleview.base.BaseContract;
import com.imooc.brvaheasyrecycleview.base.BaseLoginContract;

public interface BookHelpDetailContract {

    interface View extends BaseLoginContract.View {

        void showBookHelpDetail(BookHelp data);

        void showBestComments(CommentList list);

        void showBookHelpComments(CommentList list);

        void publishReviewResult(Comment comment, String content);
    }

    interface Presenter extends BaseLoginContract.Presenter<View> {

        void getBookHelpDetail(String id);

        void getBestComments(String helpId);

        void getBookHelpComments(String helpId, int start, int limit);

        void publishReview(String section, final String content, String token);
    }

}
