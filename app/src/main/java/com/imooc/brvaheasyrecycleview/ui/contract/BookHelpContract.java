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

import com.imooc.brvaheasyrecycleview.Bean.BookHelpList;
import com.imooc.brvaheasyrecycleview.base.BaseContract;

import java.util.List;

public interface BookHelpContract {

    interface View extends BaseContract.BaseView {
        void showBookHelpList(List<BookHelpList.HelpsBean> list, boolean isRefresh);

        void showMyError(boolean isRefresh);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getBookHelpList(String sort, String distillate, int start, int limit);
    }

}
