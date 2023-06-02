package com.adayo.app.launcher.model.adapter;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.adayo.app.launcher.R;
import com.adayo.app.launcher.presenter.function.ConfigFunction;
import com.adayo.app.launcher.ui.view.CustomItemViewLayout;
import com.adayo.app.launcher.util.MyConstantsUtil;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.adayo.app.launcher.util.MyConstantsUtil.HIGH_CONFIG_VEHICLE;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_AIQUTING;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_APA;

import static com.adayo.app.launcher.util.MyConstantsUtil.ID_AVM;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_CARBIT;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_DVR;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_MUSIC;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_MYCAR;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_NAVI;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_OFFROADINFO;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_PICTURE;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_RADIO;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_SETTING;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_TEL;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_VIDEO;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_WECHAT;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_WITHTENCENT;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_YUEYEQUAN;
import static com.adayo.app.launcher.util.MyConstantsUtil.LOW_CONFIG_VEHICLE;

public class ListRightAdapter extends RecyclerView.Adapter<ListRightAdapter.ViewHolder> {

    private static final String TAG = "ListRightAdapter";
    private String mId;
    private List<String[]> mList = new ArrayList<>();
    private List<String> list = new ArrayList<>();
    private List<Boolean> visibleList = new ArrayList<>();
    private Context mContext;
    private CustomItemViewLayout view;
    private OnTouchItemListener onTouchItemListener;
    private String dragId;
    private int mCurrentDownEventPosition = -1;
    private boolean flag = true;
    private String offLineConfiguration;
    private final MyConstantsUtil constants;
    private String[][] resArray;

    public ListRightAdapter(Context context) {
        mContext = context;
        constants = new MyConstantsUtil(mContext);
        resArray = new String[][]{
                {ID_VIDEO, String.valueOf(R.drawable.img_smallcard_video), String.valueOf(R.drawable.img_smallcard_video_b), "-1", (context.getResources().getString(R.string.video))},//视频
                {ID_TEL, String.valueOf(R.drawable.img_smallcard_tel), String.valueOf(R.drawable.img_smallcard_tel_b), "-1", (context.getResources().getString(R.string.tel))},//电话
                {ID_PICTURE, String.valueOf(R.drawable.img_smallcard_picture), String.valueOf(R.drawable.img_smallcard_picture_b), "-1", (context.getResources().getString(R.string.picture))},//图片
                {ID_SETTING, String.valueOf(R.drawable.img_smallcard_setting), String.valueOf(R.drawable.img_smallcard_setting_b), "-1", (context.getResources().getString(R.string.settings))},//系统设置
                {ID_MYCAR, String.valueOf(R.drawable.img_smallcard_mycar), String.valueOf(R.drawable.img_smallcard_mycar_b), "-1", (context.getResources().getString(R.string.mycar))},//我的车
                {ID_OFFROADINFO, String.valueOf(R.drawable.img_smallcard_crossinfo), String.valueOf(R.drawable.img_smallcard_crossinfo_b), "-1", (context.getResources().getString(R.string.orim))},//越野信息
                {ID_APA, String.valueOf(R.drawable.img_smallcard_aps), String.valueOf(R.drawable.img_smallcard_aps_b), "-1", (context.getResources().getString(R.string.apa))},//自动泊车
                {ID_AVM, String.valueOf(R.drawable.img_smallcard_avm), String.valueOf(R.drawable.img_smallcard_avm_b), "-1", (context.getResources().getString(R.string.avm))},//全景影像
                {ID_MUSIC, String.valueOf(R.drawable.icon_smallcard_music_z), String.valueOf(R.drawable.img_smallcard_music_b), String.valueOf(R.drawable.img_smallcard_music_c), (context.getResources().getString(R.string.music))},//音乐
                {ID_RADIO, String.valueOf(R.drawable.img_smallcard_fm), String.valueOf(R.drawable.img_smallcard_fm_b), String.valueOf(R.drawable.img_smallcard_fm_c), (context.getResources().getString(R.string.radio))},//收音机
                {ID_YUEYEQUAN, String.valueOf(R.drawable.img_smallcard_yueyequan), String.valueOf(R.drawable.img_smallcard_yueyequan_b), "-1", (context.getResources().getString(R.string.yueyequan))},//越野圈
                {ID_WITHTENCENT, String.valueOf(R.drawable.img_smallcard_withtencent), String.valueOf(R.drawable.img_smallcard_withtencent_b), "-1", (context.getResources().getString(R.string.tencentway))},//腾讯随行
                {ID_DVR, String.valueOf(R.drawable.img_smallcard_dvr),String.valueOf(R.drawable.img_smallcard_dvr_b), "-1", (context.getResources().getString(R.string.dvr))},//行车记录仪
                {ID_AIQUTING, String.valueOf(R.drawable.icon_smallcard_music_z), String.valueOf(R.drawable.img_smallcard_wecarflow_b), "-1", (context.getResources().getString(R.string.funcaudio))},//爱趣听
                {ID_WECHAT, String.valueOf(R.drawable.img_smallcard_wechat), String.valueOf(R.drawable.img_smallcard_wechat_b), "-1", (context.getResources().getString(R.string.wechat))},//微信
                {ID_NAVI, String.valueOf(R.drawable.img_smallcard_navi), String.valueOf(R.drawable.img_smallcard_navi_b), "-1", (context.getResources().getString(R.string.navigation))},//导航
                {ID_CARBIT, String.valueOf(R.drawable.img_smallcard_carbit), String.valueOf(R.drawable.img_smallcard_carbit_b), "-1", (context.getResources().getString(R.string.easyconnect))}
        };
        Log.d(TAG, "configurationChange: Adapter");
    }

    @Override
    public ListRightAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        offLineConfiguration = ConfigFunction.getInstance(mContext).getOffLineConfiguration();
        if (offLineConfiguration.equals(HIGH_CONFIG_VEHICLE)) {
            view = (CustomItemViewLayout) AAOP_HSkin.getLayoutInflater(parent.getContext()).inflate(R.layout.layout_smallcard_high, parent, false);
        } else if (offLineConfiguration.equals(LOW_CONFIG_VEHICLE)) {
            view = (CustomItemViewLayout) AAOP_HSkin.getLayoutInflater(parent.getContext()).inflate(R.layout.layout_smallcard_low, parent, false);
        }
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ListRightAdapter.ViewHolder holder, final int position) {
        if (visibleList.get(position)) {
            holder.parent_layout.setVisibility(View.VISIBLE);
        } else {
            holder.parent_layout.setVisibility(View.INVISIBLE);
        }
        final String[] strings = mList.get(position);
        holder.mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch: ");
                if (position == mCurrentDownEventPosition || flag == true) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            Log.d(TAG, "ACTION_DOWN: ");
                            dragId = strings[0];
                            mCurrentDownEventPosition = position;
                            flag = false;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            Log.d(TAG, "ACTION_MOVE: ");
                            break;
                        case MotionEvent.ACTION_UP:
                            Log.d(TAG, "ACTION_UP: ");
                            flag = true;
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            Log.d(TAG, "ACTION_CANCEL: ");
                            flag = true;
                            break;
                    }
                    if (onTouchItemListener != null) {
                        boolean b = onTouchItemListener.onTouchItem(position, dragId, ListRightAdapter.this, holder.parent_layout, event);
                        return b;
                    }
                } else {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_MOVE:
                        case MotionEvent.ACTION_CANCEL:
                            return true;
                        case MotionEvent.ACTION_UP:
                            return false;
                    }
                }
                return false;
            }
        });
        if (!mList.get(position)[1].equals("-1")) {
            holder.iv_one.setVisibility(View.VISIBLE);
            holder.iv_one.setBackgroundResource(Integer.parseInt(strings[1]));//图二
        } else {
            holder.iv_one.setVisibility(View.GONE);
        }
        if (!mList.get(position)[2].equals("-1")) {
            holder.iv_two.setVisibility(View.VISIBLE);
            holder.iv_two.setBackgroundResource(Integer.parseInt(strings[2]));//图三
        } else {
            holder.iv_two.setVisibility(View.GONE);
        }
        holder.tv_name.setText(strings[4]);
        AAOP_HSkin
                .getInstance()
                .applySkin(holder.parent_layout, true);
        AAOP_HSkin.with(holder.iv_one)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,Integer.parseInt(strings[1]))
                .applySkin(false);
        AAOP_HSkin.with(holder.iv_two)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,Integer.parseInt(strings[2]))
                .applySkin(false);
    }

    @Override
    public int getItemCount() {

        return mList.size();

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_one;
        private ImageView iv_two;
        private TextView tv_name;
        private CustomItemViewLayout mView;
        private CustomItemViewLayout parent_layout;

        public ViewHolder(CustomItemViewLayout view) {
            super(view);
            mView = view;
            iv_one = (ImageView) view.findViewById(R.id.iv_crossinfo_one);
            iv_two = (ImageView) view.findViewById(R.id.iv_navi_two);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            parent_layout = (CustomItemViewLayout) view.findViewById(R.id.parent_layout);

        }
    }

    public void setData(String id) {
        mId = id;
        List<String> strings = Arrays.asList(mId.split(""));//
        list = new ArrayList(strings);
        list.remove(0);
        mList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < resArray.length; j++) {
                if (list.get(i).equals(resArray[j][0])) {
                    mList.add(resArray[j]);
                    visibleList.add(true);
                    break;
                }
            }
        }

    }

    public interface OnTouchItemListener {
        boolean onTouchItem(int position, String id, RecyclerView.Adapter adapter, View v, MotionEvent event);
    }

    public void setOnTouchItemListener(OnTouchItemListener onTouchItemListener) {
        this.onTouchItemListener = onTouchItemListener;
    }

    public void setVisibility(boolean visible) {
        Boolean b = visibleList.get(mCurrentDownEventPosition);
        b = visible;
    }


}

