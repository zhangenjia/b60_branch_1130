


package com.adayo.app.setting.viewmodel.callback;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;


public class ProtectedUnPeekLiveData<T> extends LiveData<T> {

    private final HashMap<Integer, Boolean> observers = new HashMap<>();
    protected boolean isAllowNullValue;

    public void observeInActivity(@NonNull AppCompatActivity activity, @NonNull Observer<? super T> observer) {
        LifecycleOwner owner = activity;
        Integer storeId = System.identityHashCode(activity.getViewModelStore());
        observe(storeId, owner, observer);
    }

    public void observeInFragment(@NonNull Fragment fragment, @NonNull Observer<? super T> observer) {
        LifecycleOwner owner = fragment.getViewLifecycleOwner();
        Integer storeId = System.identityHashCode(fragment.getViewModelStore());
        observe(storeId, owner, observer);
    }

    private void observe(@NonNull Integer storeId,
                         @NonNull LifecycleOwner owner,
                         @NonNull Observer<? super T> observer) {

        if (observers.get(storeId) == null) {
            observers.put(storeId, true);
        }

        super.observe(owner, t -> {
            if (!observers.get(storeId)) {
                observers.put(storeId, true);
                if (t != null || isAllowNullValue) {
                    observer.onChanged(t);
                }
            }
        });
    }


    @Override
    protected void setValue(T value) {
        if (value != null || isAllowNullValue) {
            for (Map.Entry<Integer, Boolean> entry : observers.entrySet()) {
                entry.setValue(false);
            }
            super.setValue(value);
        }
    }

    protected void clear() {
        super.setValue(null);
    }
}

