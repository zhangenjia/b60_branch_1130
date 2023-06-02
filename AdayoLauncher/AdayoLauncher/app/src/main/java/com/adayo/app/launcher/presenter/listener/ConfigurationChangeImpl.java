package com.adayo.app.launcher.presenter.listener;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationChangeImpl implements IConfigurationChangeListener {

    private List<ConfigurationChangeCallBack> list = new ArrayList<>();
    private static  ConfigurationChangeImpl mConfigurationChangeImpl;

    public static ConfigurationChangeImpl getInstance() {
        if (null == mConfigurationChangeImpl) {
            synchronized (ConfigurationChangeImpl.class) {
                if (null == mConfigurationChangeImpl) {
                    mConfigurationChangeImpl = new ConfigurationChangeImpl();
                }
            }
        }
        return mConfigurationChangeImpl;
    }

    @Override
    public void configurationChange() {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i)!=null){
                Log.d("configurationChange", "! = null: ");
                list.get(i).configurationChange();
            }
        }
    }

    @Override
    public void addConfigurationChangeCallBack(ConfigurationChangeCallBack mConfigurationChangeCallBack) {
        if (!list.contains(mConfigurationChangeCallBack)) {
            list.add(mConfigurationChangeCallBack);
        }else {

        }
    }
}
