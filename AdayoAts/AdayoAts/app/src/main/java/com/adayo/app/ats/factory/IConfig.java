package com.adayo.app.ats.factory;

import java.util.Map;

public interface IConfig {
    int[] getNormalImages();

    int[] getDisImages();

    int[] getSelImages();

    int[] getGlobalTexts();

    int[] getBgImages();

    int[] getCenterTexts();

    int[] getTipsText();

    Map<Integer, Integer> getRequestModMap();

    Map<Integer, Integer> getConfirmModMap();
}
