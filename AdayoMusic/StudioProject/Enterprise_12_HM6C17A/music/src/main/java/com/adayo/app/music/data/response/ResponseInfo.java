package com.adayo.app.music.data.response;

import java.util.StringJoiner;

public class ResponseInfo {
    private final boolean mSuccess;
    private Integer mResponseCode = 0;
    private String mResponseMessage = "";
    private ResultSource mResultSource = ResultSource.NULL;

    private ResponseInfo(boolean isSuccess) {
        mSuccess = isSuccess;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ResponseInfo.class.getSimpleName() + "[", "]")
                .add("mSuccess=" + mSuccess)
                .add("mResponseCode=" + mResponseCode)
                .add("mResponseMessage='" + mResponseMessage + "'")
                .add("mResultSource=" + mResultSource)
                .toString();
    }

    public boolean isSuccess() {
        return mSuccess;
    }

    public Integer getResponseCode() {
        return mResponseCode;
    }

    public String getResponseMessage() {
        return mResponseMessage;
    }

    public ResultSource getResultSource() {
        return mResultSource;
    }

    public static class Builder {
        private final ResponseInfo responseInfo;

        public Builder(boolean isSuccess) {
            responseInfo = new ResponseInfo(isSuccess);
        }

        public Builder setResponseCode(Integer responseCode) {
            responseInfo.mResponseCode = responseCode;
            return this;
        }

        public Builder setResponseMessage(String responseMessage) {
            responseInfo.mResponseMessage = responseMessage;
            return this;
        }

        public Builder setResultSource(ResultSource resultSource) {
            responseInfo.mResultSource = resultSource;
            return this;
        }

        public ResponseInfo build() {
            return responseInfo;
        }
    }
}
