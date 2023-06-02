package com.adayo.app.crossinf.model;

import android.os.Bundle;

public class VehicleDataInFo {

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