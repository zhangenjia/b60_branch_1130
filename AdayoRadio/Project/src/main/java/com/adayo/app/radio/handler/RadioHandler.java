package com.adayo.app.radio.handler;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.adayo.app.radio.ui.fragment.RadioInfoFragment;
import com.adayo.app.radio.utils.RadioAPPLog;

import static com.adayo.app.radio.constant.Constants.RADIO_CALLBACK_UPDATEVIEW;
import static com.adayo.app.radio.constant.Constants.RADIO_CALLBACK_UPDATEVIEW_AND_LIST;
import static com.adayo.app.radio.constant.Constants.RADIO_CLEAR_LIST;
import static com.adayo.app.radio.constant.Constants.RADIO_CLEAR_LIST_AND_SEARCH;
import static com.adayo.app.radio.constant.Constants.RADIO_THREADGETLIST_UPDATECOLLECTLIST;
import static com.adayo.app.radio.constant.Constants.RADIO_THREADGETLIST_UPDATEVIEW;

/**
 *
 * @author admin
 * @date 2020/3/22
 */

public class RadioHandler extends Handler {

    private static final String TAG = "RadioHandler";
    private Context mContext;
    private Resources resources;

    public RadioHandler(Context context)
    {
        mContext = context;
        resources = mContext.getResources();
    }

    @Override
    public void handleMessage(Message msg) {
        Log.d(RadioAPPLog.TAG, TAG + " handleMessage: start msg = " +msg);
        if (msg == null)
        {
            return;
        }
        Log.d(RadioAPPLog.TAG, TAG + " handleMessage: msg.what = " + msg.what);
        switch (msg.what)
        {
            case RADIO_THREADGETLIST_UPDATEVIEW:
                Log.d(RadioAPPLog.TAG, TAG + " handleMessage: msg.what = " + msg.what);
                RadioInfoFragment.getmInstance().updateListView();
                break;
                //callback 收藏列表更新
            case RADIO_THREADGETLIST_UPDATECOLLECTLIST:
                RadioInfoFragment.getmInstance().updateView();
                RadioInfoFragment.getmInstance().updateListView();
                break;
            case RADIO_CALLBACK_UPDATEVIEW_AND_LIST:
                RadioInfoFragment.getmInstance().updateView();
                RadioInfoFragment.getmInstance().updateSearchList();
                break;
            case RADIO_CLEAR_LIST:
                RadioInfoFragment.getmInstance().clearSearchListInfo();
                break;
            case RADIO_CLEAR_LIST_AND_SEARCH:
                RadioInfoFragment.getmInstance().clearSearchListAndStartAnima();
                break;
            default:
                break;
        }
        Log.d(RadioAPPLog.TAG, TAG + " handleMessage: end");
    }
}
