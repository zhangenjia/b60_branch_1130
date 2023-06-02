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
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.adayo.app.launcher.util.MyConstantsUtil.HIGH_CONFIG_VEHICLE;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_AIQUTING;

import static com.adayo.app.launcher.util.MyConstantsUtil.ID_MUSIC;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_NAVI;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_OFFROADINFO;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_RADIO;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_TEL;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_WEATHER;
import static com.adayo.app.launcher.util.MyConstantsUtil.LOW_CONFIG_VEHICLE;


public class LeftAdapter extends RecyclerView.Adapter<LeftAdapter.ViewHolder> {
    private final static String TAG = "BottomListLeftAdapter";
    private String mId;
    private List<String[]> mList = new ArrayList<>();
    private List<String> list = new ArrayList<>();
    private List<Boolean> visibleList = new ArrayList<>();
    private Context mContext;
    private CustomItemViewLayout view;
    private OnTouchItemListener onTouchItemListener;
    private int mCurrentDownEventPosition = -1;
    private boolean flag = true;
    private String dragId;
    private String offLineConfiguration;
    private String[][] resResource;

    public LeftAdapter(Context context) {
        this.mContext = context;
        resResource = new String[][]{
                {ID_OFFROADINFO, String.valueOf(R.drawable.img_bigcard_crossinfo), mContext.getResources().getString(R.string.orim)}, {ID_NAVI, String.valueOf(R.drawable.img_bigcard_navi), mContext.getResources().getString(R.string.navigation)},
                {ID_RADIO, String.valueOf(R.drawable.img_bigcard_radio), mContext.getResources().getString(R.string.radio)}, {ID_AIQUTING, String.valueOf(R.drawable.img_bigcard_wecarflow), mContext.getResources().getString(R.string.funcaudio)},
                {ID_TEL, String.valueOf(R.drawable.img_bigcard_tel), mContext.getResources().getString(R.string.tel)}, {ID_MUSIC, String.valueOf(R.drawable.img_bigcard_music), mContext.getResources().getString(R.string.music)},
                {ID_WEATHER, String.valueOf(R.drawable.img_bigcard_weather), mContext.getResources().getString(R.string.weather)}};
    }

    @Override
    public LeftAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onBindViewHolder: ");
        offLineConfiguration = ConfigFunction.getInstance(mContext).getOffLineConfiguration();
        if (offLineConfiguration.equals(HIGH_CONFIG_VEHICLE)) {
            view = (CustomItemViewLayout) AAOP_HSkin.getLayoutInflater(parent.getContext()).inflate(R.layout.layout_bigcard_high, parent, false);
        } else if (offLineConfiguration.equals(LOW_CONFIG_VEHICLE)) {
            view = (CustomItemViewLayout) AAOP_HSkin.getLayoutInflater(parent.getContext()).inflate(R.layout.layout_bigcard_low, parent, false);
        }
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final LeftAdapter.ViewHolder holder, final int position) {

        final String[] strings = mList.get(position);
        holder.iv.setBackgroundResource(Integer.parseInt(strings[1]));
        holder.tv_name.setText(String.valueOf(strings[2]));

        if (holder.crossLeftText!=null){
            holder.crossLeftText.setVisibility(View.INVISIBLE);
        }
        if (holder.crossRightText!=null){
            holder.crossRightText.setVisibility(View.INVISIBLE);
        }
        if (holder.aqiText!=null){
            holder.aqiText.setVisibility(View.INVISIBLE);
        }


        String[] strings1 = mList.get(position);
        if (strings1[0]==ID_OFFROADINFO){
            if (holder.crossLeftText!=null){
                holder.crossLeftText.setVisibility(View.VISIBLE);
            }
            if (holder.crossRightText!=null){
                holder.crossRightText.setVisibility(View.VISIBLE);
            }

        }else if (strings1[0]==ID_WEATHER){
            if (holder.aqiText!=null){
                holder.aqiText.setVisibility(View.VISIBLE);
            }

        }

        if (visibleList.get(position)) {
            holder.parent_layout.setVisibility(View.VISIBLE);
        } else {
            holder.parent_layout.setVisibility(View.INVISIBLE);
        }
        holder.mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int pointerCount = event.getPointerCount();
                int action = (event.getAction() & MotionEvent.ACTION_MASK);
                Log.d(TAG, "onTouch: pointerCount = " + pointerCount + " action = " + action);
                if (position == mCurrentDownEventPosition || flag == true) {
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            Log.d(TAG, "ACTION_DOWN: ");
//                            ItemViewTouchDownImpl.getInstance().onDownEvent(event.getRawX());
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
                        boolean b = onTouchItemListener.onTouchItem(position, dragId, LeftAdapter.this, holder.mView, event);
                        Log.d(TAG, "onTouch: " + b);
                        return b;
                    }
                } else {
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            Log.d(TAG, "onTouch: ======ACTION_DOWN");
                        case MotionEvent.ACTION_MOVE:
                            Log.d(TAG, "onTouch: ======ACTION_MOVE");
                            return true;
                        case MotionEvent.ACTION_UP:
                            Log.d(TAG, "onTouch: ======ACTION_UP");
                            return false;
                        case MotionEvent.ACTION_CANCEL:
                            Log.d(TAG, "onTouch: ======ACTION_CANCEL");
                            return true;
                    }
                }
                Log.d(TAG, "onTouch: " + "false");
                return false;
            }
        });
        AAOP_HSkin
                .getInstance()
                .applySkin(holder.parent_layout, false);

        AAOP_HSkin.with(holder.iv)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, Integer.parseInt(strings[1]))
                .applySkin(false);

        if (holder.crossLeftText!=null){
            AAOP_HSkin.with(holder.crossLeftText)
                    .addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.color.crosstab_color)
                    .applySkin(false);
        }


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private CustomItemViewLayout mView;
        private ImageView iv;
        private CustomItemViewLayout parent_layout;
        private TextView tv_name;

        private TextView crossLeftText;
        private TextView crossRightText;
        private TextView aqiText;

        public ViewHolder(CustomItemViewLayout view) {
            super(view);
            mView = view;
            iv = (ImageView) mView.findViewById(R.id.iv);
            parent_layout = (CustomItemViewLayout) mView.findViewById(R.id.parent_layout);
            tv_name = (TextView) mView.findViewById(R.id.tv_name);

            crossLeftText = (TextView) mView.findViewById(R.id.crossLeftText);
            crossRightText = (TextView) mView.findViewById(R.id.crossRightText);
            aqiText = (TextView) mView.findViewById(R.id.aqiText);
            Log.d(TAG, "ViewHolder: ssssssssssssssssss"+crossLeftText);

        }
    }

    public void setData(String id) {
        mId = id;
        List<String> strings = Arrays.asList(mId.split(""));//
        list = new ArrayList(strings);
        list.remove(0);
        mList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < resResource.length; j++) {
                if (list.get(i).equals(resResource[j][0])) {
                    mList.add(resResource[j]);
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
