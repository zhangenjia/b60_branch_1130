package com.adayo.app.systemui.windows.views;

import android.annotation.NonNull;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.app.systemui.R;
import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.app.systemui.adapters.MessageListAdapter;
import com.adayo.app.systemui.adapters.SliceMessageAdapter;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.managers.view.WindowsManager;
import com.adayo.app.systemui.utils.LogUtil;
import com.adayo.app.systemui.windows.dialogs.TSPMessageReminder;
import com.adayo.app.systemui.windows.dialogs.TrafficRenewalReminder;
import com.adayo.proxy.msg.notify.IMessageNotifyListener;
import com.adayo.proxy.msg.notify.bean.MessageNotification;
import com.adayo.proxy.msg.notify.control.MessageManager;
import com.android.internal.widget.DefaultItemAnimator;
import com.android.internal.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;
import static com.adayo.proxy.msg.notify.bean.MessageConst.MSG_TYPE_DATA_FLOW;
import static com.adayo.proxy.msg.notify.bean.MessageConst.MSG_TYPE_SYS_UPGRADE;
import static com.adayo.proxy.msg.notify.bean.MessageConst.MSG_TYPE_TSP_NOTIFICATION;

/**
 * @author XuYue
 * @description:
 * @date :2021/11/23 16:51
 */
public class QsMessageCenterView extends RelativeLayout implements View.OnClickListener {
    private SlideRecyclerView slideRecyclerView;
    private SliceMessageAdapter sliceMessageAdapter;
    private List<MessageNotification> messageList = new ArrayList<>();

    private TextView title;
    private TextView emptyView;
    private TextView msgUnread;
    private LinearLayout editLayout;
    private ImageView msgEdit;
    private ImageView msgDeleteAll;
    private ImageView msgBack;

    private int unreadNumber = 0;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LogUtil.d(SystemUIContent.TAG, "onServiceConnected() begin");
            MessageManager.getInstance().init(SystemUIApplication.getSystemUIContext());
            MessageManager.getInstance().requestMessageListener(mListener, MSG_TYPE_DATA_FLOW | MSG_TYPE_TSP_NOTIFICATION | MSG_TYPE_SYS_UPGRADE);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            LogUtil.d(SystemUIContent.TAG, "onServiceDisconnected() begin");
        }
    };

    private IMessageNotifyListener mListener = new IMessageNotifyListener() {
        @Override
        public void notifyMsgChange(List<MessageNotification> list) {
            LogUtil.d(SystemUIContent.TAG, "notifyMsgChange() begin");
            if (list == null) {
                return;
            }
            LogUtil.d(SystemUIContent.TAG, "list = " + list.toString());
            Message msg = Message.obtain();
            msg.what = 1;
            msg.obj = list;
            mHandler.sendMessage(msg);
        }

        @Override
        public void notifyUnReaderMsg(int num, List<MessageNotification> list) {
            if (list == null) {
                return;
            }
            Message msg = Message.obtain();
            msg.what = 2;
            msg.arg1 = num;
            msg.obj = list;
            mHandler.sendMessage(msg);
        }

        @Override
        public void notifyNewMsg(List<MessageNotification> list) {
            LogUtil.d(SystemUIContent.TAG, "notifyNewMsg() begin");
            if (list == null) {
                return;
            }

            Message msg = Message.obtain();
            msg.what = 3;
            msg.obj = list;
            mHandler.sendMessage(msg);
        }

        @Override
        public void notifyServiceDead() {
            LogUtil.d(SystemUIContent.TAG, "notifyServiceDead()");
            initConnection();
        }
    };

    private final Object lock = new Object();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    LogUtil.d(SystemUIContent.TAG, "handleMessage() refresh");
                    synchronized (lock) {
                        messageList.clear();
                        messageList.addAll((List<MessageNotification>) msg.obj);
                        if (null != sliceMessageAdapter) {
                            sliceMessageAdapter.notifyDataSetChanged();
                        }
                        if (messageList.size() > 0) {
                            emptyView.setVisibility(View.GONE);
                            msgEdit.setEnabled(true);
                        } else {
                            emptyView.setVisibility(View.VISIBLE);
                            msgEdit.setEnabled(false);
                        }
                    }
                    break;
                case 3:
                    LogUtil.d(SystemUIContent.TAG, "handleMessage() refresh" + ((List<MessageNotification>) msg.obj).get(0).getmContext());
                    if(0 >= messageList.size()){
                        return;
                    }
                    switch (messageList.get(0).getmType()){
                        case MSG_TYPE_DATA_FLOW:
                            TrafficRenewalReminder.getInstance().showDialog(messageList.get(0));
                            break;
                        case MSG_TYPE_TSP_NOTIFICATION:
                            TSPMessageReminder.getInstance().showDialog(messageList.get(0));
                            break;
                        default:
                            break;
                    }
                    break;
                case 2:
                    unreadNumber = msg.arg1;
                    LogUtil.d(SystemUIContent.TAG, "handleMessage() unreadNumber = " + unreadNumber);
                    if (unreadNumber > 0) {
                        msgUnread.setText(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.message_unread) + "（" + unreadNumber + "）");
                        msgUnread.setVisibility(View.VISIBLE);
                    } else {
                        msgUnread.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public QsMessageCenterView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public QsMessageCenterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public QsMessageCenterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @android.annotation.Nullable AttributeSet attrs, int defStyleAttr) {
        View mRootView = LayoutInflater.from(getContext()).inflate(R.layout.qs_message_center_view, this, true);
        title = mRootView.findViewById(R.id.title);
        emptyView = mRootView.findViewById(R.id.empty_text);
        msgUnread = mRootView.findViewById(R.id.msg_unread);
        editLayout = mRootView.findViewById(R.id.edit_layout);
        msgEdit = mRootView.findViewById(R.id.msg_edit);
        msgEdit.setOnClickListener(this);
        msgEdit.setEnabled(false);
        msgDeleteAll = mRootView.findViewById(R.id.message_delete);
        msgDeleteAll.setOnClickListener(this);
        msgBack = mRootView.findViewById(R.id.message_back);
        msgBack.setOnClickListener(this);
        slideRecyclerView = mRootView.findViewById(R.id.msg_list);
        sliceMessageAdapter = new SliceMessageAdapter(messageList, SystemUIApplication.getSystemUIContext());
        slideRecyclerView.setLayoutManager(new LinearLayoutManager(SystemUIApplication.getSystemUIContext()));
        slideRecyclerView.addItemDecoration(new RecycleViewDivider(SystemUIApplication.getSystemUIContext(), LinearLayoutManager.VERTICAL, 32, SystemUIApplication.getSystemUIContext().getResources().getColor(R.color.transparent)));
        slideRecyclerView.setItemAnimator(new DefaultItemAnimator());
        slideRecyclerView.setAdapter(sliceMessageAdapter);
        sliceMessageAdapter.setOnItemClickListener(new SliceMessageAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                if(position >= messageList.size()){
                    return;
                }
                switch (messageList.get(position).getmType()){
                    case MSG_TYPE_DATA_FLOW:
                        TrafficRenewalReminder.getInstance().showDialog(messageList.get(position));
                        break;
                    case MSG_TYPE_TSP_NOTIFICATION:
                        TSPMessageReminder.getInstance().showDialog(messageList.get(position));
                        break;
                    case MSG_TYPE_SYS_UPGRADE:
                        startView(MSG_TYPE_SYS_UPGRADE, messageList.get(position));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onClickDelete(int position) {
                MessageNotification messageNotification = messageList.get(position);
                MessageManager.getInstance().deleteMsgNotify(messageNotification);
                if (-1 != position){
                    messageList.remove(position);
                }
                sliceMessageAdapter.notifyItemRemoved(position);
                if (position != messageList.size()) {
                    //防止数据错乱
                    sliceMessageAdapter.notifyItemRangeChanged(position, messageList.size() - position);
                }
            }
        });
        initConnection();
    }

    private void initConnection() {
        Intent intent1 = new Intent();
        intent1.setPackage("com.adayo.service.msg.nofity");
        intent1.setAction("com.adayo.service.msg.nofity.control.MessageServic");
        SystemUIApplication.getSystemUIContext().bindService(intent1, connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        emptyView.setText(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.message_empty));
        msgUnread.setText(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.message_unread) + "（" + unreadNumber + "）");
//        msgUnread.setText(String.format(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.message_unread),
//                unreadNumber));
        title.setText(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.message_center));
    }

    private void startView(int type, MessageNotification messageNotification){
        WindowsManager.setQsPanelVisibility(View.GONE);
        Intent intent = new Intent();
        Bundle mBundle = new Bundle();
        intent.setPackage("com.adayo.service.upgrade");
        intent.setAction("com.adayo.service.upgrade.UpgradeService");
        mBundle.putInt("current_upgrade_state", type);
        mBundle.putParcelable("message", messageNotification);
        intent.putExtras(mBundle);
        SystemUIApplication.getSystemUIContext().startService(intent);
        MessageManager.getInstance().markMsgReaded(messageNotification);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.msg_edit:
                setEditStatus(true);
                break;
            case R.id.message_delete:
                MessageManager.getInstance().deleteAllMsg();
                setEditStatus(false);
                break;
            case R.id.message_back:
                setEditStatus(false);
                break;
            default:
                break;
        }
    }

    private void setEditStatus(boolean editStatus) {
        if (editStatus) {
            if (null == messageList || messageList.isEmpty()) {
                return;
            }
            msgEdit.setVisibility(View.GONE);
        }
        editLayout.setVisibility(editStatus ? View.VISIBLE : View.GONE);
        Animation animation = AnimationUtils.loadAnimation(SystemUIApplication.getSystemUIContext(), editStatus ? R.anim.right_in : R.anim.right_out);
        if (null != animation) {
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (!editStatus) {
                        msgEdit.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            editLayout.startAnimation(animation);
        }
    }
}
