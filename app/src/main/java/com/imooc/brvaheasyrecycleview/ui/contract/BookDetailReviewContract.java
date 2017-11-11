package com.imooc.brvaheasyrecycleview.ui.contract;

import com.imooc.brvaheasyrecycleview.Bean.BookReview;
import com.imooc.brvaheasyrecycleview.Bean.HotReview;
import com.imooc.brvaheasyrecycleview.base.BaseContract;

import java.util.List;

public interface BookDetailReviewContract {

    interface View extends BaseContract.BaseView {
        void showBookDetailReviewList(List<HotReview.Reviews> list, boolean isRefresh);

        void showHistoryBookReview(BookReview bookReview);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getBookDetailReviewList(String bookId, String sort, int start, int limit);

        void getHistoryBookReview(String book, String token);
    }
}
