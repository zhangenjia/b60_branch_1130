
package com.adayo.app.setting.view.fragment.factory.product;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adayo.app.base.LogUtil;
import com.adayo.app.setting.configuration.ConfigurationManager;
import com.adayo.app.setting.configuration.DirectMediaBean;
import com.adayo.app.setting.R;
import com.adayo.app.setting.base.BaseFragment;
import com.adayo.app.setting.databinding.FragmentDirectMediaBinding;
import com.adayo.app.setting.view.popwindow.DirectMediaWindow;
import com.adayo.app.setting.viewmodel.fragment.Display.HighDisplayViewModel;
import com.adayo.btsetting.viewmodel.CommonHighlight;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import java.util.ArrayList;
import java.util.List;

import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_DIECT_MEDIA;


public class DirectMediaFragment extends BaseFragment<FragmentDirectMediaBinding> {
    private HighDisplayViewModel mViewModel;
    private CommonHighlight mCommonHighlight;
    private DirectMediaWindow mDirectMediaWindow;
    private List<DirectMediaBean> beanList = new ArrayList<>();
    private final static String TAG = DirectMediaFragment.class.getSimpleName();


    @Override
    protected FragmentDirectMediaBinding bindView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentDirectMediaBinding.inflate(inflater, container, false);
    }

    @Override
    protected void bindData(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.bindData(arguments, savedInstanceState);
        LogUtil.debugD(TAG);
        mViewModel = ViewModelProviders.of(requireActivity()).get(HighDisplayViewModel.class);
        mCommonHighlight = ViewModelProviders.of(requireActivity()).get(CommonHighlight.class);
    }

    private void changeSelected(ViewGroup viewGroup, boolean isSelect) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childSelected = viewGroup.getChildAt(i);
            childSelected.setSelected(isSelect);

        }
    }



    @Override
    protected void initView() {
        super.initView();
        beanList = ConfigurationManager.getInstance().getDirectMediaConfig();
    }

    @Override
    protected void initData() {
        super.initData();
        mCommonHighlight.mCommonHighlightRequest.getCommonHighlight().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer.equals(HIGHLIGHT_DIECT_MEDIA)) {
                    changeSelected(mViewBinding.clDiectMedia, true);
                } else {
                    changeSelected(mViewBinding.clDiectMedia, false);
                }

            }
        });
        mViewModel.mDirectMediaRequest.getDirectMediaLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                for (int i = 0; i < beanList.size(); i++) {
                    if (integer == beanList.get(i).getValue()) {
                        mViewBinding.tvDirectMediaPwd.setText(beanList.get(i).getTitlesId());
                    }
                }
            }
        });
    }

    @Override
    protected void initEvent() {
        super.initEvent();
mViewBinding.btnDirectMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.isPressed()) {
                    mCommonHighlight.mCommonHighlightRequest.requestHighlightModule(HIGHLIGHT_DIECT_MEDIA);
                }
                mDirectMediaWindow = new DirectMediaWindow(getAppContext(), requireActivity(), getViewLifecycleOwner());
                mDirectMediaWindow.showAtLocation(view, Gravity.TOP, 0, (int) (getResources().getDimension(R.dimen.popup_big_y_totop)));
            }
        });

    }

    @Override
    protected void freeEvent() {
        super.freeEvent();
    }

    @Override
    protected void freeData() {
        super.freeData();
    }

    @Override
    protected void freeView() {
        super.freeView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AAOP_HSkin.getInstance().refreshLanguage();
        for (int i = 0; i < beanList.size(); i++) {
            if (mViewModel.mDirectMediaRequest.getDirectMediaLiveData().getValue() == beanList.get(i).getValue()) {
                mViewBinding.tvDirectMediaPwd.setText(beanList.get(i).getTitlesId());
            }
        }
    }


    @Override
    public void onHiddenChanged(boolean isHidden) {
LogUtil.d(TAG,"isHidden = "+isHidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
LogUtil.d(TAG,"isVisibleToUser = "+isVisibleToUser);
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewBinding.clDiectMedia.post(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG,"mViewBinding.clDiectMedia.post");
            }
        });
    }
}