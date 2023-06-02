package com.adayo.app.systemui.windows.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.app.systemui.R;

public class MessageIconView extends RelativeLayout {
    private TextView foregroundNumber;

    private boolean isAdded = false;

    public MessageIconView(Context context) {
        super(context);
        initView();
    }

    public MessageIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MessageIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View mRootView = LayoutInflater.from(getContext()).inflate(R.layout.message_icon_view, this, true);
        foregroundNumber = mRootView.findViewById(R.id.foreground_number);
    }

    public void setForegroundText(int number){
        foregroundNumber.setText(number > 99 ? "99+" : number + "");
        foregroundNumber.setVisibility(number == 0 ? GONE : VISIBLE);
    }

    public void setAddTag(boolean isAdded){
        this.isAdded = isAdded;
    }
    public boolean getAddTag(){
        return isAdded;
    }
}
