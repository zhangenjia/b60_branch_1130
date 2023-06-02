package com.adayo.app.setting.view.popwindow;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.PopupWindow;

import com.adayo.app.setting.configuration.ConfigurationManager;
import com.adayo.app.setting.configuration.DirectMediaBean;
import com.adayo.app.setting.R;
import com.adayo.app.setting.databinding.PopupDirectMediaBinding;
import com.adayo.app.setting.view.adapter.DirectMediaAdapter;
import com.adayo.app.setting.viewmodel.fragment.Display.HighDisplayViewModel;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.app.base.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DirectMediaWindow extends PopupWindow {
    private final static String TAG = DirectMediaWindow.class.getSimpleName();
    private Context mContext;
    private LifecycleOwner mViewLifecycleOwner;
    private PopupDirectMediaBinding mPopViewBinding;
    private HighDisplayViewModel mViewModel;
    private DirectMediaAdapter mSimpleAdapter;
    private List<Map<String, Object>> mDataList;


    private List<DirectMediaBean> mDirectMediaBeans=new ArrayList<>();
    public DirectMediaWindow(Context context, FragmentActivity activity, LifecycleOwner lifecycleOwner) {
        super(context);
       LogUtil.debugD(TAG, "");
        mContext = context;
        mViewLifecycleOwner = lifecycleOwner;
        mViewModel = ViewModelProviders.of(activity).get(HighDisplayViewModel.class);
        initView();
        initData();
        initEvent();

    }

    public void initView() {
       LogUtil.debugD(TAG, "");
        String[] from = {"title", "info"};
        int[] to = {R.id.tv_popup_media_title1, R.id.tv_popup_media_title2};
        mPopViewBinding = PopupDirectMediaBinding.inflate(AAOP_HSkin.getLayoutInflater(mContext));this.setContentView(mPopViewBinding.getRoot());this.setWidth((int) (mContext.getResources().getDimension(R.dimen.popup_big_width)));
        this.setHeight((int) (mContext.getResources().getDimension(R.dimen.popup_big_height)));
        this.setFocusable(true);
        this.setClippingEnabled(false);
        this.setBackgroundDrawable(null);

        mDirectMediaBeans= ConfigurationManager.getInstance().getDirectMediaConfig();
        mDataList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < mDirectMediaBeans.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>(20);
            map.put("title", mContext.getString(mDirectMediaBeans.get(i).getTitlesId()));
            map.put("info", mContext.getString(mDirectMediaBeans.get(i).getInfosId()));
            mDataList.add(map);
        }
        AAOP_HSkin.getWindowViewManager().addWindowView(mPopViewBinding.getRoot());
        mSimpleAdapter = new DirectMediaAdapter(mContext, mDataList, R.layout.grid_item_direct_media, from, to);mPopViewBinding.gvPopupMediaKey.setAdapter(mSimpleAdapter);

    }

    private void initData() {
        mViewModel.mDirectMediaRequest.getDirectMediaLiveData().observe(mViewLifecycleOwner, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                for (int i = 0; i < mDirectMediaBeans.size(); i++) {
                    if(integer==mDirectMediaBeans.get(i).getValue()){
                        mSimpleAdapter.setMpostion(i);
                    }
                }
            }
        });
    }


    private void initEvent() {
        mPopViewBinding.btnPopupMediaClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               LogUtil.debugD(TAG, "btnPopupMediaClose");
                new Handler().postDelayed(DirectMediaWindow.super::dismiss, mContext.getResources().getInteger(android.R.integer.config_shortAnimTime));
            }
        });
        mPopViewBinding.gvPopupMediaKey.setRecyclerListener(new AbsListView.RecyclerListener() {
            @Override
            public void onMovedToScrapHeap(View view) {

            }
        });

mPopViewBinding.gvPopupMediaKey.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
               LogUtil.debugD(TAG, "POS = " + pos + "view = " + view.getId());
                mSimpleAdapter.setMpostion(pos);
                mViewModel.mDirectMediaRequest.requestDirectMediaButton(mDirectMediaBeans.get(pos).getValue());
            }
        });
    }

}
