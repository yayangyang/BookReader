/**
 * Copyright 2016 JustWayward Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.imooc.brvaheasyrecycleview.ui.contract;

import com.imooc.brvaheasyrecycleview.Bean.BookReview;
import com.imooc.brvaheasyrecycleview.Bean.CommentList;
import com.imooc.brvaheasyrecycleview.Bean.MyBean.Comment;
import com.imooc.brvaheasyrecycleview.Bean.MyBean.ReviewHelpful;
import com.imooc.brvaheasyrecycleview.base.BaseContract;
import com.imooc.brvaheasyrecycleview.base.BaseLoginContract;

public interface BookReviewDetailContract {

    interface View extends BaseLoginContract.View {

        void showBookReviewDetail(BookReview data);

        void showBestComments(CommentList list);

        void showBookReviewComments(CommentList list);

        void publishReviewResult(Comment comment, String content);

        void postReviewHelpfulResult(ReviewHelpful reviewHelpful,String is_helpful);
    }

    interface Presenter extends BaseLoginContract.Presenter<View> {

        void getBookReviewDetail(String id);

        void getBestComments(String bookReviewId);

        void getBookReviewComments(String bookReviewId, int start, int limit);

        void publishReview(String sectionId, final String content, String token);

        void postReviewHelpful(String sectionId, final String token, String is_helpful);
    }

}
