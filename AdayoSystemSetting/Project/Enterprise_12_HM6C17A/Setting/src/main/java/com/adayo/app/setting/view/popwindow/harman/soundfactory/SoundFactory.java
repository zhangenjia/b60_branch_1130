package com.adayo.app.setting.view.popwindow.harman.soundfactory;


import com.adayo.app.setting.view.popwindow.harman.soundfactory.bean.SoundBean;
import com.adayo.app.setting.view.popwindow.harman.soundfactory.product.ASoundProduct;
import com.adayo.app.setting.view.popwindow.harman.soundfactory.product.SoundProduct_B60;
import com.adayo.app.setting.view.popwindow.harman.soundfactory.stategy.ISoundAnalyse;
import com.adayo.app.setting.view.popwindow.harman.soundfactory.stategy.SoundAnalyseB60V;

public class SoundFactory {
    private static ISoundAnalyse mAnalyse;
    private static ASoundProduct mProduct;

    public static ASoundProduct getSoundProduct(String s)
    {
        switch (s)
        {
            case "B60V":
                if (mProduct == null) {
                    mProduct = new SoundProduct_B60();
                }
                if (mAnalyse == null) {
                    mAnalyse = new SoundAnalyseB60V();
                }
                break;

            default:
                break;
        }

        return mProduct;
    }

    public void initAllSoundInfo(SoundBean soundBean0, SoundBean soundBean1, SoundBean soundBean2, SoundBean soundBean3, SoundBean soundBean4)
    {
        if (mProduct != null) {
            mProduct.init(soundBean0, soundBean1, soundBean2, soundBean3, soundBean4);
        }

    }
}
