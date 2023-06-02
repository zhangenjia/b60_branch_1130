package com.adayo.btsetting;

import android.content.Context;

/**
 * @author Y4134
 */
public class ContextManager {

    private static volatile ContextManager mModel;
    private static final String TAG = ContextManager.class.getSimpleName();
    private Context mContext;
    private IRequestContext mRequest;

    private ContextManager() {

    }

    public static ContextManager getInstance() {
        if (mModel == null) {
            synchronized (ContextManager.class) {
                if (mModel == null) {
                    mModel = new ContextManager();
                }
            }
        }
        return mModel;
    }

    private IResponseContext response = new IResponseContext() {
        @Override
        public void response(Context context) {
            mContext = context;
            if (mRequest!=null){
                mRequest.request(mContext);
            }
        }
    };

    public IResponseContext getResponse() {
        return response;
    }

    public Context getContext() {
        return mContext;
    }

    public void setRequest(IRequestContext mRequest) {
        this.mRequest = mRequest;
    }
}
