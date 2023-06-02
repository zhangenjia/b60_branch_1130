package com.adayo.app.setting.view.dialog;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.adayo.app.setting.R;
import com.adayo.app.setting.databinding.DialogInputBinding;
import com.adayo.app.setting.model.constant.ParamConstant;
import com.adayo.app.setting.base.BaseDialogFragment;
import com.adayo.app.setting.utils.EdtHintUtil;
import com.adayo.app.setting.utils.EdtLinkBtnUtil;
import com.adayo.app.setting.utils.KeyboardUtil;
import com.adayo.app.base.LogUtil;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WifiConnDialog extends BaseDialogFragment<DialogInputBinding> {
    private final static String TAG = WifiConnDialog.class.getSimpleName();
    private String mTitle;
    private EdtLinkBtnUtil mEdtLinkBtnUtil;
    private int menableLength;

    public static WifiConnDialog newInstance(String title,int enableLength) {
       LogUtil.debugD(TAG,"");
        Bundle arguments = new Bundle();
        WifiConnDialog fragment = new WifiConnDialog();
        arguments.putString(ParamConstant.BDL_KEY_DLG_TITLE, title);
        arguments.putInt(ParamConstant.BDL_KEY_DLG_ENABLELENGTH, enableLength);
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
       LogUtil.debugD(TAG,"");
        if (Objects.nonNull(arguments)) {
            mTitle = arguments.getString(ParamConstant.BDL_KEY_DLG_TITLE);
            menableLength = arguments.getInt(ParamConstant.BDL_KEY_DLG_ENABLELENGTH);
        }
    }

    @Override
    protected void initView() {
        super.initView();
       LogUtil.debugD(TAG,"");
        onSystemUIVisibility(true);
        mTitle = getView().getContext().getString(R.string.conn_wifi_inputtitle2_begin) + mTitle + getView().getContext().getString(R.string.conn_wifi_inputtitle2_end);
        mViewBinding.tvDlginputTitle1.setText(R.string.conn_wifi_inputtitle1);
        mViewBinding.tvDlginputTitle2.setText(mTitle);
        mViewBinding.editDlginputText.setHint(EdtHintUtil.formatHint(R.string.conn_hotspot_input_hint,R.dimen.common_tvsize_big2));
        mViewBinding.editDlginputText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mViewBinding.btnDlginputEdit.setText(R.string.conn_wifi_input_confirm);
        mViewBinding.cbDlginputPwdVis.setVisibility(View.VISIBLE);
    }


    private void onSystemUIVisibility(boolean visibility) {
       LogUtil.debugD(TAG, "visibility =" + visibility);
        View decorView = mActivity.getWindow().getDecorView();
        if (visibility) {mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);} else {decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);}
    }
    @Override
    protected void initData() {
        super.initData();
       LogUtil.debugD(TAG,"");
        mEdtLinkBtnUtil = new EdtLinkBtnUtil.Builder().addEditText(mViewBinding.editDlginputText,menableLength)
                .setButton(mViewBinding.btnDlginputEdit, 0.4F)
                .diffSbc()
                .build();
    }

    @Override
    protected void initEvent() {
        super.initEvent();
       LogUtil.debugD(TAG,"");
        mEdtLinkBtnUtil.addEvent();
        mViewBinding.editDlginputText.setFilters(new InputFilter[]{(source, start, end, dest, dstart, dend) -> {
            Pattern p = Pattern.compile("[^\u4e00-\u9fa5]+$");
            Matcher m = p.matcher(source.toString());
            if (!m.matches()) {
                return "";
            }
            return null;
        }});
        mViewBinding.cbDlginputPwdVis.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mViewBinding.editDlginputText.setInputType(InputType.TYPE_CLASS_TEXT | (isChecked ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : InputType.TYPE_TEXT_VARIATION_PASSWORD));
            mViewBinding.editDlginputText.setSelection(mViewBinding.editDlginputText.getText().toString().length());
        });
        mViewBinding.btnDlginputEdit.setOnClickListener(v -> {
            callOnPositiveButtonClick(v, mViewBinding.editDlginputText.getText().toString());
            dismiss();
        });
        mViewBinding.btnDlginputClose.setOnClickListener(v -> {
            KeyboardUtil.hide(mViewBinding.editDlginputText);
            new Handler().postDelayed(this::dismiss, getAppContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
        });
    }

    @Override
    public void onResume() {
        super.onResume();
       LogUtil.debugD(TAG,"");
        KeyboardUtil.show(mViewBinding.editDlginputText, getAppContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
    }

    @Override
    public void onPause() {
        super.onPause();
       LogUtil.debugD(TAG,"");
        KeyboardUtil.hide(mViewBinding.editDlginputText);
    }

    @Override
    protected void freeEvent() {
        super.freeEvent();
       LogUtil.debugD(TAG,"");
        mEdtLinkBtnUtil.delEvent();
    }

}
