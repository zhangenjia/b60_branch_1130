package com.adayo.app.setting.view.dialog;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.adayo.app.setting.R;
import com.adayo.app.setting.databinding.DialogInputBinding;
import com.adayo.app.setting.model.constant.ParamConstant;

import com.adayo.app.setting.utils.EdtHintUtil;
import com.adayo.app.setting.utils.EdtLinkBtnUtil;
import com.adayo.app.setting.utils.KeyboardUtil;
import com.adayo.app.setting.base.BaseDialogFragment;

import com.adayo.app.base.LogUtil;

import java.util.Objects;

public class ReplyDialog extends BaseDialogFragment<DialogInputBinding> {
    private final static String TAG = ReplyDialog.class.getSimpleName();
    private String mContent;
    private EdtLinkBtnUtil mEdtLinkBtnUtil;

    public static ReplyDialog newInstance(String content) {
        Bundle arguments = new Bundle();
        ReplyDialog fragment = new ReplyDialog();
        arguments.putString(ParamConstant.BDL_KEY_DLG_REPLY_CONTENT, content);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected DialogInputBinding bindView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return DialogInputBinding.inflate(inflater);
    }

    @Override
    protected void bindData(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.bindData(arguments, savedInstanceState);
       LogUtil.debugD(TAG, "mContent = " + mContent);
        if (Objects.nonNull(arguments)) {
            mContent = arguments.getString(ParamConstant.BDL_KEY_DLG_REPLY_CONTENT);
           LogUtil.debugD(TAG, "mContent = " + mContent);
        }
    }



    private void onSystemUIVisibility(boolean visibility) {
       LogUtil.debugD(TAG, "visibility =" + visibility);
        View decorView = mActivity.getWindow().getDecorView();
        if (visibility) {mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);} else {decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);}
    }

    @Override
    protected void initView() {
        super.initView();
        onSystemUIVisibility(true);

       LogUtil.debugD(TAG, "");
        mViewBinding.tvDlginputTitle1.setText(R.string.conn_replay_dialog_title);
        mViewBinding.tvDlginputTitle2.setText(null);
        mViewBinding.editDlginputText.setText(mContent);
        mViewBinding.editDlginputText.setHint(EdtHintUtil.formatHint(R.string.conn_replay_dialog_input_hint, R.dimen.common_tvsize_big2));
        mViewBinding.editDlginputText.setSelection(mContent.length());
    }

    @Override
    protected void initData() {
        super.initData();

mEdtLinkBtnUtil = new EdtLinkBtnUtil.Builder().addEditText(mViewBinding.editDlginputText, 0).setButton(mViewBinding.btnDlginputEdit, 0.4F).diffSbc()
                .build();
    }

    @Override
    protected void initEvent() {
        super.initEvent();
       LogUtil.debugD(TAG, "");
        mEdtLinkBtnUtil.addEvent();
        mViewBinding.editDlginputText.setFilters(new InputFilter[]{(source, start, end, dest, dstart, dend) -> {
            if (" ".equals(source)) {
                return "";
            }
            return null;
        }});
        mViewBinding.btnDlginputEdit.setOnClickListener(new View.OnClickListener() {@Override
            public void onClick(View view) {
               LogUtil.i(TAG, "btnDlginputEdit");
                callOnPositiveButtonClick(view, mViewBinding.editDlginputText.getText().toString());
                dismiss();
            }
        });
        mViewBinding.btnDlginputClose.setOnClickListener(new View.OnClickListener() {@Override
            public void onClick(View view) {
               LogUtil.i(TAG, "btnDlginputClose");
                KeyboardUtil.hide(mViewBinding.editDlginputText);new Handler().postDelayed(ReplyDialog.super::dismiss, getAppContext().getResources().getInteger(android.R.integer.config_shortAnimTime));}
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.debugD(TAG, "");
        KeyboardUtil.show(mViewBinding.editDlginputText, getAppContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
    }

    @Override
    public void onPause() {
        super.onPause();
       LogUtil.debugD(TAG, "");
        KeyboardUtil.hide(mViewBinding.editDlginputText);
    }

    @Override
    protected void freeEvent() {
        super.freeEvent();
       LogUtil.debugD(TAG, "");
        mEdtLinkBtnUtil.delEvent();}

}
