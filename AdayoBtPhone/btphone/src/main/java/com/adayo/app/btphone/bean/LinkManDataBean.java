package com.adayo.app.btphone.bean;

import com.adayo.common.bluetooth.bean.PeopleInfo;

public class LinkManDataBean {

    public static final int TYPE_CHARACTER = 0;
    public static final int TYPE_DATA = 1;
    private int itemType;
    private String itemEn;

    private PeopleInfo peopleInfo;

    private static final String A_TO_Z = "[A-Z]+";


    public LinkManDataBean(PeopleInfo mPeople, int type) {
        this.itemType = type;
        this.peopleInfo = mPeople;
        this.itemEn = peopleInfo.getFirstLatter();
        if (!itemEn.matches(A_TO_Z)) {
            itemEn = "#" + itemEn;
        }
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getItemEn() {
        return itemEn;
    }

    public void setItemEn(String itemEn) {
        this.itemEn = itemEn;
    }

    public PeopleInfo getPeopleInfo() {
        return peopleInfo;
    }

    public void setPeopleInfo(PeopleInfo peopleInfo) {
        this.peopleInfo = peopleInfo;
    }

    @Override
    public String toString() {
        return "LinkManDataBean{" +
                "item_type=" + itemType +
                ", item_en='" + itemEn + '\'' +
                ", getPersonName() =" + peopleInfo.getPersonName() +
                ", getNumber()" + peopleInfo.getNumber() +
                '}';
    }
}
