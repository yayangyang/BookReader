package com.imooc.brvaheasyrecycleview.ui.contract;

import com.imooc.brvaheasyrecycleview.Bean.BookReviewList;
import com.imooc.brvaheasyrecycleview.base.BaseContract;

import java.util.List;

public interface BookReviewContract {

    interface View extends BaseContract.BaseView {
        void showBookReviewList(List<BookReviewList.ReviewsBean> list, int start);

        void showMyError(boolean isRefresh);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getBookReviewList(String sort, String type, String distillate, int start, int limit);
    }

}
