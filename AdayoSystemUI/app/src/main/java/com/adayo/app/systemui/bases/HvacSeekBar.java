package com.adayo.app.systemui.bases;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.adayo.app.systemui.utils.LogUtil;

import static com.adayo.app.systemui.configs.SystemUIContent.TAG;

@SuppressLint("AppCompatCustomView")
public class HvacSeekBar extends SeekBar {
    private Paint paint;
    private Typeface mFace;

    public HvacSeekBar(Context context) {
        this(context, null);
    }
    
    @SuppressWarnings("deprecation")
    public HvacSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(getResources().getColor(android.R.color.white));
        mFace = Typeface.createFromAsset(getContext().getAssets(),"fonts/font_beijingauto_b60v_normal_v1.1.3.otf");
        paint.setTextSize(60);
        paint.setTypeface(mFace);
        paint.setAntiAlias(true);
        paint.setDither(true);
    }
    
    @Override
    public void setThumb(Drawable thumb) {
        // TODO Auto-generated method stub
        super.setThumb(thumb);
    }

    String temp_str = "0";
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        canvas.save();
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
        int data = Integer.parseInt(temp_str);
        Rect rect = getThumb().getBounds();
        float fontWidth = paint.measureText(temp_str);
        int dataLength = String.valueOf(data).length();
        switch (dataLength){
            case 1:
                int leftOffset = 20;
                if(data == 1){
                    leftOffset = 7;
                }
                canvas.drawText(temp_str, leftOffset + rect.left + (rect.width()) /2.0F - fontWidth /2.0F
                        , rect.top - paint.ascent() - 7,  paint);
                break;
            case 2:
                if(data < 20){
                    canvas.drawText(temp_str, 12 + rect.left + (rect.width()) /2.0F - fontWidth /3.5F
                            , rect.top - paint.ascent() + (rect.height() - (paint.descent() - paint.ascent()))/2.0F,  paint);
                }else if(data == 21 || data == 31){
                    canvas.drawText(temp_str, 16 + rect.left + (rect.width()) /2.0F - fontWidth /3.5F
                            , rect.top - paint.ascent() + (rect.height() - (paint.descent() - paint.ascent()))/2.0F,  paint);
                }else{
                    canvas.drawText(temp_str, 14 + rect.left + (rect.width()) /2.0F - fontWidth /3.5F
                            , rect.top - paint.ascent() + (rect.height() - (paint.descent() - paint.ascent()))/2.0F,  paint);
                }
                break;
            case 3:
                canvas.drawText(temp_str, 12 + rect.left + (rect.width()) /2.0F - fontWidth /4.4F
                        , rect.top - paint.ascent() + (rect.height() - (paint.descent() - paint.ascent()))/2.0F,  paint);
                break;
        }
        canvas.restore();
        super.onDraw(canvas);
    }
    
    public void setValue(String value){
        StringBuffer sb = new StringBuffer();
        sb.append(value);
        temp_str = sb.toString();
        invalidate();
    }
    
    @SuppressLint("NewApi")
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void setOnSeekBarChangeListener(final OnSeekBarChangeListener l) {
        super.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setValue(progress + "");
                if (l != null) {
                    l.onProgressChanged(seekBar, progress, fromUser);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (l != null) {
                    l.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (l != null) {
                    l.onStopTrackingTouch(seekBar);
                }
            }
        });
    }
}

