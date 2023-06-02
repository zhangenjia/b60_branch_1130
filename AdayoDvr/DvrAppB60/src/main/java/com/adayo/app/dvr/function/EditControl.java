package com.adayo.app.dvr.function;

import static com.adayo.app.dvr.constant.Constant.*;
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
import com.adayo.app.dvr.adapter.EditAdapter;
import com.adayo.app.dvr.constant.Constant;
import com.adayo.app.dvr.controller.DvrController;
import com.adayo.app.dvr.entity.DvrStatusInfo;
import com.adayo.app.dvr.entity.EditEntity;
import com.adayo.app.dvr.utils.FastClickCheck;
import com.adayo.app.dvr.widget.CustomDialog;

import java.util.ArrayList;
import java.util.List;

public class EditControl implements View.OnClickListener {

    private static final String TAG = APP_TAG + EditControl.class.getSimpleName();

    private MainActivity mMainActivity;
    private static volatile EditControl mModel = null;

    private ConstraintLayout llEditBack;
    private ConstraintLayout llEditSelect;
    private ConstraintLayout llEditAllSelect;
    private ConstraintLayout llEditUallSelect;
    private ConstraintLayout llEditDelete;
    private ConstraintLayout llEditMove;
    private ImageView ivEditMove;
    private ImageView ivOutEditMove;
    private ImageView ivEditUp;
    private ImageView ivEditDown;
    private TextView tvEditCurPage;
    private TextView tvEditTotPage;
    private ImageView ivLine;
    private ImageView ivEditDelete;
    private TextView tvEditDelete;

    private ImageView ivSelectStatus;
    private TextView tvSelectStatus;
    private ImageView ivCullSelectStatus;
    private TextView  tvCullSelectStatus;

    private TextView tvEditMove;
    private TextView tvOutEditMove;


    private GridView gvEdit;
    private List<EditEntity> mEditEntities;
    private EditAdapter mEditAdapter;

    public CustomDialog mDeleteDialog;
    public CustomDialog mMoveDialog;
    public CustomDialog mUploadDialog;

    public boolean isFromAtWill = false;
    private int num = 0;
    private int selNum = 0;
    private int celNum = 0;
    private EditEntity editEntity;

    private EditControl(MainActivity mainActivity) {
        mMainActivity = mainActivity;
    }

    public static EditControl getEditControlInstance(MainActivity mainActivity) {
        if (mModel == null) {
            synchronized (EditControl.class) {
                if (mModel == null) {
                    mModel = new EditControl(mainActivity);
                }
            }
        }
        return mModel;
    }

    public void initView() {
        Log.d(TAG, "initView: ");
        llEditBack = (ConstraintLayout) findViewById(R.id.ll_edit_back);
        llEditSelect =  (ConstraintLayout) findViewById(R.id.ll_edit_select);
        llEditAllSelect = (ConstraintLayout) findViewById(R.id.ll_edit_all_select);
        llEditUallSelect = (ConstraintLayout) findViewById(R.id.ll_edit_uall_select);
        llEditDelete = (ConstraintLayout) findViewById(R.id.ll_edit_delete);
        llEditMove = (ConstraintLayout) findViewById(R.id.ll_edit_move);
        ivEditMove = (ImageView) findViewById(R.id.iv_edit_move);
        ivOutEditMove = (ImageView) findViewById(R.id.iv_out_edit_move);
        ivEditUp = (ImageView) findViewById(R.id.iv_edit_up);
        ivEditDown = (ImageView) findViewById(R.id.iv_edit_down);
        tvEditCurPage = (TextView) findViewById(R.id.tv_edit_cur_page);
        tvEditTotPage = (TextView) findViewById(R.id.tv_edit_tot_page);
        tvEditMove = (TextView) findViewById(R.id.tv_edit_move);
        tvOutEditMove = (TextView) findViewById(R.id.tv_out_edit_move);
        gvEdit = (GridView) findViewById(R.id.gv_edit);
        ivLine = (ImageView) findViewById(R.id.iv_line);
        ivSelectStatus = (ImageView) findViewById(R.id.iv_select_status);
        tvSelectStatus = (TextView) findViewById(R.id.tv_select_status);
        ivCullSelectStatus = (ImageView) findViewById(R.id.iv_cull_select_status);
        tvCullSelectStatus  = (TextView) findViewById(R.id.tv_cull_select_status);
        ivEditDelete = (ImageView) findViewById(R.id.iv_edit_delete);
        tvEditDelete = (TextView) findViewById(R.id.tv_edit_delete);

        //默认未选中状态
        updateBtnStatus(false);
        selectOrCancelAll(false);

        mEditEntities = new ArrayList<>();



    }

    private View findViewById(int id) {
        return mMainActivity.findViewById(id);
    }

    public void initListener() {
        Log.d(TAG, "initListener: ");
        llEditBack.setOnClickListener(this);
        llEditAllSelect.setOnClickListener(this);
        llEditUallSelect.setOnClickListener(this);
        llEditDelete.setOnClickListener(this);
        llEditMove.setOnClickListener(this);
        ivEditUp.setOnClickListener(this);
        ivEditDown.setOnClickListener(this);


        gvEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "notifyChange: edit DVR_St_ThumbnailSel mEditEntities.get(position).isCheck() = : "+mEditEntities.get(position).isCheck());
                if (!mEditEntities.get(position).isCheck()) {
                    DvrController.getInstance().selectThumbnail(position+1);
                } else {
                    DvrController.getInstance().cancelThumbnail(position+1);
                }
                for (EditEntity editEntity : mEditEntities) {
                    Log.d(TAG, "onItemClick: " + mEditEntities.get(position).isCheck());
                }

                mEditAdapter.notifyDataSetChanged();
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
            case R.id.ll_edit_back:
                DvrStatusInfo.getInstance().setmEditStatus(false);
                DvrStatusInfo.getInstance().setMode(Constant.NORMAL);
                DvrStatusInfo.getInstance().setmClickStatus(false);
                DvrController.getInstance().exitEditMode();
                break;
            case R.id.ll_edit_all_select:
                DvrController.getInstance().selectAll();
                break;
            case R.id.ll_edit_uall_select:
                DvrController.getInstance().cancelSelectAll();
                break;
            case R.id.ll_edit_delete:

                int str = 0;
                int str1 = 0;
                if (DvrStatusInfo.getInstance().getType().equals(Constant.PLAY_TYPE_VIDEO)) {
                    str = R.string.text_edit_delete_tips;
                    if (DvrStatusInfo.getInstance().getMode().equals(Constant.NORMAL)) {
                        str1 = R.string.text_edit_del_normal;
                    } else {
                        str1 = R.string.text_edit_del_emergency;
                    }
                } else {
                    str = R.string.text_edit_delete_picture;
                    str1 = R.string.text_edit_delete_picture1;
                }
                mDeleteDialog = CustomDialog.getInstance(mMainActivity).initView(CustomDialog.TYPE_NORMAL, R.layout.layout_custom_dialog_mid)
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

                mDeleteDialog.showDialog();
                break;

            case R.id.ll_edit_move:
                int text = 0;
                int text1 = 0;
                int text2 = 0;
                if (DvrStatusInfo.getInstance().getMode().equals(Constant.NORMAL)) {
                    text = R.string.text_edit_move_to_emergency;
                    text1 = R.string.text_move_current_to_emergency1;
                    text2 = R.string.text_move_current_to_emergency1;
                } else if (DvrStatusInfo.getInstance().getMode().equals(Constant.EMERGENCY)) {
                    text =  R.string.text_edit_move_to_normal;
                    text1 = R.string.text_move_current_to_normal1;
                    text2 = R.string.text_move_current_to_normal1;
                }

                mMoveDialog = CustomDialog.getInstance(mMainActivity).initView(CustomDialog.TYPE_NORMAL, R.layout.layout_custom_dialog)
                        .setText(text, text1, text2)
                        .setOkText(R.string.text_edit_move)
                        .setCancelText(R.string.text_cancel)
                        .setImageView(R.mipmap.icon_singlerow_transfer)
                        .initClickListener(new CustomDialog.OnClickListener() {
                            @Override
                            public void ok() {
                                if (mMode.equals(Constant.NORMAL)) {
                                    DvrController.getInstance().moveSelectToEmergency();

                                } else {
                                    DvrController.getInstance().moveSelectToNormal();
                                    mEditHandler.sendEmptyMessage(11);
                                }
                                DvrStatusInfo.getInstance().setmRefreshStatus(true);
                                DvrStatusInfo.getInstance().setmClickStatus(false);
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                mMoveDialog.showDialog();

                break;
            case R.id.iv_edit_up:
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
            case R.id.iv_edit_down:
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
        mEditHandler.sendMessage(celMessage);
    }

    public void notifyChange(String funcId, int value) {
        Log.d(TAG, "notifyChange: funcId = " + funcId + ";value = " + value);
        String currentOperaName = DvrController.getInstance().getCurrentOperaName();
        switch (funcId) {
            case DVR_SelectAllDocCorDocStore:
                if (value == 1) {
                    isSelectAll = true;
                    mEditHandler.sendEmptyMessageDelayed(2, 1000);
                } else {
                    Log.d(TAG, "notifyChange: DVR_SelectAllDocCorDocStore fault");
                }
                break;
            case DVR_CancelSelAllSelectedDoc:
                if (value == 1) {
                    isSelectAll = false;
                    mEditHandler.sendEmptyMessageDelayed(3, 1000);
                } else {
                    Log.d(TAG, "notifyChange: DVR_CancelSelAllSelectedDoc fault");
                }
                break;
            case DVR_DeleteTheSelectedDocument:
                if (value == 1) {
                    mEditHandler.sendEmptyMessage(9);

                } else {
                    Log.d(TAG, "notifyChange: DVR_DeleteTheSelectedDocument fault");
                }
                break;
            case DVR_MoveSelectedDocToNorArea:
                if (value == 1) {
                    mEditHandler.sendEmptyMessage(10);

                } else {
                    Log.d(TAG, "notifyChange: DVR_DeleteTheSelectedDocument fault");
                }
                break;
            case DVR_MoveSelectedDocToDeleteArea:
                if (value == 1) {
                    mEditHandler.sendEmptyMessage(11);

                } else {
                    Log.d(TAG, "notifyChange: DVR_DeleteTheSelectedDocument fault");
                }
                break;
            case DVR_ExitEditMode:
                if (value == 1) {
                    Log.d(TAG, "notifyChange: DVR_ExitEditMode success");
                    mEditHandler.sendEmptyMessage(4);

                } else {
                    Log.d(TAG, "notifyChange: DVR_ExitEditMode fault");
                }
                break;
            case DVR_St_Select_ThumbnailSel:
                Log.d(TAG, "EditAdapter:  1mEditEntities = " + mEditEntities.size());
                Message selMessage  =  new Message();
                selMessage.what = 6;
                selMessage.arg1 = value;
                mEditHandler.sendMessage(selMessage);
                break;
            case DVR_St_Cancel_ThumbnailSel:
                Message celMessage  =  new Message();
                celMessage.what = 14;
                celMessage.arg1 = value;
                mEditHandler.sendMessage(celMessage);
                break;
            case DVR_St_CurrentThumbnail:
                num = value;
                Log.d(TAG, "notifyChange: edit thumbnail value=" + num);
                mEditEntities.clear();
                for (int i = 0; i < num; i++) {
                    EditEntity editEntity = new EditEntity();
                    editEntity.setNum(i);
                    Log.d(TAG, "setNum: setNum="+i);
                    editEntity.setCheck(false);
                    mEditEntities.add(editEntity);

                }
                if (mEditAdapter == null){
                    mEditAdapter = new EditAdapter(mMainActivity, mEditEntities);
                    mEditHandler.sendEmptyMessage(5);
                }else {
                    mEditHandler.sendEmptyMessage(15);
                }
                break;
            case DVR_St_TotalPage:
                Message msgtota = new Message();
                msgtota.what = 7;
                msgtota.obj = value;
                mEditHandler.sendMessage(msgtota);
                break;
            case DVR_St_CurrentPage:
                Message msgCur = new Message();
                msgCur.what = 8;
                msgCur.obj = value;
                mEditHandler.sendMessage(msgCur);
                break;
            case DVR_PageUpCbFunc:
                Message msgPageUp = new Message();
                msgPageUp.what = 12;
                msgPageUp.arg1 = value;
                mEditHandler.sendMessage(msgPageUp);
                break;
            case DVR_PageDownCbFunc:
                mEditHandler.sendEmptyMessage(13);
                break;
            default:
                break;
        }

    }

    /**
     * 没有选择项时，更新删除和移动按钮
     */
    public void updateBtnStatus(boolean select) {
        Log.d(TAG, "updateBtnStatus: select " +select);
        if (select) {
            if (DvrStatusInfo.getInstance().getMode().equals(Constant.NORMAL)) {
                ivOutEditMove.setVisibility(View.GONE);
                tvOutEditMove.setVisibility(View.GONE);
                ivEditMove.setVisibility(View.VISIBLE);
                tvEditMove.setVisibility(View.VISIBLE);
            } else {
                ivOutEditMove.setVisibility(View.VISIBLE);
                tvOutEditMove.setVisibility(View.VISIBLE);
                ivEditMove.setVisibility(View.GONE);
                tvEditMove.setVisibility(View.GONE);
            }


            ivEditDelete.setEnabled(true);
            tvEditDelete.setEnabled(true);
            tvOutEditMove.setEnabled(true);
            ivOutEditMove.setEnabled(true);
            tvEditMove.setEnabled(true);
            ivEditMove.setEnabled(true);
            llEditMove.setEnabled(true);
            llEditDelete.setEnabled(true);
        } else {
            if (DvrStatusInfo.getInstance().getMode().equals(Constant.NORMAL)) {
                ivOutEditMove.setVisibility(View.GONE);
                tvOutEditMove.setVisibility(View.GONE);
                ivEditMove.setVisibility(View.VISIBLE);
                tvEditMove.setVisibility(View.VISIBLE);
            } else {
                ivOutEditMove.setVisibility(View.VISIBLE);
                tvOutEditMove.setVisibility(View.VISIBLE);
                ivEditMove.setVisibility(View.GONE);
                tvEditMove.setVisibility(View.GONE);
            }

            ivEditDelete.setEnabled(false);
            tvEditDelete.setEnabled(false);
            tvOutEditMove.setEnabled(false);
            ivOutEditMove.setEnabled(false);
            tvEditMove.setEnabled(false);
            ivEditMove.setEnabled(false);
            llEditMove.setEnabled(false);
            llEditDelete.setEnabled(false);
        }
    }

    /**
     * 更新编辑模式
     */
    public void updateEditMode() {

        mEditHandler.sendEmptyMessage(1);

    }



    /**
     * 刷新多选缩略图
     */
    public void updateSelectStatus(boolean select) {
        Log.d(TAG, "updateSelectStatus: " + select);
        int idx = 0;
        for (EditEntity editEntity : mEditEntities) {
            if (select) {
                mEditEntities.get(idx).setNum(idx);
                mEditEntities.get(idx).setCheck(true);
            } else {
                mEditEntities.get(idx).setNum(idx);
                mEditEntities.get(idx).setCheck(false);
            }
            idx++;
        }
        if (mEditAdapter != null) {
            mEditAdapter.notifyDataSetChanged();
        }

    }

    /**
     * 刷新删除视频或图片操作
     */
    public void notifyDeleteStatus() {
        mDeleteDialog.dismissDialog();
        if (mEditAdapter != null) {
            mEditAdapter.notifyDataSetChanged();
        }

    }

    /**
     * 刷新移动视频或照片操作
     */
    public void notifyMoveStatus() {
        mMoveDialog.dismissDialog();
        if (mEditAdapter != null) {
            mEditAdapter.notifyDataSetChanged();
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
            llEditAllSelect.setVisibility(View.VISIBLE);
            tvSelectStatus.setVisibility(View.VISIBLE);
            ivSelectStatus.setVisibility(View.VISIBLE) ;
            ivCullSelectStatus.setVisibility(View.GONE);
            tvCullSelectStatus.setVisibility(View.GONE);
            llEditUallSelect.setVisibility(View.GONE);
        } else {
            llEditAllSelect.setVisibility(View.GONE);
            tvSelectStatus.setVisibility(View.GONE);
            ivSelectStatus.setVisibility(View.GONE) ;
            ivCullSelectStatus.setVisibility(View.VISIBLE);
            tvCullSelectStatus.setVisibility(View.VISIBLE);
            llEditUallSelect.setVisibility(View.VISIBLE);
        }


    }

    /**
     * 更新编辑页面
     */
    public void updateEdit() {
        if ((Constant.PLAY_TYPE_VIDEO.equals(DvrStatusInfo.getInstance().getType()))
                && (Constant.EMERGENCY.equals(DvrStatusInfo.getInstance().getMode())
                || Constant.NORMAL.equals(DvrStatusInfo.getInstance().getMode()))) {
            //显示回放-普通录像/紧急录像
            llEditMove.setVisibility(View.VISIBLE);
            ivLine.setVisibility(View.VISIBLE);
        } else if (Constant.PLAY_TYPE_PHOTO.equals(DvrStatusInfo.getInstance().getType())
                && Constant.NORMAL.equals(DvrStatusInfo.getInstance().getMode())) {
            //显示回放-照片
            llEditMove.setVisibility(View.GONE);
            ivLine.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "mode ===" + DvrStatusInfo.getInstance().getMode());
            Log.d(TAG, "type ===" + DvrStatusInfo.getInstance().getType());
        }
    }

    /**
     * 更新移动
     */
    public void updateMoveTab() {
        if (DvrStatusInfo.getInstance().getMode().equals(Constant.NORMAL)) {
            ivOutEditMove.setVisibility(View.GONE);
            tvOutEditMove.setVisibility(View.GONE);
            ivEditMove.setVisibility(View.VISIBLE);
            tvEditMove.setVisibility(View.VISIBLE);
        } else {
            ivOutEditMove.setVisibility(View.VISIBLE);
            tvOutEditMove.setVisibility(View.VISIBLE);
            ivEditMove.setVisibility(View.GONE);
            tvEditMove.setVisibility(View.GONE);
        }
    }

    /**
     * 设置编辑页
     */
    public void setPageNum(int curPage, int totPage) {
        tvEditCurPage.setText(String.valueOf(curPage));
        tvEditTotPage.setText(String.valueOf(totPage));
    }

    /**
     * 上翻页
     */
    public void pageUp(int totPage) {
        if (1 == totPage ||  DvrStatusInfo.getInstance().getTotalPage() == 0) {
            ivEditUp.setImageResource(R.mipmap.icon_up_dis);
            ivEditUp.setEnabled(false);
            ivEditUp.setClickable(false);
        } else {
            ivEditUp.setImageResource(R.mipmap.icon_up_n);
            ivEditUp.setEnabled(true);
            ivEditUp.setClickable(true);
        }


    }

    /**
     * 下翻页
     */
    public void pageDown(int curPage) {
        if (DvrStatusInfo.getInstance().getTotalPage() ==  curPage || DvrStatusInfo.getInstance().getTotalPage() == 0 ) {
            ivEditDown.setImageResource(R.mipmap.icon_down_dis);
            ivEditDown.setEnabled(false);
            ivEditDown.setClickable(false);
        } else {
            ivEditDown.setImageResource(R.mipmap.icon_down_n);
            ivEditDown.setEnabled(true);
            ivEditDown.setClickable(true);
        }


    }


    private Handler mEditHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    mMainActivity.changePage(PAGE_EDIT);
                    if (mEditAdapter != null) {
                        mEditAdapter.setEditMode(true);

                        if (DvrStatusInfo.getInstance().getType() == Constant.PLAY_TYPE_PHOTO) {
                            mEditAdapter.setEditPlay(false);
                        } else {
                            mEditAdapter.setEditPlay(true);
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
                        if (mEditAdapter != null) {
                            for(int i=0;i<mEditEntities.size();i++){
                                mEditEntities.get(i).setCheck(false);
                            }
                            gvEdit.setAdapter(mEditAdapter);

                            mEditAdapter.setEditMode(true);

                            updateBtnStatus(false);


                            if (DvrStatusInfo.getInstance().getType() == Constant.PLAY_TYPE_VIDEO) {

                                mEditAdapter.setEditPlay(true);
                            } else if (DvrStatusInfo.getInstance().getType() == Constant.PLAY_TYPE_PHOTO) {
                                mEditAdapter.setEditPlay(false);

                            }
                        }

                    }
                    break;
                case 6:
                    for(int i=0;i<mEditEntities.size();i++){
                        if(mEditEntities.get(i).isNum() == msg.arg1 - 1){
                            mEditEntities.get(i).setCheck(true);
                        }
                    }
                    isRemain = false;
                    mEditAdapter.notifyDataSetChanged();
                    mEditAdapter.setEditMode(true);
                    for (int a=0;a<mEditEntities.size();a++){
                        if(mEditEntities.get(a).isCheck() == false){
                            Log.d(TAG, "EditAdapter: isRemain true" );
                            isRemain = true;
                            break;
                        }
                        Log.d(TAG, "EditAdapter: isRemain false" );
                    }
                    if(!isRemain){
                        Log.d(TAG, "EditAdapter: isRemain = "  +isRemain);
                        isSelectAll = true;
                        updateSelectStatus(true);
                    }else{
                        Log.d(TAG, "EditAdapter: isRemain  = "+ isRemain );
                        isSelectAll = false;
                    }
                    selectOrCancelAll(isSelectAll);
                    updateBtnStatus(true);
                    if (DvrStatusInfo.getInstance().getType() == Constant.PLAY_TYPE_VIDEO) {

                        mEditAdapter.setEditPlay(true);
                    } else if (DvrStatusInfo.getInstance().getType() == Constant.PLAY_TYPE_PHOTO) {
                        mEditAdapter.setEditPlay(false);

                    }
                    break;
                case 7:
                    tvEditTotPage.setText(msg.obj.toString());
                    break;
                case 8:
                    tvEditCurPage.setText(msg.obj.toString());
                    pageDown((Integer) msg.obj);
                    pageUp((Integer) msg.obj);
                    break;
                case 9:
                    notifyDeleteStatus();
                    updateSelectStatus(false);
                    selectOrCancelAll(false);
                    updateBtnStatus(false);
                    mEditAdapter.setEditMode(true);
                    isSelectAll = false;
                    if (DvrStatusInfo.getInstance().getType() == Constant.PLAY_TYPE_PHOTO) {
                        mEditAdapter.setEditPlay(false);
                    } else {
                        mEditAdapter.setEditPlay(true);
                    }
                    break;
                case 10:
                    notifyMoveStatus();
                    updateSelectStatus(false);
                    selectOrCancelAll(false);
                    updateBtnStatus(false);
                    mEditAdapter.setEditMode(true);
                    if (DvrStatusInfo.getInstance().getType() == Constant.PLAY_TYPE_PHOTO) {
                        mEditAdapter.setEditPlay(false);
                    } else {
                        mEditAdapter.setEditPlay(true);
                    }
                    break;
                case 11:
                    updateSelectStatus(false);
                    selectOrCancelAll(false);
                    updateBtnStatus(false);
                    mEditAdapter.setEditMode(true);
                    if (DvrStatusInfo.getInstance().getType() == Constant.PLAY_TYPE_PHOTO) {
                        mEditAdapter.setEditPlay(false);
                    } else {
                        mEditAdapter.setEditPlay(true);
                    }
                    break;
                case 12:
                case 13:
                    updateSelectStatus(false);
                    updateBtnStatus(false);
                    selectOrCancelAll(false);
                    break;
                case 14:
                    for(int i=0;i<mEditEntities.size();i++){
                        if(mEditEntities.get(i).isNum() == msg.arg1-1){
                            mEditEntities.get(i).setCheck(false);
                        }
                    }
                    isRemain = false;
                    mEditAdapter.notifyDataSetChanged();
                    mEditAdapter.setEditMode(true);
                    for (int a=0;a<mEditEntities.size();a++){
                        if(mEditEntities.get(a).isCheck() == true){
                            isRemain = true;
                            break;
                        }
                    }
                    if(!isRemain){
                        updateBtnStatus(false);
                        updateSelectStatus(false);

                    }
                    selectOrCancelAll(false);
                    if (DvrStatusInfo.getInstance().getType() == Constant.PLAY_TYPE_VIDEO) {

                        mEditAdapter.setEditPlay(true);
                    } else if (DvrStatusInfo.getInstance().getType() == Constant.PLAY_TYPE_PHOTO) {
                        mEditAdapter.setEditPlay(false);

                    }
                    break;
                case 15:
                    selectOrCancelAll(false);
                    updateBtnStatus(false);
                    mEditAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;

            }
        }
    };

}
