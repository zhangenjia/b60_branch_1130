package com.adayo.app.setting.view.popwindow.harman.soundfactory.product;

import android.graphics.Canvas;

import com.adayo.app.setting.view.popwindow.harman.soundfactory.bean.SoundBean;
import com.adayo.app.base.LogUtil;


abstract public class ASoundProduct {
    protected SoundBean mSoundBean0;
    protected SoundBean mSoundBean1;
    protected SoundBean mSoundBean2;
    protected SoundBean mSoundBean3;
    protected SoundBean mSoundBean4;

    public ASoundProduct()
    {

    }

    public void init(SoundBean soundBean0, SoundBean soundBean1, SoundBean soundBean2, SoundBean soundBean3, SoundBean soundBean4)
    {
       LogUtil.debugD("harmon","");
        mSoundBean0 = soundBean0;
        mSoundBean1 = soundBean1;
        mSoundBean2 = soundBean2;
        mSoundBean3 = soundBean3;
        mSoundBean4 = soundBean4;
    }

    public abstract void calculateAllSoundLocation();

    abstract public void draw(Canvas canvas);

    abstract public boolean isValidLocation(double screenX, double screenY);

    abstract public SoundBean selectLocation(double screenX, double screenY);

    abstract public SoundBean getScreenLocationBySound(int x, int y);
}
