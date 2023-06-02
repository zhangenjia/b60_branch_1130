package com.adayo.app.setting.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.lt.library.util.context.ContextUtil;


public class KeyboardUtil {
    private static Runnable sRunnable;

    private KeyboardUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    private static void postDelayed(EditText editText, long delayMillis, InputMethodManager inputMethodManager) {
        sRunnable = () -> {
            editText.requestFocus();
            inputMethodManager.showSoftInput(editText, 0);
            postRemoved(editText);
        };
        editText.postDelayed(sRunnable, delayMillis);
    }

    private static void postRemoved(EditText editText) {
        editText.removeCallbacks(sRunnable);
        sRunnable = null;
    }


    public static void show(EditText editText) {
        show(editText, 0);
    }


    public static void show(EditText editText, long delayMillis) {
        InputMethodManager inputMethodManager = (InputMethodManager) ContextUtil.getInstance()
                                                                                .getApplicationContext()
                                                                                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (delayMillis == 0) {
            editText.requestFocus();
            inputMethodManager.showSoftInput(editText, 0);
        } else {
            postDelayed(editText, delayMillis, inputMethodManager);
        }
    }


    public static void hide(EditText editText) {
        InputMethodManager inputMethodManager = (InputMethodManager) ContextUtil.getInstance()
                                                                                .getApplicationContext()
                                                                                .getSystemService(Context.INPUT_METHOD_SERVICE);
        postRemoved(editText);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }


    public static void toggle() {
        InputMethodManager inputMethodManager = (InputMethodManager) ContextUtil.getInstance()
                                                                                .getApplicationContext()
                                                                                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0, 0);
    }
}
