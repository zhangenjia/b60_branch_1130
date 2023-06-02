package com.adayo.app.launcher.offroadinfo.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import java.lang.reflect.Field;

public class FrameView extends ImageView {

    private AnimationDrawable anim;
    private int curFrame;
    private OnCurrentFrameListener mOnCurrentFrameListener;
    private int count = -1;

    public FrameView(Context context) {
        super(context);
    }

    public FrameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }

    public FrameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAnim(AnimationDrawable anim){
        this.anim = anim;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        try{
            //反射调用AnimationDrawable里的mCurFrame值
            Field field = AnimationDrawable.class
                    .getDeclaredField("mCurFrame");
            field.setAccessible(true);
            // 获取anim动画的当前帧
            curFrame = field.getInt(anim);
            if (mOnCurrentFrameListener!=null){
                mOnCurrentFrameListener.showCurrentFrame(curFrame);
            }


            Log.d("FrameViewonDraw", "onDraw: "+ curFrame);

        }catch (Exception e){e.printStackTrace();}
        super.onDraw(canvas);
    }


    public int getCurrentFrame(){

        return curFrame;

    }



    public interface OnCurrentFrameListener{

        void showCurrentFrame(int mCurrentFrame);

    }

    public void setOnCurrentFrameListener(OnCurrentFrameListener onCurrentFrameListener){

        mOnCurrentFrameListener = onCurrentFrameListener;

    }
}