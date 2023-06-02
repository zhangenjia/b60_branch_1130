package com.adayo.app.systemui.windows.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.adayo.app.systemui.R;
import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.msg.notify.bean.MessageNotification;
import com.adayo.proxy.msg.notify.control.MessageManager;

public class TSPMessageReminder extends Dialog implements View.OnClickListener {
    private volatile static TSPMessageReminder tspMessageReminder = null;

    private TextView content;
    private TextView btnConfirm;

    private MessageNotification messageNotification;

    private TSPMessageReminder(Context context) {
        super(context, R.style.TranslucentStyle);
        Window window = getWindow();
        window.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP);
        window.setType(2062);
        window.setWindowAnimations(R.style.DialogAnimation);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        layoutParams.y = 281;
        layoutParams.width = 1138;
        layoutParams.height = 518;
        window.setAttributes(layoutParams);
        setContentView(R.layout.message_sort_dialog_layout);
        AAOP_HSkin.getWindowViewManager().addWindowView(this.findViewById(android.R.id.content));
        setCanceledOnTouchOutside(true);
        content = findViewById(R.id.content);
        btnConfirm = findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(this);
    }

    public static TSPMessageReminder getInstance() {
        if (tspMessageReminder == null) {
            synchronized (VolumeDialog.class) {
                if (tspMessageReminder == null) {
                    tspMessageReminder = new TSPMessageReminder(SystemUIApplication.getSystemUIContext());
                }
            }
        }
        return tspMessageReminder;
    }

    public synchronized void showDialog(MessageNotification messageNotification) {
        this.messageNotification = messageNotification;
        if(null != content){
            content.setText(messageNotification.getmContext());
        }
        if (!isShowing()) {
            super.show();
        }
    }

    @Override
    public void show() {
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                MessageManager.getInstance().markMsgReaded(messageNotification);
                dismiss();
                break;
            default:
                break;
        }
    }
}

