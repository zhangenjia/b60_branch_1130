package com.adayo.app.picture;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;

import com.adayo.app.picture.adapter.RecyclerListAdapter;
import com.adayo.app.picture.ui.base.LogUtil;
import com.adayo.app.picture.ui.photoview.PhotoView;
import com.adayo.bpresenter.picture.contracts.IPhotoListContract;
import com.adayo.bpresenter.picture.contracts.IPhotoPlayContract;
import com.adayo.bpresenter.picture.contracts.IPictureWndManagerContract;
import com.adayo.bpresenter.picture.presenters.PhotoListPresenter;
import com.adayo.bpresenter.picture.presenters.PhotoPlayPresenter;
import com.adayo.bpresenter.picture.presenters.PictureWndManagerPresenter;
import com.adayo.common.picture.bean.ListItem;
import com.adayo.common.picture.constant.Constant;
import com.adayo.commontools.MediaConstants;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngSwitchManager;
import com.adayo.proxy.infrastructure.sourcemng.ISourceActionCallBack;
import com.adayo.proxy.media.mediascanner.AdayoMediaScanner;
import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.adayo.app.picture.constant.Constant.FRAGMENT_PICTURE_LIST;
import static com.adayo.app.picture.constant.Constant.FRAGMENT_PICTURE_PLAY;
import static com.adayo.app.picture.constant.Constant.FRAGMENT_PICTURE_PLAY_NEXT;
import static com.adayo.app.picture.constant.Constant.FRAGMENT_PICTURE_PLAY_PAUSE;
import static com.adayo.app.picture.constant.Constant.FRAGMENT_PICTURE_PLAY_PLAY_OR_STOP;
import static com.adayo.app.picture.constant.Constant.FRAGMENT_PICTURE_PLAY_PREV;
import static com.adayo.app.picture.constant.Constant.FRAGMENT_PICTURE_PLAY_ROTATE;
import static com.adayo.app.picture.constant.Constant.PICTURE_APP_STATE_FINISH;
import static com.adayo.app.picture.constant.Constant.PICTURE_LOADING_VIEW_G;
import static com.adayo.app.picture.constant.Constant.PICTURE_MSG_G;
import static com.adayo.common.picture.constant.Constant.MSG_TYPE.MSG_NO_DEVICE;
import static com.adayo.common.picture.constant.Constant.MSG_TYPE.MSG_NO_FILE;

public class MyViewModel extends ViewModel implements IPhotoPlayContract.IPhotoPlayView {
    private final static String TAG = "Picture" + MyViewModel.class.getSimpleName();
    private MutableLiveData<Integer> tag = new MutableLiveData<>();//UI层级改变显隐
    private MutableLiveData<Integer> postion = new MutableLiveData<>();//图片列表选中框
    private Context mContext;
    private MutableLiveData<Constant.MSG_TYPE> type = new MutableLiveData<>();//设备改版
    private MutableLiveData<Boolean> isctrlbar = new MutableLiveData<>();//控制栏显示状态
    private MutableLiveData<Map<String, String>> display = new MutableLiveData<>();//显示图片全屏数据
    private MutableLiveData<Integer> appstate = new MutableLiveData<>();//app生命周期
    private MutableLiveData<List<ListItem>> mItemList = new MutableLiveData<>();//显示图片列表数据
    private MutableLiveData<Boolean> isplay = new MutableLiveData<>();//是否为全屏状态
    private ISourceActionCallBack iSourceActionCallBack;//语音回调
    private PhotoPlayPresenter mPhotoPlayPresenter;
    private AdayoMediaScanner mAdayoMediaScanner;
    private PictureWndManagerPresenter mPictureWndManagerPresenter;
    private PhotoListPresenter mListPresenter;
    private IPhotoListContract.IPhotoListView mPhotoListView;
    private RecyclerListAdapter mRecyclerListAdapter;

    public void setListAdapter(RecyclerListAdapter recyclerListAdapter) {
        mRecyclerListAdapter = recyclerListAdapter;
    }

    public void initIPhotoListView() {
        /**
         * P层接口创建列表
         */
        mPhotoListView = new IPhotoListContract.IPhotoListView() {
            @Override
            public void setPresenter(IPhotoListContract.IPhotoListPresenter iPhotoListPresenter) {//初始化列表状态P层
                LogUtil.d(TAG, "");
                mListPresenter = (PhotoListPresenter) iPhotoListPresenter;
            }

            @Override
            public void removePresenter(IPhotoListContract.IPhotoListPresenter iPhotoListPresenter) {
                LogUtil.d(TAG, "");
            }

            /**
             * 更新播放列表
             *
             * @param storage_port USB口
             * @param list_type    列表类型  这个后面的枚举里面应该有介绍
             * @param list         播放列表
             */
            @Override
            public void updatePhotoListView(MediaConstants.STORAGE_PORT storage_port, PhotoListPresenter.LIST_TYPE list_type, List<ListItem> list) {
                LogUtil.d(TAG, "list =" + list);
                if (null != list && list.size() > 0) {
                    LogUtil.d(TAG, "list size=" + list.size());
                    mItemList.postValue(list);
                }
            }

            /**
             * 标记当前播放图片
             *
             * @param i 当前播放图片的序号
             */
            @Override
            public void updateCurrentIndex(int i) {
                LogUtil.d(TAG, " = " + i);
                postion.postValue(i);
                changeListSelected(i);
            }

            @Override
            public void updateCurrentPlayPath(String s) {

            }

            /**
             * 标记当前列表类型
             *
             * @param list_type 当前列表类型
             */
            @Override
            public void updateCurrentListTab(PhotoListPresenter.LIST_TYPE list_type) {
                LogUtil.i(TAG, "");
            }

            /**
             * 更新USB TAB显示（页面上需要显示几个USB）
             *
             * @param list 设备列表
             */
            @Override
            public void updateUSBTab(List<MediaConstants.STORAGE_PORT> list) {
                LogUtil.d(TAG, "");
            }

            /**
             * 标记当前选中USB
             *
             * @param storage_port USB设备
             */
            @Override
            public void selectUSBTab(MediaConstants.STORAGE_PORT storage_port) {
                LogUtil.i(TAG, "");
            }

            /**
             * 更新back键显示
             *
             * @param listItem 列表bean对象
             */
            @Override
            public void updateBackBtn(ListItem listItem) {
                LogUtil.d(TAG, "");
            }

            @Override
            public void showNoFile(boolean b) {
                LogUtil.i(TAG, "b =" + b);
                if (b) {
                    setType(MSG_NO_FILE);
                }
            }

            @Override
            public void showNoDevice(boolean b) {
                LogUtil.i(TAG, "b = " + b);
                if (b){
                    setType(MSG_NO_DEVICE);}
            }
        };

    }


    private IPictureWndManagerContract.IView mPictureWndView = new IPictureWndManagerContract.IView() {
        @Override
        public boolean showPage(Constant.SHOW_PAGE show_page, Bundle bundle) {

            return false;
        }

        @Override
        public boolean showPages(Set<Constant.SHOW_PAGE> set, Bundle bundle) {
            return false;
        }

        /**
         * 显示加载中界面
         * @param msg_type
         */
        @Override
        public void showLoading(Constant.MSG_TYPE msg_type) {
            LogUtil.d(TAG, "");
            setType(msg_type);
        }

        /**
         * 隐藏加载中界面
         */
        @Override
        public void hideLoading() {
            LogUtil.d(TAG, "");
            setTag(PICTURE_LOADING_VIEW_G);
        }

        @Override
        public void showMessage(Constant.MSG_TYPE msg_type) {
            LogUtil.d(TAG, "");
            setType(msg_type);
        }

        @Override
        public void hideMsg() {
            LogUtil.d(TAG, "");
            tag.postValue(PICTURE_MSG_G);
        }

        @Override
        public void showList() {
            LogUtil.d(TAG, "");
            tag.postValue(FRAGMENT_PICTURE_LIST);
        }

        @Override
        public void showPlay() {
            LogUtil.d(TAG, "");
            setTag(FRAGMENT_PICTURE_PLAY);
            setIsctrlbar(true);
        }

        @Override
        public void showDrivingWarning() {
            LogUtil.d(TAG, "");
        }

        @Override
        public void dismissDrivingWarning() {
            LogUtil.d(TAG, "");
        }

        @Override
        public void finishUI() {
            LogUtil.d(TAG, "");
        }
    };


    public MyViewModel() {
        LogUtil.i(TAG, "");
        iSourceActionCallBack = new ISourceActionCallBack.Stub() {//语音关闭回调
            @Override
            public void SourceOff() throws RemoteException {//通过语音控制触发的关闭软件的逻辑
                LogUtil.d(TAG, "iSourceActionCallBack");
                setAppstate(PICTURE_APP_STATE_FINISH);
                setAppstate(0);
            }
        };
        SrcMngSwitchManager.getInstance().registeAppCallBackFunc(iSourceActionCallBack, AdayoSource.ADAYO_SOURCE_USB_PHOTO);//vr关闭应用，语音关闭
        mAdayoMediaScanner = AdayoMediaScanner.getAdayoMediaScanner();
        mAdayoMediaScanner.connService();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        SrcMngSwitchManager.getInstance().unRegisteAppCallBackFunc(iSourceActionCallBack, AdayoSource.ADAYO_SOURCE_USB_PHOTO);
    }

    /**
     * //以UI为界面分页tag，各个UI层级显隐改变的回调 ,列表状态，全屏状态，显示加载中界面。。。
     *
     * @return
     */
    public MutableLiveData<Integer> getTag() {
        LogUtil.d(TAG, "");
        if (tag.getValue() == null) {
            tag.setValue(0);
        }
        return tag;
    }


    public void setTag(int x) {
        LogUtil.d(TAG, "setTag = " + x);
        tag.postValue(x);
    }

    /**
     * 媒体扫描相关改变触发的回调：设备拔出。。。
     *
     * @return
     */
    public MutableLiveData<Constant.MSG_TYPE> getType() {
        LogUtil.d(TAG, "");
        return type;
    }


    public void setType(Constant.MSG_TYPE msg_type) {
        LogUtil.d(TAG, "setType = " + msg_type);
        type.postValue(msg_type);
    }

    /**
     * 应用生命周期状态改变
     *
     * @return
     */
    public MutableLiveData<Integer> getAppstate() {
        LogUtil.d(TAG, "");
        if (appstate.getValue() == null) {
            appstate.setValue(0);
        }
        return appstate;
    }

    public void setAppstate(int x) {
        LogUtil.d(TAG, "");
        appstate.postValue(x);
    }

    /**
     * P层传输图片到列表界面
     *
     * @return
     */
    public MutableLiveData<List<ListItem>> getItemList() {
        return mItemList;
    }

    public void setItemList(List<ListItem> itemList) {
        mItemList.postValue(itemList);
    }

    public MutableLiveData<Boolean> getIsctrlbar() {
        return isctrlbar;
    }

    public void setIsctrlbar(Boolean bShow) {
        LogUtil.d(TAG, "setIsctrlbar :bShow =  " + bShow);
        isctrlbar.postValue(bShow);
    }

    public void setContext(Context context) {
        LogUtil.d(TAG, "");
        mContext = context.getApplicationContext();
    }

    /**
     * 初始化列表P层
     */
    public void initPresenter() {
        LogUtil.d(TAG, "");
        if (null == mPictureWndManagerPresenter){
            mPictureWndManagerPresenter = PictureWndManagerPresenter.getInstance(mContext);}
        mPictureWndManagerPresenter.setView(mPictureWndView);
        mPictureWndManagerPresenter.init();
        if (mListPresenter == null) {
            mListPresenter = PhotoListPresenter.getInstance();
        }
        mListPresenter.setView(mPhotoListView);
        mListPresenter.updateListAll();
    }


    public void initPlayPresenter() {
        LogUtil.d(TAG, "");
        SrcMngSwitchManager.getInstance().notifyServiceUIChange(AdayoSource.ADAYO_SOURCE_USB_PHOTO, "com.adayo.app.media");
        if (null == mPhotoPlayPresenter) {
            mPhotoPlayPresenter = PhotoPlayPresenter.getInstance();
            mPhotoPlayPresenter.setView(this);
            mPhotoPlayPresenter.update();
            isctrlbar.postValue(true);
        }

    }

    public void clearPlayPresenter(PhotoView mFullPhoto) {
        LogUtil.d(TAG, "");
        if (null != mPhotoPlayPresenter) {
            mPhotoPlayPresenter.removeView(this);
            Glide.clear(mFullPhoto);
        }
    }

    public void updateListAll() {
        LogUtil.d(TAG, "");
        mListPresenter.updateListAll();
    }

    public void updateList() {
        LogUtil.d(TAG, "");
        mListPresenter.update();
    }

    public void clearPresenter() {
        LogUtil.d(TAG, "");
        if (null != mListPresenter){
            mListPresenter.removeView(mPhotoListView);}
        if (null != mPictureWndManagerPresenter){
            mPictureWndManagerPresenter.removeView(mPictureWndView);}
    }

    /**
     * 图片列表状态图片被点击位置
     *
     * @param position
     */
    public void itemClick(int position) {
        LogUtil.d(TAG, "");

        mListPresenter.itemClick(position);
    }

    public void setPictureWndManagerPresenter(PictureWndManagerPresenter pictureWndManagerPresenter) {
        this.mPictureWndManagerPresenter = pictureWndManagerPresenter;
    }

    //---------------------------------------------PLAY-------------------

    public MutableLiveData<Boolean> getIsplay() {
        return isplay;
    }

    public void setIsplay(Boolean x) {
        LogUtil.d(TAG, "setIsplay = " + x);
        isplay.postValue(x);
    }

    @Override
    public void showFullScreenPhoto(boolean b) {
        LogUtil.d(TAG, "");

    }

    /**
     * 显示全屏图片
     *
     * @param path
     * @param name
     */
    @Override
    public void display(String path, String name) {
        LogUtil.d(TAG, "");
        setDisplay(path, name);
    }

    /**
     * 更新播放暂停状态
     *
     * @param x
     */
    @Override
    public void updatePlayPauseBtn(boolean x) {
        LogUtil.d(TAG, "x = " + x);
        setIsplay(x);
    }

    @Override
    public void setPresenter(IPhotoPlayContract.IPhotoPlayPresenter iPhotoPlayPresenter) {
        LogUtil.d(TAG, "");
        mPhotoPlayPresenter = (PhotoPlayPresenter) iPhotoPlayPresenter;
    }

    @Override
    public void removePresenter(IPhotoPlayContract.IPhotoPlayPresenter iPhotoPlayPresenter) {

    }

    /**
     * 控制栏几个按钮点击
     *
     * @param x 点击了哪个按钮
     */
    public void playControl(int x) {
        LogUtil.d(TAG, "playControl = " + x);
        switch (x) {
            case FRAGMENT_PICTURE_PLAY_PAUSE:
                mPhotoPlayPresenter.pause();
                break;
            case FRAGMENT_PICTURE_PLAY_PREV:
                mPhotoPlayPresenter.prev();
                break;
            case FRAGMENT_PICTURE_PLAY_NEXT:
                mPhotoPlayPresenter.next();
                break;
            case FRAGMENT_PICTURE_PLAY_PLAY_OR_STOP:
                mPhotoPlayPresenter.playOrStop(5000);
                break;
            case FRAGMENT_PICTURE_PLAY_ROTATE:
                break;
            default:
                break;
        }
    }

    public MutableLiveData<Map<String, String>> getDisplay() {
        return display;
    }

    /**
     * 放图片全屏
     *
     * @param path
     * @param name
     */
    public void setDisplay(String path, String name) {
        LogUtil.d(TAG, "");
        HashMap<String, String> map = new HashMap<>(1);
        map.put(path, name);
        display.postValue(map);
    }

    public void changeListSelected(int x) {
        mRecyclerListAdapter.setSelected(x);
    }

    public MutableLiveData<Integer> getPostion() {
        if (postion.getValue() == null) {
            postion.setValue(-1);
        }
        return postion;
    }
}
