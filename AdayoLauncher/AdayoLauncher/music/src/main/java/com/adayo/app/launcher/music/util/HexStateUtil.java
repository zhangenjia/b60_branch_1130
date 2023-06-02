package com.adayo.app.launcher.music.util;

public class HexStateUtil {
    private HexStateUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean has(int sourceState, int targetState) {
        return (sourceState & targetState) != 0;
    }

    public static int add(int sourceState, int offsetState) {
        return sourceState | offsetState;
    }

    public static int del(int sourceState, int offsetState) {
        return sourceState & ~offsetState;
    }
}
