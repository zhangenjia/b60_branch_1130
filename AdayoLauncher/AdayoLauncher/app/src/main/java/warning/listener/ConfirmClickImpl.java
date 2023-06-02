package warning.listener;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ConfirmClickImpl implements IConfirmClickListener {

    private static ConfirmClickImpl mConfirmClickImpl;
    private final List<CallBack> callBackList;

    public static ConfirmClickImpl getInstance() {
        if (null == mConfirmClickImpl) {
            synchronized (ConfirmClickImpl.class) {
                if (null == mConfirmClickImpl) {
                    mConfirmClickImpl = new ConfirmClickImpl();
                }
            }
        }
        return mConfirmClickImpl;
    }
    private ConfirmClickImpl(){
        callBackList = new ArrayList<>();
    }

    //    private CallBack callBack;


    @Override
    public void onConfirmClick() {
        Log.d("ConfirmClickImpl", "onConfirmClick: ");
        for (CallBack callback : callBackList) {
            callback.callBack();
        }
    }

    @Override
    public void addCallBack(CallBack callBack) {
        callBackList.add(callBack);
//        this.callBack = callBack;
    }
}
