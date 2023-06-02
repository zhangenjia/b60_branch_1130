package com.adayo.app.systemui.windows.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.utils.LogUtil;
import com.android.internal.widget.ViewPager;

/**
 * @author XuYue
 * @description:
 * @date :2021/11/15 15:27
 */
public class HvacViewPager extends ViewPager {

    private boolean slide = false;// false 禁止ViewPager左右滑动

    public HvacViewPager(Context context) {
        super(context);
        init();
    }

    public HvacViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public HvacViewPager(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init(){
        setPageTransformer(true, getPageTransformer());
        setOffscreenPageLimit(3);
    }

    public void setScrollable(boolean slide) {
        this.slide = slide;
    }

    private ViewPager.PageTransformer getPageTransformer() {
        ViewPager.PageTransformer pageTransformer;
//        if (type == 0 || type == 2) {
            // type为0 或者2 为上下翻页动画
        //                private float margin = getResources().getDimension(R.dimen.dp_10);
        pageTransformer = (page, position) -> {
            LogUtil.debugD(SystemUIContent.TAG, "page = " + page + " ; pos = " + position);
//                    page.setTranslationY(-position * margin);
            if (position >= -1.0f && position <= 0.0f) {
                // 控制左侧滑入或者划出View相对X坐标为0
                page.setTranslationX(0);
                // 旋转
                page.setPivotX(0);
                page.setPivotY(0);
                page.setRotation(-90f * position);
                page.setAlpha(1 + position);
            } else if (position > 0.0f) {
                // 控制右侧滑入或者划出控制View相对X坐标为0
                page.setTranslationX(0);
                page.setPivotX(0);
                page.setPivotY(0);
                page.setRotation(0f);
                page.setAlpha(1 - position);
            } else {
                // 控制左侧其它缓存View旋转状态固定
                page.setTranslationX(0);
                page.setPivotX(0);
                page.setPivotY(0);
                page.setRotation(90f);
                page.setAlpha(1);
            }
        };
//        } else {
//             //type 为1或者3为缩放翻页动画
//            pageTransformer = new ViewPager.PageTransformer() {
//                @Override
//                public void transformPage(View page, float position) {
//                    if (position >= -1.0f && position <= 0.0f) {
//                        // 控制左侧滑入或者划出View缩放比例
//                        page.setScaleX(1 + position * 0.1f);
//                        page.setScaleY(1 + position * 0.2f);
//                    } else if (position > 0.0f && position <= 1.0f) {
//                        // 控制右侧滑入或者划出View缩放比例
//                        page.setScaleX(1 - position * 0.1f);
//                        page.setScaleY(1 - position * 0.2f);
//                    } else {
//                        // 控制其它View缩放比例
//                        page.setScaleX(0.9f);
//                        page.setScaleY(0.8f);
//                    }
//                }
//            };
//        }
        return pageTransformer;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return slide;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return slide;
    }
}
