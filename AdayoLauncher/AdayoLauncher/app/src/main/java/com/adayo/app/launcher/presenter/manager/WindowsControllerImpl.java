package com.adayo.app.launcher.presenter.manager;
import java.util.ArrayList;

public class WindowsControllerImpl<T>  {
    private volatile static WindowsControllerImpl windowsController;
    private final ArrayList<BaseCallback<T>> mCallbacks = new ArrayList<>();
    private static final  Object lock = new Object();
    private int isVisibility;


    public static WindowsControllerImpl getInstance() {
        if (windowsController == null) {
            synchronized (WindowsControllerImpl.class) {
                if (windowsController == null) {
                    windowsController = new WindowsControllerImpl();
                }
            }
        }
        return windowsController;
    }


    public void addCallback(BaseCallback callback) {

        synchronized (lock) {
            if (null != callback && !mCallbacks.contains(callback)) {
                mCallbacks.add(callback);
            }
        }

    }

    public void notifyCallbacks(int isVisibility){
        this.isVisibility = isVisibility;
            synchronized (lock) {
                for (BaseCallback callBack : mCallbacks) {
                    notifyCallback(callBack);
                }
            }

    }

    private void notifyCallback(BaseCallback callBack){
        callBack.onDataChange(isVisibility);

    }



}
