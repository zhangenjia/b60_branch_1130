package warning;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.adayo.app.launcher.R;
import com.adayo.app.launcher.presenter.function.ConfigFunction;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import warning.listener.ConfirmClickImpl;
import warning.util.SystemPropertyUtil;

import static android.view.WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
import static com.adayo.app.launcher.util.MyConstantsUtil.HIGH_CONFIG_VEHICLE;
import static warning.service.WarningAppService.ISNEVERSHOW;
import static warning.service.WarningAppService.ISNOMALSHOW;


public class WarningDialog extends Dialog implements View.OnClickListener {

    private static final String TAG = "WarningAppService";
    private static final String SYSTEM_PROPERTY_KEY_BOOTWARNING_NEVER_SHOW = "persist.warning.neverShow";
    private CheckBox cbBootWarningNever;
    private LinearLayout llBootWarningConfirm;
    private TextView tvBootWarningCountdown;
    Context mContext;
    private String offLineConfiguration;
    private TextView rules_heigh;
    private TextView rules_low;
    private LinearLayout ll_bootWarning_confirm;
    private ConstraintLayout parent_layout;

    public WarningDialog(Context context) {
        super(context, R.style.WarningApp);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_warning);
        initWindow();
        parent_layout = (ConstraintLayout) findViewById(R.id.parent_layout);
        cbBootWarningNever = (CheckBox) findViewById(R.id.cb_bootWarning_never);
        llBootWarningConfirm = (LinearLayout) findViewById(R.id.ll_bootWarning_confirm);
        tvBootWarningCountdown = (TextView) findViewById(R.id.tv_bootWarning_countdown);
        rules_heigh = (TextView) findViewById(R.id.rules_heigh);
        rules_low = (TextView) findViewById(R.id.rules_low);
        ll_bootWarning_confirm = (LinearLayout) findViewById(R.id.ll_bootWarning_confirm);
        if (mContext.getResources().getConfiguration().locale.getCountry().equals("CN")) {
            Log.d(TAG, "onCreate: CN");
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) ll_bootWarning_confirm.getLayoutParams();
            layoutParams.setMargins(976, 844, 0, 0);
            ll_bootWarning_confirm.setLayoutParams(layoutParams);
        } else if (mContext.getResources().getConfiguration().locale.getCountry().equals("ENG")) {
            Log.d(TAG, "onCreate: ENG");
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) ll_bootWarning_confirm.getLayoutParams();
            layoutParams.setMargins(976, 866, 0, 0);
            ll_bootWarning_confirm.setLayoutParams(layoutParams);
            rules_heigh.setLineSpacing(0f, 1.3f);
        } else {
            Log.d(TAG, "onCreate: ==" + mContext.getResources().getConfiguration().locale.getCountry());
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) ll_bootWarning_confirm.getLayoutParams();
            layoutParams.setMargins(976, 866, 0, 0);
            ll_bootWarning_confirm.setLayoutParams(layoutParams);
            rules_heigh.setLineSpacing(0f, 1.3f);
        }

        offLineConfiguration = ConfigFunction.getInstance(mContext).getOffLineConfiguration();
        if (offLineConfiguration.equals(HIGH_CONFIG_VEHICLE)) {
            rules_heigh.setVisibility(View.VISIBLE);
            rules_low.setVisibility(View.INVISIBLE);
        } else {
            rules_heigh.setVisibility(View.INVISIBLE);
            rules_low.setVisibility(View.VISIBLE);
        }
        llBootWarningConfirm.setOnClickListener(this);
        cbBootWarningNever.setOnClickListener(this);

        AAOP_HSkin
                .getWindowViewManager()
                .addWindowView(parent_layout);

        AAOP_HSkin
                .with(cbBootWarningNever)
                .addViewAttrs(AAOP_HSkin.ATTR_BUTTON, R.drawable.warning_checkbox)
                .applySkin(false);
    }




    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_bootWarning_confirm) {
            Log.i(TAG, "onClick: ll_bootWarning_confirm");
            ConfirmClickImpl.getInstance().onConfirmClick();
            dismiss();
        } else if (v.getId() == R.id.cb_bootWarning_never) {
            Log.d(TAG, "onClick: cb_bootWarning_never");
            SystemPropertyUtil.setSystemProperty(SYSTEM_PROPERTY_KEY_BOOTWARNING_NEVER_SHOW,
                    cbBootWarningNever.isChecked() ? ISNEVERSHOW : ISNOMALSHOW);
        }
    }

    @SuppressWarnings("deprecation")
    private void initWindow() {
        Window window = getWindow();
        if (window != null) {
            window.setType(FIRST_SYSTEM_WINDOW + 58);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
            layoutParams.flags = FLAG_LAYOUT_NO_LIMITS;
            layoutParams.width = 1920;
            layoutParams.height = 1080;
            window.setAttributes(layoutParams);
        }
    }

}
