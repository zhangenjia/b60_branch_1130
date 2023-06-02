package warning;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.adayo.app.launcher.skin.MyAttrHandler;
import com.adayo.app.launcher.util.SystemPropertiesUtil;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.aaop_hskin_helper.AAOP_HSkinHelper;

import warning.service.WarningAppService;

import static com.adayo.app.launcher.util.MyConstantsUtil.WARNING_SKIP;
import static com.adayo.app.launcher.util.MyConstantsUtil.WARNING_STATE;


public class LauncherApplication extends Application {
    private static final String SYSTEM_PROPERTY_LAUNCH_COUNT_RECORD_KEY = "launchcountrecord";
    private static final String TAG = LauncherApplication.class.getSimpleName();
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        AAOP_HSkin
                .getInstance()
                .registerSkinAttrHandler("lottie", new MyAttrHandler());
        AAOP_HSkinHelper
                .init(getApplicationContext(), true, "AdayoLauncher");
        initWarningService();
    }

    /**
     * 开机警告
     *
     * @param
     */

    private void initWarningService() {

        Intent intent = new Intent(this, WarningAppService.class);
        intent.setAction("com.adayo.warningapp.service.WarningAppService");
        startService(intent);
    }

    private String getLaunchCountProperties() {
        String string = SystemPropertiesUtil.getInstance().getStringMethod(SYSTEM_PROPERTY_LAUNCH_COUNT_RECORD_KEY, "");
        return string;
    }

    public static Context getContext() {
        return context;
    }
}
