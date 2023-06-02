package com.adayo.app.dvr.function;

import static com.adayo.app.dvr.constant.Constant.DVR_CancelSelAllSelectedDoc;
import static com.adayo.app.dvr.constant.Constant.DVR_DeleteTheSelectedDocument;
import static com.adayo.app.dvr.constant.Constant.DVR_ExitEditMode;
import static com.adayo.app.dvr.constant.Constant.DVR_PageDownCbFunc;
import static com.adayo.app.dvr.constant.Constant.DVR_PageUpCbFunc;
import static com.adayo.app.dvr.constant.Constant.DVR_SelectAllDocCorDocStore;
import static com.adayo.app.dvr.constant.Constant.DVR_St_Cancel_ThumbnailSel;
import static com.adayo.app.dvr.constant.Constant.DVR_St_CurrentPage;
import static com.adayo.app.dvr.constant.Constant.DVR_St_CurrentThumbnail;
import static com.adayo.app.dvr.constant.Constant.DVR_St_Select_ThumbnailSel;
import static com.adayo.app.dvr.constant.Constant.DVR_St_TotalPage;
import static com.adayo.app.dvr.constant.Constant.PAGE_EDIT_WILL;
import static com.adayo.app.dvr.constant.Constant.PAGE_PLAYBACK;
import static com.adayo.app.dvr.constant.TagConstant.APP_TAG;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.adayo.app.dvr.R;
import com.adayo.app.dvr.activity.MainActivity;
import com.adayo.app.dvr.adapter.EditWillAdapter;
import com.adayo.app.dvr.constant.Constant;
import com.adayo.app.dvr.controller.DvrController;
import com.adayo.app.dvr.entity.DvrStatusInfo;
import com.adayo.app.dvr.entity.EditEntity;
import com.adayo.app.dvr.utils.FastClickCheck;
import com.adayo.app.dvr.utils.SystemPropertiesPresenter;
import com.adayo.app.dvr.widget.CustomDialog;
import com.adayo.proxy.deviceservice.AAOP_DeviceServiceManager;

import java.util.ArrayList;
import java.util.List;

public class EditWillControl implements View.OnClickListener {

    private static final String TAG = APP_TAG + EditWillControl.class.getSimpleName();

    private MainActivity mMainActivity;
    private static volatile EditWillControl mModel = null;
    private static final String comFigHigh = "HM6C17A";

    private ConstraintLayout llEditWillBack;
    private ConstraintLayout llEditWillSelect;
    private ConstraintLayout llEditWillAllSelect;
    private ConstraintLayout llEditWillUallSelect;
    private ConstraintLayout llEditWillDelete;

    private ConstraintLayout llEditWillUpload;
    private ImageView ivEditWillUp;
    private ImageView ivEditWillDown;
    private TextView tvEditWillCurPage;
    private TextView tvEditWillTotPage;
    private ImageView ivLineWill;
    private ImageView ivEditWillDelete;
    private TextView tvEditWillDelete;
    private ImageView ivEditWillUpload;
    private TextView tvEditWillUpload;

    private ImageView ivSelectWillStatus;
    private TextView tvSelectWillStatus;
    private ImageView ivWillCullSelectStatus;
    private TextView  tvWillCullSelectStatus;




    private GridView gvEditWill;
    private List<EditEntity> mEditWillEntities;
    private EditWillAdapter mEditWillAdapter;

    public CustomDialog mDeleteWillDialog;
    public CustomDialog mUploadDialog;

    public String model ;
    private int num = 0;

    private EditWillControl(MainActivity mainActivity) {
        mMainActivity = mainActivity;
    }

    public static EditWillControl getEditControlInstance(MainActivity mainActivity) {
        if (mModel == null) {
            synchronized (EditControl.class) {
                if (mModel == null) {
                    mModel = new EditWillControl(mainActivity);
                }
            }
        }
        return mModel;
    }

    public void initView() {
        Log.d(TAG, "initView: ");
        llEditWillBack = (ConstraintLayout) findViewById(R.id.ll_edit_will_back);
        llEditWillSelect  = (ConstraintLayout) findViewById(R.id.ll_edit_will_select);
        llEditWillAllSelect = (ConstraintLayout) findViewById(R.id.ll_edit_will_all_select);
        llEditWillUallSelect = (ConstraintLayout) findViewById(R.id.ll_edit_will_uall_select);
        llEditWillDelete = (ConstraintLayout) findViewById(R.id.ll_edit_will_delete);
        llEditWillUpload = (ConstraintLayout) findViewById(R.id.ll_edit_will_upload);
        ivEditWillUp = (ImageView) findViewById(R.id.iv_edit_will_up);
        ivEditWillDown = (ImageView) findViewById(R.id.iv_edit_will_down);
        tvEditWillCurPage = (TextView) findViewById(R.id.tv_edit_will_cur_page);
        tvEditWillTotPage = (TextView) findViewById(R.id.tv_edit_will_tot_page);
        gvEditWill = (GridView) findViewById(R.id.gv_edit_will);
        ivLineWill = (ImageView) findViewById(R.id.iv_line_will);
        ivSelectWillStatus = (ImageView) findViewById(R.id.iv_select_will_status);
        tvSelectWillStatus = (TextView) findViewById(R.id.tv_select_will_status);
        ivWillCullSelectStatus = (ImageView) findViewById(R.id.iv_will_cull_select_status);
        tvWillCullSelectStatus = (TextView) findViewById(R.id.tv_will_cull_select_status);
        ivEditWillDelete = (ImageView) findViewById(R.id.iv_edit_will_delete);
        tvEditWillDelete = (TextView) findViewById(R.id.tv_edit_will_delete);
        ivEditWillUpload = (ImageView) findViewById(R.id.iv_edit_will_upload);
        tvEditWillUpload = (TextView) findViewById(R.id.tv_edit_will_upload);
         model = SystemPropertiesPresenter.getInstance().getProperty("ro.project.name", "123456");
        Log.d(TAG, "initView: 1111122223333  model ===" + model);
        if (comFigHigh.equals(model)){
            llEditWillUpload.setVisibility(View.VISIBLE);
            ivLineWill.setVisibility(View.VISIBLE);
        }else {
            llEditWillUpload.setVisibility(View.GONE);
            ivLineWill.setVisibility(View.GONE);
        }
        //默认未选中状态
        updateBtnStatus(false);
        selectOrCancelAll(false);

        mEditWillEntities = new ArrayList<>();


    }

    private View findViewById(int id) {
        return mMainActivity.findViewById(id);
    }

    public void initListener() {
        Log.d(TAG, "initListener: ");
        llEditWillBack.setOnClickListener(this);
        llEditWillAllSelect.setOnClickListener(this);
        llEditWillUallSelect.setOnClickListener(this);
        llEditWillDelete.setOnClickListener(this);
        llEditWillUpload.setOnClickListener(this);
        ivEditWillUp.setOnClickListener(this);
        ivEditWillDown.setOnClickListener(this);


        gvEditWill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "notifyChange: edit DVR_St_ThumbnailSel mEditWillEntities.get(position).isCheck() = : "+mEditWillEntities.get(position).isCheck());
                if (!mEditWillEntities.get(position).isCheck()) {
                    DvrController.getInstance().selectThumbnail(position+1);
                } else {
                    DvrController.getInstance().cancelThumbnail(position+1);
                }
                for (EditEntity editEntity : mEditWillEntities) {
                    Log.d(TAG, "onItemClick: " + mEditWillEntities.get(position).isCheck());
                }
                mEditWillAdapter.notifyDataSetChanged();
                if(!DvrStatusInfo.getInstance().ismClickStatus()){
                    DvrStatusInfo.getInstance().setmClickStatus(true);
                }
            }

        });


    }

    private boolean isSelect = false;
    private boolean isSelectAll = false;
    private boolean isRemain = false;
    private boolean isSelectThumbnail = false;
    private String mMode = DvrStatusInfo.getInstance().getMode();
    private String mType = DvrStatusInfo.getInstance().getType();

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: ");
        switch (v.getId()) {
            case R.id.ll_edit_will_back:
                DvrStatusInfo.getInstance().setmEditWillStatus(false);
                DvrStatusInfo.getInstance().setmClickStatus(false);
                DvrStatusInfo.getInstance().setMode(Constant.NORMAL);
                DvrController.getInstance().exitEditMode();
                break;
            case R.id.ll_edit_will_all_select:
                    DvrController.getInstance().selectAll();
                break;
            case  R.id.ll_edit_will_uall_select:
                DvrController.getInstance().cancelSelectAll();
                break;
            case R.id.ll_edit_will_delete:
                int str = 0;
                int str1 = 0;
                if (DvrStatusInfo.getInstance().getAtWillType().equals(Constant.PLAY_TYPE_ATWILL_VIDEO)) {
                    str = R.string.text_edit_delete_tips;
                    if (DvrStatusInfo.getInstance().getMode().equals(Constant.NORMAL)) {
                        str1 = R.string.text_edit_del_normal;
                    } else {
                        str1 = R.string.text_edit_del_emergency;
                    }
                } else {
                    str = R.string.text_edit_delete_file;
                    str1 = R.string.text_edit_delete_file_content;
                }
                mDeleteWillDialog = CustomDialog.getInstance(mMainActivity).initView(CustomDialog.TYPE_NORMAL, R.layout.layout_custom_dialog_mid)
                        .setText(str, str1, 0)
                        .setOkText(R.string.text_edit_delete)
                        .setCancelText(R.string.text_cancel)
                        .setImageView(R.mipmap.icon_singlerow_delete)
                        .initClickListener(new CustomDialog.OnClickListener() {
                            @Override
                            public void ok() {

                                DvrController.getInstance().deleteSelectDoc();
                                DvrStatusInfo.getInstance().setmRefreshStatus(true);
                                DvrStatusInfo.getInstance().setmClickStatus(false);


                            }

                            @Override
                            public void cancel() {

                            }
                        });
                mDeleteWillDialog.showDialog();
                break;
            case R.id.ll_edit_will_upload:

                int text3 = 0;
                int text4 = 0;
                int text5 = 0;

                text3 = R.string.text_edit_upload_title;
                text4 = R.string.text_edit_upload_body;


                mUploadDialog = CustomDialog.getInstance(mMainActivity).initView(CustomDialog.TYPE_NORMAL, R.layout.layout_custom_dialog)
                        .setText(text3, text4, text5)
                        .setOkText(R.string.text_edit_upload_confirm)
                        .setCancelText(R.string.text_cancel)
                        .setImageView(R.mipmap.icon_singlerow_upload)
                        .initClickListener(new CustomDialog.OnClickListener() {
                            @Override
                            public void ok() {
                                DvrController.getInstance().uploadAtWill();
                                DvrStatusInfo.getInstance().setmRefreshStatus(true);
                                DvrStatusInfo.getInstance().setmClickStatus(false);
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                mUploadDialog.showDialog();
                break;
            case R.id.iv_edit_will_up:
                //TODO 全都防抖
                if (!FastClickCheck.isFastClick()) {
                    return;
                }
                //TODO 防抖处理 end
                DvrController.getInstance().pageUp();
                if (DvrStatusInfo.getInstance().ismClickStatus()){
                    noclaue();
                }
                break;
            case R.id.iv_edit_will_down:
                //TODO 全都防抖
                if (!FastClickCheck.isFastClick()) {
                    return;
                }
                //TODO 防抖处理 end
                DvrController.getInstance().pageDown();
                if (DvrStatusInfo.getInstance().ismClickStatus()){
                    noclaue();
                }
                break;
            default:
                break;
        }
    }
    public void noclaue(){
        Message celMessage  =  new Message();
        celMessage.what = 14;
        celMessage.arg1 = 9;
        DvrStatusInfo.getInstance().setmClickStatus(false);
        mEditWillHandler.sendMessage(celMessage);
    }
    /**
     * 没有选择项时，更新删除和移动按钮
     */
    public void updateBtnStatus(boolean select) {
        Log.d(TAG, "updateBtnStatus: select " +select);
        if (select) {
            ivEditWillDelete.setEnabled(true);
            tvEditWillDelete.setEnabled(true);
            ivEditWillUpload.setEnabled(true);
            tvEditWillUpload.setEnabled(true);
            llEditWillDelete.setEnabled(true);
            llEditWillUpload.setEnabled(true);
        } else {
            ivEditWillDelete.setEnabled(false);
            tvEditWillDelete.setEnabled(false);
            ivEditWillUpload.setEnabled(false);
            tvEditWillUpload.setEnabled(false);
            llEditWillDelete.setEnabled(false);
            llEditWillUpload.setEnabled(false);
        }
    }


    /**
     *
     * 随心拍 便捷模式
     * **/
    public void updateWillEditMode() {

        mEditWillHandler.sendEmptyMessage(1);

    }


    /**
     * 刷新多选缩略图
     */
    public void updateSelectStatus(boolean select) {
        Log.d(TAG, "updateSelectStatus: " + select);
        int idx = 0;
        for (EditEntity editEntity : mEditWillEntities) {
            if (select) {
                mEditWillEntities.get(idx).setNum(idx);
                mEditWillEntities.get(idx).setCheck(true);
            } else {
                mEditWillEntities.get(idx).setNum(idx);
                mEditWillEntities.get(idx).setCheck(false);
            }
            idx++;
        }
        if (mEditWillAdapter != null) {
            mEditWillAdapter.notifyDataSetChanged();
        }

    }

    /**
     * 刷新删除视频或图片操作
     */
    public void notifyDeleteStatus() {
        mDeleteWillDialog.dismissDialog();
        if (mEditWillAdapter != null) {
            mEditWillAdapter.notifyDataSetChanged();
        }

    }



    /**
     * 切换全选框状态
     *
     * @param
     */
    private void selectOrCancelAll(boolean isSelectAll) {
        Log.d(TAG, "selectOrCancelAll: " + isSelectAll);
        if (!isSelectAll) {
            llEditWillAllSelect.setVisibility(View.VISIBLE);
            tvSelectWillStatus.setVisibility(View.VISIBLE);
            ivSelectWillStatus.setVisibility(View.VISIBLE) ;
            ivWillCullSelectStatus.setVisibility(View.GONE);
            tvWillCullSelectStatus.setVisibility(View.GONE);
            llEditWillUallSelect.setVisibility(View.GONE);
        } else {
            llEditWillAllSelect.setVisibility(View.GONE);
            tvSelectWillStatus.setVisibility(View.GONE);
            ivSelectWillStatus.setVisibility(View.GONE) ;
            ivWillCullSelectStatus.setVisibility(View.VISIBLE);
            tvWillCullSelectStatus.setVisibility(View.VISIBLE);
            llEditWillUallSelect.setVisibility(View.VISIBLE);
        }


    }
    /**
     * 上翻页
     */
    public void pageUp(int totPage) {
        if (1 == totPage || DvrStatusInfo.getInstance().getTotalPage() <=  0) {
            ivEditWillUp.setImageResource(R.mipmap.icon_up_dis);
            ivEditWillUp.setEnabled(false);
            ivEditWillUp.setClickable(false);
        } else {
            ivEditWillUp.setImageResource(R.mipmap.icon_up_n);
            ivEditWillUp.setEnabled(true);
            ivEditWillUp.setClickable(true);
        }


    }

    /**
     * 下翻页
     */
    public void pageDown(int curPage) {
        if ( DvrStatusInfo.getInstance().getTotalPage() ==  curPage || DvrStatusInfo.getInstance().getTotalPage() <=  0 ) {
            ivEditWillDown.setImageResource(R.mipmap.icon_down_dis);
            ivEditWillDown.setEnabled(false);
            ivEditWillDown.setClickable(false);
        } else {
            ivEditWillDown.setImageResource(R.mipmap.icon_down_n);
            ivEditWillDown.setEnabled(true);
            ivEditWillDown.setClickable(true);
        }


    }
    /**
     * 更新编辑页面
     */
    public void updateEditAtWill() {

            //显示随心拍-照片/视频
        if (comFigHigh.equals(model)){
            llEditWillUpload.setVisibility(View.VISIBLE);
            ivLineWill.setVisibility(View.VISIBLE);
        }else {
            llEditWillUpload.setVisibility(View.GONE);
            ivLineWill.setVisibility(View.GONE);
        }

    }

    public void notifyChange(String funcId, int value) {
        Log.d(TAG, "notifyChange: funcId = " + funcId + ";value = " + value);
        switch (funcId) {
            case DVR_SelectAllDocCorDocStore:
                if (value == 1) {
                    isSelectAll = true;
                    mEditWillHandler.sendEmptyMessageDelayed(2, 500);
                } else {
                    Log.d(TAG, "notifyChange: DVR_SelectAllDocCorDocStore fault");
                }
                break;
            case DVR_CancelSelAllSelectedDoc:
                if (value == 1) {
                    isSelectAll = false;
                    mEditWillHandler.sendEmptyMessageDelayed(3, 500);
                } else {
                    Log.d(TAG, "notifyChange: DVR_CancelSelAllSelectedDoc fault");
                }
                break;
            case DVR_DeleteTheSelectedDocument:
                if (value == 1) {
                    mEditWillHandler.sendEmptyMessage(9);

                } else {
                    Log.d(TAG, "notifyChange: DVR_DeleteTheSelectedDocument fault");
                }
                break;
            case DVR_ExitEditMode:
                if (value == 1) {
                    Log.d(TAG, "notifyChange: DVR_ExitEditMode success");
                    mEditWillHandler.sendEmptyMessage(4);

                } else {
                    Log.d(TAG, "notifyChange: DVR_ExitEditMode fault");
                }
                break;
            case DVR_St_Select_ThumbnailSel:
                Log.d(TAG, "EditWillAdapter:  1mEditEntities = " + mEditWillEntities.size());
                Message selMessage  =  new Message();
                selMessage.what = 6;
                selMessage.arg1 = value;
                mEditWillHandler.sendMessage(selMessage);
                break;
            case DVR_St_Cancel_ThumbnailSel:
                Message celMessage  =  new Message();
                celMessage.what = 14;
                celMessage.arg1 = value;
                mEditWillHandler.sendMessage(celMessage);
                break;
            case DVR_St_CurrentThumbnail:
                num = value;
                Log.d(TAG, "notifyChange: EditWillAdapter thumbnail value=" + num);
                mEditWillEntities.clear();
                for (int i = 0; i < num; i++) {
                    EditEntity editEntity = new EditEntity();
                    editEntity.setNum(i);
                    Log.d(TAG, "setNum: setNum="+i);
                    editEntity.setCheck(false);
                    mEditWillEntities.add(editEntity);

                }
                if (mEditWillAdapter == null){
                    mEditWillAdapter = new EditWillAdapter(mMainActivity, mEditWillEntities);
                    mEditWillHandler.sendEmptyMessage(5);
                }else {
                    mEditWillHandler.sendEmptyMessage(15);
                }

                break;
            case DVR_St_TotalPage:
                Message msgtota = new Message();
                msgtota.what = 7;
                msgtota.obj = value;
                mEditWillHandler.sendMessage(msgtota);
                break;
            case DVR_St_CurrentPage:
                Message msgCur = new Message();
                msgCur.what = 8;
                msgCur.obj = value;
                mEditWillHandler.sendMessage(msgCur);
                break;
            case DVR_PageUpCbFunc:
                Message msgPageUp = new Message();
                msgPageUp.what = 12;
                msgPageUp.arg1 = value;
                mEditWillHandler.sendMessage(msgPageUp);
                break;
            case DVR_PageDownCbFunc:
                mEditWillHandler.sendEmptyMessage(13);
                break;
            default:
                break;
        }
    }
    private Handler mEditWillHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    mMainActivity.changePage(PAGE_EDIT_WILL);
                    if (mEditWillAdapter != null) {
                        mEditWillAdapter.setEditMode(true);

                        if (DvrStatusInfo.getInstance().getAtWillType() == Constant.PLAY_TYPE_ATWILL_PHOTO) {
                            mEditWillAdapter.setEditPlay(false);
                        } else {
                            mEditWillAdapter.setEditPlay(true);
                        }
                    }
                    updateSelectStatus(false);
                    break;
                case 2:
                    updateSelectStatus(true);
                    selectOrCancelAll(true);
                    updateBtnStatus(true);
                    break;
                case 3:
                    updateSelectStatus(false);
                    selectOrCancelAll(false);
                    updateBtnStatus(false);
                    break;
                case 4:
                    mMainActivity.changePage(PAGE_PLAYBACK);
                    updateSelectStatus(false);
                    selectOrCancelAll(false);
                    updateBtnStatus(false);
                    break;
                case 5:
                    Log.d(TAG, "handleMessage: edit ===" + isSelectAll);
                    if (!isSelectAll) {
                        if (mEditWillAdapter != null) {
                            for(int i=0;i<mEditWillEntities.size();i++){
                                mEditWillEntities.get(i).setCheck(false);
                            }
                            gvEditWill.setAdapter(mEditWillAdapter);

                            mEditWillAdapter.setEditMode(true);

                            updateBtnStatus(false);


                            if (DvrStatusInfo.getInstance().getAtWillType() == Constant.PLAY_TYPE_ATWILL_VIDEO) {

                                mEditWillAdapter.setEditPlay(true);
                            } else if (DvrStatusInfo.getInstance().getAtWillType() == Constant.PLAY_TYPE_ATWILL_PHOTO) {
                                mEditWillAdapter.setEditPlay(false);

                            }
                        }

                    }
                    break;
                case 6:
                    for(int i=0;i<mEditWillEntities.size();i++){
                        if(mEditWillEntities.get(i).isNum() == msg.arg1 - 1){
                            mEditWillEntities.get(i).setCheck(true);
                        }
                    }
                    isRemain = false;
                    mEditWillAdapter.notifyDataSetChanged();
                    mEditWillAdapter.setEditMode(true);
                    for (int a=0;a<mEditWillEntities.size();a++){
                        if(mEditWillEntities.get(a).isCheck() == false){
                            Log.d(TAG, "EditWillAdapter: isRemain true" );
                            isRemain = true;
                            break;
                        }
                        Log.d(TAG, "EditWillAdapter: isRemain false" );
                    }
                    if(!isRemain){
                        Log.d(TAG, "EditWillAdapter: isRemain = "  +isRemain);
                        isSelectAll = true;
                        updateSelectStatus(true);
                    }else{
                        Log.d(TAG, "EditWillAdapter: isRemain  = "+ isRemain );
                        isSelectAll = false;
                    }
                    selectOrCancelAll(isSelectAll);
                    updateBtnStatus(true);
                    if (DvrStatusInfo.getInstance().getAtWillType() == Constant.PLAY_TYPE_ATWILL_VIDEO) {

                        mEditWillAdapter.setEditPlay(true);
                    } else if (DvrStatusInfo.getInstance().getAtWillType() == Constant.PLAY_TYPE_ATWILL_PHOTO) {
                        mEditWillAdapter.setEditPlay(false);

                    }
                    break;
                case 7:
                    tvEditWillTotPage.setText(msg.obj.toString());
                    break;
                case 8:
                    tvEditWillCurPage.setText(msg.obj.toString());
                    pageDown((Integer) msg.obj);
                    pageUp((Integer) msg.obj);
                    break;
                case 9:
                    notifyDeleteStatus();
                    selectOrCancelAll(false);
                    updateBtnStatus(false);
                    mEditWillAdapter.setEditMode(true);
                    isSelectAll = false;
                    if (DvrStatusInfo.getInstance().getAtWillType() == Constant.PLAY_TYPE_ATWILL_PHOTO) {
                        mEditWillAdapter.setEditPlay(false);
                    } else {
                        mEditWillAdapter.setEditPlay(true);
                    }
                    updateSelectStatus(false);
                    break;
                case 12:
                case 13:
                    updateSelectStatus(false);
                    updateBtnStatus(false);
                    selectOrCancelAll(false);
                    break;
                case 14:
                    for(int i=0;i<mEditWillEntities.size();i++){
                        if(mEditWillEntities.get(i).isNum() == msg.arg1-1){
                            mEditWillEntities.get(i).setCheck(false);
                        }
                    }
                    isRemain = false;
                    mEditWillAdapter.notifyDataSetChanged();
                    mEditWillAdapter.setEditMode(true);
                    for (int a=0;a<mEditWillEntities.size();a++){
                        if(mEditWillEntities.get(a).isCheck() == true){
                            isRemain = true;
                            break;
                        }
                    }
                    if(!isRemain){
                        updateBtnStatus(false);
                        updateSelectStatus(false);

                    }
                    selectOrCancelAll(false);
                    if (DvrStatusInfo.getInstance().getAtWillType() == Constant.PLAY_TYPE_ATWILL_VIDEO) {

                        mEditWillAdapter.setEditPlay(true);
                    } else if (DvrStatusInfo.getInstance().getAtWillType() == Constant.PLAY_TYPE_ATWILL_PHOTO) {
                        mEditWillAdapter.setEditPlay(false);

                    }
                    break;
                case 15:
                    selectOrCancelAll(false);
                    updateBtnStatus(false);
                    mEditWillAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;

            }
        }
    };
}
