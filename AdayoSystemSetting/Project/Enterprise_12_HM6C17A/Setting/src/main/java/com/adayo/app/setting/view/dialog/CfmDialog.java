package com.adayo.app.setting.view.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adayo.app.setting.R;
import com.adayo.app.setting.databinding.DlgCfmBinding;
import com.adayo.app.setting.model.constant.ParamConstant;
import com.adayo.app.setting.base.BaseDialogFragment;
import com.adayo.app.base.LogUtil;

import java.util.Objects;


public class CfmDialog extends BaseDialogFragment<DlgCfmBinding> {
    private final static String TAG = CfmDialog.class.getSimpleName();
    private String mTitle;
    private String mMsg;
    private String mPositive;
    private String mNegative;

    public static CfmDialog newInstance(String title, String msg, String positive, String negative) {
        Bundle arguments = new Bundle();
        CfmDialog fragment = new CfmDialog();
        arguments.putString(ParamConstant.BDL_KEY_DLG_TITLE, title);arguments.putString(ParamConstant.BDL_KEY_DLG_MSG, msg);arguments.putString(ParamConstant.BDL_KEY_DLG_POSITIVE, positive);arguments.putString(ParamConstant.BDL_KEY_DLG_NEGATIVE, negative);fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected DlgCfmBinding bindView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return DlgCfmBinding.inflate(inflater);
    }

    @Override
    protected void bindData(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.bindData(arguments, savedInstanceState);
       LogUtil.debugD(TAG,"");
        if (Objects.nonNull(arguments)) {
            mTitle = arguments.getString(ParamConstant.BDL_KEY_DLG_TITLE);
            mMsg = arguments.getString(ParamConstant.BDL_KEY_DLG_MSG);
            mPositive = arguments.getString(ParamConstant.BDL_KEY_DLG_POSITIVE);
            mNegative = arguments.getString(ParamConstant.BDL_KEY_DLG_NEGATIVE);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(838, 518);
        }
    }
    @Override
    protected void initView() {
        super.initView();
       LogUtil.debugD(TAG,"");
        if (mTitle != null) {
            mViewBinding.tvCfmTitle.setText(mTitle);
        } else{
            mViewBinding.tvCfmTitle.setVisibility(View.GONE);
        }
        if (mMsg != null) {
            mViewBinding.tvCfmMsg.setText(mMsg);
        } else{
            mViewBinding.tvCfmMsg.setVisibility(View.GONE);
        }
        if (mPositive != null) {
            mViewBinding.btnCfmPositive.setText(mPositive);
        } else{
            mViewBinding.btnCfmPositive.setVisibility(View.GONE);
        }
        if (mNegative != null) {
            mViewBinding.btnCfmNegative.setText(mNegative);
        } else{
            mViewBinding.btnCfmNegative.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        mViewBinding.btnCfmPositive.setOnClickListener(v -> {
           LogUtil.i(TAG,"btnCfmPositive");
            callOnPositiveButtonClick(v, null);
            dismiss();
        });

        mViewBinding.btnCfmNegative.setOnClickListener(v -> {
           LogUtil.i(TAG,"btnCfmNegative");
            callOnNegativeButtonClick(v, null);
            dismiss();
        });
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(this.getActivity(),R.style.TransparentStyle);
    }
}
