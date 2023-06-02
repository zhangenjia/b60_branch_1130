package com.adayo.app.camera.signalview;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.adayo.app.camera.R;
import com.adayo.proxy.aaop_camera.signalview.base.BaseValuesSignalView;
import com.adayo.proxy.aaop_camera.signalview.state.SignalViewState;

/**
 * @author Yiwen.Huan
 * created at 2021/8/19 17:23
 */
public class VersionSignalView extends BaseValuesSignalView {

    private int value = 0;

    @Override
    protected void processEventBehavior(View view, SignalViewState state) {

    }

    @Override
    protected void processSignalBehavior(View view, SignalViewState state) {
        value = state.signalValue;
        if (0 == value) {
            //mcu升级时复归会默认发0，所以不能对0进行处理
            Log.d("AdayoCamera", TAG + " - processSignalBehavior:  version is 0 , do not set it");
            return;
        }
        StringBuilder sb = new StringBuilder();
        int viewId = view.getId();
        if (viewId == R.id.tv_dialog_update_version_curr || viewId == R.id.tv_update_version_curr) {
            String preStr = view.getContext().getString(R.string.avm_dialog_update_current_version);
            sb.append(preStr);
        } else if (viewId == R.id.tv_dialog_update_version || viewId == R.id.tv_update_version) {
            String preStr = view.getContext().getString(R.string.avm_dialog_update_version);
            sb.append(preStr);
        }

        sb.append("V")
                .append((value >> 14 ) & 0x03)
                .append(".")
                .append((value >> 7) & 0x7f)
                .append(".")
                .append(value & 0x7f);
        if (view instanceof TextView) {
            ((TextView) view).setText(sb.toString());
        }

    }
}
