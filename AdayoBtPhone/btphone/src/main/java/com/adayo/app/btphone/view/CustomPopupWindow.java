package com.adayo.app.btphone.view;

import android.view.View;
import android.widget.PopupWindow;

public class CustomPopupWindow extends PopupWindow {
    private int y = 0;
    private int x = 0;
    private int width = 0;
    private int height = 0;

    public CustomPopupWindow(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
        super.update(x, y, width, height);
    }

    @Override
    public void update(int x, int y, int width, int height) {
        super.update(x, y, width, height);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
