package com.adayo.app.crossinf.ui.fragment;

import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment  {

    public abstract void registerOnResumeListener(OnFragmentResumeListener listener);
    public  interface OnFragmentResumeListener {
        void onFragmentResume();
    }
}
