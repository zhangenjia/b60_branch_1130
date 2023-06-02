package com.adayo.app.systemui.windows.dialogs;

import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

import static com.adayo.app.systemui.configs.HvacContent.BCM_SEAT_MASSAGE_INTENSITY_CTRL;
import static com.adayo.app.systemui.configs.HvacContent.BCM_SEAT_MASSAGE_MODE_CTRL;
import static com.adayo.app.systemui.configs.HvacContent.MASSAGE_INTENSITY_CLOSE;
import static com.adayo.app.systemui.configs.HvacContent.MASSAGE_INTENSITY_HIGH;
import static com.adayo.app.systemui.configs.HvacContent.MASSAGE_INTENSITY_LOW;
import static com.adayo.app.systemui.configs.HvacContent.MASSAGE_INTENSITY_MIDDLE;
import static com.adayo.app.systemui.configs.HvacContent.MASSAGE_MODE_BUTTERFLY;
import static com.adayo.app.systemui.configs.HvacContent.MASSAGE_MODE_CATWALK;
import static com.adayo.app.systemui.configs.HvacContent.MASSAGE_MODE_CROSS;
import static com.adayo.app.systemui.configs.HvacContent.MASSAGE_MODE_LOIN;
import static com.adayo.app.systemui.configs.HvacContent.MASSAGE_MODE_PULSE;
import static com.adayo.app.systemui.configs.HvacContent.MASSAGE_MODE_SERPENTINE;
import static com.adayo.app.systemui.configs.HvacContent.MASSAGE_MODE_SINGLE_ROW;
import static com.adayo.app.systemui.configs.HvacContent.MASSAGE_MODE_WAVE;
import static com.adayo.app.systemui.configs.HvacContent.SEAT_ROW_1_LEFT;
import static com.adayo.app.systemui.configs.HvacContent.SEAT_ROW_1_RIGHT;

import android.app.Dialog;
import android.car.hardware.hvac.AAOP_HvacManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.app.systemui.R;
import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.managers.view.WindowsManager;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

public class MassageSetBox extends Dialog implements View.OnClickListener {
    private volatile static MassageSetBox massageSetBox = null;

    private RelativeLayout messageBg;

    private TextView close;
    private TextView low;
    private TextView middle;
    private TextView high;
    private TextView pulse;
    private TextView wave;
    private TextView catwalk;
    private TextView single;
    private TextView cross;
    private TextView butterfly;
    private TextView loin;
    private TextView serpentine;

    private MassageSetBox(Context context) {
        super(context, R.style.TransparentStyle);

        Window window = getWindow();
        window.setGravity(Gravity.TOP | Gravity.LEFT);
        window.setType(TYPE_APPLICATION_OVERLAY);
        window.setWindowAnimations(R.style.MassageAnimationFrontLeft);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        layoutParams.y = 0;
        layoutParams.width = 838;
        layoutParams.height = 397;
        window.setAttributes(layoutParams);
        setContentView(R.layout.massage_box_layout);
        AAOP_HSkin.getWindowViewManager().addWindowView(this.findViewById(android.R.id.content));
        setCanceledOnTouchOutside(true);
        close = findViewById(R.id.close);
        close.setOnClickListener(this);
        low = findViewById(R.id.low);
        low.setOnClickListener(this);
        middle = findViewById(R.id.middle);
        middle.setOnClickListener(this);
        high = findViewById(R.id.high);
        high.setOnClickListener(this);
        pulse = findViewById(R.id.pulse);
        pulse.setOnClickListener(this);
        wave = findViewById(R.id.wave);
        wave.setOnClickListener(this);
        catwalk = findViewById(R.id.catwalk);
        catwalk.setOnClickListener(this);
        single = findViewById(R.id.single);
        single.setOnClickListener(this);
        cross = findViewById(R.id.cross);
        cross.setOnClickListener(this);
        butterfly = findViewById(R.id.butterfly);
        butterfly.setOnClickListener(this);
        loin = findViewById(R.id.loin);
        loin.setOnClickListener(this);
        serpentine = findViewById(R.id.serpentine);
        serpentine.setOnClickListener(this);
        messageBg = findViewById(R.id.message_bg);
        AAOP_HSkin.with(messageBg).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.massage_bg).applySkin(false);
        AAOP_HSkin.with(close).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector_massage_bg).applySkin(false);
        AAOP_HSkin.with(cross).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector_massage_bg).applySkin(false);
        AAOP_HSkin.with(low).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector_massage_bg).applySkin(false);
        AAOP_HSkin.with(middle).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector_massage_bg).applySkin(false);
        AAOP_HSkin.with(high).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector_massage_bg).applySkin(false);
        AAOP_HSkin.with(pulse).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector_massage_bg).applySkin(false);
        AAOP_HSkin.with(wave).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector_massage_bg).applySkin(false);
        AAOP_HSkin.with(catwalk).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector_massage_bg).applySkin(false);
        AAOP_HSkin.with(single).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector_massage_bg).applySkin(false);
        AAOP_HSkin.with(butterfly).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector_massage_bg).applySkin(false);
        AAOP_HSkin.with(loin).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector_massage_bg).applySkin(false);
        AAOP_HSkin.with(serpentine).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector_massage_bg).applySkin(false);
    }

    public static MassageSetBox getInstance() {
        if (massageSetBox == null) {
            synchronized (MassageSetBox.class) {
                if (massageSetBox == null) {
                    massageSetBox = new MassageSetBox(SystemUIApplication.getSystemUIContext());
                }
            }
        }
        return massageSetBox;
    }

    private final int DISMISS_VOLUME_POPUP = 10001;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case DISMISS_VOLUME_POPUP:
                    dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    private void resetHandle() {
        mHandler.removeMessages(DISMISS_VOLUME_POPUP);
        mHandler.sendEmptyMessageDelayed(DISMISS_VOLUME_POPUP, SystemUIContent.HVAC_POPUP_DELAYED);
    }

    private void sendStatus(int vehicleId, int type){
        AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).setIntProperty(vehicleId, areaId, type);
    }

    private int areaId;
    public synchronized void showPanel(int resId, int x, int y, int area, int mode, int status) {
        areaId = area;
        Window window = getWindow();
        window.setGravity(Gravity.TOP | Gravity.LEFT);
        window.setType(TYPE_APPLICATION_OVERLAY);
        window.setWindowAnimations(resId);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        layoutParams.x = x;
        layoutParams.y = y;
        layoutParams.width = 838;
        layoutParams.height = 397;
        window.setAttributes(layoutParams);
        if (!isShowing()) {
            super.show();
        }
        resetHandle();
        updateLevelView(status, areaId);
        updateModeView(mode, areaId);
    }

    public void updateLevelView(int value, int area){
        if(area != areaId){
            return;
        }
        int correctValue = 0;
//        if(areaId == SEAT_ROW_1_LEFT || areaId == SEAT_ROW_1_RIGHT){
//            correctValue = -1;
//        }
        close.setSelected(value <= MASSAGE_INTENSITY_CLOSE + correctValue);
        low.setSelected(value == MASSAGE_INTENSITY_LOW + correctValue);
        middle.setSelected(value == MASSAGE_INTENSITY_MIDDLE + correctValue);
        high.setSelected(value == MASSAGE_INTENSITY_HIGH + correctValue);
    }

    public void updateModeView(int value, int area){
        if(area != areaId){
            return;
        }
        pulse.setSelected(value == MASSAGE_MODE_PULSE);
        wave.setSelected(value == MASSAGE_MODE_WAVE);
        catwalk.setSelected(value == MASSAGE_MODE_CATWALK);
        single.setSelected(value == MASSAGE_MODE_SINGLE_ROW);
        cross.setSelected(value == MASSAGE_MODE_CROSS);
        butterfly.setSelected(value == MASSAGE_MODE_BUTTERFLY);
        loin.setSelected(value == MASSAGE_MODE_LOIN);
        serpentine.setSelected(value == MASSAGE_MODE_SERPENTINE);
    }
    @Override
    public void show() {

    }

    @Override
    public void dismiss() {
        mHandler.removeMessages(DISMISS_VOLUME_POPUP);
        super.dismiss();
    }
    @Override
    public void onClick(View v) {
        WindowsManager.resetHvacHandler(true);
        int correctValue = 0;
        if(areaId == SEAT_ROW_1_LEFT || areaId == SEAT_ROW_1_RIGHT){
            correctValue = -1;
        }
        resetHandle();
        switch (v.getId()) {
            case R.id.close:
                sendStatus(BCM_SEAT_MASSAGE_INTENSITY_CTRL, MASSAGE_INTENSITY_CLOSE + correctValue);
                break;
            case R.id.low:
                sendStatus(BCM_SEAT_MASSAGE_INTENSITY_CTRL, MASSAGE_INTENSITY_LOW + correctValue);
                break;
            case R.id.middle:
                sendStatus(BCM_SEAT_MASSAGE_INTENSITY_CTRL, MASSAGE_INTENSITY_MIDDLE + correctValue);
                break;
            case R.id.high:
                sendStatus(BCM_SEAT_MASSAGE_INTENSITY_CTRL, MASSAGE_INTENSITY_HIGH + correctValue);
                break;
            case R.id.pulse:
                sendStatus(BCM_SEAT_MASSAGE_MODE_CTRL, MASSAGE_MODE_PULSE);
                break;
            case R.id.wave:
                sendStatus(BCM_SEAT_MASSAGE_MODE_CTRL, MASSAGE_MODE_WAVE);
                break;
            case R.id.catwalk:
                sendStatus(BCM_SEAT_MASSAGE_MODE_CTRL, MASSAGE_MODE_CATWALK);
                break;
            case R.id.single:
                sendStatus(BCM_SEAT_MASSAGE_MODE_CTRL, MASSAGE_MODE_SINGLE_ROW);
                break;
            case R.id.cross:
                sendStatus(BCM_SEAT_MASSAGE_MODE_CTRL, MASSAGE_MODE_CROSS);
                break;
            case R.id.butterfly:
                sendStatus(BCM_SEAT_MASSAGE_MODE_CTRL, MASSAGE_MODE_BUTTERFLY);
                break;
            case R.id.loin:
                sendStatus(BCM_SEAT_MASSAGE_MODE_CTRL, MASSAGE_MODE_LOIN);
                break;
            case R.id.serpentine:
                sendStatus(BCM_SEAT_MASSAGE_MODE_CTRL, MASSAGE_MODE_SERPENTINE);
                break;
            default:
                break;
        }
    }
}
