package com.adayo.app.music.data.response;

import java.util.StringJoiner;

public class DataResult<T> {
    private final T mEntity;
    private final ResponseInfo mResponseInfo;

    public DataResult(T entity, ResponseInfo responseInfo) {
        mEntity = entity;
        mResponseInfo = responseInfo;
    }

    public T getResult() {
        return mEntity;
    }

    public ResponseInfo getResponseInfo() {
        return mResponseInfo;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DataResult.class.getSimpleName() + "[", "]")
                .add("mEntity=" + mEntity)
                .add("mResponseInfo=" + mResponseInfo)
                .toString();
    }

    public interface Result<T> {
        void onResult(DataResult<T> dataResult);
    }
}
