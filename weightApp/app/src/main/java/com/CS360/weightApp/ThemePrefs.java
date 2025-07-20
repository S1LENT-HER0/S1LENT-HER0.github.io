package com.CS360.weightApp;

import android.content.Context;
import android.content.SharedPreferences;

// this handles the dark theme preference
public class ThemePrefs {

    private static final String PREFS = "prefs_theme";
    private static final String KEY_DARK = "dark_mode";

    public static boolean isDark(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_DARK, false);
    }

    public static void setDark(Context ctx, boolean enabled) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_DARK, enabled).apply();
    }
}
