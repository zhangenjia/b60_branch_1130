//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.adayo.app.picture.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment<T> extends Fragment {
    protected View mContentView;
    protected ICommunication mCommunication;
    private Context mContext;

    public BaseFragment() {
    }

    public abstract int getLayout();

    public abstract void initView();

    public abstract void initData();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mContentView = inflater.inflate(this.getLayout(), container, false);
        this.mContext = this.getActivity();
            this.initView();
            this.initData();

        return this.mContentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public View getContentView() {
        return this.mContentView;
    }


    public void setCommunication(ICommunication communication) {
        this.mCommunication = communication;
    }
}
