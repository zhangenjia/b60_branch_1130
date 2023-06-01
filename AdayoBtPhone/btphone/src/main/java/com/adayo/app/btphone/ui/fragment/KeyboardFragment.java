package com.adayo.app.btphone.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;


import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.adayo.app.btphone.R;
import com.adayo.app.btphone.callback.IBTDialCallback;
import com.adayo.app.btphone.callback.ICallViewCallback;
import com.adayo.app.btphone.constant.Constant;
import com.adayo.app.btphone.manager.BTCallLogManager;
import com.adayo.app.btphone.manager.BTCallManager;
import com.adayo.app.btphone.manager.BTDialManager;
import com.adayo.app.btphone.manager.DialogManager;
import com.adayo.app.btphone.view.KeyboardQueryResultView;
import com.adayo.common.bluetooth.bean.PeopleInfo;
import com.adayo.common.bluetooth.constant.BtDef;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class KeyboardFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = Constant.TAG + KeyboardFragment.class.getSimpleName();

    private Handler mHandler;
    private BTDialManager mBtDialManager;
    private List<PeopleInfo> peopleInfos = new ArrayList<>();

    private WindowManager.LayoutParams mWindowParams;
    private KeyboardQueryResultView queryResultView;
    private Window mWindow;
    private InputMethodManager mInputMethodMgr;

    private RelativeLayout mKeyboardRL;
    private EditText mKeyboardET;
    private ImageView mKey0;
    private ImageView mKey1;
    private ImageView mKey2;
    private ImageView mKey3;
    private ImageView mKey4;
    private ImageView mKey5;
    private ImageView mKey6;
    private ImageView mKey7;
    private ImageView mKey8;
    private ImageView mKey9;
    private ImageView mKeyStar;
    private ImageView mKeyPound;
    private ImageView mKeyCall;
    private ImageView mKeyDel;

    private static final String FACTORY = "*#445566#*";

    public KeyboardFragment() {
    }

    @SuppressLint("ValidFragment")
    public KeyboardFragment(Handler handler) {
        Log.i(TAG, "KeyboardFragment");
        mHandler = handler;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        mBtDialManager = new BTDialManager(getActivity().getApplicationContext());
        notifyMainPageUpdateDeviceName(mBtDialManager.getDeviceName());
        mBtDialManager.registerBTDial(callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keyboard, container, false);
        mKeyboardRL = view.findViewById(R.id.keyboard_rl);
        mKeyboardET = view.findViewById(R.id.keyborad_et);
        mKeyboardET.addTextChangedListener(textCallback);
        mKey0 = view.findViewById(R.id.key_0_iv);
        mKey0.setOnClickListener(this);
        mKey0.setmClickSound(1000);
        mKey1 = view.findViewById(R.id.key_1_iv);
        mKey1.setOnClickListener(this);
        mKey1.setmClickSound(1001);
        mKey2 = view.findViewById(R.id.key_2_iv);
        mKey2.setOnClickListener(this);
        mKey2.setmClickSound(1002);
        mKey3 = view.findViewById(R.id.key_3_iv);
        mKey3.setOnClickListener(this);
        mKey3.setmClickSound(1003);
        mKey4 = view.findViewById(R.id.key_4_iv);
        mKey4.setOnClickListener(this);
        mKey4.setmClickSound(1004);
        mKey5 = view.findViewById(R.id.key_5_iv);
        mKey5.setOnClickListener(this);
        mKey5.setmClickSound(1005);
        mKey6 = view.findViewById(R.id.key_6_iv);
        mKey6.setOnClickListener(this);
        mKey6.setmClickSound(1006);
        mKey7 = view.findViewById(R.id.key_7_iv);
        mKey7.setOnClickListener(this);
        mKey7.setmClickSound(1007);
        mKey8 = view.findViewById(R.id.key_8_iv);
        mKey8.setOnClickListener(this);
        mKey8.setmClickSound(1008);
        mKey9 = view.findViewById(R.id.key_9_iv);
        mKey9.setOnClickListener(this);
        mKey9.setmClickSound(1009);
        mKeyStar = view.findViewById(R.id.key_star_iv);
        mKeyStar.setOnClickListener(this);
        mKeyStar.setmClickSound(1000);
        mKeyPound = view.findViewById(R.id.key_pound_iv);
        mKeyPound.setOnClickListener(this);
        mKeyPound.setmClickSound(1000);
        mKeyCall = view.findViewById(R.id.key_call_iv);
        mKeyCall.setOnClickListener(this);
        mKeyDel = view.findViewById(R.id.key_del_iv);
        mKeyDel.setOnClickListener(this);
        mKeyDel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mKeyboardET.setText("");
                return true;
            }
        });
        initWindow();
        mInputMethodMgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (mInputMethodMgr.isActive()) {
            mInputMethodMgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        switch (view.getId()) {
            case R.id.key_del_iv:
                deleteChar(mKeyboardET);
                break;
            case R.id.key_call_iv:
                String number = mKeyboardET.getText().toString();
                //小健哥要求输入特殊号码时发送一个广播
                if (FACTORY.equals(number)) {
                    Intent intent = new Intent();
                    intent.setAction("com.adayo.bluetooth.call");
                    getContext().sendBroadcast(intent);
                }
                if ("".equals(number)) {
                    number = BTCallLogManager.getInstance(getActivity().getApplicationContext()).getLastRecentCall();
                    if (null != number && !"".equals(number)) {
                        mKeyboardET.getText().append(number);
                    }
                    return;
                }
                if (null != mBtDialManager) {
                    deleteAllChar(mKeyboardET);
                    Log.i(TAG, "dialCall number = " + number);
                    mBtDialManager.dialCall(number);
                }
                break;
            case R.id.key_0_iv:
                addChar(mKeyboardET, "0");
                break;
            case R.id.key_1_iv:
                addChar(mKeyboardET, "1");
                break;
            case R.id.key_2_iv:
                addChar(mKeyboardET, "2");
                break;
            case R.id.key_3_iv:
                addChar(mKeyboardET, "3");
                break;
            case R.id.key_4_iv:
                addChar(mKeyboardET, "4");
                break;
            case R.id.key_5_iv:
                addChar(mKeyboardET, "5");
                break;
            case R.id.key_6_iv:
                addChar(mKeyboardET, "6");
                break;
            case R.id.key_7_iv:
                addChar(mKeyboardET, "7");
                break;
            case R.id.key_8_iv:
                addChar(mKeyboardET, "8");
                break;
            case R.id.key_9_iv:
                addChar(mKeyboardET, "9");
                break;
            case R.id.key_star_iv:
                addChar(mKeyboardET, "*");
                break;
            case R.id.key_pound_iv:
                addChar(mKeyboardET, "#");
                break;
            default:
                break;
        }
    }


    public void addChar(EditText editText, String s) {
        int index = editText.getSelectionStart();
        Editable editable = editText.getText();
        editable.insert(index, s);
        editText.requestFocus();
    }

    public void deleteChar(EditText editText) {
        int index = editText.getSelectionStart();
        if (index == 0) {
            return;
        }
        Editable editable = editText.getText();
        editable.delete(index - 1, index);
        editText.requestFocus();
    }

    public static void deleteAllChar(EditText editText) {
        int index = editText.getSelectionStart();
        if (index == 0) {
            return;
        }
        Editable editable = editText.getText();
        editable.clear();
        editText.requestFocus();
    }

    private IBTDialCallback callback = new IBTDialCallback() {

        @Override
        public void updateContacts(List<PeopleInfo> list) {
            Log.i(TAG, "updateContacts list.size = " + list.size());
            if (DialogManager.getInstance().keyboardDialogIsShow()) {
                queryResultView.setData(list);
            }
        }

        @Override
        public void updateActiveDeviceChanged(String address, String name, boolean isMutiDev) {
            notifyMainPageUpdateDeviceName(name);
        }
    };

    private TextWatcher textCallback = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(editable.length() > 1){
                if(!BTCallLogManager.getInstance(getContext()).getNoCallLogInfo()){
                    DialogManager.getInstance().showKeyboardDialog();
                    if (null != mBtDialManager) {
                        mBtDialManager.searchContacts(editable.toString(), BtDef.PBAP_STORAGE_CALL_LOGS);
                    }
                }
            }else{
                if(null != queryResultView){
                    queryResultView.clearData();
                }
                DialogManager.getInstance().dismissKeyboardDialog();
            }
        }
    };

    private void initWindow() {
        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.gravity = Gravity.TOP | Gravity.START;
        mWindowParams.x = 50;
        mWindowParams.y = 213;
        mWindowParams.width = 1209;
        mWindowParams.height = 751;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.windowAnimations = android.R.style.Animation_Dialog;
        queryResultView = new KeyboardQueryResultView(getActivity(), mBtDialManager);
        DialogManager.getInstance().getKeyboardDialog(getActivity()).setContentView(queryResultView);
        mWindow = DialogManager.getInstance().getKeyboardDialog(getActivity()).getWindow();
        mWindow.setAttributes(mWindowParams);
        onSystemUIVisibility(true);
    }

    private void onSystemUIVisibility(boolean visibility) {
        Log.d(TAG, "onTabItemSelected():visibility = " + visibility);

        View decorView = mWindow.getDecorView();
        if (visibility) {// 非全屏
            Log.i(TAG, "设置SystemUI 显示");
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);//导航栏不占位

            mWindow.setNavigationBarColor(Color.TRANSPARENT);
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//状态栏透明
        } else {// 全屏
            Log.i(TAG, "设置SystemUI 隐藏");
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);// 隐藏导航栏、状态栏
        }
    }

    public void notifyMainPageUpdateDeviceName(String deviceName) {
        if (null != mHandler) {
            Message message = mHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("btDeviceName", deviceName);
            message.setData(bundle);
            mHandler.sendMessage(message);
        }
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
        //设置中英文后，由于fragment没有重新创建，mhandler是空，所以需要重新获取下设备名称
        notifyMainPageUpdateDeviceName(mBtDialManager.getDeviceName());
    }

    public void notifyBTNotConnect() {
        if (null != mKeyboardET) {
            mKeyboardET.setText("");
        }
        DialogManager.getInstance().dismissKeyboardDialog();
    }

    public void notifyBTPhoneConnectState(boolean connState){
        if(null != mKeyboardRL){
            if(connState){
                Log.i(TAG, "notifyBTPhoneConnectState true");
                mKeyboardRL.setAlpha(1f);
                mKey0.setClickable(true);
                mKey1.setClickable(true);
                mKey2.setClickable(true);
                mKey3.setClickable(true);
                mKey4.setClickable(true);
                mKey5.setClickable(true);
                mKey6.setClickable(true);
                mKey7.setClickable(true);
                mKey8.setClickable(true);
                mKey9.setClickable(true);
                mKeyStar.setClickable(true);
                mKeyPound.setClickable(true);
                mKeyCall.setClickable(true);
                mKeyDel.setClickable(true);
            }else {
                Log.i(TAG, "notifyBTPhoneConnectState false");
                mKeyboardRL.setAlpha(0.5f);
                mKey0.setClickable(false);
                mKey1.setClickable(false);
                mKey2.setClickable(false);
                mKey3.setClickable(false);
                mKey4.setClickable(false);
                mKey5.setClickable(false);
                mKey6.setClickable(false);
                mKey7.setClickable(false);
                mKey8.setClickable(false);
                mKey9.setClickable(false);
                mKeyStar.setClickable(false);
                mKeyPound.setClickable(false);
                mKeyCall.setClickable(false);
                mKeyDel.setClickable(false);
            }
        }
    }


}