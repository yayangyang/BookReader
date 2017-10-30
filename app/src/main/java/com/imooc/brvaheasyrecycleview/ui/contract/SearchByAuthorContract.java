package com.imooc.brvaheasyrecycleview.ui.contract;

import com.imooc.brvaheasyrecycleview.Bean.BooksByTag;
import com.imooc.brvaheasyrecycleview.base.BaseContract;

import java.util.List;

public interface SearchByAuthorContract {

    interface View extends BaseContract.BaseView {
        void showSearchResultList(List<BooksByTag.TagBook> list);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getSearchResultList(String author);
    }

}
