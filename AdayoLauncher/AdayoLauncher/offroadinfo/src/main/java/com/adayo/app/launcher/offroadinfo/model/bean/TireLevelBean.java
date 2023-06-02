package com.adayo.app.launcher.offroadinfo.model.bean;

/**
 * @author ADAYO-21
 */
public class TireLevelBean implements Comparable<TireLevelBean> {
    private int level;
    private int type;
    private int position;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "TireLevelBean{" +
                "level=" + level +
                ", type=" + type +
                ", position=" + position +
                '}';
    }

    @Override
    public int compareTo(TireLevelBean bean) {
        if (level > bean.getLevel()){ //优先级高的放到position小的位置 相同优先等级的 后进来的放到最position小的位置
            return 1;
        }else {
            return -1;
        }
    }
}
