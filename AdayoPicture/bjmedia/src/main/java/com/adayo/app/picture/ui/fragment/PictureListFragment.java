package com.adayo.app.picture.ui.fragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.GridLayout;

import com.adayo.app.picture.MyViewModel;
import com.adayo.app.picture.R;
import com.adayo.app.picture.adapter.RecyclerListAdapter;
import com.adayo.app.picture.ui.base.BaseFragment;
import com.adayo.common.picture.bean.ListItem;
import com.adayo.app.picture.ui.base.LogUtil;

import java.util.List;

public class PictureListFragment extends BaseFragment implements View.OnClickListener {
    private static String TAG = "Picture" + PictureListFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private RecyclerListAdapter mRecyclerListAdapter;
    private MyViewModel mMyViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtil.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        LogUtil.i(TAG, "onViewCreated: ");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        LogUtil.i(TAG, "");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onStart() {
        LogUtil.i(TAG, "[ onStart ] in ");
        super.onStart();
    }

    @Override
    public void onResume() {
        LogUtil.v(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    public void onPause() {
        LogUtil.i(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    public void onStop() {
        LogUtil.i(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        LogUtil.i(TAG, "onDestroyView: ");
        super.onDestroyView();
    }
    @Override
    public void onDestroy() {
        LogUtil.i(TAG, "onDestroy: ");
        super.onDestroy();
      //  mMyViewModel.clearPresenter();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtil.i(TAG, "--------------onHiddenChanged----------" + hidden);
        if (!hidden) {
            mMyViewModel.updateListAll();
        }
    }

    @Override
    public int getLayout() {
        LogUtil.i(TAG, " ");
        return R.layout.fragment_picture_list;
    }

    private <T extends View> T bindView(int sourceId) {
        LogUtil.i(TAG, " ");
        return mContentView.findViewById(sourceId);
    }

    @Override
    public void initView() {
        LogUtil.i(TAG, " ");
        mMyViewModel = ViewModelProviders.of(getActivity()).get(MyViewModel.class);
        itemListChanged();
        typeChanged();
        mRecyclerView = (RecyclerView) bindView(R.id.photo_list);
        @SuppressLint("WrongConstant")
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 6, GridLayout.VERTICAL, false);//初始化recycleview网格布局
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerListAdapter = new RecyclerListAdapter(getActivity(),mMyViewModel.getPostion().getValue());
        mRecyclerView.setAdapter(mRecyclerListAdapter);
        mRecyclerListAdapter.setOnItemClickListener(new RecyclerListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LogUtil.d(TAG, "");
                mMyViewModel.itemClick(position);
            }
        });
        mMyViewModel.setListAdapter(mRecyclerListAdapter);
    }

    private void itemListChanged() {
        LogUtil.i(TAG, " ");
        mMyViewModel.getItemList().observe(this, new Observer<List<ListItem>>() {
            @Override
            public void onChanged(@Nullable List<ListItem> mItemList) {
                LogUtil.d(TAG, "List<ListItem>  onChanged ="+mItemList);
                if(mItemList==null){
                    return;
                }
                LogUtil.d(TAG, "List<ListItem>  size ="+mItemList.size());
                mRecyclerListAdapter.setPhotoData(mItemList);
            }
        });
    }

    private void typeChanged() {
        LogUtil.i(TAG, " ");
        mMyViewModel.getType().observe(this, new Observer<com.adayo.common.picture.constant.Constant.MSG_TYPE>() {
            @Override
            public void onChanged(@Nullable com.adayo.common.picture.constant.Constant.MSG_TYPE type) {
                LogUtil.d(TAG, "ListFragment  MSG_TYPE = " + type);
                switch (type) {
                    case MSG_UNMOUNTED:
                    case MSG_NO_FILE:
                    case MSG_NO_DEVICE:
                        setData();
                        break;
                    case MSG_FILE_UNSUPPORTED:
                    default:
                        break;
                }
            }

        });
    }

    private void setData() {
        LogUtil.i(TAG, " ");
        if (mMyViewModel.getItemList().getValue() != null) {
            mMyViewModel.getItemList().getValue().clear();
        }
        mRecyclerListAdapter.setPhotoData(mMyViewModel.getItemList().getValue());
    }

    @Override
    public void initData() {
        LogUtil.v(TAG, " ");
    }



    @Override
    public void onClick(View v) {
        LogUtil.v(TAG, " ");
    }
}
