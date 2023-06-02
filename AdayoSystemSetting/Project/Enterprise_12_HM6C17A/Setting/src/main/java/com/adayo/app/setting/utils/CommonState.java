package com.adayo.app.setting.utils;



public class CommonState<T> {

    private final DevState<T> mState = new DevState<>();

    public CommonState() {

        setNormal();
    }


    public T getType() {
        return mState.getObject();
    }


    public CommonState<T> setType(final T type) {
        mState.setObject(type);
        return this;
    }


    public boolean equalsType(final T type) {
        return mState.equalsObject(type);
    }


    public long getUUID() {
        return mState.getTokenUUID();
    }


    public long randomUUID() {
        return mState.randomTokenUUID();
    }


    public boolean equalsUUID(final long uuid) {
        return mState.equalsTokenUUID(uuid);
    }


    public int getState() {
        return mState.getState();
    }


    public CommonState<T> setState(final int state) {
        mState.setState(state);
        return this;
    }


    public boolean equalsState(final int state) {
        return mState.equalsState(state);
    }


    public boolean isNormal() {
        return equalsState(DevFinal.INT.NORMAL);
    }


    public boolean isIng() {
        return equalsState(DevFinal.INT.ING);
    }


    public boolean isSuccess() {
        return equalsState(DevFinal.INT.SUCCESS);
    }


    public boolean isFail() {
        return equalsState(DevFinal.INT.FAIL);
    }


    public boolean isError() {
        return equalsState(DevFinal.INT.ERROR);
    }


    public boolean isStart() {
        return equalsState(DevFinal.INT.START);
    }


    public boolean isRestart() {
        return equalsState(DevFinal.INT.RESTART);
    }


    public boolean isEnd() {
        return equalsState(DevFinal.INT.END);
    }


    public boolean isPause() {
        return equalsState(DevFinal.INT.PAUSE);
    }


    public boolean isResume() {
        return equalsState(DevFinal.INT.RESUME);
    }


    public boolean isStop() {
        return equalsState(DevFinal.INT.STOP);
    }


    public boolean isCancel() {
        return equalsState(DevFinal.INT.CANCEL);
    }


    public boolean isCreate() {
        return equalsState(DevFinal.INT.CREATE);
    }


    public boolean isDestroy() {
        return equalsState(DevFinal.INT.DESTROY);
    }


    public boolean isRecycle() {
        return equalsState(DevFinal.INT.RECYCLE);
    }


    public boolean isInit() {
        return equalsState(DevFinal.INT.INIT);
    }


    public boolean isEnabled() {
        return equalsState(DevFinal.INT.ENABLED);
    }


    public boolean isEnabling() {
        return equalsState(DevFinal.INT.ENABLING);
    }


    public boolean isDisabled() {
        return equalsState(DevFinal.INT.DISABLED);
    }


    public boolean isDisabling() {
        return equalsState(DevFinal.INT.DISABLING);
    }


    public boolean isConnected() {
        return equalsState(DevFinal.INT.CONNECTED);
    }


    public boolean isConnecting() {
        return equalsState(DevFinal.INT.CONNECTING);
    }


    public boolean isDisconnected() {
        return equalsState(DevFinal.INT.DISCONNECTED);
    }


    public boolean isSuspended() {
        return equalsState(DevFinal.INT.SUSPENDED);
    }


    public boolean isUnknown() {
        return equalsState(DevFinal.INT.UNKNOWN);
    }


    public boolean isInsert() {
        return equalsState(DevFinal.INT.INSERT);
    }


    public boolean isDelete() {
        return equalsState(DevFinal.INT.DELETE);
    }


    public boolean isUpdate() {
        return equalsState(DevFinal.INT.UPDATE);
    }


    public boolean isSelect() {
        return equalsState(DevFinal.INT.SELECT);
    }


    public boolean isEncrypt() {
        return equalsState(DevFinal.INT.ENCRYPT);
    }


    public boolean isDecrypt() {
        return equalsState(DevFinal.INT.DECRYPT);
    }


    public boolean isReset() {
        return equalsState(DevFinal.INT.RESET);
    }


    public boolean isClose() {
        return equalsState(DevFinal.INT.CLOSE);
    }


    public boolean isOpen() {
        return equalsState(DevFinal.INT.OPEN);
    }


    public boolean isExit() {
        return equalsState(DevFinal.INT.EXIT);
    }


    public CommonState<T> setNormal() {
        return setState(DevFinal.INT.NORMAL);
    }


    public CommonState<T> setIng() {
        return setState(DevFinal.INT.ING);
    }


    public CommonState<T> setSuccess() {
        return setState(DevFinal.INT.SUCCESS);
    }


    public CommonState<T> setFail() {
        return setState(DevFinal.INT.FAIL);
    }


    public CommonState<T> setError() {
        return setState(DevFinal.INT.ERROR);
    }


    public CommonState<T> setStart() {
        return setState(DevFinal.INT.START);
    }


    public CommonState<T> setRestart() {
        return setState(DevFinal.INT.RESTART);
    }


    public CommonState<T> setEnd() {
        return setState(DevFinal.INT.END);
    }


    public CommonState<T> setPause() {
        return setState(DevFinal.INT.PAUSE);
    }


    public CommonState<T> setResume() {
        return setState(DevFinal.INT.RESUME);
    }


    public CommonState<T> setStop() {
        return setState(DevFinal.INT.STOP);
    }


    public CommonState<T> setCancel() {
        return setState(DevFinal.INT.CANCEL);
    }


    public CommonState<T> setCreate() {
        return setState(DevFinal.INT.CREATE);
    }


    public CommonState<T> setDestroy() {
        return setState(DevFinal.INT.DESTROY);
    }


    public CommonState<T> setRecycle() {
        return setState(DevFinal.INT.RECYCLE);
    }


    public CommonState<T> setInit() {
        return setState(DevFinal.INT.INIT);
    }


    public CommonState<T> setEnabled() {
        return setState(DevFinal.INT.ENABLED);
    }


    public CommonState<T> setEnabling() {
        return setState(DevFinal.INT.ENABLING);
    }


    public CommonState<T> setDisabled() {
        return setState(DevFinal.INT.DISABLED);
    }


    public CommonState<T> setDisabling() {
        return setState(DevFinal.INT.DISABLING);
    }


    public CommonState<T> setConnected() {
        return setState(DevFinal.INT.CONNECTED);
    }


    public CommonState<T> setConnecting() {
        return setState(DevFinal.INT.CONNECTING);
    }


    public CommonState<T> setDisconnected() {
        return setState(DevFinal.INT.DISCONNECTED);
    }


    public CommonState<T> setSuspended() {
        return setState(DevFinal.INT.SUSPENDED);
    }


    public CommonState<T> setUnknown() {
        return setState(DevFinal.INT.UNKNOWN);
    }


    public CommonState<T> setInsert() {
        return setState(DevFinal.INT.INSERT);
    }


    public CommonState<T> setDelete() {
        return setState(DevFinal.INT.DELETE);
    }


    public CommonState<T> setUpdate() {
        return setState(DevFinal.INT.UPDATE);
    }


    public CommonState<T> setSelect() {
        return setState(DevFinal.INT.SELECT);
    }


    public CommonState<T> setEncrypt() {
        return setState(DevFinal.INT.ENCRYPT);
    }


    public CommonState<T> setDecrypt() {
        return setState(DevFinal.INT.DECRYPT);
    }


    public CommonState<T> setReset() {
        return setState(DevFinal.INT.RESET);
    }


    public CommonState<T> setClose() {
        return setState(DevFinal.INT.CLOSE);
    }


    public CommonState<T> setOpen() {
        return setState(DevFinal.INT.OPEN);
    }


    public CommonState<T> setExit() {
        return setState(DevFinal.INT.EXIT);
    }
}