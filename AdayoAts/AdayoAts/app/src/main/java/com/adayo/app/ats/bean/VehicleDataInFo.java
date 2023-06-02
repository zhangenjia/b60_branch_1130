package com.adayo.app.ats.bean;

import android.os.Bundle;
import static com.adayo.app.ats.util.Constants.ATS_VERSION;

public class VehicleDataInFo {
    private static final String TAG = ATS_VERSION + VehicleDataInFo.class.getSimpleName();
    public VehicleDataInFo(int id, Bundle bundle) {
        this.id = id;
        this.bundle = bundle;
    }

    private int id;
    private Bundle bundle;

    public int getId() {
        return id;
    }

    public Bundle getBundle() {
        return bundle;
    }

}