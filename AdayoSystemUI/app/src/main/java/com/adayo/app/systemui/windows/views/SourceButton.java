package com.adayo.app.systemui.windows.views;

import android.annotation.Nullable;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.adayo.app.systemui.interfaces.BaseCallback;
import com.adayo.app.systemui.bases.BaseTextView;
import com.adayo.app.systemui.bean.SystemUISourceInfo;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.managers.business.SourceControllerImpl;
import com.adayo.app.systemui.managers.view.WindowsManager;
import com.adayo.app.systemui.utils.LogUtil;

import java.util.HashMap;

import static com.adayo.app.systemui.configs.HvacContent.HVAC_FRONT;
import static com.adayo.app.systemui.configs.SystemUIContent.PARAMETER_KEY;
import static com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType.SourceSwitch;

public class SourceButton extends BaseTextView implements BaseCallback<SystemUISourceInfo> {
    private SourceControllerImpl sourceController;

    public SourceButton(Context context) {
        super(context);
    }

    public SourceButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SourceButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onClick(View v) {
        if (null != sourceController) {
            LogUtil.debugD(SystemUIContent.TAG, "mLoop = " + mLoop + " ; mySource = " + source + " ; CurrentUISource = " + sourceController.getCurrentUISource());
            WindowsManager.setQsPanelVisibility(View.GONE);
            WindowsManager.setHvacPanelVisibility(View.GONE, HVAC_FRONT, true, true);
            int mySourceSwitch;
            HashMap<String, String> map = new HashMap<>();
            if(null != parameter) {
                map.put(PARAMETER_KEY, parameter);
            }
            if (mLoop) {
                mySourceSwitch = (null == sourceController.getCurrentUISource() || source.equals(sourceController.getCurrentUISource())) ?
                        (sourceSwitch == 1 ? 3 : SourceSwitch.APP_OFF.getValue()) : (sourceSwitch == 1 ? 2 : SourceSwitch.APP_ON.getValue());
                sourceController.requestSource(mySourceSwitch, source, map);
            } else {
//                if(null != sourceController.getCurrentUISource() && source.equals(sourceController.getCurrentUISource())){
//                    return;
//                }
                sourceController.requestSource(sourceSwitch == 1 ? 2 : SourceSwitch.APP_ON.getValue(), source, map);
            }
        }
    }

    @Override
    protected void analyticConfig() {
        sourceController = SourceControllerImpl.getInstance();
    }

    @Override
    public void onDataChange(SystemUISourceInfo data) {
        if (null != data) {
            setSelected(source == data.getUiSource());
        }
    }
}
