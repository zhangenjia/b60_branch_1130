package com.adayo.app.btphone.view;

import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.adayo.app.btphone.R;
import com.adayo.app.btphone.adapter.KeyboardQueryResultAdapter;
import com.adayo.app.btphone.callback.ICallViewCallback;
import com.adayo.app.btphone.constant.Constant;
import com.adayo.app.btphone.manager.BTCallManager;
import com.adayo.app.btphone.manager.BTDialManager;
import com.adayo.app.btphone.utils.EditUtils;
import com.adayo.common.bluetooth.bean.PeopleInfo;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * TODO: document your custom view class.
 */
public class KeyboardQueryResultView extends ConstraintLayout{

    private static final String TAG = Constant.TAG + KeyboardQueryResultView.class.getSimpleName();

    private Context mContext;

    private View rootView;
    private RecyclerView mQueryResultRV;
    private KeyboardQueryResultAdapter mAdapter;
    private BTDialManager mBTDialManager;

    public KeyboardQueryResultView(Context context, BTDialManager btDialManager) {
        super(context);
        mBTDialManager = btDialManager;
        init(context);
    }

    public KeyboardQueryResultView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public KeyboardQueryResultView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        rootView = AAOP_HSkin.getLayoutInflater(context).inflate(R.layout.keyboard_query_view,this);
        mQueryResultRV = rootView.findViewById(R.id.query_rv);
        mQueryResultRV.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        mAdapter = new KeyboardQueryResultAdapter(context,mBTDialManager);
        mQueryResultRV.setAdapter(mAdapter);
    }

    public void setData(List<PeopleInfo> list){
        Log.i(TAG,"setData list.size = "+list.size());
        mAdapter.setQueryResultInfo(list);
    }

    public void clearData(){
        mAdapter.clearData();
    }

}
