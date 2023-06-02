//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.adayo.app.picture.ui.base;

import android.support.v4.app.Fragment;

import com.adayo.app.interfaces.IFragmentTouchListener;

import java.util.List;

public interface ICommunication {
    void showPage(int var1);

    void showPages(List<Integer> var1);

    void hidePage(int var1);

    void hidePages(List<Integer> var1);

    List<Integer> getCurrentPages();

    void registerFragmentTouchListener(IFragmentTouchListener var1);

    void unRegisterFragmentTouchListener(IFragmentTouchListener var1);

    void addFragment(int var1, int var2, Fragment var3);

    void removeFragment(int var1, int var2, Fragment var3);
}
