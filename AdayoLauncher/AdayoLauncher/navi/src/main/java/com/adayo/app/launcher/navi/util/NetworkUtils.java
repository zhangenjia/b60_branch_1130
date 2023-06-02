package com.adayo.app.launcher.navi.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.util.Log;

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";
    private final ConnectivityManager mConnectManager;
    private final Context mContext;
    private OnNetWorkChangeListener onNetWorkChangeListener;
    private static NetworkUtils mNetworkUtils;

    public static NetworkUtils getInstance(Context context) {
        if (null == mNetworkUtils) {
            synchronized (NetworkUtils.class) {
                if (null == mNetworkUtils) {
                    mNetworkUtils = new NetworkUtils(context);
                }
            }
        }
        return mNetworkUtils;
    }

    public NetworkUtils(Context mContext) {
        this.mContext = mContext;
        mConnectManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        if (mConnectManager != null) {
            mConnectManager.registerNetworkCallback(networkRequest, mNetworkCallback);
        } else {

        }
    }

    private final ConnectivityManager.NetworkCallback mNetworkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if ((info.getType() == ConnectivityManager.TYPE_WIFI) || (info.getType() == ConnectivityManager.TYPE_MOBILE)) {
                        if (onNetWorkChangeListener != null) {
                            onNetWorkChangeListener.onNetChange(true);
                        }
                    } else {
                        if (onNetWorkChangeListener != null) {
                            onNetWorkChangeListener.onNetChange(false);
                        }
                    }
                } else {
                    if (onNetWorkChangeListener != null) {
                        onNetWorkChangeListener.onNetChange(false);
                    }
                }
            } else {
                if (onNetWorkChangeListener != null) {
                    onNetWorkChangeListener.onNetChange(false);
                }
            }
        }

        @Override
        public void onLosing(Network network, int maxMsToLive) {
            super.onLosing(network, maxMsToLive);
            Log.d("bug1013178_", "onLosing 3: ");
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            Log.d("bug1013178_", "onLost: ");

        }

        @Override
        public void onUnavailable() {
            super.onUnavailable();
        }

        @Override
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
        }

        @Override
        public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
            super.onLinkPropertiesChanged(network, linkProperties);
        }
    };

    public interface OnNetWorkChangeListener {
        void onNetChange(boolean isConnected);
    }

    public void setonNetWorkChangeListener(OnNetWorkChangeListener onNetWorkChangeListener) {
        this.onNetWorkChangeListener = onNetWorkChangeListener;
    }
}
