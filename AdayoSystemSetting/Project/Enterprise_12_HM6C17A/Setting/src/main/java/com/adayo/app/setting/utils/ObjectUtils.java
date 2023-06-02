package com.adayo.app.setting.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;




public final class ObjectUtils {

    private ObjectUtils() {
    }

    private static final String TAG = ObjectUtils.class.getSimpleName();


    public static boolean isEmpty(final Object object) {
        if (object == null) {
            return true;
        }
        try {
            if (object.getClass().isArray() && Array.getLength(object) == 0) {
                return true;
            }
            if (object instanceof CharSequence && object.toString().length() == 0) {
                return true;
            }
            if (object instanceof Collection && ((Collection<?>) object).isEmpty()) {
                return true;
            }
            if (object instanceof Map && ((Map<?, ?>) object).isEmpty()) {
                return true;
            }
        } catch (Exception e) {
            JCLogUtils.eTag(TAG, e, "isEmpty");
        }
        return false;
    }


    public static boolean isNotEmpty(final Object object) {
        return !isEmpty(object);
    }


    public static <T> boolean equals(
            final T value1,
            final T value2
    ) {
        if (value1 != null && value2 != null) {
            try {
                if (value1 instanceof String && value2 instanceof String) {
                    return value1.equals(value2);
                } else if (value1 instanceof CharSequence && value2 instanceof CharSequence) {
                    CharSequence v1 = (CharSequence) value1;
                    CharSequence v2 = (CharSequence) value2;
                    int length = v1.length();
                    if (length == v2.length()) {
                        for (int i = 0; i < length; i++) {
                            if (v1.charAt(i) != v2.charAt(i)) {
                                return false;
                            }
                        }
                        return true;
                    }
                    return false;
                }
                return value1.equals(value2);
            } catch (Exception e) {
                JCLogUtils.eTag(TAG, e, "equals");
            }
            return false;
        }
        return (value1 == null && value2 == null);
    }


    public static <T> T requireNonNull(
            final T object,
            final String message
    )
            throws NullPointerException {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }


    public static <T> T getOrDefault(
            final T object,
            final T defaultObject
    ) {
        return (object != null) ? object : defaultObject;
    }


    public static int hashCode(final Object object) {
        return object != null ? object.hashCode() : 0;
    }


    public static String getObjectTag(final Object object) {
        if (object == null) {
            return null;
        }
        return object.getClass().getName() + Integer.toHexString(object.hashCode());
    }


    public static <T> T convert(final Object object) {
        if (object == null) {
            return null;
        }
        try {
            return (T) object;
        } catch (Exception e) {
            JCLogUtils.eTag(TAG, e, "convert");
        }
        return null;
    }
}