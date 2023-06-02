package com.adayo.app.launcher.presenter.listener;

public interface IConfigurationChangeListener {
     void configurationChange();

     void addConfigurationChangeCallBack(ConfigurationChangeCallBack mConfigurationChangeCallBack);

     interface ConfigurationChangeCallBack{

         void configurationChange();
     }
}
