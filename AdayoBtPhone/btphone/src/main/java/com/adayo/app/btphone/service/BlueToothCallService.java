package com.adayo.app.btphone.service;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

import com.adayo.app.btphone.R;
import com.adayo.app.btphone.constant.Constant;
import com.adayo.app.btphone.manager.DialogManager;
import com.adayo.app.btphone.view.FloatView;
import com.adayo.bpresenter.bluetooth.services.BluetoothService;
import com.adayo.common.bluetooth.constant.BtDef;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;
import com.adayo.proxy.system.aaop_systemservice.AAOP_SystemServiceManager;
import com.adayo.proxy.system.aaop_systemservice.contants.AAOP_SystemServiceContantsDef;

import org.json.JSONException;
import org.json.JSONObject;

public class BlueToothCallService extends BluetoothService {

    private static final String TAG = Constant.TAG + BlueToothCallService.class.getSimpleName();

    private WindowManager.LayoutParams mWindowParams;
    private FloatView mCallView;
    private Dialog mDialog;
    private Window mWindow;

    public final static int CALL_WINDOW_X_MIN = 55;
    public final static int CALL_WINDOW_X_MAX = 988;
    public final static int CALL_WINDOW_Y_MIN = 110;
    public final static int CALL_WINDOW_Y_MAX = 538;

    private final static int SHAREDATA_ANSWER_BY_KEY = 153;

    Handler handler = new Handler(Looper.getMainLooper());
    Handler mhandler = new Handler(Looper.getMainLooper());
    Runnable runnable ;

    public BlueToothCallService() {
    }

    @Override
    public int getCallViewType() {
        Log.i(TAG,"getCallViewType");
        return BtDef.CALL_VIEW_TYPE_FLOATVIEW;
    }

    @Override
    public void showCallView() {
        Log.i(TAG,"showCallView");
        showWindow();
    }

    @Override
    public void hideCallView() {
        Log.i(TAG,"hideCallView");
        removeWindow();
    }

    @Override
    public void initFloatView() {
        Log.i(TAG,"initFloatView");
        createWindow();
        registerAnswerbyKey();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeWindow();
        mWindowParams = null;
        unregisterAnswerbyKey();
    }

    private void createWindow() {
        Log.i(TAG,"createWindow");
        mWindowParams = new WindowManager.LayoutParams();
//        mWindowParams.gravity = Gravity.TOP;
        mWindowParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowParams.x = 444;
        mWindowParams.y = 0;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.type = 2059;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    }

    int dismissDialogState = -1;
    private void removeWindow() {
        Log.i(TAG,"removeWindow");
        dismissDialogState = 0;
        runnable = new Runnable() {
            @Override
            public void run() {
                dismissDialogAnimation();
            }
        };
        mhandler.postDelayed(runnable,500);

//        mDialog = null;
//        mCallView = null;
        mWindow = null;
    }

    private void showWindow(){
        Log.i(TAG,"showWindow");
        if(null != runnable){
            if(null != runnable){
                mhandler.removeCallbacks(runnable);
                runnable = null;
            }
            DialogManager.getInstance().dismissCallDialog();
            mCallView = null;
        }
        //解除关屏
        try{
            AAOP_SystemServiceManager.getInstance().setScreenState(AAOP_SystemServiceContantsDef.AAOP_SCREEN_STATUS.AAOP_SCREEN_ON);
        }catch (RemoteException e){
            e.printStackTrace();
        }
        if(null != mWindowParams){
            mWindowParams.x = 425;
            mWindowParams.y = 727;
            mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            mWindowParams.height = 228;
            if(null == mCallView){
                mCallView = new FloatView(this);
            }
            mCallView.setOnUpdateViewListener(new OnUpdateViewListener() {
                @Override
                public void onMoveView(int x, int y) {
                    int tmpX = x;
                    int tmpY = y;
                    if(mWindow != null && mWindowParams!=null) {
//                        if(tmpX < CALL_WINDOW_X_MIN) tmpX = CALL_WINDOW_X_MIN;
//                        if(tmpX > CALL_WINDOW_X_MAX) tmpX = CALL_WINDOW_X_MAX;
                        if(tmpY < CALL_WINDOW_Y_MIN) {
                            tmpY = CALL_WINDOW_Y_MIN;
                        }
//                        if(tmpY > CALL_WINDOW_Y_MAX) tmpY = CALL_WINDOW_Y_MAX;
                        mWindowParams.x = tmpX;
                        mWindowParams.y = tmpY;
                        mWindow.setAttributes(mWindowParams);
                        mWindow.setBackgroundDrawableResource(R.color.vifrification);
                        mWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        mWindow.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                    }
                }

                @Override
                public void onKeyboardOpen(boolean open) {
                    if(mWindow != null && mWindowParams!=null) {
                        if(open) {
                            mWindowParams.y = 129;
                            mWindowParams.height = 826;
                        } else {
                            mWindowParams.y = 727;
                            mWindowParams.height = 228;
                        }
                        mWindow.setAttributes(mWindowParams);
                        mWindow.setBackgroundDrawableResource(R.color.vifrification);
                        mWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        mWindow.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                    }
                }
            });
            DialogManager.getInstance().getCallDialog(this).setContentView(mCallView);
            AAOP_HSkin.getWindowViewManager().addWindowView(mCallView);
            mWindow = DialogManager.getInstance().getCallDialog(this).getWindow();
            mWindow.setAttributes(mWindowParams);
            mWindow.setBackgroundDrawableResource(R.color.vifrification);
            mWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            mWindow.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            showDialogAnimation();
        }
    }

    public interface OnUpdateViewListener {
        void onMoveView(int x, int y);
        void onKeyboardOpen(boolean open);
    }

    /**
     * showDialogAnimation
     * 淡入+平移
     */
    public void showDialogAnimation(){
        Log.d("CommonDialog", "dismissDialogState = "+dismissDialogState);
        if(dismissDialogState == 0){
            mhandler.removeCallbacks(runnable);
            DialogManager.getInstance().dismissCallDialog();
        }
        DialogManager.getInstance().showCallDialog();
        Constant.CALL_DIALOG_SHOW_STATE = Constant.CALL_DIALOG_SHOW;
        TranslateAnimation animTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f, Animation.RELATIVE_TO_SELF,1.0f,Animation.RELATIVE_TO_SELF,0f);
        animTranslate.setDuration(1000);
        animTranslate.setFillAfter(true);

        AlphaAnimation animAlpha = new AlphaAnimation(0f, 1.0f);
        animAlpha.setDuration(500);
        animAlpha.setFillAfter(true);

        AnimationSet setAnim = new AnimationSet(true);
        setAnim.addAnimation(animTranslate);
        setAnim.addAnimation(animAlpha);
        mCallView.getFloatView().startAnimation(setAnim);
    }


    /**
     * 获取当前Dialog坐标
     */
    /*int x =0;
    int y =0;
    private void getCurrentPosition(){
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        x = params.x;
        y = params.y;
    }*/



    /**
     * dismissDialogAnimation
     * 淡出+平移
     */
    public void dismissDialogAnimation(){
        Log.d("CommonDialog", "dismissDialogAnimation");
        if(null != mCallView){
            mCallView.getFloatView().clearAnimation();
        }
        TranslateAnimation animTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f, Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,1.0f);
        animTranslate.setDuration(1000);
        animTranslate.setFillAfter(true);

        AlphaAnimation animAlpha = new AlphaAnimation(1.0f, 0f);
        animAlpha.setDuration(500);
        animAlpha.setFillAfter(true);

        AnimationSet setAnim = new AnimationSet(true);
        setAnim.addAnimation(animTranslate);
        setAnim.addAnimation(animAlpha);

        setAnim.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mCallView = null;
                runnable = null;
                dismissDialogState = 1;
                DialogManager.getInstance().dismissCallDialog();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        if(null != mCallView){
            mCallView.getFloatView().startAnimation(setAnim);
        }
    }

    private void registerAnswerbyKey(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                ShareDataManager.getShareDataManager().registerShareDataListener(SHAREDATA_ANSWER_BY_KEY, mShareDataListener);
            }
        });
    }

    private void unregisterAnswerbyKey(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                ShareDataManager.getShareDataManager().unregisterShareDataListener(SHAREDATA_ANSWER_BY_KEY, mShareDataListener);
            }
        });
    }

    private IShareDataListener mShareDataListener = new IShareDataListener() {
        @Override
        public void notifyShareData(int i, String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                String callAnswer = jsonObject.getString("CALL_ANSWER");
                Log.d("BlueToothCallService", "callAnswer = "+callAnswer);
                if(null != callAnswer && !callAnswer.equals("")){
                    if(null != mCallView){
                        mCallView.notifyAnswerByKey(callAnswer);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
