package com.adayo.app.launcher.ui.fragment;

import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_AVM;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_APA;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.adayo.app.launcher.R;
import com.adayo.app.launcher.model.adapter.AllAppAdapter;
import com.adayo.app.launcher.model.bean.AllAppBean;
import com.adayo.app.launcher.presenter.factory.CardMappingFactory;
import com.adayo.app.launcher.presenter.function.ConfigFunction;
import com.adayo.app.launcher.presenter.listener.TouchImpl;
import com.example.BcmImpl;
import com.adayo.app.launcher.ui.view.CustemGridView;
import com.adayo.app.launcher.util.MyConstantsUtil;
import com.adayo.app.launcher.util.SystemPropertiesUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AllAppFragment extends Fragment implements TouchImpl.CallBack {

    private static String TAG = AllAppFragment.class.getSimpleName();
    private List<AllAppBean> adapterUsedList = new ArrayList<>();
    private CustemGridView gv_allapp;
    private AllAppAdapter adapter;
    private String sortedString = "";
    private List<AllAppBean> configDefaultList = new ArrayList<>();
    private List sortedList;
    private Context context;

    /**
     * fragment onAttach 在Fragment 和 Activity 建立关联时调用（Activity 传递到此方法内）
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context.getApplicationContext());
        this.context = context.getApplicationContext();
        Log.d(TAG, "onAttach: ");
    }

    /**
     * fragment onCreate 创建该fragment，类似于Activity.onCreate，大部分时候可以在其中初始化除了view之外的东西；
     *
     * @param
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
    }

    /**
     * fragment onCreateView 当Fragment 创建视图时调用
     *
     * @param
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TouchImpl.getInstance().addCallBack(this);
        configDefaultList = CardMappingFactory.getInstance().getAllAppCardResource(ConfigFunction.getInstance(context).getOffLineConfiguration());
        sortedString = CardMappingFactory.getInstance().getAllAppCardMapping(ConfigFunction.getInstance(context).getOffLineConfiguration());
        getAllAppCardId();
        View view = initView(inflater.inflate(R.layout.layout_right_fragment, container, false));
        registerBcmDataListener();
        return view;
    }


    /**
     * 初始化数据---从SP中读取上一次存储的allapp info 获得排序，首次则按默认list排序
     *
     * @param
     */
    private void getAllAppCardId() {
        String mAllAppIdMapping = getAllAppCardIdFromSp();
        if (mAllAppIdMapping == null || mAllAppIdMapping.equals("")) {
            adapterUsedList = new ArrayList<>(configDefaultList);
            setAllAppCardIdToSp(sortedString);
            Log.d(TAG, "getAllAppCardId: ID_AVMaaa  "+BcmImpl.getInstance().getPowerStatus() );
        } else {
            sortedString = mAllAppIdMapping;
            List<String> strings = Arrays.asList(mAllAppIdMapping.split(""));//
            sortedList = new ArrayList(strings);
            sortedList.remove(0);
            for (int i = 0; i < sortedList.size(); i++) {
                for (int j = 0; j < configDefaultList.size(); j++) {
                    if (configDefaultList.get(j).getmId().equals(sortedList.get(i))) {
                        AllAppBean allAppBean = configDefaultList.get(j);
                        if (ID_APA.equals(allAppBean.getmId())) {
                            if (BcmImpl.getInstance().getNewEngineStatus() > 0&&"false".equals(BcmImpl.getInstance().getTrailerMode())) {
                                adapterUsedList.add(new AllAppBean(allAppBean.getmId(), allAppBean.getmResource(), allAppBean.getmName(), true));
                            } else {
                                adapterUsedList.add(new AllAppBean(allAppBean.getmId(), allAppBean.getmResource(), allAppBean.getmName(), false));
                            }
                        } else if (ID_AVM.equals(allAppBean.getmId())) {
                            Log.d(TAG, "getAllAppCardId: ID_AVMbbb "+BcmImpl.getInstance().getPowerStatus() );
                            if (BcmImpl.getInstance().getPowerStatus() > 0) {
                                adapterUsedList.add(new AllAppBean(allAppBean.getmId(), allAppBean.getmResource(), allAppBean.getmName(), true));
                            } else {
                                adapterUsedList.add(new AllAppBean(allAppBean.getmId(), allAppBean.getmResource(), allAppBean.getmName(), false));
                            }
                        } else {
                            Log.d(TAG, "getAllAppCardId: ID_ccccccc  "+allAppBean.getmId() );
                            adapterUsedList.add(new AllAppBean(allAppBean.getmId(), allAppBean.getmResource(), allAppBean.getmName(), true));
                        }
                    }
                }
            }
        }

    }


    /**
     * 初始化视图
     *
     * @param
     */
    private View initView(View view) {
        gv_allapp = (CustemGridView) view.findViewById(R.id.gv_allapp);
        adapter = new AllAppAdapter(getContext(), adapterUsedList);
        gv_allapp.setAdapter(adapter);
        gv_allapp.setOnChangeListener(new CustemGridView.OnChangeListener() {
            @Override
            public void onChange(int from, int to) {
                //这里的处理需要注意下
                AllAppBean beanfrom = adapterUsedList.get(from);
                //交换值
                adapterUsedList.set(from, adapterUsedList.get(to));
                adapterUsedList.set(to, beanfrom);
                adapter.notifyDataSetChanged();
                StringBuffer stringBuffer = new StringBuffer(sortedString);
                StringBuffer replace = stringBuffer.replace(from, from + 1, adapterUsedList.get(from).getmId());
                StringBuffer replace1 = replace.replace(to, to + 1, adapterUsedList.get(to).getmId());
                String s2 = replace1.toString();
                sortedString = s2;
                setAllAppCardIdToSp(s2);

            }

        });
        return view;
    }

    /**
     * 初始化数据--获取SP中卡片json ,通过json对象排列顺序 对 " all app "卡片进行排序
     *
     * @param
     */
    private String getAllAppCardIdFromSp() {
        String string = SystemPropertiesUtil.getInstance().getStringMethod(MyConstantsUtil.LAUNCHER_ALLAPPCARD_KEY, "");
        return string;
    }

    /**
     * 存储数据，把卡片的list转成json存到sp中
     *
     * @param
     */
    private void setAllAppCardIdToSp(String mIdMapping) {
        SystemPropertiesUtil.getInstance().setProperty(MyConstantsUtil.LAUNCHER_ALLAPPCARD_KEY, mIdMapping);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        adapter.notifyDataSetChanged();
        Log.d(TAG, "onConfigurationChanged: ");
    }

    @Override
    public boolean callBack() {
        return gv_allapp.getDragstate();
    }

    public void registerBcmDataListener() {
        BcmImpl.getInstance().setOnBcmDataChangeListener(new BcmImpl.OnBcmDataChangeListener() {
            @Override
            public void powerStatusChange(int value) {
                for (int i = 0; i < adapterUsedList.size(); i++) {
                    if (adapterUsedList.get(i).getmId() == ID_AVM) {
                        Log.d(TAG, "getAllAppCardId: ID_AVM"+BcmImpl.getInstance().getPowerStatus() );
                        if (BcmImpl.getInstance().getPowerStatus() > 0) {

                            adapterUsedList.get(i).setEnable(true);
                        } else {
                            adapterUsedList.get(i).setEnable(false);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                Log.d(TAG, "BcmDataListener powerStatusChange: " + adapterUsedList);
            }

            @Override
            public void engineStatusChange(int value) {
                for (int i = 0; i < adapterUsedList.size(); i++) {
                    if (adapterUsedList.get(i).getmId() == ID_APA) {
                        if (BcmImpl.getInstance().getNewEngineStatus() > 0&&"false".equals(BcmImpl.getInstance().getTrailerMode())) {
                            adapterUsedList.get(i).setEnable(true);

                        } else {
                            adapterUsedList.get(i).setEnable(false);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
                Log.d(TAG, "BcmDataListener engineStatusChange: " + adapterUsedList);
            }

            @Override
            public void onTrailerModeStatusChange() {
                for (int i = 0; i < adapterUsedList.size(); i++) {

                    if (adapterUsedList.get(i).getmId() == ID_APA) {
                        Log.d(TAG, "getAllAppCardId: ID_APA"+BcmImpl.getInstance().getNewEngineStatus() );
                        if (BcmImpl.getInstance().getNewEngineStatus() > 0&&"false".equals(BcmImpl.getInstance().getTrailerMode())) {
                            adapterUsedList.get(i).setEnable(true);

                        } else {
                            adapterUsedList.get(i).setEnable(false);
                        }
                    }
                }


                adapter.notifyDataSetChanged();
            }
        }, 1);
    }

}
