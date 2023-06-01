package com.adayo.app.btphone.utils;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.lang.reflect.Method;

/**
 */
public class EditUtils {
    /**
     * 删除选中的字符
     * @param editText
     */
    public static void deleteSelChar(EditText editText, int start, int end) {
        Editable editable = editText.getText();
        if(start == end) {
            int index = editText.getSelectionStart();
            if(index > 0) {
                editable.delete(index - 1, index);
            }
        } else {
            editable.delete(start, end);
        }
        editText.requestFocus();
    }

    /**
     * 删除光标前字符
     * @param editText
     */
    public static void deleteChar(EditText editText) {
        int index = editText.getSelectionStart();
        if(index == 0) {
            return;
        }
        Editable editable = editText.getText();
        editable.delete(index - 1, index);
        editText.requestFocus();
    }
    /**
     * 删除全部字符
     * @param editText
     */
    public static void deleteAllChar(EditText editText) {
        int index = editText.getSelectionStart();
        if(index == 0) {
            return;
        }
        Editable editable = editText.getText();
        editable.clear();
        editText.requestFocus();
    }
    /**
     * 光标后添加字符
     * @param editText
     * @param data
     */
    public static void addChar(EditText editText, String data){
        int index = editText.getSelectionStart();
        Editable editable = editText.getText();
        editable.insert(index, data);
        String s = editText.getText().toString();
        Log.d("EditUtils","70 s = "+s);
        int contentLength = 20;
        String addContent = "...";
        if(s.length() > contentLength && !s.contains(addContent)){
            s = addContent + s.substring(s.length() - contentLength);
            editText.setText(s);
            editText.setSelection(editText.length());
        }
        if(s.contains(addContent) && s.length() > contentLength + 3){
            s.replace(addContent,"");
            String temp = s.substring(s.length() - contentLength);
            editText.setText(addContent+temp);
            editText.setSelection(editText.length());
        }
        editText.requestFocus();
    }

    /**
     * 设置edittext点击显示光标，但是不弹出软键盘
     * @param mActivity
     * @param mEditText
     */
    public static void setEditTextOnFocusNoKeyboard(Activity mActivity, EditText mEditText){
        try {
            Class<EditText> cls = EditText.class;
            Method setSoftInputShownOnFocus;
            setSoftInputShownOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            setSoftInputShownOnFocus.setAccessible(true);
            setSoftInputShownOnFocus.invoke(mEditText, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭软键盘
     *
     * @param view
     */
    public static void closeKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
