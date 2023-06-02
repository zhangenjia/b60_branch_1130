
package com.adayo.app.setting.view.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.adayo.app.base.LogUtil;
import com.adayo.app.setting.R;
import com.adayo.app.setting.base.BaseFragment;
import com.adayo.app.setting.databinding.FragmentVoiceBinding;
import com.adayo.app.setting.highlight.VoiceHighlight;
import com.adayo.app.setting.view.dialog.WakeWordNameDialog;
import com.adayo.app.setting.view.popwindow.CommonDialog;
import com.adayo.app.setting.view.popwindow.CommonVoiceDialog;
import com.adayo.app.setting.view.popwindow.VoiceWakeFreeEleWindow;
import com.adayo.app.setting.viewmodel.fragment.VoiceViewModel;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.flyco.customtablayout.listener.OnTabSelectListener;

import java.lang.reflect.Method;
import java.util.Objects;

import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_NO;
import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_VOICE;


public class VoiceFragment extends BaseFragment<FragmentVoiceBinding> {
    private final static String TAG = VoiceFragment.class.getSimpleName();
    private VoiceViewModel mViewModel;
    private WakeWordNameDialog mWakeWordNameDialog;
    private VoiceWakeFreeEleWindow mVoiceWakeFreeEleWindow;
    private VoiceHighlight mVoiceHighlight;
    private CommonDialog mCommonDialog;
    private CommonVoiceDialog mCommonVoiceDialog;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {

            LogUtil.debugD(TAG,"message = "+message.what);
            if (mViewBinding.sbVoiceAssistantSwitch.getCurrentTab()==0&&!mViewModel.mVoiceRequest.getWakeRoleLiveData().getValue().equals("bjbj")) {
                mViewModel.mVoiceRequest.requestWakeRole("bjbj");
            }else if(mViewBinding.sbVoiceAssistantSwitch.getCurrentTab()==1&&!mViewModel.mVoiceRequest.getWakeRoleLiveData().getValue().equals("libai")){
                mViewModel.mVoiceRequest.requestWakeRole("libai");
            }else if(mViewBinding.sbVoiceAssistantSwitch.getCurrentTab()==2&&!mViewModel.mVoiceRequest.getWakeRoleLiveData().getValue().equals("daji") ){
                mViewModel.mVoiceRequest.requestWakeRole("daji");
            }
        }
    };

    public static VoiceFragment newInstance(String param1, String param2) {
        VoiceFragment fragment = new VoiceFragment();
        Bundle args = new Bundle();
        args.putString(BDL_KEY_PARAM1, param1);
        args.putString(BDL_KEY_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected FragmentVoiceBinding bindView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentVoiceBinding.inflate(inflater, container, false);
    }

    @Override
    protected void bindData(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.bindData(arguments, savedInstanceState);
        LogUtil.debugD(TAG, "");
        mViewModel = ViewModelProviders.of(requireActivity()).get(VoiceViewModel.class);
        mVoiceHighlight = ViewModelProviders.of(requireActivity()).get(VoiceHighlight.class);

    }

    private void changeSelected(ViewGroup viewGroup, boolean isSelect) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childSelected = viewGroup.getChildAt(i);
            childSelected.setSelected(isSelect);

        }
    }

    @Override
    protected void initView() {
        super.initView();
        LogUtil.d(TAG, "");
        mViewBinding.sbVoiceAssistantSwitch.setTabData(new String[]{getString(R.string.voice_assistant_info_beijing), getString(R.string.voice_assistant_info_libai), getString(R.string.voice_assistant_info_daji)});
        mViewBinding.tgBtnVoiceWakeFreeSwitch.setFadeBack(false);
        mViewBinding.tgBtnVoiceWakeSwitch.setFadeBack(false);

        mViewBinding.sbContinuousDialogue.setFadeBack(false);
        mViewBinding.stlContinuousLengthOfTime.setTabData(new String[]{getString(R.string.voice_4), getString(R.string.voice_5), getString(R.string.voice_6)});
        mViewBinding.stlConversationScreenDisplay.setTabData(new String[]{getString(R.string.voice_7), getString(R.string.voice_8)});
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewBinding.tgBtnVoiceWakeFreeSwitch.setFadeBack(true);
        mViewBinding.tgBtnVoiceWakeSwitch.setFadeBack(true);

        mViewBinding.sbContinuousDialogue.setFadeBack(true);
        mViewModel.mVoiceRequest.initDuplexEnable();
        mViewModel.mVoiceRequest.initDuplexActiveTime();
        mViewModel.mVoiceRequest.initShowValidResult();
    }

    @Override
    protected void initData() {
        super.initData();

        mViewModel.mVoiceRequest.getWakeLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                LogUtil.debugD(TAG, "VoiceWake UI");
                if (mViewBinding.tgBtnVoiceWakeSwitch.isFadeBack()) {
                    mViewBinding.tgBtnVoiceWakeSwitch.setCheckedNoEvent(aBoolean);
                } else {
                    mViewBinding.tgBtnVoiceWakeSwitch.setCheckedImmediatelyNoEvent(aBoolean);
                }
                if (false == aBoolean) {
                    mViewBinding.groupVoiceWake.setVisibility(View.GONE);
                    mViewBinding.tvVoiceWakeOff.setVisibility(View.VISIBLE);
                    mViewBinding.ivVoiceWakeOff.setVisibility(View.VISIBLE);
                } else {
                    mViewBinding.groupVoiceWake.setVisibility(View.VISIBLE);
                    mViewBinding.tvVoiceWakeOff.setVisibility(View.GONE);
                    mViewBinding.ivVoiceWakeOff.setVisibility(View.GONE);
                }
            }
        });

        mViewModel.mVoiceRequest.getWakeWordLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                LogUtil.debugD(TAG, "VoiceWakeWord UI");

                mViewBinding.tvVoiceWakeWord.setText(s);
            }
        });
        mViewModel.mVoiceRequest.getWakeWordWrongLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                LogUtil.i(TAG, "WakeWordWrong UI = " + s);
                if (s != null) {
                    if (mCommonVoiceDialog == null) {
                        LogUtil.i(TAG, "mCommonDialog =null ");
                        mCommonVoiceDialog = CommonVoiceDialog.getCommonDialog(getActivity());
                    }
                    mCommonVoiceDialog.initStringView(s, R.drawable.icon_failure);
                    if (!mCommonVoiceDialog.isShowing()) {
                        LogUtil.i(TAG, "mCommonDialog show ");
                        mCommonVoiceDialog.show();
                    }
                }
            }
        });

        mViewModel.mVoiceRequest.getWakeRoleLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                LogUtil.debugD(TAG, "VoiceAssistant UI = " + s);
                Message message=new Message();
                message.what=1;
                mHandler.sendMessageDelayed(message,5000);
                switch (s) {
                    case "nhbj":
                    case "bjbj":
                        if (mViewBinding.sbVoiceAssistantSwitch.getCurrentTab() != 0) {
                            mViewBinding.sbVoiceAssistantSwitch.setSelectTab(0);

                        }
                        break;
                    case "libai":
                        if (mViewBinding.sbVoiceAssistantSwitch.getCurrentTab() != 1) {
                            mViewBinding.sbVoiceAssistantSwitch.setSelectTab(1);

                        }
                        break;
                    case "daji":
                        if (mViewBinding.sbVoiceAssistantSwitch.getCurrentTab() != 2) {
                            mViewBinding.sbVoiceAssistantSwitch.setSelectTab(2);
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        mViewModel.mVoiceRequest.getWakeFreeEleLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                LogUtil.debugD(TAG, "BtnVoiceWakeFreeSwitch UI = " + aBoolean);
                if (mViewBinding.tgBtnVoiceWakeFreeSwitch.isFadeBack()) {
                    mViewBinding.tgBtnVoiceWakeFreeSwitch.setCheckedNoEvent(aBoolean);
                } else {
                    mViewBinding.tgBtnVoiceWakeFreeSwitch.setCheckedImmediatelyNoEvent(aBoolean);
                }
            }
        });
        mVoiceHighlight.mVoiceHighlightRequest.getVoiceHighlight().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer.equals(HIGHLIGHT_VOICE)) {
                    changeSelected(mViewBinding.clVoice, true);
                } else if (integer.equals(HIGHLIGHT_NO)) {
                    changeSelected(mViewBinding.clVoice, false);
                }
            }
        });

        mViewModel.mVoiceRequest.getIsDuplexEnableLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                mViewBinding.sbContinuousDialogue.setCheckedImmediatelyNoEvent(aBoolean);
                if (aBoolean) {
                    setStlEnable((LinearLayout) mViewBinding.stlContinuousLengthOfTime.getChildAt(0), 3, true);
                    setStlEnable((LinearLayout) mViewBinding.stlConversationScreenDisplay.getChildAt(0), 2, true);
                    mViewBinding.stlContinuousLengthOfTime.setAlpha(1);
                    mViewBinding.stlConversationScreenDisplay.setAlpha(1);
                    mViewBinding.tvContinuousLengthOfTime.setAlpha(1);
                    mViewBinding.tvConversationScreenDisplay.setAlpha(1);
                } else {
                    LogUtil.debugD(TAG, " stlContinuousLengthOfTime = ");
                    setStlEnable((LinearLayout) mViewBinding.stlContinuousLengthOfTime.getChildAt(0), 3, false);
                    setStlEnable((LinearLayout) mViewBinding.stlConversationScreenDisplay.getChildAt(0), 2, false);
                    mViewBinding.stlContinuousLengthOfTime.setAlpha((float) 0.38);
                    mViewBinding.stlConversationScreenDisplay.setAlpha((float) 0.38);
                    mViewBinding.tvContinuousLengthOfTime.setAlpha((float) 0.38);
                    mViewBinding.tvConversationScreenDisplay.setAlpha((float) 0.38);
                }
            }
        });

        mViewModel.mVoiceRequest.getDuplexActiveTimeLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s == null) {
                    LogUtil.w(TAG, "DuplexActiveTime IS NULL");
                    return;
                }
                switch (s) {
                    case "15":
                        mViewBinding.stlContinuousLengthOfTime.setSelectTab(0);
                        break;
                    case "30":
                        mViewBinding.stlContinuousLengthOfTime.setSelectTab(1);
                        break;
                    case "60":
                        mViewBinding.stlContinuousLengthOfTime.setSelectTab(2);
                        break;
                    default:
                        break;
                }
            }
        });

        mViewModel.mVoiceRequest.getIsShowValidLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                mViewBinding.stlConversationScreenDisplay.setSelectTab(aBoolean ? 1 : 0);
            }
        });

    }

    private void setStlEnable(LinearLayout linearLayout, int number, boolean enable) {
        for (int i = 0; i < number; ++i) {
            View tabView = linearLayout.getChildAt(i);
            tabView.setEnabled(enable);
        }
    }

    @Override
    protected void initEvent() {
        super.initEvent();

        mViewBinding.tgBtnVoiceWakeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LogUtil.debugD(TAG, "UI  tgBtnVoiceWakeSwitch =" + b);
                if (compoundButton.isPressed()) {
                    mViewModel.mVoiceRequest.requestWake(b);
                    mVoiceHighlight.mVoiceHighlightRequest.requestHighlightModule(HIGHLIGHT_VOICE);
                }
            }
        });

        mViewBinding.btnVoiceWakeWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.debugD(TAG, "UI btnVoiceWakeWord");
                if (view.isPressed()) {
                    mVoiceHighlight.mVoiceHighlightRequest.requestHighlightModule(HIGHLIGHT_VOICE);
                    mWakeWordNameDialog = WakeWordNameDialog.newInstance(mViewModel.mVoiceRequest.getWakeWordLiveData().getValue());
                    mWakeWordNameDialog.setWindowType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                            .setLayout((int) (getResources().getDimension(R.dimen.popup_big_width)), (int) (getResources().getDimension(R.dimen.popup_big_height)))
                            .setGravity(Gravity.TOP)
                            .setOffsetY((int) (getResources().getDimension(R.dimen.popup_big_y_totop)))
                            .setOnPositiveButtonClickListener((dialogFragment, view1, object) -> {
                                mViewModel.mVoiceRequest.requestWakeWord((String) object);
                            })
                            .show(requireFragmentManager(), getClass().getSimpleName());
                }
            }
        });

        mViewBinding.sbVoiceAssistantSwitch.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int progress, boolean fromUser) {
                if (fromUser) {
                    mVoiceHighlight.mVoiceHighlightRequest.requestHighlightModule(HIGHLIGHT_VOICE);
                    LogUtil.debugD(TAG, "UI mVoiceAssistant = " + progress);
                    switch (progress) {
                        case 0:
if(!mHandler.hasMessages(1)){
                              LogUtil.debugD(TAG,"requestWakeRole(\"bjbj\")");
                              mViewModel.mVoiceRequest.requestWakeRole("bjbj");
                          }

                            break;
                        case 1:
                            if(!mHandler.hasMessages(1)){
                                LogUtil.debugD(TAG,"requestWakeRole(\"libai\")");
                                mViewModel.mVoiceRequest.requestWakeRole("libai");
                            }
                            break;
                        case 2:

                            if(!mHandler.hasMessages(1)){
                                LogUtil.debugD(TAG,".requestWakeRole(\"daji\")");
                                mViewModel.mVoiceRequest.requestWakeRole("daji");
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        });

        mViewBinding.tgBtnVoiceWakeFreeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LogUtil.debugD(TAG, "UI tgBtnVoiceWakeFreeSwitch = " + b);
                if (compoundButton.isPressed()) {
                    mViewModel.mVoiceRequest.requestWakeFreeEle(b);
                    mVoiceHighlight.mVoiceHighlightRequest.requestHighlightModule(HIGHLIGHT_VOICE);
                }
            }
        });

        mViewBinding.tvVoiceWakeFreeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.debugD(TAG, "UI tvVoiceWakeFreeInfo ");
                if (view.isPressed()) {
                    mVoiceHighlight.mVoiceHighlightRequest.requestHighlightModule(HIGHLIGHT_VOICE);
                    mVoiceWakeFreeEleWindow = new VoiceWakeFreeEleWindow(getAppContext());
                    mVoiceWakeFreeEleWindow.showAtLocation(view, Gravity.TOP, 0, (int) (getResources().getDimension(R.dimen.popup_big_y_totop)));
                }
            }
        });

        mViewBinding.sbContinuousDialogue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isPressed()) {
                    mViewModel.mVoiceRequest.requestDuplexEnabled(b);
                    mVoiceHighlight.mVoiceHighlightRequest.requestHighlightModule(HIGHLIGHT_VOICE);
                }
            }
        });
        mViewBinding.stlContinuousLengthOfTime.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int i, boolean b) {
                if (!b) {
                    return;
                }
                switch (i) {
                    case 0:
                        mViewModel.mVoiceRequest.requestDuplexActiveTime("15");
                        break;
                    case 1:
                        mViewModel.mVoiceRequest.requestDuplexActiveTime("30");
                        break;
                    case 2:
                        mViewModel.mVoiceRequest.requestDuplexActiveTime("60");
                        break;
                    default:
                        break;
                }
                mVoiceHighlight.mVoiceHighlightRequest.requestHighlightModule(HIGHLIGHT_VOICE);
            }
        });

        mViewBinding.stlConversationScreenDisplay.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int i, boolean b) {
                if (!b) {
                    return;
                }
                mVoiceHighlight.mVoiceHighlightRequest.requestHighlightModule(HIGHLIGHT_VOICE);
                if (i == 0) {
                    mViewModel.mVoiceRequest.requestDuplexValidResult(false);
                } else {
                    mViewModel.mVoiceRequest.requestDuplexValidResult(true);
                }

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.debugD(TAG, "");
        mVoiceHighlight.mVoiceHighlightRequest.requestHighlightModule(HIGHLIGHT_NO);

    }

    @Override
    protected void hideView() {
        super.hideView();
        LogUtil.debugD(TAG, "");
        if (Objects.nonNull(mWakeWordNameDialog)) {
            mWakeWordNameDialog.dismiss();
        }
    }

    @Override
    protected void freeEvent() {
        super.freeEvent();
        mCommonDialog = null;
        mViewModel.mVoiceRequest.getWakeWordWrongLiveData().setValue(null);
    }

    @Override
    protected void freeData() {
        super.freeData();
    }

    @Override
    protected void freeView() {
        super.freeView();
    }

    @Override
    public void onHiddenChanged(boolean isHidden) {
        super.onHiddenChanged(isHidden);
        mVoiceHighlight.mVoiceHighlightRequest.requestHighlightModule(HIGHLIGHT_NO);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mViewBinding.stlConversationScreenDisplay.setTabData(new String[]{getString(R.string.voice_7), getString(R.string.voice_8)});
        AAOP_HSkin.getInstance().refreshLanguage();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler=null;
    }
}