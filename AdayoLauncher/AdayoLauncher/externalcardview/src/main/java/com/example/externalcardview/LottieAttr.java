package com.example.externalcardview;

import com.adayo.proxy.aaop_hskin.entity.DynamicAttr;

public class LottieAttr extends DynamicAttr {
    public String json;
    public LottieAttr(String attrName,String json) {
        super(attrName);
        keepInstance = true;
        this.json = json;
    }


}
