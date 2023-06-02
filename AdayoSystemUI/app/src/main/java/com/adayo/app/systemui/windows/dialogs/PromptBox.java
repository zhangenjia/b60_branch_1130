package com.adayo.app.systemui.windows.dialogs;

import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.adayo.app.systemui.R;
import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.utils.LogUtil;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;


public class PromptBox extends Dialog {
    private volatile static PromptBox promptBox = null;

    private ImageView volIcon;
    private TextView volText;

    private PromptBox(Context context) {
        super(context, R.style.TransparentStyle);

        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        window.setType(TYPE_APPLICATION_OVERLAY);
        window.setWindowAnimations(R.style.DialogAnimation);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        layoutParams.y = 21;
        layoutParams.width = 838;
        layoutParams.height = 188;
        window.setAttributes(layoutParams);
        setContentView(R.layout.prompt_box_layout);
        AAOP_HSkin.getWindowViewManager().addWindowView(this.findViewById(android.R.id.content));
        setCanceledOnTouchOutside(false);
        volIcon = findViewById(R.id.icon_box);
        volText = findViewById(R.id.text);
    }

    public static PromptBox getInstance() {
        if (promptBox == null) {
            synchronized (PromptBox.class) {
                if (promptBox == null) {
                    promptBox = new PromptBox(SystemUIApplication.getSystemUIContext());
                }
            }
        }
        return promptBox;
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
        mHandler.sendEmptyMessageDelayed(DISMISS_VOLUME_POPUP, SystemUIContent.VOLUME_POPUP_DELAYED);
    }

    public synchronized void showPanel(int src, String content) {
        if (volIcon == null || volText == null) {
            return;
        }
        volIcon.setImageResource(src);
        volText.setText(content);
        if (!isShowing()) {
            super.show();
        }
        resetHandle();
    }

    @Override
    public void show() {

    }

    @Override
    public void dismiss() {
        mHandler.removeMessages(DISMISS_VOLUME_POPUP);
        super.dismiss();
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getRawY() < 509 || event.getRawX() < 484 ||
//                event.getRawY() > 686 || event.getRawX() > 1410) {
//            LogUtil.debugD(SystemUIContent.TAG, "dismiss");
//            dismiss();
//        }
//        return true;
//    }
}
