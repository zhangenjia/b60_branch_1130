package com.adayo.app.systemui.bases;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.adayo.app.systemui.R;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.utils.LogUtil;

@SuppressLint("AppCompatCustomView")
public abstract class BaseTextView extends TextView {
    private boolean isPrivateClickRegister = false;
    private boolean isPrivateLongClickRegister = false;
    private OnClickListener onClickListener;
    private OnLongClickListener onLongClickListener;

    protected int controlType;
    protected int sourceSwitch;
    protected boolean needShowText;
    protected boolean responseLongClick;
    protected String source;
    protected String parameter;
    protected boolean mLoop;
    protected int systemStatus;

    protected abstract void analyticConfig();

    public BaseTextView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public BaseTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public BaseTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr){
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.customButton);
        controlType = array.getInt(R.styleable.customButton_controlType, -1);
        sourceSwitch = array.getInt(R.styleable.customButton_sourceSwitch, -1);
        parameter = array.getString(R.styleable.customButton_parameter);
        int sourceType = array.getInt(R.styleable.customButton_sourceType, -1);
        if(sourceType >= 0 && sourceType < SystemUIContent.sourceTypes.length) {
            source = SystemUIContent.sourceTypes[sourceType];
        }
        needShowText = array.getBoolean(R.styleable.customButton_needShowText, false);
        mLoop = array.getBoolean(R.styleable.customButton_loop, false);
        responseLongClick = array.getBoolean(R.styleable.customButton_responseLongClick, false);
        systemStatus = array.getInt(R.styleable.customButton_systemStatus, SystemUIContent.SYSTEM_STATUS_SCREEN);
        array.recycle();
        LogUtil.d(SystemUIContent.TAG, "controlType = " + controlType +
        " ; source = " + source + " ; needShowText = " + needShowText +
                " ; mLoop = " + mLoop + " ; responseLongClick = " + responseLongClick +
                " ; systemStatus = " + systemStatus);
        analyticConfig();
        this.setOnClickListener(v -> {
            onClick(v);
            if(null != onClickListener) {
                onClickListener.onClick(v);
            }
        });
        isPrivateClickRegister = true;
        this.setOnLongClickListener(v -> {
            if(responseLongClick) {
                onLongClick(v);
            }
            if(null != onLongClickListener) {
                onLongClickListener.onLongClick(v);
            }
            return responseLongClick;
        });
        isPrivateLongClickRegister = true;
    }

    protected void onClick(View v){}

    protected void onLongClick(View v){}

    @Override
    public void setOnClickListener(OnClickListener l) {
        if(!isPrivateClickRegister) {
            super.setOnClickListener(l);
        } else {
            onClickListener = l;
        }
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        if(!isPrivateLongClickRegister){
            super.setOnLongClickListener(l);
        } else {
            onLongClickListener = l;
        }
    }
}
