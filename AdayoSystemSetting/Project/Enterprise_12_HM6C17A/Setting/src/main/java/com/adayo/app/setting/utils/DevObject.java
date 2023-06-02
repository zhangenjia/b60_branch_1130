package com.adayo.app.setting.utils;

import java.io.Serializable;
import java.util.UUID;




public class DevObject<T>
        implements Serializable {

    private final int    mUUID      = UUID.randomUUID().hashCode();
    private       T      mObject;
    private       Object mTag;
    private       int    mModelId;
    private       String mCode;
    private       int    mType;
    private       int    mState;
    private       long   mTokenUUID = UUID.randomUUID().hashCode();

    public DevObject() {
    }

    public DevObject(final T object) {
        this.mObject = object;
    }

    public DevObject(
            final T object,
            final Object tag
    ) {
        this.mObject = object;
        this.mTag    = tag;
    }


    public final int getUUID() {
        return mUUID;
    }


    public T getObject() {
        return mObject;
    }


    public DevObject<T> setObject(final T object) {
        this.mObject = object;
        return this;
    }


    public Object getTag() {
        return mTag;
    }


    public <CTO> CTO convertTag() {
        try {
            return (CTO) mTag;
        } catch (Exception ignored) {
        }
        return null;
    }


    public DevObject<T> setTag(final Object tag) {
        this.mTag = tag;
        return this;
    }


    public int getModelId() {
        return mModelId;
    }


    public DevObject<T> setModelId(final int modelId) {
        this.mModelId = modelId;
        return this;
    }


    public String getCode() {
        return mCode;
    }


    public DevObject<T> setCode(final String code) {
        this.mCode = code;
        return this;
    }


    public DevObject<T> setCode(final int code) {
        return setCode(String.valueOf(code));
    }


    public int getType() {
        return mType;
    }


    public DevObject<T> setType(final int type) {
        this.mType = type;
        return this;
    }


    public int getState() {
        return mState;
    }


    public DevObject<T> setState(final int state) {
        this.mState = state;
        return this;
    }


    public long getTokenUUID() {
        return mTokenUUID;
    }


    public DevObject<T> setTokenUUID(final long uuid) {
        this.mTokenUUID = uuid;
        return this;
    }


    public long randomTokenUUID() {
        mTokenUUID = UUID.randomUUID().hashCode();
        return mTokenUUID;
    }


    public boolean equalsObject(final T object) {
        return object != null && ObjectUtils.equals(this.mObject, object);
    }


    public boolean equalsTag(final Object tag) {
        return tag != null && ObjectUtils.equals(this.mTag, tag);
    }


    public boolean equalsModelId(final int modelId) {
        return this.mModelId == modelId;
    }


    public boolean equalsCode(final int code) {
        return equalsCode(String.valueOf(code));
    }


    public boolean equalsCode(final String code) {
        return code != null && ObjectUtils.equals(this.mCode, code);
    }


    public boolean equalsType(final int type) {
        return this.mType == type;
    }


    public boolean equalsState(final int state) {
        return this.mState == state;
    }


    public boolean equalsTokenUUID(final long uuid) {
        return this.mTokenUUID == uuid;
    }


    public boolean isCorrect() {
        return false;
    }


    public static boolean isCorrect(final DevObject<?> data) {
        return data != null && data.isCorrect();
    }
}