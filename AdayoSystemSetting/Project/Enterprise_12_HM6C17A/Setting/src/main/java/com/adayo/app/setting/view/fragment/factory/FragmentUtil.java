package com.adayo.app.setting.view.fragment.factory;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;

import com.adayo.app.base.LogUtil;
import com.lt.library.util.fragment.IFragmentFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;



public class FragmentUtil {
    private IFragmentFactory mFragmentFactory;
    private static Map<String, Fragment> mFragmentMap = new ArrayMap<>();

    private FragmentUtil() {
    }

    private static FragmentUtil getInstance() {
        return FragmentUtilHolder.INSTANCE;
    }

    @Nullable
    private static Fragment getFragment(String fragmentTag, String param1, String param2, FragmentManager fragmentManager) {
        Fragment fragment;
        fragment = fragmentManager.findFragmentByTag(fragmentTag);
        LogUtil.d("fragment1 " + fragment);
        if (Objects.isNull(fragment)) {
            fragment = mFragmentMap.get(fragmentTag);
            LogUtil.d("fragment2 " + fragment);
            if (fragment == null) {
                fragment = getInstance().mFragmentFactory.createProduct(fragmentTag, param1, param2);
            } else {
                mFragmentMap.remove(fragmentTag);
            }
        }
        return fragment;
    }

    private static void setFragmentAnimations(@NonNull int[] animations, FragmentTransaction fragmentTransaction) {
        if (Objects.isNull(animations)) {
            return;
        }
        if (animations.length == 2) {
            fragmentTransaction.setCustomAnimations(animations[0], animations[1]);
        } else if (animations.length == 4) {
            fragmentTransaction.setCustomAnimations(animations[0], animations[1], animations[2], animations[3]);
        } else {
            throw new IllegalArgumentException("unexpected animations = " + Arrays.toString(animations));
        }
    }

    private static void hideFragmentForParent(@Nullable Fragment fragment, List<Fragment> addedFragmentList, List<Integer> hideParentIdList, FragmentTransaction fragmentTransaction) {
        for (Fragment addedFragment : addedFragmentList) {
            if (Objects.equals(addedFragment, fragment)) {
                continue;
            }
            View fragmentView = addedFragment.getView();
            if (Objects.isNull(fragmentView)) {
                LogUtil.w("FragmentUtil", "fragmentView = " + null);
                continue;
            }
            ViewGroup viewGroup = (ViewGroup) fragmentView.getParent();
            if (Objects.isNull(viewGroup)) {
                LogUtil.w("FragmentUtil", "viewGroup = " + null);
                continue;
            }
            if (hideParentIdList.contains(viewGroup.getId())) {
                fragmentTransaction.hide(addedFragment);
                LogUtil.d(addedFragment + " will hide");
            }
        }
    }

    private static void showFragmentForTarget(@NonNull Fragment fragment, String fragmentTag, Integer showParentId, FragmentTransaction fragmentTransaction) {
        LogUtil.d("GG isAdded: " + fragment.isAdded() + ", isHidden: " + fragment.isHidden() + ", isVisible: " + fragment.isVisible() + ", isResumed: " + fragment.isResumed() + ", isRemoving: " + fragment.isRemoving() + ", isInLayout: " + fragment.isInLayout());
        if (!fragment.isAdded()) {
            fragmentTransaction.add(showParentId, fragment, fragmentTag);
            LogUtil.d(fragment + " will add");
        }else {
            fragmentTransaction.show(fragment);
            LogUtil.d(fragment + " will show");
        }}


    public static void switchFragment(String fragmentTag, int[] hideHostIds, Integer showHostId, FragmentManager fragmentManager) {
        switchFragment(fragmentTag, null, null, hideHostIds, showHostId, null, fragmentManager);
    }


    public static void switchFragment(String fragmentTag, String param1, String param2, int[] hideHostIds, Integer showHostId, FragmentManager fragmentManager) {
        switchFragment(fragmentTag, param1, param2, hideHostIds, showHostId, null, fragmentManager);
    }


    public static void switchFragment(String fragmentTag, int[] hideHostIds, Integer showHostId, int[] animations, FragmentManager fragmentManager) {
        switchFragment(fragmentTag, null, null, hideHostIds, showHostId, animations, animations, fragmentManager);
    }


    public static void switchFragment(String fragmentTag, String param1, String param2, int[] hideHostIds, Integer showHostId, int[] animations, FragmentManager fragmentManager) {
        switchFragment(fragmentTag, param1, param2, hideHostIds, showHostId, animations, animations, fragmentManager);
    }


    public static void switchFragment(String fragmentTag, String param1, String param2, int[] hideHostIds, Integer showHostId, int[] hideAnimations, int[] showAnimations, FragmentManager fragmentManager) {
        Fragment fragment = getFragment(fragmentTag, param1, param2, fragmentManager);
        LogUtil.d("fragment = " + fragment);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        setFragmentAnimations(hideAnimations, fragmentTransaction);
        hideFragmentForParent(fragment, fragmentManager.getFragments(), IntStream.of(hideHostIds).boxed().collect(Collectors.toList()), fragmentTransaction);
        if (Objects.nonNull(fragment)) {
            setFragmentAnimations(showAnimations, fragmentTransaction);
            showFragmentForTarget(fragment, fragmentTag, showHostId, fragmentTransaction);
        }
        fragmentTransaction.commitNow();
    }

    public static Fragment removeFragment(String fragmentTag, FragmentManager fragmentManager) {
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag);
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commitNow();
            mFragmentMap.put(fragmentTag, fragment);
        }
        LogUtil.d("fragment =" + fragment);
        return fragment;

    }

    public static void setFragmentFactory(IFragmentFactory fragmentFactory) {
        getInstance().mFragmentFactory = fragmentFactory;
    }

    private static class FragmentUtilHolder {
        private static final FragmentUtil INSTANCE = new FragmentUtil();
    }
}
