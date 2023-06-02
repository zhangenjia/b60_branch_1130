package com.adayo.app.btphone.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.app.btphone.R;
import com.adayo.app.btphone.adapter.CallLogAdapter;
import com.adayo.app.btphone.callback.IBTCallLogCallback;
import com.adayo.app.btphone.constant.Constant;
import com.adayo.app.btphone.manager.BTCallLogManager;
import com.adayo.app.btphone.manager.BTLinkManManager;
import com.adayo.app.btphone.view.SyncFailDialog;
import com.adayo.common.bluetooth.bean.PeopleInfo;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 通话记录
 */
public class CallLogFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = Constant.TAG + CallLogFragment.class.getSimpleName();
    private Handler mHandler;
    private BTCallLogManager mBTCallLogManager;
    private EditText mSearchET;
    private TextView mSyncTV;
    private TextView mCancelSyncTV;
    private TextView mSyncingTV;
    private TextView mNoNumberListTV;
    private RelativeLayout mSearchRL;
    private RelativeLayout mRecyclerViewRL;
    private RecyclerView mRecycleView;
    private RelativeLayout mSyncIngRL;
    private LinearLayout mSyncFailedLL;
    private TextView mSyncFailedTV;
    private ImageView mSyncingIV;
    private CallLogAdapter mAdapter;
    private Context mContext;
    private AnimationDrawable mAnimDrawable;
    private boolean mNoCallLogInfo = false;

    public CallLogFragment() {
    }

    @SuppressLint("ValidFragment")
    public CallLogFragment(Handler handler, Context context) {
        // Required empty public constructor
        mHandler = handler;
        mContext = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBTCallLogManager = BTCallLogManager.getInstance(getActivity().getApplicationContext());
        mBTCallLogManager.registerBTCallLog(callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call_log, container, false);
        mSyncTV = view.findViewById(R.id.sync_tv);
        mSyncTV.setOnClickListener(this);
        mCancelSyncTV = view.findViewById(R.id.cancle_sync_tv);
        mCancelSyncTV.setOnClickListener(this);
        mSyncingTV = view.findViewById(R.id.syncing_tv);
        mNoNumberListTV = view.findViewById(R.id.no_number_list_tv);
        mSearchET = view.findViewById(R.id.search_et);
        mSearchET.addTextChangedListener(textWatcher);
        mSearchET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mSearchET.setFocusable(true);
                mSearchET.setFocusableInTouchMode(true);
                mSearchET.requestFocus();
                return false;
            }
        });
        mSearchET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i(TAG, "onFocusChange hasFocus = " + hasFocus);
                if (hasFocus) {
                    mBTCallLogManager.setSearchMode(true);
                }
            }
        });
        mSearchRL = view.findViewById(R.id.search_rl);
        mRecyclerViewRL = view.findViewById(R.id.recyclerview_rl);
        mRecycleView = view.findViewById(R.id.recyclerview);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        mSyncIngRL = view.findViewById(R.id.sync_ing_rl);
        mSyncingIV = view.findViewById(R.id.syncing_iv);
        mAnimDrawable = (AnimationDrawable) mSyncingIV.getDrawable();
        mAdapter = new CallLogAdapter(getActivity().getApplicationContext(), mBTCallLogManager);
        mRecycleView.setAdapter(mAdapter);
        mSyncFailedLL = view.findViewById(R.id.sync_failed_ll);
        mSyncFailedTV = view.findViewById(R.id.sync_failed_tv);
        mSyncFailedTV.setOnClickListener(this);

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (null != mAdapter && null != mRecycleView) {
                mAdapter.playLayoutAnimation(mRecycleView);
            }
        } else {
            hideInputMethod();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sync_tv:
            case R.id.sync_failed_tv:
                mBTCallLogManager.setSearchMode(false);
                if(mSearchET.length() == 0){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mBTCallLogManager.downloadCallLog();
                        }
                    }, 200);
                }
                mSearchET.setText("");
                hideInputMethod();
                break;
            case R.id.cancle_sync_tv:
                Log.i(TAG, "cancelDownloadCallLog");
                mBTCallLogManager.cancelDownloadCallLog();
                break;
            default:
                break;
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        private String temp;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            Log.i(TAG, "onTextChanged beforeTextChanged");

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            Log.i(TAG, "onTextChanged charSequence = " + charSequence);
            temp = charSequence.toString();
            if (charSequence.length() == 0) {
                mSyncTV.setText(R.string.sync);
            } else {
                mSyncTV.setText(R.string.cancel);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            Log.i(TAG, "onTextChanged afterTextChanged");
            String info = editable.toString();
            if (null == info) {
                return;
            }
//            String limit = getLimitSubstring(info);
//            if(!limit.equals(temp)){
//                mSearchET.setText(limit);
//                mSearchET.setSelection(limit.length());
//            }else{
            if (null != mBTCallLogManager) {
                mBTCallLogManager.loadCallLog(info);
            }
//            }
            // 禁止EditText输入空格
//            if (info.contains(" ")) {
//                String[] str = info.split(" ");
//                for (int i = 0; i < str.length; i++) {
//                    Log.i(TAG,"afterTextChanged 347, str["+i+"]:" + str[i]);
//                    sb = sb + str[i];
//                }
//            }

        }
    };

    private String getLimitSubstring(String inputStr) {
        int orignLen = inputStr.length();
        int resultLen = 0;
        String temp = null;
        for (int i = 0; i < orignLen; i++) {
            temp = inputStr.substring(i, i + 1);
            try {// 3 bytes to indicate chinese word,1 byte to indicate english
                // word ,in utf-8 encode
                if (temp.getBytes("utf-8").length == 3) {
                    resultLen += 2;
                } else {
                    resultLen++;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (resultLen > 36) {
                return inputStr.substring(0, i);
            }
        }
        return inputStr;
    }

    private IBTCallLogCallback callback = new IBTCallLogCallback() {
        @Override
        public void updateCallLogSyncState(String state) {
            Log.i(TAG, "updateCallLogSyncState state = " + state);
            if (null != mAnimDrawable && mAnimDrawable.isRunning()) {
                mAnimDrawable.stop();
            }
            switch (state) {
                case "SYNCHRONIZING":
                    mSyncIngRL.setVisibility(View.VISIBLE);
                    mRecyclerViewRL.setVisibility(View.GONE);
                    mSearchRL.setVisibility(View.GONE);
                    mSyncFailedLL.setVisibility(View.GONE);
                    if (null != mAnimDrawable && !mAnimDrawable.isRunning()) {
                        mAnimDrawable.start();
                    }
                    break;
                case "SUCCESSED":
                    if (mNoCallLogInfo) {
                        return;
                    }
                    mSyncIngRL.setVisibility(View.GONE);
                    mSearchRL.setVisibility(View.VISIBLE);
                    mRecyclerViewRL.setVisibility(View.VISIBLE);
                    mSyncFailedLL.setVisibility(View.GONE);
                    break;
                case "INTERRUPTED":
                case "FAILED":
                    if(isHidden()){
                        return;
                    }
                    if (null != mContext) {
                        SyncFailDialog syncFailDialog = new SyncFailDialog(mContext);
                        syncFailDialog.show();
                    }
                    break;
                case "READY":
                    break;
                default:
                    break;
            }
        }

        @Override
        public void updateAllCallLog(List<PeopleInfo> peopleList) {
            Log.i(TAG, "updateAllCallLog peopleList.size = " + peopleList.size());
            if (null != mAdapter) {
                mAdapter.setCallLogInfo(peopleList);
            } else {
                return;
            }
            if (null != mRecycleView) {
                mRecycleView.smoothScrollToPosition(0);
                mAdapter.playLayoutAnimation(mRecycleView);
            }
        }

        @Override
        public void showNoCallLogDataAlert() {
            mNoCallLogInfo = true;
            mSyncIngRL.setVisibility(View.GONE);
            mSearchRL.setVisibility(View.GONE);
            mRecyclerViewRL.setVisibility(View.GONE);
            mSyncFailedLL.setVisibility(View.VISIBLE);
        }

        @Override
        public void hideNoCallLogDataAlert() {
            mNoCallLogInfo = false;
            mSyncIngRL.setVisibility(View.GONE);
            mSearchRL.setVisibility(View.VISIBLE);
            mRecyclerViewRL.setVisibility(View.VISIBLE);
            mSyncFailedLL.setVisibility(View.GONE);
        }

        @Override
        public void showNotFoundCallLogAlert() {
            Log.i(TAG, "showNotFoundCallLogAlert");
        }

        @Override
        public void hideNotFoundCallLogAlert() {
            Log.i(TAG, "hideNotFoundCallLogAlert");
        }
    };

    public void hideInputMethod() {
        if (null != mContext && null != mSearchET) {
            InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            boolean isOpen = inputMethodManager.isActive();//isOpen若返回true，则表示输入法打开
            if (isOpen) {
                inputMethodManager.hideSoftInputFromWindow(mSearchET.getWindowToken(), 0);
            }
            mSearchET.setFocusable(false);
            mSearchET.setFocusableInTouchMode(false);
        }
    }

    public void setContext(Context context){
        mContext = context;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mSearchET.setHint(R.string.search);
        if(mSearchET.getText().length() == 0){
            mSyncTV.setText(R.string.sync);
        }else{
            mSyncTV.setText(R.string.cancel);
        }
        mCancelSyncTV.setText(R.string.cancel);
        mSyncingTV.setText(R.string.syncing);
        mNoNumberListTV.setText(R.string.no_number_list);
        mSyncFailedTV.setText(R.string.sync);
        mAdapter.notifyDataSetChanged();
    }
}