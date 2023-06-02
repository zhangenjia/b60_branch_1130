package com.adayo.app.btphone.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
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
import com.adayo.app.btphone.adapter.LinkManAdapter;
import com.adayo.app.btphone.bean.LinkManDataBean;
import com.adayo.app.btphone.callback.IBTLinkManCallback;
import com.adayo.app.btphone.constant.Constant;
import com.adayo.app.btphone.linkmanstate.ILinkManState;
import com.adayo.app.btphone.linkmanstate.LinkManHFPAndA2DPConnectState;
import com.adayo.app.btphone.linkmanstate.LinkManHFPAndA2DPUnConnectState;
import com.adayo.app.btphone.linkmanstate.LinkManSyncFailedState;
import com.adayo.app.btphone.linkmanstate.LinkManSyncSuccessState;
import com.adayo.app.btphone.linkmanstate.LinkManSyncingState;
import com.adayo.app.btphone.manager.BTLinkManManager;
import com.adayo.app.btphone.utils.ListUtils;
import com.adayo.app.btphone.view.SlideBar;
import com.adayo.app.btphone.view.SyncFailDialog;
import com.adayo.common.bluetooth.bean.PeopleInfo;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 联系人
 */
public class LinkManFragment extends Fragment implements View.OnClickListener, SlideBar.OnTouchAssortListener, SlideBar.OnDrownListener, SlideBar.OnVisibilityChangedListener {

    private static final String TAG = Constant.TAG + LinkManFragment.class.getSimpleName();

    private Handler mHandler;
    private BTLinkManManager mBTLinkManManager;
    private TextView mSyncTV;
    private TextView mCancelSyncTV;
    private TextView mSyncingTV;
    private TextView mNoNumberListTV;
    private RelativeLayout mSearchRL;
    private RelativeLayout mRecyclerViewRL;
    private RecyclerView mRecycleView;
    private RelativeLayout mSyncIngRL;
    private ImageView mSyncingIV;
    private LinearLayout mSyncFailedLL;
    private TextView mSyncFailedTV;
    private LinearLayoutManager mLinearLayout;
    private LinkManAdapter mAdapter;
    private EditText mSearchET;
    private SlideBar mSlideBar;
    private String mSlideBarString = "";
    private List<LinkManDataBean> mListData = new ArrayList<>();
    private List<View> mDisplayList = new ArrayList<>();
    private ILinkManState mLinkManState;
    private LinkManSyncingState mSyncingState;
    private LinkManSyncSuccessState mSyncSuccessState;
    private LinkManSyncFailedState mSyncFailedState;
    private LinkManHFPAndA2DPUnConnectState mHFPUnConnect;
    private LinkManHFPAndA2DPConnectState mHFPConnect;
    private Context mContext;
    private AnimationDrawable mAnimDrawable;
    public boolean mNoLinkManInfo = false;
    public int mContactListSize;

    /**
     * 记录目标项位置
     */
    private int mToPosition = -1;

    private static final String SPACE = " ";

    public LinkManFragment() {
    }

    @SuppressLint("ValidFragment")
    public LinkManFragment(Handler handler, Context context) {
        mHandler = handler;
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBTLinkManManager = new BTLinkManManager(getActivity().getApplicationContext(), getActivity());
        mBTLinkManManager.registerContactInfo(callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_linkman, container, false);
        mSyncTV = view.findViewById(R.id.sync_tv);
        mSyncTV.setOnClickListener(this);
        mCancelSyncTV = view.findViewById(R.id.cancle_sync_tv);
        mCancelSyncTV.setOnClickListener(this);
        mSyncingTV = view.findViewById(R.id.syncing_tv);
        mNoNumberListTV = view.findViewById(R.id.no_number_list_tv);
        mSearchRL = view.findViewById(R.id.search_rl);
        mRecyclerViewRL = view.findViewById(R.id.recyclerview_rl);
        mRecycleView = view.findViewById(R.id.recyclerview);
        mLinearLayout = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mRecycleView.setLayoutManager(mLinearLayout);
        mSyncIngRL = view.findViewById(R.id.sync_ing_rl);
        mSyncingIV = view.findViewById(R.id.syncing_iv);
        mAnimDrawable = (AnimationDrawable) mSyncingIV.getDrawable();
        mSyncFailedLL = view.findViewById(R.id.sync_failed_ll);
        mSyncFailedTV = view.findViewById(R.id.sync_failed_tv);
        mSyncFailedTV.setOnClickListener(this);
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
                    mBTLinkManManager.setSearchMode(true);
                }
            }
        });
        mAdapter = new LinkManAdapter(getActivity().getApplicationContext(), mBTLinkManManager, mItemClickHandler);
        mRecycleView.setAdapter(mAdapter);
        mRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                updateSlideBar();
            }
        });
        mSlideBar = view.findViewById(R.id.sb_linkman);
        mSlideBar.setOnTouchAssortListener(this);
        mSlideBar.setOnDrownListener(this);
        mSlideBar.setOnVisibilityChangedListener(this);

        mDisplayList.add(mSearchRL);
        mDisplayList.add(mSyncIngRL);
        mDisplayList.add(mRecyclerViewRL);
        mDisplayList.add(mSlideBar);
        mDisplayList.add(mSyncFailedLL);
        mSyncingState = new LinkManSyncingState(mDisplayList);
        mSyncSuccessState = new LinkManSyncSuccessState(mDisplayList, this);
        mSyncFailedState = new LinkManSyncFailedState(mDisplayList, this);
        mHFPConnect = new LinkManHFPAndA2DPConnectState(mDisplayList, this);
        mHFPUnConnect = new LinkManHFPAndA2DPUnConnectState(mDisplayList);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            hideInputMethod();
        }
    }

    IBTLinkManCallback callback = new IBTLinkManCallback() {
        @Override
        public void updateContactsSyncState(String state) {
            Log.i(TAG, "updateContactsSyncState state = " + state);
            if (null != mAnimDrawable && mAnimDrawable.isRunning()) {
                mAnimDrawable.stop();
            }
            switch (state) {
                case "SYNCHRONIZING":
                    mLinkManState = mSyncingState;
                    mLinkManState.updateDisplayStatus();
                    if (null != mAnimDrawable && !mAnimDrawable.isRunning()) {
                        mAnimDrawable.start();
                    }
                    break;
                case "SUCCESSED":
                    mLinkManState = mSyncSuccessState;
                    mLinkManState.updateDisplayStatus();
                    break;
                case "INTERRUPTED":
                    if(isHidden()){
                        return;
                    }
                    if (null != mContext) {
                        SyncFailDialog syncFailDialog = new SyncFailDialog(mContext);
                        syncFailDialog.show();
                    }

                    break;
                case "FAILED":
                    mLinkManState = mSyncFailedState;
                    mLinkManState.updateDisplayStatus();
                    break;
                case "READY":
                    break;
                default:
                    break;
            }
        }

        @Override
        public void updateContactsList(List<PeopleInfo> list, List<String> list1) {
            Log.i(TAG, "updateContactsList list.size = " + list.size() + " list1.size = " + list1.size());
            mContactListSize = list.size();
            if (mContactListSize == 0) {
                mRecyclerViewRL.setVisibility(View.GONE);
            } else {
                mRecyclerViewRL.setVisibility(View.VISIBLE);
                if(null != mSearchET && mSearchET.getText().length() == 0){
                    mSlideBar.setVisibility(View.VISIBLE);
                }
            }
            if (null == list || list.size() == 0) {
                return;
            }
            mListData.clear();
            for (PeopleInfo data : list) {
                if (data.getPersonName() == null || data.getNumbers().get(0).getPhoneNumber() == null) {
                    continue;
                }
                LinkManDataBean bean = new LinkManDataBean(data, LinkManDataBean.TYPE_DATA);
                Log.i(TAG, bean.getPeopleInfo().getPersonName() + "[" + bean.getPeopleInfo().getFirstLatter() + "|" + bean.getPeopleInfo().getPinyin() + "]:" + data.getNumbers().get(0).getPhoneNumber() + "; " + data.getNumbers().size() + " numbers");
                mListData.add(bean);
            }
            if (null != mListData && mListData.size() > 0) {
                ListUtils.sortList(mListData);
            }

            if (null != mAdapter) {
                mAdapter.setContactsInfo(mListData,mSearchET.getText().toString());
            }
            if (null != mRecycleView) {
                mRecycleView.smoothScrollToPosition(0);
                mAdapter.playLayoutAnimation(mRecycleView);
            }
        }

        @Override
        public void updateHfpState(boolean state) {
            if (state) {
                mLinkManState = mHFPConnect;
            } else {
                mLinkManState = mHFPUnConnect;
            }
            mLinkManState.updateDisplayStatus();
        }

        @Override
        public void updateA2DPState(boolean state) {
            if (state) {
                mLinkManState = mHFPConnect;
            } else {
                mLinkManState = mHFPUnConnect;
            }
            mLinkManState.updateDisplayStatus();
        }

        @Override
        public void updateNoContactsAlertState(boolean showNoContacts) {
            mNoLinkManInfo = showNoContacts;
            if (showNoContacts) {
                mSyncFailedLL.setVisibility(View.VISIBLE);
                mRecyclerViewRL.setVisibility(View.GONE);
                mSearchRL.setVisibility(View.GONE);
                mSyncIngRL.setVisibility(View.GONE);
            } else {
                mSyncFailedLL.setVisibility(View.GONE);
                mRecyclerViewRL.setVisibility(View.VISIBLE);
                mSearchRL.setVisibility(View.VISIBLE);
                mSyncIngRL.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sync_tv:
            case R.id.sync_failed_tv:
                mBTLinkManManager.setSearchMode(false);
                if(mSearchET.length() == 0){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mBTLinkManManager.downloadContacts();
                        }
                    }, 200);
                }
                mSearchET.setText("");
                hideInputMethod();
                break;
            case R.id.cancle_sync_tv:
                mBTLinkManManager.cancelDownloadContacts();
                break;
            default:
                break;
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        private String temp;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            Log.i(TAG, "beforeTextChanged");
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int i1, int i2) {
            Log.i(TAG, "onTextChanged---charSequence:" + charSequence + "; start:" + start + "; count:" + i1 + "; after:" + i2);
            temp = charSequence.toString();
            if (charSequence.length() == 0) {
                mSlideBar.setVisibility(View.VISIBLE);
                updateSlideBar();
                mSyncTV.setText(R.string.sync);
            } else {
                mSlideBar.setVisibility(View.GONE);
                mSlideBar.disShowCharacter();
                mSyncTV.setText(R.string.cancel);
            }
            if (charSequence.toString().contains(SPACE)) {
                return;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String info = editable.toString();
            Log.i(TAG, "afterTextChanged 342, info:" + info);
            String sb = "";
//            String limit = getLimitSubstring(info);
//            Log.i(TAG,"afterTextChanged 347, limit = " + limit+ " temp = "+temp);
//            if(!limit.equals(temp)){
//                mSearchET.setText(limit);
//                mSearchET.setSelection(limit.length());
//            }else{
            if (null != mBTLinkManManager) {
                mBTLinkManManager.searchContacts(info);
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


    @Override
    public boolean onTouchAssortListener(String s) {
        boolean ret = false;
        mToPosition = -1;
        for (int i = 0; i < mListData.size(); i++) {
            if (s.equals(mListData.get(i).getItemEn().substring(0, 1))) {
                mToPosition = i;
                Log.i(TAG, "onTouchAssortListener: index=" + i + "; s=" + s);
                break;
            }
        }
        if (mToPosition != -1) {
            if (!mSlideBarString.equals(s)) {
                mSlideBarString = s;
                smoothMoveToPosition(mToPosition);
            }
            ret = true;
        }
        return ret;
    }

    @Override
    public void onDrown() {
    }

    @Override
    public void onVisibilityChanged(int state) {
        Log.i(TAG, "OnVisibilityChanged state = " + state);
        if (state == View.VISIBLE) {
//            if (!isHidden()) {
            updateSlideBar();
//            }
        } else {
            mSlideBar.disShowCharacter();
        }
    }

    /**
     * 滑动到指定位置
     *
     * @param position
     */
    private void smoothMoveToPosition(final int position) {
        int firstItem = mLinearLayout.findFirstVisibleItemPosition();
        int lastItem = mLinearLayout.findLastVisibleItemPosition();
        Log.i(TAG, "firstItem:" + firstItem + ",lastItem:" + lastItem + ",position:" + position);
        if (position < firstItem) {
            // 第一种:跳转位置在第一个可见位置之前
            mRecycleView.scrollToPosition(position);
        } else if (position <= lastItem) {
            // 第二种:跳转位置在第一个可见位置之后
            int movePosition = position - firstItem;
            int top = mRecycleView.getChildAt(movePosition).getTop();
            mRecycleView.scrollBy(0, top);
        } else {
            // 第三种:跳转位置在最后可见项之后
            mLinearLayout.scrollToPositionWithOffset(position, 0);
        }
    }

    public void updateSlideBar() {
        RecyclerView.LayoutManager layoutManager = mRecycleView.getLayoutManager();
        if (mAdapter.getItemCount() > 0) {
            int position = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            String initial = mAdapter.getItemInitialByPosition(position);
            Log.i(TAG, "updateSlideBar : FirstVisibleItemPosition = " + position + "; initial = " + initial);
            if (initial.length() > 0) {
                mSlideBar.updateCharacter(initial.substring(0, 1));
            } else {
                mSlideBar.updateCharacter("");
            }
        } else {
            Log.i(TAG, "updateSlideBar : hide");
            mSlideBar.updateCharacter("");
        }
    }

    public void hideSlidebar() {
        if (null != mSlideBar) {
            mSlideBar.setVisibility(View.GONE);
        }
    }

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

    public Handler mItemClickHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0) {
                hideInputMethod();
            }
            return false;
        }
    });

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
    }
}