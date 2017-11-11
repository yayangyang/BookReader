package com.imooc.brvaheasyrecycleview.component;

import com.imooc.brvaheasyrecycleview.ui.activity.MainActivity;
import com.imooc.brvaheasyrecycleview.ui.activity.PublishOptionActivity;
import com.imooc.brvaheasyrecycleview.ui.activity.PublishVoteActivity;
import com.imooc.brvaheasyrecycleview.ui.activity.SplashActivity;
import com.imooc.brvaheasyrecycleview.ui.activity.UserInfoActivity;
import com.imooc.brvaheasyrecycleview.ui.activity.WelcomeActivity;

import dagger.Component;

/**
 * Created by Administrator on 2017/11/5.
 */

@Component(dependencies = AppComponent.class)
public interface MyComponent {

    SplashActivity inject(SplashActivity activity);

    WelcomeActivity inject(WelcomeActivity activity);

    PublishOptionActivity inject(PublishOptionActivity activity);

    PublishVoteActivity inject(PublishVoteActivity activity);

    UserInfoActivity inject(UserInfoActivity activity);

}
