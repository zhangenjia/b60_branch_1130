package com.adayo.app.launcher.presenter.function;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";
    /**
     * 网络是否已连接，不一定能上网
     *
     * @return true:已连接 false:未连接
     */
    @SuppressWarnings("deprecation")
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean iConnected(@NonNull Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
//            谷歌已经将ConnectivityManager.getActiveNetworkInfo标记为已过时,所以在targetSdkVersion <29 之前时判断当前网络是否连接变更为新的Api:ConnectivityManager.getNetworkCapabilities
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities networkCapabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
                if (networkCapabilities != null) {
//                    return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)     //wifi的连接状态
//                            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)     //数据的连接状态
//                            || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
                    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);      //是否连接到互联网，即是否连接上了WIFI或者移动蜂窝网络
                }
            } else {
                NetworkInfo networkInfo = manager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
        }
        return false;
    }

    /**
     * Wifi是否已连接
     *
     * @return true:已连接 false:未连接
     */
    @SuppressWarnings("deprecation")
    public static boolean isWifiConnected(@NonNull Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities networkCapabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
                if (networkCapabilities != null) {
                    return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                }
            } else {
                NetworkInfo networkInfo = manager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
            }
        }
        return false;
    }

    /**
     * 数据是否已连接
     *
     * @return true:已连接 false:未连接
     */
    @SuppressWarnings("deprecation")
    public static boolean isMobileData(@NonNull Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities networkCapabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
                if (networkCapabilities != null) {
                    return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
                }
            } else {
                NetworkInfo networkInfo = manager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        return false;
    }
    /**
     * 判断网络是否可用，可以上网
     *
     * @return true可用 false不可用
     */
    @SuppressWarnings("deprecation")
    public static boolean isNetworkAvailable(@NonNull Context context) {
        Log.d(TAG, "isNetworkAvailable: ");
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities networkCapabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
                if (networkCapabilities != null) {
                    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
                }
            } else {
                NetworkInfo networkInfo = manager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
        }
        return false;
    }
}
