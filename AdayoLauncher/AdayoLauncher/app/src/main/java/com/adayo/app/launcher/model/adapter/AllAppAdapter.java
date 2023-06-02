package com.adayo.app.launcher.model.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.adayo.app.launcher.R;
import com.adayo.app.launcher.presenter.function.OpenAppFunction;
import com.adayo.app.launcher.ui.fragment.ActivityStartAnimHelper;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import java.util.HashMap;
import java.util.List;
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
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_USERGUIDE;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_VIDEO;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_WECHAT;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_WITHTENCENT;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_YUEYEQUAN;
import com.adayo.app.launcher.model.bean.AllAppBean;

public class AllAppAdapter extends BaseAdapter {

    private static final String TAG = "AllAppAdapter";
    private Context context;
    private List<AllAppBean> list;
    private ViewHolder holder;

    public AllAppAdapter(Context context, List<AllAppBean> list) {
        this.context = context;
        this.list = list;
        Log.d(TAG, " BcmDataListener AllAppAdapter: " + list);
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            holder = new ViewHolder(convertView);
            LayoutInflater layoutInflater = AAOP_HSkin.getLayoutInflater(context);
            convertView = layoutInflater.inflate(R.layout.layout_allapp_item, null);
            holder.item_image = (ImageView) convertView.findViewById(R.id.item_image);
            holder.item_text = (TextView) convertView.findViewById(R.id.item_text);
            holder.iv_select = (LinearLayout) convertView.findViewById(R.id.iv_select);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (list.get(position).getmId().equals(ID_APA)) {
            Log.d(TAG, "getView: ID_APA" + list.get(position).isEnable());
            if (list.get(position).isEnable()) {
                holder.item_image.setBackgroundResource(list.get(position).getmResource());
                AAOP_HSkin.with(holder.iv_select)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.allapp_frame_s)
                        .applySkin(false);
                holder.item_text.setAlpha(1f);
            } else {
                holder.item_image.setBackgroundResource(R.drawable.icon_smallcard_parking_dis);
                AAOP_HSkin.with(holder.iv_select)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.button_bg_dis)
                        .applySkin(false);
                holder.item_text.setAlpha(0.38f);
            }

        } else if (list.get(position).getmId().equals(ID_AVM)) {
            Log.d(TAG, "getView: ID_AVM" + list.get(position).isEnable());
            if (list.get(position).isEnable()) {
                holder.item_image.setBackgroundResource(list.get(position).getmResource());
                AAOP_HSkin.with(holder.iv_select)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.allapp_frame_s)
                        .applySkin(false);
                holder.item_text.setAlpha(1f);
            } else {
                holder.item_image.setBackgroundResource(R.drawable.icon_smallcard_panoramic_dis);
                AAOP_HSkin.with(holder.iv_select)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.button_bg_dis)
                        .applySkin(false);
                holder.item_text.setAlpha(0.38f);
            }

        } else {
            holder.item_image.setBackgroundResource(list.get(position).getmResource());
            AAOP_HSkin.with(holder.iv_select)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.allapp_frame_s)
                    .applySkin(false);
            holder.item_text.setAlpha(1f);

        }
        if (holder.iv_select != null) {
            holder.iv_select.setAlpha(0);
        }

        holder.item_text.setText(context.getResources().getString(Integer.parseInt(list.get(position).getmName())));


        final View finalConvertView = convertView;

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> map = new HashMap<>();
                Bundle bundle = ActivityStartAnimHelper.addScaleAnimParam(finalConvertView, position, map);
                Log.d("onclick", "onClick: " + list.get(position).getmId());
                switch (list.get(position).getmId()) {
                    case ID_PICTURE:
                        OpenAppFunction.getInstance().mOpenPictureApp();
                        break;
                    case ID_VIDEO:
                        OpenAppFunction.getInstance().mOpenVideoApp();
                        break;
                    case ID_TEL:
                        OpenAppFunction.getInstance().mOpenTel();
                        break;
                    case ID_SETTING:
                        OpenAppFunction.getInstance().mOpenSettingApp();
                        break;
                    case ID_RADIO:
                        OpenAppFunction.getInstance().mOpenRadioApp();
                        break;
                    case ID_MYCAR:
                        OpenAppFunction.getInstance().mOpenMyCar();
                        break;
                    case ID_AVM:
                        OpenAppFunction.getInstance().mOpenAVM();
                        break;
                    case ID_AIQUTING:
                        OpenAppFunction.getInstance().mOpenAiquting();
                        break;
                    case ID_WITHTENCENT:
                        OpenAppFunction.getInstance().mOpenWithTecent(context);
                        break;
                    case ID_WECHAT:
                        OpenAppFunction.getInstance().mOpenWechat();
                        break;
                    case ID_DVR:
                        OpenAppFunction.getInstance().mOpenDvr();
                        break;
                    case ID_MUSIC:
                        OpenAppFunction.getInstance().mOpenMusic();
                        break;
                    case ID_NAVI:
                        OpenAppFunction.getInstance().mOpenNavi(context);
                        break;
                    case ID_OFFROADINFO:
                        OpenAppFunction.getInstance().mOpenOffRoadInfo(bundle);
                        break;
                    case ID_APA:
                        OpenAppFunction.getInstance().mOpenApa(context);
                        break;
                    case ID_YUEYEQUAN:
                        OpenAppFunction.getInstance().mOpenyueyequan(context);
                        break;
                    case ID_CARBIT:
                        OpenAppFunction.getInstance().mOpenCarbit(context);
                        break;
                    case ID_USERGUIDE:
                        OpenAppFunction.getInstance().mOpenUserGuide();
                        break;
                    default:
                        break;
                }
            }
        });
        return convertView;
    }

    class ViewHolder {
        protected ImageView item_image;
        protected TextView item_text;
        protected LinearLayout llAllappFrame;
        protected LinearLayout iv_select;

        public ViewHolder(View convertView) {
            if (convertView != null) {
                item_image = (ImageView) convertView.findViewById(R.id.item_image);
                item_text = (TextView) convertView.findViewById(R.id.item_text);
                llAllappFrame = (LinearLayout) convertView.findViewById(R.id.iv_select);
                iv_select = (LinearLayout) convertView.findViewById(R.id.iv_select);
            }
        }
    }

    public void setDataList(List list) {
        this.list = list;
    }
}
