package com.adayo.app.setting.reply;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;

import com.adayo.app.base.LogUtil;
import com.adayo.app.setting.R;
import com.adayo.app.setting.base.BaseFragment;
import com.adayo.app.setting.databinding.FragmentConnReplyBinding;
import com.adayo.app.setting.view.dialog.ReplyDialog;
import com.adayo.btsetting.viewmodel.CommonHighlight;

import static android.graphics.Color.parseColor;
import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_REPLY;


public class CommonReplyFragment extends BaseFragment<FragmentConnReplyBinding> implements View.OnClickListener {
    private final static String TAG = CommonReplyFragment.class.getSimpleName();
    private ReplyViewModel mViewModel;
    private ReplyDialog mReplyDialog;
    private CommonHighlight mCommonHighlight;


    @Override
    protected FragmentConnReplyBinding bindView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentConnReplyBinding.inflate(inflater, container, false);
    }

    @Override
    protected void bindData(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.bindData(arguments, savedInstanceState);
        LogUtil.debugD(TAG, "");
        mViewModel = ViewModelProviders.of(requireActivity()).get(ReplyViewModel.class);
        mCommonHighlight = ViewModelProviders.of(requireActivity()).get(CommonHighlight.class);
    }

    @Override
    protected void initView() {
        super.initView();
        mViewBinding.radioButton.setOnClickListener(this);
        mViewBinding.radioButton2.setOnClickListener(this);
        mViewBinding.radioButton3.setOnClickListener(this);
        mViewBinding.tgBtnReplySwitch.setFadeBack(false);
    }

    private void changeSelected(ViewGroup viewGroup, boolean isSelect) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childSelected = viewGroup.getChildAt(i);
            childSelected.setSelected(isSelect);

        }
    }

    @Override
    protected void initData() {
        super.initData();
        mCommonHighlight.mCommonHighlightRequest.getCommonHighlight().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer.equals(HIGHLIGHT_REPLY)) {
                    changeSelected(mViewBinding.clReply, true);
                } else {
                    changeSelected(mViewBinding.clReply, false);
                }

            }
        });
        mViewModel.mReplyRequest.getMreplySwitch().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != mViewBinding.tgBtnReplySwitch.isChecked()) {
                    if (mViewBinding.tgBtnReplySwitch.isFadeBack()) {
                        mViewBinding.tgBtnReplySwitch.setCheckedNoEvent(aBoolean);
                    } else {
                        mViewBinding.tgBtnReplySwitch.setCheckedImmediatelyNoEvent(aBoolean);
                    }
                }
                if (mViewBinding.tgBtnReplySwitch.isChecked()) {
                    mViewBinding.radioButton.setTextColor(parseColor("#ffffff"));
                    mViewBinding.radioButton2.setTextColor(parseColor("#ffffff"));
                    radioListener();
                } else {
                    mViewBinding.radioButton.setTextColor(parseColor("#61ffffff"));
                    mViewBinding.radioButton2.setTextColor(parseColor("#61ffffff"));
                    cancelRadioListener();
                }

            }
        });
        mViewModel.mReplyRequest.getMreplyEdit().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (!s.equals(mViewBinding.radioButton3.getText())) {
                    mViewBinding.radioButton3.setText(s);
                }
            }
        });
        mViewModel.mReplyRequest.getMreplyButton().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean b) {
                LogUtil.debugD(TAG);
                if (b) {
                    if (!mViewBinding.radioButton.isChecked()) {
                        mViewBinding.radioButton.setChecked(true);
                    }
                    mViewBinding.radioButton2.setChecked(false);

                } else {
                    if (!mViewBinding.radioButton2.isChecked()) {
                        mViewBinding.radioButton2.setChecked(true);
                    }
                    mViewBinding.radioButton.setChecked(false);
                }
            }
        });

    }

    @Override
    protected void initEvent() {
        super.initEvent();
        LogUtil.debugD(TAG);

        mViewBinding.tgBtnReplySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LogUtil.debugD(TAG);
                if (compoundButton.isPressed()) {
                    mCommonHighlight.mCommonHighlightRequest.requestHighlightModule(HIGHLIGHT_REPLY);
                    mViewModel.mReplyRequest.requestEnableState(b);
                    if (b) {
                        mViewBinding.radioButton.setTextColor(parseColor("#ffffff"));
                        mViewBinding.radioButton2.setTextColor(parseColor("#ffffff"));
                        radioListener();
                    } else {
                        mViewBinding.radioButton.setTextColor(parseColor("#61ffffff"));
                        mViewBinding.radioButton2.setTextColor(parseColor("#61ffffff"));
                        cancelRadioListener();
                    }
                }
            }
        });
    }


    private void radioListener() {
        LogUtil.debugD(TAG);
        mViewBinding.radioButton3.setEnabled(true);
        mViewBinding.radioButton.setEnabled(true);
        mViewBinding.radioButton2.setEnabled(true);
    }

    private void cancelRadioListener() {
        LogUtil.debugD(TAG);
        mViewBinding.radioButton3.setEnabled(false);
        mViewBinding.radioButton.setEnabled(false);
        mViewBinding.radioButton2.setEnabled(false);


    }

    @Override
    public void onClick(View view) {
        LogUtil.debugD(TAG);
        if (view.isPressed()) {
            mCommonHighlight.mCommonHighlightRequest.requestHighlightModule(HIGHLIGHT_REPLY);
            switch (view.getId()) {
                case R.id.radioButton3:
                    mReplyDialog = ReplyDialog.newInstance(mViewModel.mReplyRequest.getMreplyEdit().getValue());
                    mReplyDialog.setWindowType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY).setLayout((int) (getResources().getDimension(R.dimen.popup_big_width)), (int) (getResources().getDimension(R.dimen.popup_big_height)))
                            .setOffsetY((int) (getResources().getDimension(R.dimen.popup_big_y_totop)))
                            .setGravity(Gravity.TOP)
                            .setOnPositiveButtonClickListener((dialogFragment, view1, object) -> {
                                LogUtil.debugD(TAG);
                                if (object != null) {
                                    String devnm = (String) object;
                                    mViewModel.mReplyRequest.requestEdit(devnm);
                                    mViewBinding.radioButton3.setText(devnm);
                                }
                                })
                            .show(requireFragmentManager(), getClass().getSimpleName());
                    break;
                case R.id.radioButton:
                    mViewModel.mReplyRequest.requestReplyButton(true);
                    mViewBinding.radioButton2.setChecked(false);
                    break;
                case R.id.radioButton2:
                    mViewModel.mReplyRequest.requestReplyButton(false);
                    mViewBinding.radioButton.setChecked(false);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewBinding.tgBtnReplySwitch.setFadeBack(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mViewBinding.radioButton3.setText(mViewModel.mReplyRequest.getMreplyEdit().getValue());
    }
}
