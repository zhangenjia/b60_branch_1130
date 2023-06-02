package com.adayo.app.camera.signalview;

import android.car.VehiclePropertyIds;
import android.util.Log;
import android.view.View;

import com.adayo.app.camera.MainService;
import com.adayo.app.camera.constants.EventIds;
import com.adayo.proxy.aaop_camera.AAOP_Camera;
import com.adayo.proxy.aaop_camera.signalview.base.BaseSignalView;
import com.adayo.proxy.aaop_camera.signalview.state.SignalViewState;

/**
 * @author Yiwen.Huan
 * created at 2021/11/15 13:57
 */
public class RvcRadarErrorSV extends BaseSignalView {

    private volatile boolean systemError = false;

    @Override
    protected void processEventBehavior(View view, SignalViewState state) {
    }

    @Override
    protected void processSignalBehavior(View view, SignalViewState state) {
        if (String.valueOf(VehiclePropertyIds.RADAR_FAULT_INFORMATION).equals(state.signalKey)) {
            processRadarError(state.signalValue);
            return;
        }

        if (String.valueOf(VehiclePropertyIds.REAR_TADAR_SYSTEM_STATUS).equals(state.signalKey)) {
            processSystemError(state);
        }
    }

    public void processSystemError(SignalViewState state) {
        Log.d("AdayoCamera", TAG + " - processSystemError() called with: state.value = [" + state.signalValue + "]");
        if (3 == state.signalValue) {
            setSystemError(true);
            AAOP_Camera.sendEvent(EventIds.RVC_RADAR_SYSTEM_ERROR);
            return;
        }
        if (4 == state.signalValue) {
            setSystemError(true);
            AAOP_Camera.sendEvent(EventIds.RVC_RADAR_SYSTEM_LIMIT);
            return;
        }
        if (isSystemError()) {
            setSystemError(false);
            int value = AAOP_Camera.getSignalValue(VehiclePropertyIds.RADAR_FAULT_INFORMATION, 0);
            processRadarError(value);
        }
    }

    private synchronized boolean isSystemError() {
        Log.d("AdayoCamera", TAG + " - isSystemError() returned: " + systemError);
        return systemError;
    }

    private synchronized void setSystemError(boolean error) {
        Log.d("AdayoCamera", TAG + " - setSystemError() called with: error = [" + error + "]");
        this.systemError = error;
    }

    public void processRadarError(int value) {
        Log.d("AdayoCamera", TAG + " - processSignalBehavior: value = " + value);
        int behindLeft = getBitValue(value, 0);
        int behindMiddleLeft = getBitValue(value, 1);
        int behindMiddleRight = getBitValue(value, 2);
        int behindRight = getBitValue(value, 3);
        int frontLeft = 0;
        int frontMiddleLeft = 0;
        int frontMiddleRight = 0;
        int frontRight = 0;
        if(MainService.hasFront){
            frontLeft = getBitValue(value, 4);
            frontMiddleLeft = getBitValue(value, 5);
            frontMiddleRight = getBitValue(value, 6);
            frontRight = getBitValue(value, 7);
        }

        if (isSystemError()) {
            return;
        }

        if (1 == behindLeft) {
            AAOP_Camera.sendEvent(EventIds.RVC_RADAR_BEHIND_LEFT_ERROR);
            if (isError(frontLeft, frontMiddleLeft, frontMiddleRight, frontRight)) {
                AAOP_Camera.sendEvent(EventIds.RVC_RADAR_SYSTEM_ERROR);
                return;
            } else {
                AAOP_Camera.sendEvent(EventIds.RVC_RADAR_BEHIND_ERROR);
            }
        } else {
            int signalValue = AAOP_Camera.getSignalValue(VehiclePropertyIds.DISTANCE_FROM_REAR_LEFT_TO_OBSTACLE, 0);
            AAOP_Camera.notifySignalDataChanged(VehiclePropertyIds.DISTANCE_FROM_REAR_LEFT_TO_OBSTACLE, signalValue);
        }


        if (1 == behindMiddleLeft) {
            AAOP_Camera.sendEvent(EventIds.RVC_RADAR_BEHIND_MIDDLE_LEFT_ERROR);
            if (isError(frontLeft, frontMiddleLeft, frontMiddleRight, frontRight)) {
                AAOP_Camera.sendEvent(EventIds.RVC_RADAR_SYSTEM_ERROR);
                return;
            } else {
                AAOP_Camera.sendEvent(EventIds.RVC_RADAR_BEHIND_ERROR);
            }
        } else {
            int signalValue = AAOP_Camera.getSignalValue(VehiclePropertyIds.DISTANCE_FROM_REAR_LEFT_CENTER_TO_OBSTACLE, 0);
            AAOP_Camera.notifySignalDataChanged(VehiclePropertyIds.DISTANCE_FROM_REAR_LEFT_CENTER_TO_OBSTACLE, signalValue);
        }
        if (1 == behindMiddleRight) {
            AAOP_Camera.sendEvent(EventIds.RVC_RADAR_BEHIND_MIDDLE_RIGHT_ERROR);
            if (isError(frontLeft, frontMiddleLeft, frontMiddleRight, frontRight)) {
                AAOP_Camera.sendEvent(EventIds.RVC_RADAR_SYSTEM_ERROR);
                return;
            } else {
                AAOP_Camera.sendEvent(EventIds.RVC_RADAR_BEHIND_ERROR);
            }
        } else {
            int signalValue = AAOP_Camera.getSignalValue(VehiclePropertyIds.DISTANCE_FROM_REAR_RIGHT_CENTER_TO_OBSTACLE, 0);
            AAOP_Camera.notifySignalDataChanged(VehiclePropertyIds.DISTANCE_FROM_REAR_RIGHT_CENTER_TO_OBSTACLE, signalValue);
        }
        if (1 == behindRight) {
            AAOP_Camera.sendEvent(EventIds.RVC_RADAR_BEHIND_RIGHT_ERROR);
            if (isError(frontLeft, frontMiddleLeft, frontMiddleRight, frontRight)) {
                AAOP_Camera.sendEvent(EventIds.RVC_RADAR_SYSTEM_ERROR);
                return;
            } else {
                AAOP_Camera.sendEvent(EventIds.RVC_RADAR_BEHIND_ERROR);
            }
        } else {
            int signalValue = AAOP_Camera.getSignalValue(VehiclePropertyIds.DISTANCE_FROM_REAR_RIGHT_TO_OBSTACLE, 0);
            AAOP_Camera.notifySignalDataChanged(VehiclePropertyIds.DISTANCE_FROM_REAR_RIGHT_TO_OBSTACLE, signalValue);
        }

        if (1 == frontLeft) {
            AAOP_Camera.sendEvent(EventIds.RVC_RADAR_FRONT_LEFT_ERROR);
            if (isError(behindLeft, behindMiddleLeft, behindMiddleRight, behindRight)) {
                AAOP_Camera.sendEvent(EventIds.RVC_RADAR_SYSTEM_ERROR);
                return;
            } else {
                AAOP_Camera.sendEvent(EventIds.RVC_RADAR_FRONT_ERROR);
            }
        } else {
            int signalValue = AAOP_Camera.getSignalValue(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_LEFT_TO_THE_OBSTACLE, 0);
            AAOP_Camera.notifySignalDataChanged(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_LEFT_TO_THE_OBSTACLE, signalValue);
        }
        if (1 == frontMiddleLeft) {
            AAOP_Camera.sendEvent(EventIds.RVC_RADAR_FRONT_MIDDLE_LEFT_ERROR);
            if (isError(behindLeft, behindMiddleLeft, behindMiddleRight, behindRight)) {
                AAOP_Camera.sendEvent(EventIds.RVC_RADAR_SYSTEM_ERROR);
                return;
            } else {
                AAOP_Camera.sendEvent(EventIds.RVC_RADAR_FRONT_ERROR);
            }
        } else {
            int signalValue = AAOP_Camera.getSignalValue(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_LEFT_CENTER_TO_THE_OBSTACLE, 0);
            AAOP_Camera.notifySignalDataChanged(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_LEFT_CENTER_TO_THE_OBSTACLE, signalValue);
        }
        if (1 == frontMiddleRight) {
            AAOP_Camera.sendEvent(EventIds.RVC_RADAR_FRONT_MIDDLE_RIGHT_ERROR);
            if (isError(behindLeft, behindMiddleLeft, behindMiddleRight, behindRight)) {
                AAOP_Camera.sendEvent(EventIds.RVC_RADAR_SYSTEM_ERROR);
                return;
            } else {
                AAOP_Camera.sendEvent(EventIds.RVC_RADAR_FRONT_ERROR);
            }
        } else {
            int signalValue = AAOP_Camera.getSignalValue(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_RIGHT_CENTER_TO_THE_OBSTACLE, 0);
            AAOP_Camera.notifySignalDataChanged(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_RIGHT_CENTER_TO_THE_OBSTACLE, signalValue);
        }
        if (1 == frontRight) {
            AAOP_Camera.sendEvent(EventIds.RVC_RADAR_FRONT_RIGHT_ERROR);
            if (isError(behindLeft, behindMiddleLeft, behindMiddleRight, behindRight)) {
                AAOP_Camera.sendEvent(EventIds.RVC_RADAR_SYSTEM_ERROR);
                return;
            } else {
                AAOP_Camera.sendEvent(EventIds.RVC_RADAR_FRONT_ERROR);
            }
        } else {
            int signalValue = AAOP_Camera.getSignalValue(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_RIGHT_TO_THE_OBSTACLE, 0);
            AAOP_Camera.notifySignalDataChanged(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_RIGHT_TO_THE_OBSTACLE, signalValue);
        }

        if (!isError(behindLeft, behindMiddleLeft, behindMiddleRight, behindRight) &&
                !isError(frontLeft, frontMiddleLeft, frontMiddleRight, frontRight)) {
            AAOP_Camera.sendEvent(EventIds.RVC_RADAR_HINT_DISMISS);
        }


    }


    public int getBitValue(int intValue, int position) {
        return ((intValue >> position) & 0x1);
    }

    public boolean isError(int left, int middleLeft, int middleRight, int right) {
        return left == 1 || middleLeft == 1 || middleRight == 1 || right == 1;
    }

}
