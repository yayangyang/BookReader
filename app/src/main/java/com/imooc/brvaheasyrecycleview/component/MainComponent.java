package com.imooc.brvaheasyrecycleview.component;


import com.imooc.brvaheasyrecycleview.ui.activity.MainActivity;
import com.imooc.brvaheasyrecycleview.ui.activity.SettingActivity;
import com.imooc.brvaheasyrecycleview.ui.fragment.RecommendFragment;

import dagger.Component;

@Component(dependencies = AppComponent.class)
public interface MainComponent {
    MainActivity inject(MainActivity activity);

    RecommendFragment inject(RecommendFragment fragment);

    SettingActivity inject(SettingActivity activity);
//    WifiBookActivity inject(WifiBookActivity activity);
}