package com.imooc.brvaheasyrecycleview.view.myView;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.imooc.brvaheasyrecycleview.utils.LogUtils;

/**
 * Created by Administrator on 2017/11/4.
 */

public class InterceptFrameLayout extends FrameLayout {

    public boolean isIntercept=false;

    public InterceptFrameLayout(Context context) {
        super(context);
    }

    public InterceptFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isIntercept;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        LogUtils.e("onDraw");
        super.onDraw(canvas);
    }
}
