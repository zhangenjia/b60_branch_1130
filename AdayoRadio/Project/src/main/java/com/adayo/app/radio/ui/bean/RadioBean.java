package com.adayo.app.radio.ui.bean;

import android.provider.MediaStore;

import java.io.Serializable;

/**
 * @author ADAYO-06
 */
public class RadioBean implements Serializable{
        private int mId = 0;
        private String mFreq = "87.5";
        private int mIndex = 0;
        private String mBand = "FM";
        private boolean mIsPlay = false;
        private boolean mIsCollect = false;
        private boolean mIsSelect = false;
        private boolean mIsSelectMode = false;
        private String mName = "";

        public RadioBean() {
        }

        public String getFreq() {
            return this.mFreq;
        }

        public int getIndex() {
            return this.mIndex;
        }

        public String getBand() {
            return this.mBand;
        }

        public boolean getIsSelect() {
            return this.mIsSelect;
        }

        public void setIsSelect(boolean mIsSelect) {
            this.mIsSelect = mIsSelect;
        }

        public boolean getIsPlay() {
            return this.mIsPlay;
        }

        public boolean getIsCollect() {
            return this.mIsCollect;
        }

        public boolean getIsSelectMode() {
            return this.mIsSelectMode;
        }

        public void setmIsSelectMode(boolean mode) {
            this.mIsSelectMode = mode;
        }

        public String getName() {
            return this.mName;
        }

        public void setFreq(String mFreq) {
            this.mFreq = mFreq;
        }

        public void setIndex(int mIndex) {
            this.mIndex = mIndex;
        }

        public void setBand(String mBand) {
            this.mBand = mBand;
        }

        public void setIsPlay(boolean mIsPlay) {
            this.mIsPlay = mIsPlay;
        }

        public void setmIsCollect(boolean mIsCollect) {
            this.mIsCollect = mIsCollect;
        }

        public void setName(String mName) {
            this.mName = mName;
        }

        public void setId(int id) {
            this.mId = id;
        }

        public int getId() {
            return this.mId;
        }

        @Override
        public String toString() {
            return "RadioBean{mId=" + this.mId + ", mFreq='" + this.mFreq + '\'' + ", mIndex=" + this.mIndex + ", mBand='" + this.mBand + '\'' + ", mIsPlay=" + this.mIsPlay + ", mIsCollect=" + this.mIsCollect + ", mIsSelect=" + this.mIsSelect + ", mIsSelectMode=" + this.mIsSelectMode + ", mName='" + this.mName + '\'' + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o != null && this.getClass() == o.getClass()) {
                RadioBean radioBean =  (RadioBean) o;
                return this.mIsPlay == radioBean.mIsPlay && this.mIsCollect == radioBean.mIsCollect && this.mIsSelect == radioBean.mIsSelect && this.mIsSelectMode == radioBean.mIsSelectMode && this.mFreq.equals(radioBean.mFreq) && this.mBand.equals(radioBean.mBand) && this.mName.equals(radioBean.mName);
            } else {
                return false;
            }
        }
}
