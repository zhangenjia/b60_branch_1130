package warning.listener;

public interface IConfirmClickListener {

    void onConfirmClick( );

    void addCallBack(CallBack callBack);

    interface CallBack {
        void callBack();
    }
}
