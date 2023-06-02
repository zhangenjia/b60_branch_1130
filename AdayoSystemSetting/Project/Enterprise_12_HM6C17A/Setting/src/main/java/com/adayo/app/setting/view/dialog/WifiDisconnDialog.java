package com.adayo.app.setting.view.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.adayo.app.setting.R;
import com.adayo.app.setting.databinding.DialogConfirmThreeBinding;
import com.adayo.app.setting.base.BaseDialogFragment;
import com.adayo.app.base.LogUtil;


public class WifiDisconnDialog extends BaseDialogFragment<DialogConfirmThreeBinding> {
    private final static String TAG = WifiDisconnDialog.class.getSimpleName();
    public static WifiDisconnDialog newInstance() {
        return new WifiDisconnDialog();
    }

    @Override
    protected DialogConfirmThreeBinding bindView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return DialogConfirmThreeBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        super.initView();
       LogUtil.debugD(TAG,"");
        mViewBinding.tvCfmTitle.setText(R.string.conn_wifi_disconn_dlg_title);
        mViewBinding.tvCfmMsg.setText(R.string.conn_wifi_disconn_dlg_msg);
        mViewBinding.btnCfmPositive.setText(R.string.conn_wifi_disconn_dlg_neutral_btn);
        mViewBinding.btnCfmNegative.setText(R.string.conn_wifi_disconn_dlg_negative_btn);
        mViewBinding.btnCfmNeutral.setText(R.string.conn_wifi_disconn_dlg_positive_btn);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
       LogUtil.debugD(TAG,"");
        mViewBinding.btnCfmPositive.setOnClickListener(v -> {
           LogUtil.i(TAG,"btnCfmPositive");
            dismiss();
        });
        mViewBinding.btnCfmNegative.setOnClickListener(v -> {
           LogUtil.i(TAG,"btnCfmNegative");
            callOnNegativeButtonClick(v, null);
            dismiss();
        });
        mViewBinding.btnCfmNeutral.setOnClickListener(v -> {
           LogUtil.i(TAG,"btnCfmNeutral");
            callOnPositiveButtonClick(v, null);
            dismiss();
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(this.getActivity(),R.style.TransparentStyle);
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(838, 470);
        }
    }
}
