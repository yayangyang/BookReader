package com.imooc.brvaheasyrecycleview.ui.contract;

import com.imooc.brvaheasyrecycleview.Bean.CategoryList;
import com.imooc.brvaheasyrecycleview.base.BaseContract;

public interface TopCategoryListContract {

    interface View extends BaseContract.BaseView {
        void showCategoryList(CategoryList data);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getCategoryList();
    }

}
